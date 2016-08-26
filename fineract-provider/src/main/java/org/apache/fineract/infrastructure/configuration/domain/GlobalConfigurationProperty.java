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

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.configuration.data.GlobalConfigurationPropertyData;
import org.apache.fineract.infrastructure.configuration.exception.GlobalConfigurationPropertyCannotBeModfied;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.security.exception.ForcePasswordResetException;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "c_configuration")
public class GlobalConfigurationProperty extends AbstractPersistable<Long> {

    @Column(name = "name", nullable = false)
    private final String name;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "value", nullable = true)
    private String value;
    
    @Column(name = "date_value", nullable = true)
    private Date dateValue;

    @Column(name = "description", nullable = true)
    private final String description;

    @Column(name = "is_trap_door", nullable = false)
    private boolean isTrapDoor;

    protected GlobalConfigurationProperty() {
        this.name = null;
        this.enabled = false;
        this.value = null;
        this.dateValue = null;
        this.description = null;
        this.isTrapDoor = false;
    }

    public GlobalConfigurationProperty(final String name, final boolean enabled, final String value, final Date dateValue ,final String description,
            final boolean isTrapDoor) {
        this.name = name;
        this.enabled = enabled;
        this.value = value;
        this.dateValue = dateValue;
        this.description = description;
        this.isTrapDoor = isTrapDoor;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public String getValue() {
        return this.value;
    }
    
    public Date getDateValue(){
        return this.dateValue;
    }

    public String getName(){
    	return this.name;
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(7);

        if (this.isTrapDoor == true) { throw new GlobalConfigurationPropertyCannotBeModfied(this.getId()); }

        final String enabledParamName = "enabled";
        if (command.isChangeInBooleanParameterNamed(enabledParamName, this.enabled)) {
            final Boolean newValue = command.booleanPrimitiveValueOfParameterNamed(enabledParamName);
            actualChanges.put(enabledParamName, newValue);
            this.enabled = newValue;
        }

        final String valueParamName = "value";
        final String previousValue = this.value;
        if (command.isChangeInStringParameterNamed(valueParamName, this.value)) {
            final String newValue = command.stringValueOfParameterNamed(valueParamName);
            actualChanges.put(valueParamName, newValue);
            this.value = newValue;
        }
        
        final String dateValueParamName = "dateValue";
        if(command.isChangeInDateParameterNamed(dateValueParamName, this.dateValue)){
            final Date newDateValue = command.DateValueOfParameterNamed(dateValueParamName);
            actualChanges.put(dateValueParamName, newDateValue);
            this.dateValue = newDateValue;
        }

        final String passwordPropertyName = "force-password-reset-days";
        if (this.name.equalsIgnoreCase(passwordPropertyName)) {
            if (this.enabled == true && command.hasParameter(valueParamName) && loangValue(this.value) == 0 || this.enabled == true
                    && !command.hasParameter(valueParamName) && loangValue(previousValue) == 0) { throw new ForcePasswordResetException(); }
        }

        return actualChanges;

    }

    public static GlobalConfigurationProperty newSurveyConfiguration(final String name) {
        return new GlobalConfigurationProperty(name, false, null, null, null, false);
    }
    
    private Long loangValue(String value) {
        if (value == null) { return null; }
        return Long.parseLong(value);
    }
    
    public GlobalConfigurationPropertyData toData() {
        return new GlobalConfigurationPropertyData(getName(), isEnabled(), getValue(), getDateValue(), this.getId(), this.description,
                this.isTrapDoor);
    }

}