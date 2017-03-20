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
    public String encrypt(String value) {
        TextEncryptor encryptor = getEncryptorForCurrentTenant();
        String encryptedString = encryptor.encrypt(value);
        return encryptedString;
    }

    private TextEncryptor getEncryptorForCurrentTenant() {
        FineractPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
        Long password1 = tenant.getId();
        String password2 = tenant.getTenantIdentifier();
        String saltText = tenant.getName();
        return getEncryptor(saltText, password1, password2);
    }

    private TextEncryptor getEncryptor(String saltText, Object... passwords) {
        String salt = new String(Hex.encodeHex(saltText.getBytes()));
        StringBuilder key = new StringBuilder();
        for (Object password : passwords) {
            key.append(password);
        }
        return Encryptors.text(key.toString(), salt);
    }

    @Override
    public String decrypt(String value) {
        TextEncryptor encryptor = getEncryptorForCurrentTenant();
        String decryptedString = encryptor.decrypt(value);
        return decryptedString;
    }

    @Override
    public String decrypt(String value, String salt, Object... passwords) {
        TextEncryptor encryptor = getEncryptor(salt, passwords);
        String decryptedString = encryptor.decrypt(value);
        return decryptedString;
    }

    @Override
    public String encrypt(String value, String salt, Object... passwords) {
        TextEncryptor encryptor = getEncryptor(salt, passwords);
        String encryptedString = encryptor.encrypt(value);
        return encryptedString;
    }
}
