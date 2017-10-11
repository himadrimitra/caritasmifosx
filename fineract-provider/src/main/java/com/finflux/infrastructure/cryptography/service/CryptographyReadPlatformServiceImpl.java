package com.finflux.infrastructure.cryptography.service;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;

import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.useradministration.domain.AppUserRepositoryWrapper;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.infrastructure.cryptography.api.CryptographyApiConstants;
import com.finflux.infrastructure.cryptography.data.CryptographyData;
import com.finflux.infrastructure.cryptography.data.CryptographyEntityType;
import com.finflux.infrastructure.cryptography.data.CryptographyKeyType;

@Service
public class CryptographyReadPlatformServiceImpl implements CryptographyReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final CryptographyDataMapper dataMapper;
    private final AppUserRepositoryWrapper appUserRepository;
    private final ConfigurationDomainService configurationDomainService;

    @Autowired
    public CryptographyReadPlatformServiceImpl(final RoutingDataSource dataSource, final AppUserRepositoryWrapper appUserRepository,
            final ConfigurationDomainService configurationDomainService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dataMapper = new CryptographyDataMapper();
        this.appUserRepository = appUserRepository;
        this.configurationDomainService = configurationDomainService;
    }

    @Override
    public CryptographyData getPublicKey(final String entityType, final String username) {
        final CryptographyEntityType cryptographyEntityType = CryptographyEntityType.fromString(entityType);
        Long userId = null;
        if (cryptographyEntityType.isLogin()) {
            userId = this.appUserRepository.findAppUserIdByName(username);
        }
        return getKey(cryptographyEntityType.getValue(), CryptographyKeyType.PUBLIC.getValue(), userId);
    }

    @Override
    public CryptographyData getPrivateKey(final String entityType, final String username) {
        final CryptographyEntityType cryptographyEntityType = CryptographyEntityType.fromString(entityType);
        Long userId = null;
        if (cryptographyEntityType.isLogin()) {
            userId = this.appUserRepository.findAppUserIdByName(username);
        }
        return getKey(cryptographyEntityType.getValue(), CryptographyKeyType.PRIVATE.getValue(), userId);
    }

    private CryptographyData getKey(final Integer entityType, final Integer keyType, final Long userId) {
        final String sql;
        try {
            if (userId == null) {
                sql = "SELECT " + this.dataMapper.schema() + " WHERE ck.entity_type = ? and ck.key_type = ? ";
                return this.jdbcTemplate.queryForObject(sql, this.dataMapper, new Object[] { entityType, keyType });
            }
            sql = "SELECT " + this.dataMapper.schema() + " WHERE ck.entity_type = ? and ck.key_type = ? and ck.user_id = ? ";
            return this.jdbcTemplate.queryForObject(sql, this.dataMapper, new Object[] { entityType, keyType, userId });
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

    private static final class CryptographyDataMapper implements RowMapper<CryptographyData> {

        private final String schemaSql;

        public String schema() {
            return this.schemaSql;
        }

        public CryptographyDataMapper() {
            final StringBuilder sqlBuilder = new StringBuilder(200);
            sqlBuilder.append("ck.id AS id,ck.entity_type AS entityType, ck.key_type AS keyType ");
            sqlBuilder.append(",AES_DECRYPT(ck.key_value,'" + CryptographyApiConstants.userLoginPasswordEncDecKey + "') AS keyValue ");
            sqlBuilder.append(",ck.user_id AS userId ");
            sqlBuilder.append("FROM f_cryptography_key ck ");
            this.schemaSql = sqlBuilder.toString();
        }

        @Override
        public CryptographyData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long id = rs.getLong("id");
            final String entityType = rs.getString("entityType");
            final String keyType = rs.getString("keyType");
            final Blob keyValue = rs.getBlob("keyValue");
            final Long userId = rs.getLong("userId");
            return CryptographyData.instance(id, entityType, keyType, keyValue, userId);
        }
    }

    @Override
    public String decryptEncryptedTextUsingRSAPrivateKey(String encryptedText, final String entityType, final String username,
            final boolean isBase64Encoded) {
        String decryptedText = null;
        final CryptographyData privateKeyData = getPrivateKey(entityType, username);
        if (privateKeyData.getUserId() != null && this.configurationDomainService.isEnabledEveryUserLoginGenerateNewCryptographicKeyPair()) {
            resetKey(privateKeyData.getId());
        }
        final byte[] privateKeyAsByte = Base64.decodeBase64(privateKeyData.getKeyValue());
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyAsByte));
            Cipher decryptKey = Cipher.getInstance("RSA");
            decryptKey.init(Cipher.DECRYPT_MODE, privateKey);
            if (isBase64Encoded) {
                // final byte[] decoded = Base64.decodeBase64(encryptedText);
                // encryptedText = Hex.encodeHexString(decoded);
                encryptedText = base64ToHex(encryptedText);
                byte[] decryptByte = decryptKey.doFinal(DatatypeConverter.parseHexBinary(encryptedText));
                decryptedText = new String(decryptByte);
            } else {
                byte[] decryptByte = decryptKey.doFinal(encryptedText.getBytes());
                decryptedText = new String(decryptByte);
            }
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return decryptedText;
    }

    private void resetKey(final Long id) {
        final String resetKeySql = "UPDATE `f_cryptography_key` set `key_value` = '' where `id` = ? ";
        this.jdbcTemplate.update(resetKeySql, new Object[] { id });
    }

    private String base64ToHex(String s) {
        String b64map = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
        Character b64pad = '=';
        String ret = "";
        Integer i;
        Integer k = 0;
        Integer slop = 0;
        Integer v;
        for (i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == b64pad) break;
            v = b64map.indexOf(s.charAt(i));
            if (v < 0) continue;
            if (k == 0) {
                ret += int2char(v >> 2);
                slop = v & 3;
                k = 1;
            } else if (k == 1) {
                ret += int2char((slop << 2) | (v >> 4));
                slop = v & 0xf;
                k = 2;
            } else if (k == 2) {
                ret += int2char(slop);
                ret += int2char(v >> 2);
                slop = v & 3;
                k = 3;
            } else {
                ret += int2char((slop << 2) | (v >> 4));
                ret += int2char(v & 0xf);
                k = 0;
            }
        }
        if (k == 1) ret += int2char(slop << 2);
        return ret;
    }

    private Character int2char(Integer n) {
        String BI_RM = "0123456789abcdefghijklmnopqrstuvwxyz";
        return BI_RM.charAt(n);
    }
}
