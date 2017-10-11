package com.finflux.infrastructure.cryptography.service;

import com.finflux.infrastructure.cryptography.data.CryptographyEntityType;

public interface CryptographyWritePlatformService {

    void generateKeyPairAndStoreIntoDataBase(final CryptographyEntityType cryptographyEntityType, final String username,
            final boolean isUpdate);
}