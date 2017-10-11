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
package org.apache.fineract.portfolio.client.data;

import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;

/**
 * Immutable data object represent client identity data.
 */
public class ClientIdentifierData {

    private final Long id;
    private final Long clientId;
    private final CodeValueData documentType;
    private final String documentKey;
    private final String description;
    private final String status;
    private final Collection<CodeValueData> allowedDocumentTypes;
    private final List<EnumOptionData> clientIdentifierStatusOptions;

    public static ClientIdentifierData singleItem(final Long id, final Long clientId, final CodeValueData documentType,
            final String documentKey, final String status, final String description) {
        final Collection<CodeValueData> allowedDocumentTypes = null;
        final List<EnumOptionData> clientIdentifierStatusOptions = null;
        return new ClientIdentifierData(id, clientId, documentType, documentKey, description, status, allowedDocumentTypes,
                clientIdentifierStatusOptions);
    }

    public static ClientIdentifierData template(final Collection<CodeValueData> codeValues,
            final List<EnumOptionData> clientIdentifierStatusOptions) {
        final Long id = null;
        final Long clientId = null;
        final CodeValueData documentType = null;
        final String documentKey = null;
        final String description = null;
        final String status = null;
        return new ClientIdentifierData(id, clientId, documentType, documentKey, description, status, codeValues,
                clientIdentifierStatusOptions);
    }

    public static ClientIdentifierData template(final ClientIdentifierData data, final Collection<CodeValueData> codeValues,
            final List<EnumOptionData> clientIdentifierStatusOptions) {
        return new ClientIdentifierData(data.id, data.clientId, data.documentType, data.documentKey, data.description, data.status,
                codeValues, clientIdentifierStatusOptions);
    }

    public ClientIdentifierData(final Long id, final Long clientId, final CodeValueData documentType, final String documentKey,
            final String description, final String status, final Collection<CodeValueData> allowedDocumentTypes,
            final List<EnumOptionData> clientIdentifierStatusOptions) {
        this.id = id;

        this.clientId = clientId;
        this.documentType = documentType;
        this.documentKey = documentKey;
        this.description = description;
        this.allowedDocumentTypes = allowedDocumentTypes;
        this.status = status;
        this.clientIdentifierStatusOptions = clientIdentifierStatusOptions;
    }

    public CodeValueData getDocumentType() {
        return this.documentType;
    }

    public String getDocumentKey() {
        return this.documentKey;
    }

    public Long getId() {
        return this.id;
    }

    public Long getClientId() {
        return this.clientId;
    }

    public String getDescription() {
        return this.description;
    }

    public String getStatus() {
        return this.status;
    }

    public Collection<CodeValueData> getAllowedDocumentTypes() {
        return this.allowedDocumentTypes;
    }

    public List<EnumOptionData> getClientIdentifierStatusOptions() {
        return this.clientIdentifierStatusOptions;
    }
}