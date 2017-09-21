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
package org.apache.fineract.infrastructure.core.domain;

public class MifosPlatformTenant {

    private final Long id;
    private final String tenantIdentifier;
    private final String name;
    private final String schemaName;
    private final String schemaServer;
    private final String schemaServerPort;
    private final String schemaUsername;
    private final String schemaPassword;
    private final String timezoneId;
    private final boolean autoUpdateEnabled;
    private final int initialSize;
    private final boolean testOnBorrow;
    private final long validationInterval;
    private final boolean removeAbandoned;
    private final int removeAbandonedTimeout;
    private final boolean logAbandoned;
    private final int abandonWhenPercentageFull;
    private final int maxActive;
    private final int minIdle;
    private final int maxIdle;
    private final int suspectTimeout;
    private final int timeBetweenEvictionRunsMillis;
    private final int minEvictableIdleTimeMillis;
    private final int maxRetriesOnDeadlock;
    private final int maxIntervalBetweenRetries;

    public MifosPlatformTenant(final Long id, final String tenantIdentifier, final String name, final String schemaName,
            final String schemaServer, final String schemaServerPort, final String schemaUsername, final String schemaPassword,
            final String timezoneId, final boolean autoUpdateEnabled, final int initialSize, final boolean testOnBorrow,
            final long validationInterval, final boolean removeAbandoned, final int removeAbandonedTimeout, final boolean logAbandoned,
            final int abandonWhenPercentageFull, final int maxActive, final int minIdle, final int maxIdle, final int suspectTimeout,
            final int timeBetweenEvictionRunsMillis, final int minEvictableIdleTimeMillis, final int maxRetriesOnDeadlock,
            final int maxIntervalBetweenRetries) {
        this.id = id;
        this.tenantIdentifier = tenantIdentifier;
        this.name = name;
        this.schemaName = schemaName;
        this.schemaServer = schemaServer;
        this.schemaServerPort = schemaServerPort;
        this.schemaUsername = schemaUsername;
        this.schemaPassword = schemaPassword;
        this.timezoneId = timezoneId;
        this.autoUpdateEnabled = autoUpdateEnabled;
        this.initialSize = initialSize;
        this.testOnBorrow = testOnBorrow;
        this.validationInterval = validationInterval;
        this.removeAbandoned = removeAbandoned;
        this.removeAbandonedTimeout = removeAbandonedTimeout;
        this.logAbandoned = logAbandoned;
        this.abandonWhenPercentageFull = abandonWhenPercentageFull;
        this.maxActive = maxActive;
        this.minIdle = minIdle;
        this.maxIdle = maxIdle;
        this.suspectTimeout = suspectTimeout;
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
        this.maxIntervalBetweenRetries = maxIntervalBetweenRetries;
        this.maxRetriesOnDeadlock = maxRetriesOnDeadlock;
    }

    public String databaseURL() {
        final String url = new StringBuilder("jdbc:mysql://").append(this.schemaServer).append(':').append(this.schemaServerPort)
                .append('/').append(this.schemaName).toString();
        return url;
    }

    public int getMaxActive() {
        return this.maxActive;
    }

    public int getMinIdle() {
        return this.minIdle;
    }

    public int getMaxIdle() {
        return this.maxIdle;
    }

    public int getTimeBetweenEvictionRunsMillis() {
        return this.timeBetweenEvictionRunsMillis;
    }

    public int getMinEvictableIdleTimeMillis() {
        return this.minEvictableIdleTimeMillis;
    }

    public int getInitialSize() {
        return initialSize;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public long getValidationInterval() {
        return validationInterval;
    }

    public boolean isRemoveAbandoned() {
        return removeAbandoned;
    }

    public int getRemoveAbandonedTimeout() {
        return removeAbandonedTimeout;
    }

    public boolean isLogAbandoned() {
        return logAbandoned;
    }

    public int getAbandonWhenPercentageFull() {
        return abandonWhenPercentageFull;
    }

    public Long getId() {
        return this.id;
    }

    public String getTenantIdentifier() {
        return this.tenantIdentifier;
    }

    public String getName() {
        return this.name;
    }

    public String getSchemaName() {
        return this.schemaName;
    }

    public String getSchemaUsername() {
        return this.schemaUsername;
    }

    public String getSchemaPassword() {
        return this.schemaPassword;
    }

    public String getTimezoneId() {
        return this.timezoneId;
    }

    public boolean isAutoUpdateEnabled() {
        return this.autoUpdateEnabled;
    }

    public int getSuspectTimeout() {
        return this.suspectTimeout;
    }

    public int getMaxRetriesOnDeadlock() {
        return maxRetriesOnDeadlock;
    }

    public int getMaxIntervalBetweenRetries() {
        return maxIntervalBetweenRetries;
    }

}