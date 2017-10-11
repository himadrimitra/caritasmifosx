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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.data.GeoTag;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepositoryUtils;
import org.apache.fineract.infrastructure.documentmanagement.domain.StorageType;
import org.apache.fineract.portfolio.common.domain.EntityType;
import org.apache.poi.util.IOUtils;

public class ImageData {

    private final Long imageId;
    private final String location;
    private final Integer storageType;
    private final String entityDisplayName;
    private final EntityType entityType;
    private final Long entityId;
    private final String createdBy;
    private final Date createdOn;
    private final String name;

    private File file;
    private ContentRepositoryUtils.IMAGE_FILE_EXTENSION fileExtension;
    private InputStream inputStream;

    private final GeoTag geoTag;

    public ImageData(final Long imageId, final String location, final Integer storageType, final String entityDisplayName,
            final GeoTag geoTag) {
        this.imageId = imageId;
        this.location = location;
        this.storageType = storageType;
        this.entityDisplayName = entityDisplayName;
        this.geoTag = geoTag;
        this.entityType = null;
        this.entityId = null;
        this.createdBy = null;
        this.createdOn = null;
        this.name = this.location.substring(this.location.lastIndexOf("\\") + 1, this.location.length());
    }

    public ImageData(final Long imageId, final String location, final Integer storageType, final String entityDisplayName,
            final GeoTag geoTag, final EntityType entityType, final Long entityId, final String createdBy, final Date createdOn) {

        this.imageId = imageId;
        this.location = location;
        this.name = this.location.substring(this.location.lastIndexOf("\\") + 1, this.location.length());
        this.storageType = storageType;
        this.entityDisplayName = entityDisplayName;
        this.geoTag = geoTag;
        this.entityType = entityType;
        this.entityId = entityId;
        this.createdBy = createdBy;
        this.createdOn = createdOn;
    }

    public byte[] getContent() {
        // TODO Vishwas Fix error handling
        try {
            if (this.inputStream == null) {
                final FileInputStream fileInputStream = new FileInputStream(this.file);
                return IOUtils.toByteArray(fileInputStream);
            }

            return IOUtils.toByteArray(this.inputStream);
        } catch (final IOException e) {
            return null;
        }
    }

    public byte[] resizeImage(final InputStream in, final int maxWidth, final int maxHeight) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        resizeImage(in, out, maxWidth, maxHeight);
        return out.toByteArray();
    }

    public void resizeImage(final InputStream in, final OutputStream out, final int maxWidth, final int maxHeight) throws IOException {

        final BufferedImage src = ImageIO.read(in);
        if (src.getWidth() <= maxWidth && src.getHeight() <= maxHeight) {
            out.write(getContent());
            return;
        }
        final float widthRatio = (float) src.getWidth() / maxWidth;
        final float heightRatio = (float) src.getHeight() / maxHeight;
        final float scaleRatio = widthRatio > heightRatio ? widthRatio : heightRatio;

        // TODO(lindahl): Improve compressed image quality (perhaps quality
        // ratio)

        final int newWidth = (int) (src.getWidth() / scaleRatio);
        final int newHeight = (int) (src.getHeight() / scaleRatio);
        final int colorModel = this.fileExtension == ContentRepositoryUtils.IMAGE_FILE_EXTENSION.JPEG ? BufferedImage.TYPE_INT_RGB
                : BufferedImage.TYPE_INT_ARGB;
        final BufferedImage target = new BufferedImage(newWidth, newHeight, colorModel);
        final Graphics2D g = target.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(src, 0, 0, newWidth, newHeight, Color.BLACK, null);
        g.dispose();
        ImageIO.write(target, this.fileExtension != null ? this.fileExtension.getValueWithoutDot() : "jpeg", out);
    }

    public byte[] getContentOfSize(final Integer maxWidth, final Integer maxHeight) {
        if (maxWidth == null && maxHeight == null) { return getContent(); }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(this.file);
            final byte[] out = resizeImage(fis, maxWidth != null ? maxWidth : Integer.MAX_VALUE,
                    maxHeight != null ? maxHeight : Integer.MAX_VALUE);
            return out;
        } catch (final IOException ex) {
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (final IOException ex) {}
            }
        }
    }

    private void setImageContentType(final String filename) {
        this.fileExtension = ContentRepositoryUtils.IMAGE_FILE_EXTENSION.JPEG;

        if (StringUtils.endsWith(filename.toLowerCase(), ContentRepositoryUtils.IMAGE_FILE_EXTENSION.GIF.getValue())) {
            this.fileExtension = ContentRepositoryUtils.IMAGE_FILE_EXTENSION.GIF;
        } else if (StringUtils.endsWith(filename, ContentRepositoryUtils.IMAGE_FILE_EXTENSION.PNG.getValue())) {
            this.fileExtension = ContentRepositoryUtils.IMAGE_FILE_EXTENSION.PNG;
        }
    }

    public void updateContent(final File file) {
        this.file = file;
        if (this.file != null) {
            setImageContentType(this.file.getName());
        }
    }

    public String contentType() {
        return ContentRepositoryUtils.IMAGE_MIME_TYPE.fromFileExtension(this.fileExtension).getValue();
    }

    public StorageType storageType() {
        return StorageType.fromInt(this.storageType);
    }

    public String name() {
        return this.file.getName();
    }

    public String location() {
        return this.location;
    }

    public void updateContent(final InputStream objectContent) {
        this.inputStream = objectContent;
    }

    public String getEntityDisplayName() {
        return this.entityDisplayName;
    }

    public Long getImageId() {
        return this.imageId;
    }

    public GeoTag getGeoTag() {
        return this.geoTag;
    }

    public EntityType getEntityType() {
        return this.entityType;
    }

    public Long getEntityId() {
        return this.entityId;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Date getCreatedOn() {
        return this.createdOn;
    }

    public String getName() {
        return this.name;
    }
}
