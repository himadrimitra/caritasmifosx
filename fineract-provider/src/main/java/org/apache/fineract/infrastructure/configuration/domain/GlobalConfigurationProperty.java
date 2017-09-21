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
package org.apache.fineract.infrastructure.configuration.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "c_configuration")
public class GlobalConfigurationProperty extends AbstractPersistable<Long> {

    @Column(name = "name", nullable = false)
    private final String name;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "value", nullable = true)
    private Long value;
    
    @Column(name = "description", nullable = true)
    private final String description;

    protected GlobalConfigurationProperty() {
        this.name = null;
        this.enabled = false;
        this.value = null;
        this.description = null;
    }

    public GlobalConfigurationProperty(final String name, final boolean enabled, final Long value,
    		final String description) {
        this.name = name;
        this.enabled = enabled;
        this.value = value;
        this.description = description;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public Long getValue() {
        return this.value;
    }

    public boolean updateTo(final boolean value) {
        final boolean updated = this.enabled != value;
        this.enabled = value;
        return updated;
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(7);

        final String enabledParamName = "enabled";
        if (command.isChangeInBooleanParameterNamed(enabledParamName, this.enabled)) {
            final Boolean newValue = command.booleanPrimitiveValueOfParameterNamed(enabledParamName);
            actualChanges.put(enabledParamName, newValue);
            this.enabled = newValue;
        }

        final String valueParamName = "value";
        if (command.isChangeInLongParameterNamed(valueParamName, this.value)) {
            final Long newValue = command.longValueOfParameterNamed(valueParamName);
            actualChanges.put(valueParamName, newValue);
            this.value = newValue;
        }

        return actualChanges;

    }

    public static GlobalConfigurationProperty newSurveyConfiguration(final String name)
    {
        return new GlobalConfigurationProperty(name, false, null, null);
    }

}