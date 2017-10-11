package com.finflux.portfolio.external.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.finflux.common.security.service.PlatformCryptoService;
import com.finflux.common.security.service.SecurityUtils;
import com.finflux.portfolio.external.ExternalServiceType;
import com.finflux.portfolio.external.data.ExternalServicePropertyData;
import com.finflux.portfolio.external.data.ExternalServicesData;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.external.exception.ExternalServicesNotFoundException;

@Service
public class ExternalServicesReadServiceImpl implements ExternalServicesReadService {

    private final JdbcTemplate jdbcTemplate;
    private final ExternalServicesPropertiesMapper propertiesMapper = new ExternalServicesPropertiesMapper();
    private final PlatformCryptoService platformCryptoService ;
    private final SecurityUtils securityUtils;

    @Autowired
    public ExternalServicesReadServiceImpl(final RoutingDataSource dataSource, final PlatformCryptoService platformCryptoService,
            final SecurityUtils securityUtils) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.platformCryptoService = platformCryptoService;
        this.securityUtils = securityUtils;
    }

    @Override
    public ExternalServicesData findOneWithNotFoundException(final Long id) {
        ExternalServicesMapper externalServicesMapper = new ExternalServicesMapper();
        String sql = "select " + externalServicesMapper.schema() + " where es.id = ?";
        try {
            ExternalServicesData data = this.jdbcTemplate.queryForObject(sql, externalServicesMapper, id);
            return data;
        } catch (final EmptyResultDataAccessException e) {
            throw new ExternalServicesNotFoundException(id);
        }

    }

    @Override
    public Collection<ExternalServicesData> findExternalServicesByType(final Integer type) {
        ExternalServicesMapper externalServicesMapper = new ExternalServicesMapper();
        String sql = "select " + externalServicesMapper.schema() + " where es.id = ?";
        return this.jdbcTemplate.query(sql, externalServicesMapper, type);
    }

    @Override
    public Collection<ExternalServicesData> findAllExternalServices() {
        ExternalServicesMapper externalServicesMapper = new ExternalServicesMapper();
        String sql = "select " + externalServicesMapper.schema() ;
        return this.jdbcTemplate.query(sql, externalServicesMapper);
    }

    @Override
    public List<ExternalServicePropertyData> findClearPropertiesForExternalServices(Long id) {
        String sql = "select " + propertiesMapper.schema() + " where esp.external_service_id = ?";
        List<ExternalServicePropertyData> externalServicePropertyDataList =  this.jdbcTemplate.query(sql, propertiesMapper, id);
        if(externalServicePropertyDataList!=null){
            for(ExternalServicePropertyData propertyData: externalServicePropertyDataList){
                if(propertyData.getEncrypted()){
                    //set Clear Values
                    propertyData.setValue(platformCryptoService.decrypt(propertyData.getValue()));
                    propertyData.setEncrypted(false);
                }
            }
        }
        return externalServicePropertyDataList;
    }

    @Override
    public List<ExternalServicePropertyData> findMaskedPropertiesForExternalServices(Long id) {
        String sql = "select " + propertiesMapper.schema() + " where esp.external_service_id = ?";
        List<ExternalServicePropertyData> externalServicePropertyDataList =  this.jdbcTemplate.query(sql, propertiesMapper, id);
        if(externalServicePropertyDataList!=null){
            for(ExternalServicePropertyData propertyData: externalServicePropertyDataList){
                if(propertyData.getEncrypted()){
                    String decryptedValue = platformCryptoService.decrypt(propertyData.getValue());
                    String maskedValue = securityUtils.mask(decryptedValue);
                    propertyData.setValue(maskedValue);
                }
            }
        }
        return externalServicePropertyDataList;
    }

    private class ExternalServicesMapper implements RowMapper<ExternalServicesData> {

        public String schema() {
            StringBuilder sb = new StringBuilder();
            sb.append(" es.id as id, es.name as name, es.display_code as displayCode, es.type as type ");
            sb.append(" from f_external_service_details es ");
            return sb.toString();
        }

        @Override
        public ExternalServicesData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Long id = rs.getLong("id");
            final String name = rs.getString("name");
            final String displayCode = rs.getString("displayCode");
            final Integer type = JdbcSupport.getInteger(rs, "type");
            final EnumOptionData enumType = ExternalServiceType.fromInt(type).getEnumOptionData();
            return new ExternalServicesData(id, name, displayCode, enumType);
        }

    }

    private class ExternalServicesPropertiesMapper implements RowMapper<ExternalServicePropertyData> {

        public String schema() {
            StringBuilder sb = new StringBuilder();
            sb.append(" esp.name as name, esp.value as value, esp.is_encrypted as isEncrypted ");
            sb.append(" from f_external_service_properties esp ");
            return sb.toString();
        }

        @Override
        public ExternalServicePropertyData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            Map<String,String> hashMap = new HashMap<>();
            final String name = rs.getString("name");
            final String value = rs.getString("value");
            final Boolean isEncrypted = rs.getBoolean( "isEncrypted");
            return new ExternalServicePropertyData(name, value, isEncrypted);
        }

    }

}
