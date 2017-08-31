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
package org.apache.fineract.infrastructure.documentmanagement.data;

import org.apache.fineract.infrastructure.documentmanagement.domain.StorageType;

/**
 * Immutable data object represent document being managed on platform.
 */
public class DocumentData {

    private final Long id;
    private final String parentEntityType;
    private final Long parentEntityId;
    private final String name;
    private final String fileName;
    private final Long size;
    private final String type;
    private final String description;
    private final String location;
    private final Integer storageType;
    private final Long reportIdentifier;
    private final Long tagIdentifier;
    private final String tagValue;

    public DocumentData(final Long id, final String parentEntityType, final Long parentEntityId, final String name, final String fileName,
            final Long size, final String type, final String description, final String location, final Integer storageType,
            final Long reportIdentifier, final Long tagIdentifier, final String tagValue) {
        this.id = id;
        this.parentEntityType = parentEntityType;
        this.parentEntityId = parentEntityId;
        this.name = name;
        this.fileName = fileName;
        this.size = size;
        this.type = type;
        this.description = description;
        this.location = location;
        this.storageType = storageType;
        this.reportIdentifier = reportIdentifier;
        this.tagIdentifier = tagIdentifier;
        this.tagValue = tagValue;
    }

    public DocumentData(final String location) {
        this.location = location;
        this.id = null;
        this.parentEntityType = null;
        this.parentEntityId = null;
        this.name = null;
        this.fileName = null;
        this.size = null;
        this.type = null;
        this.description = null;
        this.storageType = null;
        this.reportIdentifier = null;
        this.tagIdentifier = null;
        this.tagValue = null;
    }

    public String contentType() {
        return this.type;
    }

    public String fileName() {
        return this.fileName;
    }

    public String fileLocation() {
        return this.location;
    }

    public StorageType storageType() {
        return StorageType.fromInt(this.storageType);
    }

    public String getParentEntityType() {
        return this.parentEntityType;
    }

    public Long getParentEntityId() {
        return this.parentEntityId;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getFileName() {
        return this.fileName;
    }

    public Long getSize() {
        return this.size;
    }

    public String getType() {
        return this.type;
    }

    public String getDescription() {
        return this.description;
    }

    public String getLocation() {
        return this.location;
    }

    public Integer getStorageType() {
        return this.storageType;
    }

    public Long getReportIdentifier() {
        return this.reportIdentifier;
    }

    public Long getTagIdentifier() {
        return this.tagIdentifier;
    }

    public String getTagValue() {
        return this.tagValue;
    }

}