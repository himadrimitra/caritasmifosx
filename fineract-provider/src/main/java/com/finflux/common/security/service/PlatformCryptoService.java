package com.finflux.common.security.service;

/**
 * Created by dhirendra on 30/01/17.
 */
public interface PlatformCryptoService {

    String encrypt(String value);

    String decrypt(String value);

    String decrypt(String value, String salt, Object... passwords);

    String encrypt(String value, String salt, Object... passwords);
}
