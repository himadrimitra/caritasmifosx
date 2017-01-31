package com.finflux.common.security.service;

import org.apache.commons.codec.binary.Hex;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

@Service()
public class PlatformCryptoServiceImpl implements PlatformCryptoService {

    private final ConfigurationDomainService configurationDomainService;

    @Autowired
    public PlatformCryptoServiceImpl(final RoutingDataSource dataSource,
                                     final ConfigurationDomainService configurationDomainService) {
        this.configurationDomainService = configurationDomainService;
    }

    @Override
    public String encrypt(String value) {
        TextEncryptor encryptor = getEncryptorForCurrentTenant();
        String encryptedString = encryptor.encrypt(value);
        return encryptedString;
    }

    private TextEncryptor getEncryptorForCurrentTenant() {
        FineractPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
        String salt = new String(Hex.encodeHex(tenant.getName().getBytes()));
        return Encryptors.text(tenant.getId()+tenant.getTenantIdentifier(),salt);
    }


    @Override
    public String decrypt(String value) {
        TextEncryptor encryptor = getEncryptorForCurrentTenant();
        String decryptedString = encryptor.decrypt(value);
        return decryptedString;
    }

    @Override
    public String mask(String value) {
        String maskRegex = configurationDomainService.getMaskedRegex();
        String maskReplaceCharacter = configurationDomainService.getMaskedCharacter();
        if(value!=null){
            return value.replaceAll(maskRegex,maskReplaceCharacter);
        }
        return null;
    }
}
