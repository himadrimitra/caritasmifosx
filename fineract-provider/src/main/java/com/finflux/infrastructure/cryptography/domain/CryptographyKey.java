package com.finflux.infrastructure.cryptography.domain;

import java.sql.Blob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_cryptography_key", uniqueConstraints = { @UniqueConstraint(columnNames = { "entity_type", "key_type", "user_id" }, name = "UQ_f_cryptography_entity_type_key_type") })
public class CryptographyKey extends AbstractPersistable<Long> {

    @Column(name = "entity_type", nullable = false)
    private Integer entityType;

    @Column(name = "key_type", length = 100, nullable = false)
    private Integer keyType;

    @Column(name = "key_value", nullable = false)
    private Blob keyValue;

    @Column(name = "user_id", nullable = true)
    private Long userId;

    protected CryptographyKey() {}

}