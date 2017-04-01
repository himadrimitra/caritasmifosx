package com.finflux.infrastructure.cryptography.service;

import com.finflux.infrastructure.cryptography.data.CryptographyData;

public interface CryptographyReadPlatformService {

    CryptographyData getPublicKey(final String entityType);

    CryptographyData getPrivateKey(final String entityType);
}
