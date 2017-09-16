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
package org.apache.fineract.infrastructure.documentmanagement.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepository;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepositoryFactory;
import org.apache.fineract.infrastructure.documentmanagement.data.DocumentData;
import org.apache.fineract.infrastructure.documentmanagement.data.DocumentTagData;
import org.apache.fineract.infrastructure.documentmanagement.data.FileData;
import org.apache.fineract.infrastructure.documentmanagement.exception.DocumentNotFoundException;
import org.apache.fineract.infrastructure.documentmanagement.service.DocumentWritePlatformServiceJpaRepositoryImpl.DOCUMENT_MANAGEMENT_ENTITY;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class DocumentReadPlatformServiceImpl implements DocumentReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final ContentRepositoryFactory contentRepositoryFactory;
    
    @Autowired
    public DocumentReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final ContentRepositoryFactory documentStoreFactory) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.contentRepositoryFactory = documentStoreFactory;
    }

    @Override
    public Collection<DocumentData>  retrieveAllDocuments(final String entityType, final Long entityId) {
        this.context.authenticatedUser();
        final String select = "select " ;
        // TODO verify if the entities are valid and a user
        // has data
        // scope for the particular entities
        final List<DocumentData> documents = new ArrayList<>() ;
        final DocumentMapper mapper = new DocumentMapper(true, true);
        final String sql = select + mapper.schema() + " order by d.id";
        List<DocumentData> existingDocuments = this.jdbcTemplate.query(sql, mapper, new Object[] { entityType, entityId });
        if(existingDocuments != null && existingDocuments.size() > 0) {
            documents.addAll(existingDocuments) ;
        }
        final Long productId = getProductIdBasedOnEntityTypeAndId(entityType, entityId);
        final VirtualDocumentMapper virtualDocumentsMapper = new VirtualDocumentMapper(entityType, entityId) ;
        String virtualDocsSql = select + virtualDocumentsMapper.schema() + " and tags.entity_type=?";
        Object[] params = new Object[] { entityType, entityId, entityType };
        if (productId != null) {
            virtualDocsSql = virtualDocsSql + " and (tags.product_id = ? OR tags.product_id IS NULL) ";
            params = new Object[] { entityType, entityId, entityType, productId };
        }
        List<DocumentData> virtualDocuments = this.jdbcTemplate.query(virtualDocsSql, virtualDocumentsMapper, params);
        if(virtualDocuments != null && virtualDocuments.size() > 0) {
            documents.addAll(virtualDocuments) ;
        }
        return documents ;
    }

    private Long getProductIdBasedOnEntityTypeAndId(String entityType, Long entityId) {
        String sql = null;
        if (DOCUMENT_MANAGEMENT_ENTITY.LOANAPPLICATION.toString().equalsIgnoreCase(entityType)) {
            sql = "SELECT loan_product_id FROM f_loan_application_reference WHERE id = ?";
        } else if (DOCUMENT_MANAGEMENT_ENTITY.LOANS.toString().equalsIgnoreCase(entityType)) {
            sql = "SELECT product_id FROM m_loan WHERE id = ?";
        } else if (DOCUMENT_MANAGEMENT_ENTITY.SAVINGS.toString().equalsIgnoreCase(entityType)) {
            sql = "SELECT product_id FROM m_savings_account WHERE id = ?";
        }
        if (StringUtils.isBlank(sql)) { return null; }
        return this.jdbcTemplate.queryForObject(sql, Long.class, new Object[] { entityId });
    }

    @Override
    public FileData retrieveFileData(final String entityType, final Long entityId, final Long documentId) {
        try {
            final DocumentMapper mapper = new DocumentMapper(false, false);
            final DocumentData documentData = fetchDocumentDetails(entityType, entityId, documentId, mapper);
            final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository(documentData.storageType());
            return contentRepository.fetchFile(documentData);
        } catch (final EmptyResultDataAccessException e) {
            throw new DocumentNotFoundException(entityType, entityId, documentId);
        }
    }

    @Override
    public DocumentData retrieveDocument(final String entityType, final Long entityId, final Long documentId) {
        try {
            final DocumentMapper mapper = new DocumentMapper(true, true);
            return fetchDocumentDetails(entityType, entityId, documentId, mapper);
        } catch (final EmptyResultDataAccessException e) {
            throw new DocumentNotFoundException(entityType, entityId, documentId);
        }
    }

    /**
     * @param entityType
     * @param entityId
     * @param documentId
     * @param mapper
     * @return
     */
    private DocumentData fetchDocumentDetails(final String entityType, final Long entityId, final Long documentId,
            final DocumentMapper mapper) {
        final String sql = "select " + mapper.schema() + " and d.id=? ";
        return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { entityType, entityId, documentId });
    }

    private static final class DocumentMapper implements RowMapper<DocumentData> {

        private final boolean hideLocation;
        private final boolean hideStorageType;
        private final StringBuilder builder ;
        
        public DocumentMapper(final boolean hideLocation, final boolean hideStorageType) {
            this.hideLocation = hideLocation;
            this.hideStorageType = hideStorageType;
            builder = new StringBuilder() ;
            builder.append("d.id as id, d.parent_entity_type as parentEntityType, d.parent_entity_id as parentEntityId, d.name as name, ") ;
            builder.append(" d.file_name as fileName, d.size as fileSize, d.type as fileType, ") ;
            builder.append("d.description as description, d.location as location, d.storage_type_enum as storageType, ") ;
            builder.append(" mapping.id as reportIdentifier, mapping.display_name as documentDisplayName, code.code_value as tagName, ") ;
            builder.append(" d.tag_id as tagIdentifier ") ;
            builder.append(" FROM m_document d ") ;
            builder.append(" LEFT JOIN f_entity_tag_report_mapping mapping ON d.report_mapping_id = mapping.id ") ;
            builder.append(" LEFT JOIN m_code_value code ON d.tag_id=code.id ") ;
            builder.append(" where d.parent_entity_type=? and d.parent_entity_id=? ") ;
        }

        public String schema() {
            return builder.toString() ;
        }

        @Override
        public DocumentData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final Long parentEntityId = JdbcSupport.getLong(rs, "parentEntityId");
            final Long fileSize = JdbcSupport.getLong(rs, "fileSize");
            final String parentEntityType = rs.getString("parentEntityType");
            final String name = rs.getString("name");
            final String fileName = rs.getString("fileName");
            final String fileType = rs.getString("fileType");
            final String description = rs.getString("description");
            final Long reportIdentifier = rs.getLong("reportIdentifier") ;
            final Long tagIdentifier = rs.getLong("tagIdentifier") ;
            final String tagName = rs.getString("tagName") ;
            String location = null;
            Integer storageType = null;
            if (!this.hideLocation) {
                location = rs.getString("location");
            }
            if (!this.hideStorageType) {
                storageType = rs.getInt("storageType");
            }
            return new DocumentData(id, parentEntityType, parentEntityId, name, fileName, fileSize, fileType, description, location,
                    storageType, reportIdentifier, tagIdentifier, tagName);
        }
    }

    private static final class VirtualDocumentMapper implements RowMapper<DocumentData> {

        private final StringBuilder builder ;
        private final String entityType ;
        private final Long entityId ;
        
        public VirtualDocumentMapper(final String entityType, final Long entityId) {
            this.entityType = entityType ;
            this.entityId = entityId ;
            builder = new StringBuilder() ;
            builder.append("mappings.id as mappingId, mappings.report_id reportId, mappings.entity_tag_id as tagIdentifier, ") ;
            builder.append(" codeValue.code_value as tagvalue, mappings.output_type as outputType, mappings.display_name as fileName, ") ;
            builder.append(" mappings.description as description ") ;
            builder.append(" from f_entity_tag_report_mapping mappings ") ;
            builder.append(" LEFT JOIN f_entity_tags tags ON tags.id = mappings.entity_tag_id ") ;
            builder.append(" LEFT JOIN m_code_value codeValue ON tags.tag_id = codeValue.id ") ;
            builder.append(" where mappings.id not in ") ;
            builder.append(" (select doc.report_mapping_id from m_document doc where doc.parent_entity_type=? and doc.parent_entity_id=? and doc.report_mapping_id is not null) ") ;
        }

        public String schema() {
            return builder.toString();
        }

        @Override
        public DocumentData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long mappingId = JdbcSupport.getLong(rs, "mappingId");
            final Long reportId = JdbcSupport.getLong(rs, "reportId") ;
            final Long tagIdentifier = JdbcSupport.getLong(rs, "tagIdentifier") ;
            final String tagvalue = rs.getString("tagvalue") ;
            final String fileName = rs.getString("fileName");
            final String description = rs.getString("description") ;
            final String fileType = rs.getString("outputType");
            String location = null;
            Integer storageType = null;
            final Long fileSize = null ;
            final Long id = null ;
            final String name = fileName ;
            return new DocumentData(id, entityType, entityId, name, fileName, fileSize, fileType, description, location,
                    storageType, mappingId, tagIdentifier, tagvalue);
        }
    }
    
    @Override
    public DocumentData retrieveDocument(String entityType, Long entityId, Long documentId, boolean isHideLocation,
            boolean idHideStorageType) {
        try {
            final DocumentMapper mapper = new DocumentMapper(isHideLocation, idHideStorageType);
            return fetchDocumentDetails(entityType, entityId, documentId, mapper);
        } catch (final EmptyResultDataAccessException e) {
            throw new DocumentNotFoundException(entityType, entityId, documentId);
        }
    }

    @Override
    public DocumentTagData retrieveDocumentTagData(Long reportMappingId) {
        DocumentTagDataMapper mapper = new DocumentTagDataMapper() ;
        String query = "select " + mapper.query() + " where mapping.id=?" ;
        return this.jdbcTemplate.queryForObject(query, mapper, new Object[] {reportMappingId});
    }
    
    class DocumentTagDataMapper implements RowMapper<DocumentTagData> {

        private final StringBuilder queryBuilder ;
        public DocumentTagDataMapper() {
            this.queryBuilder = new StringBuilder() ;
            this.queryBuilder.append("report.report_name as repoprtName, report.report_type as reportType, report.report_category as reportCategory, ") ;
            this.queryBuilder.append("tags.tag_id as tagId, mapping.output_type as ouputType from f_entity_tag_report_mapping mapping ") ;
            this.queryBuilder.append("LEFT JOIN  stretchy_report report ON report.id = mapping.report_id ") ;
            this.queryBuilder.append("LEFT JOIN f_entity_tags tags ON tags.id = mapping.entity_tag_id ") ;
        }
        @Override
        public DocumentTagData mapRow(ResultSet rs, int rowNum) throws SQLException {
            final String reportName = rs.getString("repoprtName") ;
            final String reportType = rs.getString("reportType") ;
            final String reportCategory = rs.getString("reportCategory") ;
            final Long tagId = rs.getLong("tagId") ;
            final String outputType = rs.getString("ouputType") ;
            return new DocumentTagData(reportName, reportType, reportCategory, tagId, outputType) ;
        }
        
        public String query() {
            return this.queryBuilder.toString() ;
        }
    }

}