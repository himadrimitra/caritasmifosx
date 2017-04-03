package com.finflux.infrastructure.cryptography.service;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;

import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.finflux.infrastructure.cryptography.api.CryptographyApiConstants;
import com.finflux.infrastructure.cryptography.data.CryptographyData;
import com.finflux.infrastructure.cryptography.domain.CryptographyKey;
import com.finflux.infrastructure.cryptography.domain.CryptographyKeyRepository;

@Service
public class CryptographyWritePlatformServiceImpl implements CryptographyWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(CryptographyWritePlatformServiceImpl.class);

    private final JdbcTemplate jdbcTemplate;
    private final CryptographyKeyRepository cryptographyKeyRepository;
    private final CryptographyReadPlatformService cryptographyReadPlatformService;

    @Autowired
    public CryptographyWritePlatformServiceImpl(final RoutingDataSource dataSource,
            final CryptographyKeyRepository cryptographyKeyRepository, final CryptographyReadPlatformService cryptographyReadPlatformService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.cryptographyKeyRepository = cryptographyKeyRepository;
        this.cryptographyReadPlatformService = cryptographyReadPlatformService;
    }

    @Override
    public void createOrChangeLoginPrivateAndPublicKeys(final boolean isUpdate) {
        final List<CryptographyKey> cryptographyKeys = this.cryptographyKeyRepository
                .findByEntityType(CryptographyApiConstants.keyTypeLogin);
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            byte[] publicKeyAsBytes = publicKey.getEncoded();
            byte[] privateKeyAsBytes = privateKey.getEncoded();
            if (cryptographyKeys == null || cryptographyKeys.isEmpty()) {
                insertEncryptedKey(CryptographyApiConstants.keyTypeLogin, CryptographyApiConstants.publicKey, publicKeyAsBytes);
                insertEncryptedKey(CryptographyApiConstants.keyTypeLogin, CryptographyApiConstants.privateKey, privateKeyAsBytes);
            } else if (isUpdate) {
                updateEncryptedKey(CryptographyApiConstants.keyTypeLogin, CryptographyApiConstants.publicKey, publicKeyAsBytes);
                updateEncryptedKey(CryptographyApiConstants.keyTypeLogin, CryptographyApiConstants.privateKey, privateKeyAsBytes);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private int insertEncryptedKey(final String types, final String keyType, final byte[] blobKey) {
        final String dbSecreteKey = CryptographyApiConstants.userLoginPasswordEncDecKey;
        final String insertSql = "INSERT INTO `f_cryptography_key` (`entity_type`,`key_type`, `key_value`) VALUES (?, ? ,AES_ENCRYPT(?,?)) ";
        return this.jdbcTemplate.update(insertSql, new Object[] { types, keyType, blobKey, dbSecreteKey });
    }

    private int updateEncryptedKey(final String types, final String keyType, final byte[] blobKey) {
        final String dbSecreteKey = CryptographyApiConstants.userLoginPasswordEncDecKey;
        final String insertSql = "UPDATE `f_cryptography_key` set `key_value` = AES_ENCRYPT(?,?) where (`types` = ? and `key_type` = ?) ";
        return this.jdbcTemplate.update(insertSql, new Object[] { blobKey, dbSecreteKey, types, keyType });
    }

    @Override
    public void generateKeyPairAndStoreIntoDataBase(final String entityType) {
        if (entityType != null && entityType.equalsIgnoreCase(CryptographyApiConstants.keyTypeLogin)) {
            final boolean isUpdate = false;
            createOrChangeLoginPrivateAndPublicKeys(isUpdate);
        }
    }

    @Override
    public String decryptEncryptedTextUsingRSAPrivateKey(String encryptedText, final String entityType, final boolean isBase64Encoded) {
        String decryptedText = null;
        CryptographyData privateKeyData = null;
        privateKeyData = this.cryptographyReadPlatformService.getPrivateKey(entityType);
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

    @SuppressWarnings("unused")
    private void handleDataIntegrityIssues(final DataIntegrityViolationException dve) {
        final Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("UQ_f_cryptography_entity_type_key_type")) { throw new PlatformDataIntegrityException(
                "error.msg.cryptography.key.duplicated", "Cryptography keys already exists"); }
        logAsErrorUnexpectedDataIntegrityException(dve);
        throw new PlatformDataIntegrityException("error.msg.cryptography.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
        logger.error(dve.getMessage(), dve);
    }
}
