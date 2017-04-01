package com.finflux.infrastructure.cryptography.domain;

import java.sql.Blob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_cryptography_key", uniqueConstraints = { @UniqueConstraint(columnNames = { "entity_type", "key_type" }, name = "UQ_f_cryptography_entity_type_key_type") })
public class CryptographyKey extends AbstractPersistable<Long> {

    @Column(name = "entity_type", length = 100, nullable = false)
    private String entityType;

    @Column(name = "key_type", length = 100, nullable = false)
    private String keyType;

    @Column(name = "key_value", nullable = false)
    private Blob keyValue;

    protected CryptographyKey() {}

    CryptographyKey(final String entityType, final String keyType, final Blob keyValue) {
        this.entityType = entityType;
        this.keyType = keyType;
        this.keyValue = keyValue;
    }

    public static CryptographyKey create(final String entityType, final String keyType, final Blob keyValue) {
        return new CryptographyKey(entityType, keyType, keyValue);
    }

    public Blob getKeyValue() {
        return this.keyValue;
    }
}
