package com.finflux.infrastructure.cryptography.service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.useradministration.domain.AppUserRepositoryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.finflux.infrastructure.cryptography.api.CryptographyApiConstants;
import com.finflux.infrastructure.cryptography.data.CryptographyEntityType;
import com.finflux.infrastructure.cryptography.data.CryptographyKeyType;
import com.finflux.infrastructure.cryptography.domain.CryptographyKey;
import com.finflux.infrastructure.cryptography.domain.CryptographyKeyRepository;

@Service
public class CryptographyWritePlatformServiceImpl implements CryptographyWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(CryptographyWritePlatformServiceImpl.class);

    private final JdbcTemplate jdbcTemplate;
    private final CryptographyKeyRepository cryptographyKeyRepository;
    private final AppUserRepositoryWrapper appUserRepository;

    @Autowired
    public CryptographyWritePlatformServiceImpl(final RoutingDataSource dataSource,
            final CryptographyKeyRepository cryptographyKeyRepository, final AppUserRepositoryWrapper appUserRepository) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.cryptographyKeyRepository = cryptographyKeyRepository;
        this.appUserRepository = appUserRepository;
    }

    private void createOrChangeLoginPrivateAndPublicKeys(final boolean isUpdate, final Long userId) {
        final List<CryptographyKey> cryptographyKeys = this.cryptographyKeyRepository.findByEntityType(CryptographyEntityType.LOGIN
                .getValue());
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            byte[] publicKeyAsBytes = publicKey.getEncoded();
            byte[] privateKeyAsBytes = privateKey.getEncoded();
            if (cryptographyKeys == null || cryptographyKeys.isEmpty()) {
                insertEncryptedKey(CryptographyEntityType.LOGIN.getValue(), CryptographyKeyType.PUBLIC.getValue(), publicKeyAsBytes, userId);
                insertEncryptedKey(CryptographyEntityType.LOGIN.getValue(), CryptographyKeyType.PRIVATE.getValue(), privateKeyAsBytes,
                        userId);
            } else if (isUpdate) {
                updateEncryptedKey(CryptographyEntityType.LOGIN.getValue(), CryptographyKeyType.PUBLIC.getValue(), publicKeyAsBytes, userId);
                updateEncryptedKey(CryptographyEntityType.LOGIN.getValue(), CryptographyKeyType.PRIVATE.getValue(), privateKeyAsBytes,
                        userId);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private Integer insertEncryptedKey(final Integer entityType, final Integer keyType, final byte[] blobKey, final Long userId) {
        final String insertSql;
        try {
            if (userId == null) {
                insertSql = "INSERT INTO `f_cryptography_key` (`entity_type`,`key_type`, `key_value`) VALUES (?, ? ,AES_ENCRYPT(?,?)) ";
                return this.jdbcTemplate.update(insertSql, new Object[] { entityType, keyType, blobKey,
                        CryptographyApiConstants.userLoginPasswordEncDecKey });
            }
            insertSql = "INSERT INTO `f_cryptography_key` (`entity_type`,`key_type`, `key_value`, `user_id`) VALUES (?, ? ,AES_ENCRYPT(?,?), ?) ";
            return this.jdbcTemplate.update(insertSql, new Object[] { entityType, keyType, blobKey,
                    CryptographyApiConstants.userLoginPasswordEncDecKey, userId });
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(dve);
            return null;
        }
    }

    private Integer updateEncryptedKey(final Integer entityType, final Integer keyType, final byte[] blobKey, final Long userId) {
        final String updateSql;
        try {
            if (userId == null) {
                updateSql = "UPDATE `f_cryptography_key` set `key_value` = AES_ENCRYPT(?,?) where `entity_type` = ? and `key_type` = ? ";
                return this.jdbcTemplate.update(updateSql, new Object[] { blobKey, CryptographyApiConstants.userLoginPasswordEncDecKey,
                        entityType, keyType });
            }
            updateSql = "UPDATE `f_cryptography_key` set `key_value` = AES_ENCRYPT(?,?) where `entity_type` = ? and `key_type` = ? and `user_id` = ? ";
            return this.jdbcTemplate.update(updateSql, new Object[] { blobKey, CryptographyApiConstants.userLoginPasswordEncDecKey,
                    entityType, keyType, userId });
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(dve);
            return null;
        }
    }

    @Override
    public void generateKeyPairAndStoreIntoDataBase(final CryptographyEntityType cryptographyEntityType, final String username,
            final boolean isUpdate) {
        if (cryptographyEntityType.isLogin()) {
            final Long userId = this.appUserRepository.findAppUserIdByName(username);
            createOrChangeLoginPrivateAndPublicKeys(isUpdate, userId);
        }
    }

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