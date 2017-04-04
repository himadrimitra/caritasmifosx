package com.finflux.infrastructure.cryptography.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CryptographyKeyRepository extends JpaRepository<CryptographyKey, Long>, JpaSpecificationExecutor<CryptographyKey> {

    CryptographyKey findByEntityTypeAndKeyType(final String entityType, final String keyType);

    List<CryptographyKey> findByEntityType(final String entityType);
}