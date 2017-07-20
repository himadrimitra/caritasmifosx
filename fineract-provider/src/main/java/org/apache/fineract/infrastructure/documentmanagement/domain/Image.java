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
package org.apache.fineract.infrastructure.documentmanagement.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.GeoTag;
import org.apache.fineract.infrastructure.documentmanagement.api.ImagesApiConstants;
import org.apache.fineract.portfolio.common.domain.EntityType;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_image")
public final class Image extends AbstractPersistable<Long> {

    @Column(name = "location", length = 500)
    private String location;

    @Column(name = "storage_type_enum")
    private Integer storageType;

    @Column(name="geo_tag")
    private String geoTag ;
    
    @Column(name="entity_type")
    private Integer entityType;
    
    @Column(name="entity_id")
    private Integer entityId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private AppUser createdBy;
    
    @Column(name="created_on")
    private Date createdOn;
    
    public Image(final String location, final StorageType storageType, final GeoTag geoTag,final Integer entityType,final Integer entityId, final AppUser createdBy,final Date createdOn) {
        this.location = location;
        this.storageType = storageType.getValue();
        if(geoTag != null) this.geoTag = geoTag.toString() ;
        this.entityType=entityType;
        this.entityId=entityId;
        this.createdBy=createdBy;
        this.createdOn=createdOn;
    }

    public Image(final String location, final StorageType storageType,JsonCommand command,final AppUser createdBy,final LocalDate createdOn){
        this.location = location;
        this.storageType = storageType.getValue();
        if(geoTag != null) this.geoTag = geoTag.toString() ;
        if (command != null) {
            this.entityType=EntityType.getEntityTypeByString(command.parsedJson().getAsJsonObject().get(ImagesApiConstants.entityNameParam).getAsString()).getValue();
            this.entityId = command.parsedJson().getAsJsonObject().get(ImagesApiConstants.entityIdParam).getAsInt();
        }
        this.createdBy=createdBy;
        this.createdOn=createdOn.toDate();
    }
    protected Image() {

    }

    public String getLocation() {
        return this.location;
    }

    public Integer getStorageType() {
        return this.storageType;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public void setStorageType(final Integer storageType) {
        this.storageType = storageType;
    }

    public void setGeoTag(final GeoTag geoTag) {
        this.geoTag = geoTag != null ? geoTag.toString() : null ;
    }

    public String getGeoTag() {
        return this.geoTag;
    }

    public Integer getEntityType() {
        return this.entityType;
    }

    public Integer getEntityId() {
        return this.entityId;
    }
}