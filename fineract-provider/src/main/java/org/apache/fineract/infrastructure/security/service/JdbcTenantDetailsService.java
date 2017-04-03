/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.infrastructure.security.service;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;
import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenantConnection;
import org.apache.fineract.infrastructure.security.exception.InvalidTenantIdentiferException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.common.security.service.PlatformCryptoService;
import com.finflux.infrastructure.cryptography.api.CryptographyApiConstants;

/**
 * A JDBC implementation of {@link TenantDetailsService} for loading a tenants
 * details by a <code>tenantIdentifier</code>.
 */
@Service
public class JdbcTenantDetailsService implements TenantDetailsService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformCryptoService platformCryptoService;

    @Autowired
    public JdbcTenantDetailsService(@Qualifier("tenantDataSourceJndi") final DataSource dataSource,
            final PlatformCryptoService platformCryptoService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.platformCryptoService = platformCryptoService;
    }

    private final class TenantMapper implements RowMapper<FineractPlatformTenant> {
        
        private final StringBuilder sqlBuilder = new StringBuilder("t.id, ts.id as connectionId , ")//
                .append(" t.timezone_id as timezoneId , t.name,t.identifier, ts.schema_name as schemaName, AES_DECRYPT(ts.schema_server,'" + CryptographyApiConstants.dBConnectionEncDecKey + "') as schemaServer,")//
                .append(" ts.schema_server_port as schemaServerPort, ts.auto_update as autoUpdate,")//
                .append(" AES_DECRYPT(ts.schema_username,'" + CryptographyApiConstants.dBConnectionEncDecKey + "') as schemaUsername, AES_DECRYPT(ts.schema_password,'" + CryptographyApiConstants.dBConnectionEncDecKey + "') as schemaPassword , ts.pool_initial_size as initialSize,")//
                .append(" ts.pool_validation_interval as validationInterval, ts.pool_remove_abandoned as removeAbandoned, ts.pool_remove_abandoned_timeout as removeAbandonedTimeout,")//
                .append(" ts.pool_log_abandoned as logAbandoned, ts.pool_abandon_when_percentage_full as abandonedWhenPercentageFull, ts.pool_test_on_borrow as testOnBorrow,")//
                .append(" ts.pool_max_active as poolMaxActive, ts.pool_min_idle as poolMinIdle, ts.pool_max_idle as poolMaxIdle,")//
                .append(" ts.pool_suspect_timeout as poolSuspectTimeout, ts.pool_time_between_eviction_runs_millis as poolTimeBetweenEvictionRunsMillis,")//
                .append(" ts.pool_min_evictable_idle_time_millis as poolMinEvictableIdleTimeMillis,")//
                .append(" ts.deadlock_max_retries as maxRetriesOnDeadlock,")//
                .append(" ts.deadlock_max_retry_interval as maxIntervalBetweenRetries, ")//
                .append(" AES_DECRYPT(t.tenant_key,'" + CryptographyApiConstants.dBConnectionEncDecKey + "') as tenantKey,")
                .append(" ts.server_connection_details_for_encryption as serverConnectionDetails ")
                .append(" from tenants t left join tenant_server_connections ts on t.oltp_Id=ts.id ");

        public String schema() {
            return this.sqlBuilder.toString();
        }

        @Override
        public FineractPlatformTenant mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long id = rs.getLong("id");
            final String tenantIdentifier = rs.getString("identifier");
            final String name = rs.getString("name");
            final String timezoneId = rs.getString("timezoneId");
            FineractPlatformTenantConnection connection = null;
            final String encriptedTenantKey = convertBlobToString(rs.getBlob("tenantKey"));
            final String tenantKey = platformCryptoService.decrypt(encriptedTenantKey,name,id,tenantIdentifier);
            final FineractPlatformTenant partialTenantInfo = new FineractPlatformTenant(id, tenantIdentifier, name, timezoneId, connection, tenantKey);
            connection = getDBConnection(rs,partialTenantInfo);
            return new FineractPlatformTenant(id, tenantIdentifier, name, timezoneId, connection, tenantKey);
        }

        // gets the DB connection
        private FineractPlatformTenantConnection getDBConnection(ResultSet rs, final FineractPlatformTenant tenant) throws SQLException {

            final Long connectionId = rs.getLong("connectionId");
            final String schemaName = rs.getString("schemaName");
            final String encriptedSchemaServer = convertBlobToString(rs.getBlob("schemaServer"));
            final String schemaServer = platformCryptoService.decrypt(encriptedSchemaServer, tenant.getName(), tenant.getId(),
                    tenant.getTenantKey(), tenant.getTenantIdentifier());
            final String schemaServerPort = rs.getString("schemaServerPort");
            final String encriptedSchemaUsername = convertBlobToString(rs.getBlob("schemaUsername"));
            final String schemaUsername = platformCryptoService.decrypt(encriptedSchemaUsername, tenant.getName(), tenant.getId(),
                    tenant.getTenantKey(), tenant.getTenantIdentifier());
            final String encriptedSchemaPassword = convertBlobToString(rs.getBlob("schemaPassword"));
            final String schemaPassword = platformCryptoService.decrypt(encriptedSchemaPassword, tenant.getName(), tenant.getId(),
                    tenant.getTenantKey(), tenant.getTenantIdentifier());
            final boolean autoUpdateEnabled = rs.getBoolean("autoUpdate");
            final int initialSize = rs.getInt("initialSize");
            final boolean testOnBorrow = rs.getBoolean("testOnBorrow");
            final long validationInterval = rs.getLong("validationInterval");
            final boolean removeAbandoned = rs.getBoolean("removeAbandoned");
            final int removeAbandonedTimeout = rs.getInt("removeAbandonedTimeout");
            final boolean logAbandoned = rs.getBoolean("logAbandoned");
            final int abandonWhenPercentageFull = rs.getInt("abandonedWhenPercentageFull");
            final int maxActive = rs.getInt("poolMaxActive");
            final int minIdle = rs.getInt("poolMinIdle");
            final int maxIdle = rs.getInt("poolMaxIdle");
            final int suspectTimeout = rs.getInt("poolSuspectTimeout");
            final int timeBetweenEvictionRunsMillis = rs.getInt("poolTimeBetweenEvictionRunsMillis");
            final int minEvictableIdleTimeMillis = rs.getInt("poolMinEvictableIdleTimeMillis");
            int maxRetriesOnDeadlock = rs.getInt("maxRetriesOnDeadlock");
            int maxIntervalBetweenRetries = rs.getInt("maxIntervalBetweenRetries");
            final String serverConnectionDetails = rs.getString("serverConnectionDetails");
            maxRetriesOnDeadlock = bindValueInMinMaxRange(maxRetriesOnDeadlock, 0, 15);
            maxIntervalBetweenRetries = bindValueInMinMaxRange(maxIntervalBetweenRetries, 1, 15);

            return new FineractPlatformTenantConnection(connectionId, schemaName, schemaServer, schemaServerPort, schemaUsername,
                    schemaPassword, autoUpdateEnabled, initialSize, validationInterval, removeAbandoned, removeAbandonedTimeout,
                    logAbandoned, abandonWhenPercentageFull, maxActive, minIdle, maxIdle, suspectTimeout, timeBetweenEvictionRunsMillis,
                    minEvictableIdleTimeMillis, maxRetriesOnDeadlock, maxIntervalBetweenRetries, testOnBorrow, serverConnectionDetails);
            
        }

        private String convertBlobToString(final Blob blob) {
            try {
                final byte[] bdata = blob.getBytes(1, (int) blob.length());
                return new String(bdata);
                // return Base64.encodeBase64String(blob.getBytes(1, (int)
                // blob.length()));
            } catch (SQLException | NullPointerException e) {
                return null;
            }
        }

        private int bindValueInMinMaxRange(final int value, int min, int max) {
            if (value < min) {
                return min;
            } else if (value > max) { return max; }
            return value;
        }
    }

    @Override
    @Cacheable(value = "tenantsById")
    public FineractPlatformTenant loadTenantById(final String tenantIdentifier) {

        try {
            final TenantMapper rm = new TenantMapper();
            final String sql = "select  " + rm.schema() + " where t.identifier like ?";

            return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { tenantIdentifier });
        } catch (final EmptyResultDataAccessException e) {
            throw new InvalidTenantIdentiferException("The tenant identifier: " + tenantIdentifier + " is not valid.");
        }
    }

    @Override
    public List<FineractPlatformTenant> findAllTenants() {
        final TenantMapper rm = new TenantMapper();
        final String sql = "select  " + rm.schema();

        final List<FineractPlatformTenant> fineractPlatformTenants = this.jdbcTemplate.query(sql, rm, new Object[] {});
        return fineractPlatformTenants;
    }

    @Override
    public void encrypteAllTenantsServerCredentials() {
        final NonEncryptedTenantMapper rm = new NonEncryptedTenantMapper();
        final String sql = "select  " + rm.schema();
        final List<FineractPlatformTenant> fineractPlatformTenants = this.jdbcTemplate.query(sql, rm, new Object[] {});
        if (fineractPlatformTenants != null && !fineractPlatformTenants.isEmpty()) {
            for (final FineractPlatformTenant fineractPlatformTenant : fineractPlatformTenants) {
                if (fineractPlatformTenant.getTenantKey() == null || fineractPlatformTenant.getTenantKey().trim().length() == 0) {
                    generateAndStoreEncryptedTenantKey(fineractPlatformTenant);
                }
                if (fineractPlatformTenant.getConnection() != null
                        && fineractPlatformTenant.getConnection().getServerConnectionDetails() != null) {
                    generateAndStoreEncryptedServerConnectionDetails(fineractPlatformTenant);
                }
            }
        }
    }

    private void generateAndStoreEncryptedServerConnectionDetails(final FineractPlatformTenant fineractPlatformTenant) {
        final String dbSecreteKey = CryptographyApiConstants.dBConnectionEncDecKey;
        final StringBuilder sb = new StringBuilder(20);
        sb.append(fineractPlatformTenant.getName());
        final Object[] passwords = { fineractPlatformTenant.getId().toString(),
                sb.reverse().toString().trim().toLowerCase().replaceAll("\\s+", ""), fineractPlatformTenant.getTenantIdentifier() };
        final String salt = fineractPlatformTenant.getName();
        final String[] splitServerConnectionDetails = fineractPlatformTenant.getConnection().getServerConnectionDetails().split("\\|");
        final String encryptedSchemaServer = this.platformCryptoService
                .encrypt(splitServerConnectionDetails[0].toString(), salt, passwords);
        final String encryptedSchemaUsername = this.platformCryptoService.encrypt(splitServerConnectionDetails[1].toString(), salt,
                passwords);
        final String encryptedSchemaPassword = this.platformCryptoService.encrypt(splitServerConnectionDetails[2].toString(), salt,
                passwords);
        final StringBuilder updateSql = new StringBuilder(20);
        updateSql.append("UPDATE tenant_server_connections ts ");
        updateSql.append("SET ts.schema_server = AES_ENCRYPT(?,?), ");
        updateSql.append("ts.schema_username = AES_ENCRYPT(?,?),");
        updateSql.append("ts.schema_password = AES_ENCRYPT(?,?),");
        updateSql.append("ts.server_connection_details_for_encryption = null,ts.is_server_connection_details_encrypted = 1 ");
        updateSql.append("WHERE ts.id = ? ");
        this.jdbcTemplate.update(updateSql.toString(), new Object[] { encryptedSchemaServer, dbSecreteKey, encryptedSchemaUsername,
                dbSecreteKey, encryptedSchemaPassword, dbSecreteKey, fineractPlatformTenant.getConnection().getConnectionId() });
    }

    private String generateAndStoreEncryptedTenantKey(final FineractPlatformTenant fineractPlatformTenant) {
        final String dbSecreteKey = CryptographyApiConstants.dBConnectionEncDecKey;
        final StringBuilder sb = new StringBuilder(20);
        sb.append(fineractPlatformTenant.getName());
        final String value = sb.reverse().toString().trim().toLowerCase().replaceAll("\\s+", "");
        final Object[] passwords = { fineractPlatformTenant.getId().toString(), fineractPlatformTenant.getTenantIdentifier() };
        final String salt = fineractPlatformTenant.getName();
        final String encryptedTenantKey = this.platformCryptoService.encrypt(value, salt, passwords);
        final String updateEncryptedTenantKeySql = "UPDATE tenants t SET t.tenant_key = AES_ENCRYPT(?,?) WHERE t.id = ?";
        this.jdbcTemplate.update(updateEncryptedTenantKeySql,
                new Object[] { encryptedTenantKey, dbSecreteKey, fineractPlatformTenant.getId() });
        return encryptedTenantKey;
    }

    private final class NonEncryptedTenantMapper implements RowMapper<FineractPlatformTenant> {

        private final String schema;

        public NonEncryptedTenantMapper() {
            final StringBuilder sqlBuilder = new StringBuilder(200);
            sqlBuilder.append("t.id, ts.id as connectionId , ");
            sqlBuilder
                    .append(" t.timezone_id as timezoneId , t.name,t.identifier, ts.schema_name as schemaName, ts.schema_server as schemaServer,");
            sqlBuilder.append(" ts.schema_server_port as schemaServerPort, ts.auto_update as autoUpdate,");
            sqlBuilder
                    .append(" ts.schema_username as schemaUsername, ts.schema_password as schemaPassword , ts.pool_initial_size as initialSize,");
            sqlBuilder
                    .append(" ts.pool_validation_interval as validationInterval, ts.pool_remove_abandoned as removeAbandoned, ts.pool_remove_abandoned_timeout as removeAbandonedTimeout,");
            sqlBuilder
                    .append(" ts.pool_log_abandoned as logAbandoned, ts.pool_abandon_when_percentage_full as abandonedWhenPercentageFull, ts.pool_test_on_borrow as testOnBorrow,");
            sqlBuilder.append(" ts.pool_max_active as poolMaxActive, ts.pool_min_idle as poolMinIdle, ts.pool_max_idle as poolMaxIdle,");
            sqlBuilder
                    .append(" ts.pool_suspect_timeout as poolSuspectTimeout, ts.pool_time_between_eviction_runs_millis as poolTimeBetweenEvictionRunsMillis,");

            sqlBuilder.append(" ts.pool_min_evictable_idle_time_millis as poolMinEvictableIdleTimeMillis,");
            sqlBuilder.append(" ts.deadlock_max_retries as maxRetriesOnDeadlock,");
            sqlBuilder.append(" ts.deadlock_max_retry_interval as maxIntervalBetweenRetries, ");
            sqlBuilder.append(" AES_DECRYPT(t.tenant_key,'" + CryptographyApiConstants.dBConnectionEncDecKey + "') as tenantKey,");
            sqlBuilder.append(" ts.server_connection_details_for_encryption as serverConnectionDetails ");
            sqlBuilder.append(" from tenants t ");
            sqlBuilder.append(" join tenant_server_connections ts on ts.is_server_connection_details_encrypted = 0 ");
            sqlBuilder.append(" and (t.oltp_Id=ts.id or t.report_id=ts.id) ");

            schema = sqlBuilder.toString();
        }

        public String schema() {
            return schema;
        }

        @Override
        public FineractPlatformTenant mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long id = rs.getLong("id");
            final String tenantIdentifier = rs.getString("identifier");
            final String name = rs.getString("name");
            final String timezoneId = rs.getString("timezoneId");
            FineractPlatformTenantConnection connection = null;
            final String tenantKey = convertBlobToString(rs.getBlob("tenantKey"));
            connection = getDBConnection(rs);
            return new FineractPlatformTenant(id, tenantIdentifier, name, timezoneId, connection, tenantKey);
        }

        private String convertBlobToString(final Blob blob) {
            try {
                final byte[] bdata = blob.getBytes(1, (int) blob.length());
                return new String(bdata);
                // return Base64.encodeBase64String(blob.getBytes(1, (int)
                // blob.length()));
            } catch (SQLException | NullPointerException e) {
                return null;
            }
        }

        // gets the DB connection
        private FineractPlatformTenantConnection getDBConnection(ResultSet rs) throws SQLException {
            final Long connectionId = rs.getLong("connectionId");
            final String schemaName = rs.getString("schemaName");
            final String schemaServer = null;
            final String schemaServerPort = rs.getString("schemaServerPort");
            final String schemaUsername = null;
            final String schemaPassword = null;
            final boolean autoUpdateEnabled = rs.getBoolean("autoUpdate");
            final int initialSize = rs.getInt("initialSize");
            final boolean testOnBorrow = rs.getBoolean("testOnBorrow");
            final long validationInterval = rs.getLong("validationInterval");
            final boolean removeAbandoned = rs.getBoolean("removeAbandoned");
            final int removeAbandonedTimeout = rs.getInt("removeAbandonedTimeout");
            final boolean logAbandoned = rs.getBoolean("logAbandoned");
            final int abandonWhenPercentageFull = rs.getInt("abandonedWhenPercentageFull");
            final int maxActive = rs.getInt("poolMaxActive");
            final int minIdle = rs.getInt("poolMinIdle");
            final int maxIdle = rs.getInt("poolMaxIdle");
            final int suspectTimeout = rs.getInt("poolSuspectTimeout");
            final int timeBetweenEvictionRunsMillis = rs.getInt("poolTimeBetweenEvictionRunsMillis");
            final int minEvictableIdleTimeMillis = rs.getInt("poolMinEvictableIdleTimeMillis");
            int maxRetriesOnDeadlock = rs.getInt("maxRetriesOnDeadlock");
            int maxIntervalBetweenRetries = rs.getInt("maxIntervalBetweenRetries");
            final String serverConnectionDetails = rs.getString("serverConnectionDetails");
            return new FineractPlatformTenantConnection(connectionId, schemaName, schemaServer, schemaServerPort, schemaUsername,
                    schemaPassword, autoUpdateEnabled, initialSize, validationInterval, removeAbandoned, removeAbandonedTimeout,
                    logAbandoned, abandonWhenPercentageFull, maxActive, minIdle, maxIdle, suspectTimeout, timeBetweenEvictionRunsMillis,
                    minEvictableIdleTimeMillis, maxRetriesOnDeadlock, maxIntervalBetweenRetries, testOnBorrow, serverConnectionDetails);

        }
    }
}