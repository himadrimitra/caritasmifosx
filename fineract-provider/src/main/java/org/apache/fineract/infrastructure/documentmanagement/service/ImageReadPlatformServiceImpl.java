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
import java.util.Collection;
import java.util.Date;

import org.apache.fineract.infrastructure.core.data.GeoTag;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepository;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepositoryFactory;
import org.apache.fineract.infrastructure.documentmanagement.data.ImageData;
import org.apache.fineract.infrastructure.documentmanagement.domain.Image;
import org.apache.fineract.infrastructure.documentmanagement.domain.ImageRepository;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.organisation.staff.domain.StaffRepositoryWrapper;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.exception.ImageNotFoundException;
import org.apache.fineract.portfolio.common.domain.EntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ImageReadPlatformServiceImpl implements ImageReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final ContentRepositoryFactory contentRepositoryFactory;
    private final ClientRepositoryWrapper clientRepositoryWrapper;
    private final StaffRepositoryWrapper staffRepositoryWrapper;
    private final ImageRepository imageRepository;

    @Autowired
    public ImageReadPlatformServiceImpl(final RoutingDataSource dataSource, final ContentRepositoryFactory documentStoreFactory,
            final ClientRepositoryWrapper clientRepositoryWrapper, final StaffRepositoryWrapper staffRepositoryWrapper,
            final ImageRepository imageRepository) {
        this.staffRepositoryWrapper = staffRepositoryWrapper;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.contentRepositoryFactory = documentStoreFactory;
        this.clientRepositoryWrapper = clientRepositoryWrapper;
        this.imageRepository = imageRepository;

    }

    private static final class ImageMapper implements RowMapper<ImageData> {

        private final String entityDisplayName;

        public ImageMapper(final String entityDisplayName) {
            this.entityDisplayName = entityDisplayName;
        }

        public String schema(final String entityType) {
            final StringBuilder builder = new StringBuilder(
                    "image.id as id, image.location as location, image.storage_type_enum as storageType, image.geo_tag as geoTag ");
            if (EntityType.CLIENT.getDisplayName().equalsIgnoreCase(entityType)) {
                builder.append(" from m_image image , m_client client " + " where client.image_id = image.id and client.id=?");
            } else if (EntityType.STAFF.getDisplayName().equalsIgnoreCase(entityType)) {
                builder.append("from m_image image , m_staff staff " + " where staff.image_id = image.id and staff.id=?");
            } else {
                builder.append("from m_image image where image.id=?");
            }
            return builder.toString();
        }

        @Override
        public ImageData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final String location = rs.getString("location");
            final Integer storageType = JdbcSupport.getInteger(rs, "storageType");
            final String geoTagJson = rs.getString("geoTag");
            final GeoTag geoTag = GeoTag.from(geoTagJson);
            return new ImageData(id, location, storageType, this.entityDisplayName, geoTag);
        }
    }

    private static final class ImageDataMapper implements RowMapper<ImageData> {

        public String schema() {
            final StringBuilder builder = new StringBuilder(
                    "image.id as id, image.location as location, image.storage_type_enum as storageType,");
            builder.append(" image.geo_tag as geoTag,image.entity_type as entityType,image.entity_id as entityId,");
            builder.append("CONCAT(appuser.firstname,' ',appuser.lastname) as createdBy,image.created_date as createdOn ");
            builder.append(" from m_image image LEFT JOIN m_appuser appuser on image.createdby_id=appuser.id ");
            builder.append("where image.entity_type=? and image.entity_id=?");
            return builder.toString();
        }

        @Override
        public ImageData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final String location = rs.getString("location");
            final Integer storageType = JdbcSupport.getInteger(rs, "storageType");
            final String geoTagJson = rs.getString("geoTag");
            final GeoTag geoTag = GeoTag.from(geoTagJson);
            final Integer entityType = rs.getInt("entityType");
            final EntityType entityTypeEnum = EntityType.fromInt(entityType);
            final Long entityId = rs.getLong("entityId");
            final String createdBy = rs.getString("createdBy");
            final Date createdOn = rs.getDate("createdOn");
            return new ImageData(id, location, storageType, null, geoTag, entityTypeEnum, entityId, createdBy, createdOn);
        }
    }

    @Override
    public ImageData retrieveImage(final String entityType, final Long entityId) {
        try {
            Object owner;
            String displayName = null;
            if (EntityType.CLIENT.getDisplayName().equalsIgnoreCase(entityType)) {
                owner = this.clientRepositoryWrapper.findOneWithNotFoundDetection(entityId);
                displayName = ((Client) owner).getDisplayName();
            } else if (EntityType.STAFF.getDisplayName().equalsIgnoreCase(entityType)) {
                owner = this.staffRepositoryWrapper.findOneWithNotFoundDetection(entityId);
                displayName = ((Staff) owner).displayName();
            }
            final ImageMapper imageMapper = new ImageMapper(displayName);

            final String sql = "select " + imageMapper.schema(entityType);

            final ImageData imageData = this.jdbcTemplate.queryForObject(sql, imageMapper, new Object[] { entityId });
            final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository(imageData.storageType());
            final ImageData result = contentRepository.fetchImage(imageData);

            if (result.getContent() == null) { throw new ImageNotFoundException(entityType, entityId); }

            return result;
        } catch (final EmptyResultDataAccessException e) {
            throw new ImageNotFoundException("clients", entityId);
        }
    }

    @Override
    public ImageData retrieveImage(final Long imageId) {
        try {
            final Image image = this.imageRepository.findOne(imageId);
            final ImageData imageData = new ImageData(imageId, image.getLocation(), image.getStorageType(), null,
                    GeoTag.from(image.getGeoTag()));
            final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository(imageData.storageType());
            final ImageData result = contentRepository.fetchImage(imageData);

            if (result.getContent() == null) { throw new ImageNotFoundException(EntityType.fromInt(image.getEntityType()).getCode(),
                    image.getEntityId().longValue()); }

            return result;
        } catch (final EmptyResultDataAccessException e) {
            throw new ImageNotFoundException("image", imageId);
        }
    }

    @Override
    public Collection<ImageData> retrieveAllImages(final String entityType, final Long entityId) {
        try {
            final ImageDataMapper imageDataMapper = new ImageDataMapper();
            final String sql = "select " + imageDataMapper.schema();
            final EntityType entityTypeEnum = EntityType.getEntityTypeByString(entityType);
            return this.jdbcTemplate.query(sql, imageDataMapper, new Object[] { entityTypeEnum.getValue(), entityId });
        } catch (final EmptyResultDataAccessException e) {
            throw new ImageNotFoundException("clients", entityId);
        }
    }
}