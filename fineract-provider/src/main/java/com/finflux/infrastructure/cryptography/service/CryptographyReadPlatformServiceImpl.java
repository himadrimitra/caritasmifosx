package com.finflux.infrastructure.cryptography.service;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.infrastructure.cryptography.api.CryptographyApiConstants;
import com.finflux.infrastructure.cryptography.data.CryptographyData;
import com.finflux.infrastructure.cryptography.exception.CryptographyKeyNotFoundException;

@Service
public class CryptographyReadPlatformServiceImpl implements CryptographyReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final CryptographyDataMapper dataMapper;

    @Autowired
    public CryptographyReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dataMapper = new CryptographyDataMapper();
    }

    @Override
    public CryptographyData getPublicKey(final String entityType) {
        return getKey(entityType, CryptographyApiConstants.publicKey);
    }

    @Override
    public CryptographyData getPrivateKey(final String entityType) {
        return getKey(entityType, CryptographyApiConstants.privateKey);
    }

    private CryptographyData getKey(final String entityType, final String keyType) {
        final String sql = "SELECT " + this.dataMapper.schema() + " WHERE ck.entity_type = ? and ck.key_type = ? ";
        try {
            return this.jdbcTemplate.queryForObject(sql, this.dataMapper, new Object[] { entityType, keyType });
        } catch (final EmptyResultDataAccessException e) {
            throw new CryptographyKeyNotFoundException(keyType);
        }
    }

    private static final class CryptographyDataMapper implements RowMapper<CryptographyData> {

        private final String schemaSql;

        public String schema() {
            return this.schemaSql;
        }

        public CryptographyDataMapper() {
            final StringBuilder sqlBuilder = new StringBuilder(200);
            sqlBuilder.append("ck.entity_type AS entityType, ck.key_type AS keyType, ");
            sqlBuilder.append("AES_DECRYPT(ck.key_value,'" + CryptographyApiConstants.userLoginPasswordEncDecKey + "') AS keyValue ");
            sqlBuilder.append("FROM f_cryptography_key ck ");
            this.schemaSql = sqlBuilder.toString();
        }

        @Override
        public CryptographyData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final String entityType = rs.getString("entityType");
            final String keyType = rs.getString("keyType");
            final Blob keyValue = rs.getBlob("keyValue");
            return CryptographyData.instance(entityType, keyType, keyValue);
        }

    }
}
