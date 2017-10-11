package com.finflux.infrastructure.cryptography.service;

import com.finflux.infrastructure.cryptography.data.CryptographyData;

public interface CryptographyReadPlatformService {

    CryptographyData getPublicKey(final String entityType, final String username);

    CryptographyData getPrivateKey(final String entityType, final String username);

    String decryptEncryptedTextUsingRSAPrivateKey(final String encryptedText, final String entityType, final String username,
            final boolean isBase64Encoded);
}
