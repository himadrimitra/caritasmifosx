package com.finflux.common.security.service;

/**
 * Created by dhirendra on 30/01/17.
 */
public interface PlatformCryptoService {

    String encrypt(final String value);

    String decrypt(final String value);

    String decrypt(final String value, final String salt, final Object... passwords);

    String encrypt(final String value, final String salt, final Object... passwords);
}
