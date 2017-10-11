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
package org.apache.fineract.spm.data;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public class SurveyData {

    private Long id;
    private List<ComponentData> componentDatas;
    private List<QuestionData> questionDatas;
    private Integer entityTypeId;
    private String entityTypeCode;
    private String entityTypeValue;
    private String key;
    private String name;
    private String description;
    private String countryCode;
    private Date validFrom;
    private Date validTo;
    private boolean isActive;
    private Boolean isCoOfficerRequired;

    // Template Data
    private Collection<EnumOptionData> surveyEntityTypes;

    public SurveyData() {
        super();
    }

    public SurveyData(final Long id, final List<ComponentData> componentDatas, final List<QuestionData> questionDatas,
            final EnumOptionData entityType, final String key, final String name, final String description, final String countryCode,
            final Date validFrom, final Date validTo, final boolean isActive, final Boolean isCoOfficerRequired) {
        super();
        this.id = id;
        this.componentDatas = componentDatas;
        this.questionDatas = questionDatas;
        if (entityType != null) {
            this.entityTypeId = entityType.getId().intValue();
            this.entityTypeCode = entityType.getCode();
            this.entityTypeValue = entityType.getValue();
        }

        this.key = key;
        this.name = name;
        this.description = description;
        this.countryCode = countryCode;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.isActive = isActive;
        this.isCoOfficerRequired = isCoOfficerRequired;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ComponentData> getComponentDatas() {
        return this.componentDatas;
    }

    public void setComponentDatas(List<ComponentData> componentDatas) {
        this.componentDatas = componentDatas;
    }

    public List<QuestionData> getQuestionDatas() {
        return this.questionDatas;
    }

    public void setQuestionDatas(List<QuestionData> questionDatas) {
        this.questionDatas = questionDatas;
    }

    public Integer getEntityTypeId() {
        return this.entityTypeId;
    }

    public void setEntityTypeId(Integer entityTypeId) {
        this.entityTypeId = entityTypeId;
    }

    public String getEntityTypeCode() {
        return this.entityTypeCode;
    }

    public void setEntityTypeCode(String entityTypeCode) {
        this.entityTypeCode = entityTypeCode;
    }

    public String getEntityTypeValue() {
        return this.entityTypeValue;
    }

    public void setEntityTypeValue(String entityTypeValue) {
        this.entityTypeValue = entityTypeValue;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Date getValidFrom() {
        return this.validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return this.validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Collection<EnumOptionData> getSurveyEntityTypes() {
        return this.surveyEntityTypes;
    }

    public void setSurveyEntityTypes(Collection<EnumOptionData> surveyEntityTypes) {
        this.surveyEntityTypes = surveyEntityTypes;
    }

    public Boolean isCoOfficerRequired() {
        return this.isCoOfficerRequired;
    }

    public void setCoOfficerRequired(final Boolean isCoOfficerRequired) {
        this.isCoOfficerRequired = isCoOfficerRequired;
    }
}
