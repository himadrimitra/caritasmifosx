package com.finflux.infrastructure.cryptography.service;


public interface CryptographyWritePlatformService {

    void createOrChangeLoginPrivateAndPublicKeys(final boolean isUpdate);

    String decryptEncryptedTextUsingRSAPrivateKey(final String encryptedText, final String entityType, final boolean isBase64Encoded);

    void generateKeyPairAndStoreIntoDataBase(final String entityType);
}
