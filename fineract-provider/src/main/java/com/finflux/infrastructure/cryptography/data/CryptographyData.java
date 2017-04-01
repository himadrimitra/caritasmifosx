package com.finflux.infrastructure.cryptography.data;

import java.sql.Blob;
import java.sql.SQLException;

import org.apache.commons.codec.binary.Base64;

public class CryptographyData {

    private final String entityType;
    private final String keyType;
    private final String keyValue;

    CryptographyData(final String entityType, final String keyType, final String keyValue) {
        this.entityType = entityType;
        this.keyType = keyType;
        this.keyValue = keyValue;
    }

    public static CryptographyData instance(final String entityType, String keyType, final Blob keyValue) throws SQLException {
        return new CryptographyData(entityType, keyType, Base64.encodeBase64String(keyValue.getBytes(1, (int) keyValue.length())));
    }

    public String getEntityType() {
        return this.entityType;
    }

    public String getKeyType() {
        return this.keyType;
    }

    public String getKeyValue() {
        return this.keyValue;
    }
}
