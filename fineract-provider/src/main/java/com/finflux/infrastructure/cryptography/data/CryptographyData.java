package com.finflux.infrastructure.cryptography.data;

import java.sql.Blob;
import java.sql.SQLException;

import org.apache.commons.codec.binary.Base64;

public class CryptographyData {

    private final Long id;
    private final String entityType;
    private final String keyType;
    private final String keyValue;
    private final Long userId;

    CryptographyData(final Long id, final String entityType, final String keyType, final String keyValue, final Long userId) {
        this.id = id;
        this.entityType = entityType;
        this.keyType = keyType;
        this.keyValue = keyValue;
        this.userId = userId;
    }

    public static CryptographyData instance(final Long id, final String entityType, String keyType, final Blob keyValue, final Long userId)
            throws SQLException {
        return new CryptographyData(id, entityType, keyType, Base64.encodeBase64String(keyValue.getBytes(1, (int) keyValue.length())),
                userId);
    }

    public Long getId() {
        return this.id;
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

    public Long getUserId() {
        return this.userId;
    }
}
