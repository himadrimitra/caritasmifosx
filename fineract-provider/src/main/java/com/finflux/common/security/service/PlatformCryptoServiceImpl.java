package com.finflux.common.security.service;

import org.apache.commons.codec.binary.Hex;
import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

@Service()
public class PlatformCryptoServiceImpl implements PlatformCryptoService {

    @Override
    public String encrypt(final String value) {
        final TextEncryptor encryptor = getEncryptorForCurrentTenant();
        return encryptor.encrypt(value);
    }

    private TextEncryptor getEncryptorForCurrentTenant() {
        final FineractPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
        final Long password1 = tenant.getId();
        final String password2 = tenant.getTenantIdentifier();
        final String tenantKey = tenant.getTenantKey();
        final String saltText = tenant.getName();
        return getEncryptor(saltText, password1, tenantKey, password2);
    }

    private TextEncryptor getEncryptor(final String saltText, final Object... passwords) {
        final String salt = new String(Hex.encodeHex(saltText.getBytes()));
        final StringBuilder key = new StringBuilder();
        for (final Object password : passwords) {
            key.append(password);
        }
        return Encryptors.text(key.toString(), salt);
    }

    @Override
    public String decrypt(final String value) {
        final TextEncryptor encryptor = getEncryptorForCurrentTenant();
        return encryptor.decrypt(value);
    }

    @Override
    public String decrypt(final String value, final String salt, final Object... passwords) {
        final TextEncryptor encryptor = getEncryptor(salt, passwords);
        return encryptor.decrypt(value);
    }

    @Override
    public String encrypt(final String value, final String salt, final Object... passwords) {
        final TextEncryptor encryptor = getEncryptor(salt, passwords);
        return encryptor.encrypt(value);
    }
}
