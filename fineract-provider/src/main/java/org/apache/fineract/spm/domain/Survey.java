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
package org.apache.fineract.spm.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_surveys")
public class Survey extends AbstractPersistable<Long> {

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "survey", orphanRemoval = true)
    @OrderBy("sequenceNo")
    private List<Component> components;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "survey", orphanRemoval = true)
    @OrderBy("sequenceNo")
    private List<Question> questions;

    @Column(name = "entity_type", length = 3)
    private Integer entityType;

    @Column(name = "a_key", length = 32)
    private String key;

    @Column(name = "a_name", length = 255)
    private String name;

    @Column(name = "description", length = 4096)
    private String description;

    @Column(name = "country_code", length = 2)
    private String countryCode;

    @Column(name = "valid_from")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date validFrom;

    @Column(name = "valid_to")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date validTo;

    @Column(name = "is_active")
    private boolean isActive = true;

    public Survey() {
        super();
    }

    public List<Component> getComponents() {
        return components;
    }

    @SuppressWarnings("unused")
    public void setComponents(List<Component> components) {
        if (this.components == null) {
            this.components = new ArrayList<Component>();
        }
        this.components.clear();
        this.components.addAll(components);
    }

    public List<Question> getQuestions() {
        return questions;
    }

    @SuppressWarnings("unused")
    public void setQuestions(List<Question> questions) {
        if (this.questions == null) {
            this.questions = new ArrayList<Question>();
        }
        this.questions.clear();
        this.questions.addAll(questions);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public Integer getEntityType() {
        return this.entityType;
    }

    public void setEntityType(final Integer entityType) {
        this.entityType = entityType;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void setActive(final boolean isActive) {
        this.isActive = isActive;
    }
}
