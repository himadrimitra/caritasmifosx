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
package org.apache.fineract.infrastructure.codes.domain;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.codes.exception.SystemDefinedCodeCannotBeChangedException;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Cacheable
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "Code")
@Table(name = "m_code", uniqueConstraints = { @UniqueConstraint(columnNames = { "code_name" }, name = "code_name") })
public class Code extends AbstractPersistable<Long> {

    @Column(name = "code_name", length = 100)
    private String name;

    @Column(name = "is_system_defined")
    private final boolean systemDefined;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "code", orphanRemoval = true)
    private Set<CodeValue> values;
    
    @Column(name = "parent_id")
    private Long parentId;

    public static Code fromJson(final JsonCommand command) {
        final String name = command.stringValueOfParameterNamed("name");
        final Long parentId = command.longValueOfParameterNamed("parentId");
        return new Code(name, parentId);
    }
    
    public static Code createNew(final String name) {
		final Long parentId = null;
        return new Code(name, parentId);
    }

    protected Code() {
        this.systemDefined = false;
        this.parentId = null;
    }

    private Code(final String name, final Long parentId) {
        this.name = name;
        this.systemDefined = false;
        this.parentId = parentId;
    }

    public String name() {
        return this.name;
    }

    public boolean isSystemDefined() {
        return this.systemDefined;
    }

    public Map<String, Object> update(final JsonCommand command) {

        if (this.systemDefined) { throw new SystemDefinedCodeCannotBeChangedException(); }

        final Map<String, Object> actualChanges = new LinkedHashMap<>(1);

        final String firstnameParamName = "name";
        final String parentIdParamName = "parentId";
        if (command.isChangeInStringParameterNamed(firstnameParamName, this.name)) {
            final String newValue = command.stringValueOfParameterNamed(firstnameParamName);
            actualChanges.put(firstnameParamName, newValue);
            this.name = StringUtils.defaultIfEmpty(newValue, null);
        }
        if (command.isChangeInLongParameterNamed(parentIdParamName, this.parentId)) {
            final Long newValue = command.longValueOfParameterNamed(parentIdParamName);
            actualChanges.put(parentIdParamName, newValue);
            this.parentId = newValue;
        }

        return actualChanges;
    }

    public boolean remove(final CodeValue codeValueToDelete) {
        return this.values.remove(codeValueToDelete);
    }
}