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

import java.io.InputStream;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.GeoTag;
import org.apache.fineract.infrastructure.core.domain.Base64EncodedImage;
import org.apache.fineract.infrastructure.documentmanagement.api.ImagesApiConstants;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepository;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepositoryFactory;
import org.apache.fineract.infrastructure.documentmanagement.domain.Image;
import org.apache.fineract.infrastructure.documentmanagement.domain.ImageRepository;
import org.apache.fineract.infrastructure.documentmanagement.domain.StorageType;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.organisation.staff.domain.StaffRepositoryWrapper;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.common.domain.EntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.task.domain.Task;
import com.finflux.task.domain.TaskRepositoryWrapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class ImageWritePlatformServiceJpaRepositoryImpl implements ImageWritePlatformService {

    private final ContentRepositoryFactory contentRepositoryFactory;
    private final ClientRepositoryWrapper clientRepositoryWrapper;
    private final ImageRepository imageRepository;
    private final StaffRepositoryWrapper staffRepositoryWrapper;
    private final TaskRepositoryWrapper taskRepositoryWrapper;

    @Autowired
    public ImageWritePlatformServiceJpaRepositoryImpl(final ContentRepositoryFactory documentStoreFactory,
            final ClientRepositoryWrapper clientRepositoryWrapper, final ImageRepository imageRepository,
            StaffRepositoryWrapper staffRepositoryWrapper, final TaskRepositoryWrapper taskRepositoryWrapper) {
        this.contentRepositoryFactory = documentStoreFactory;
        this.clientRepositoryWrapper = clientRepositoryWrapper;
        this.imageRepository = imageRepository;
        this.staffRepositoryWrapper = staffRepositoryWrapper;
        this.taskRepositoryWrapper = taskRepositoryWrapper;
    }

    @Transactional
    @Override
    public CommandProcessingResult saveOrUpdateImage(JsonCommand command) {
        Object owner = null;
        JsonElement json = command.parsedJson();
        String entityName = null;
        Long entityId = null;
        entityName = json.getAsJsonObject().get(ImagesApiConstants.entityNameParam).getAsString();
        entityId = json.getAsJsonObject().get(ImagesApiConstants.entityIdParam).getAsLong();
        if (entityName.equalsIgnoreCase(EntityType.CLIENT.getDisplayName())
                || entityName.equalsIgnoreCase(EntityType.STAFF.getDisplayName())
                || entityName.equalsIgnoreCase(EntityType.TASK.getDisplayName())) {
            owner = deletePreviousImage(entityName, entityId);
        }
        
        return updateImage(owner, json.getAsJsonObject().get(ImagesApiConstants.imageLocationParam).getAsString(),StorageType.fromInt(json.getAsJsonObject().get(ImagesApiConstants.storageTypeParam).getAsInt()), command);
    }
    
    @Override
    @Transactional
    public String saveImageInRepository(String imageName,InputStream inputStream, Long fileSize,Base64EncodedImage encodedImage,Long entityId,String entityName){
        final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository();
        String imageLocation=null;
        if(encodedImage!=null){
            imageLocation = contentRepository.saveImage(encodedImage, entityId,imageName,entityName);
        }else{
            imageLocation=contentRepository.saveImage(inputStream, entityId, imageName, fileSize,entityName);
        }
        JsonObject json=new JsonObject();
        json.addProperty(ImagesApiConstants.imageLocationParam, imageLocation);
        json.addProperty(ImagesApiConstants.storageTypeParam,contentRepository.getStorageType().getValue());
        json.addProperty(ImagesApiConstants.entityNameParam,entityName);
        json.addProperty(ImagesApiConstants.entityIdParam,entityId);
        return json.toString();
    }
    
    @Transactional
    @Override
    public CommandProcessingResult deleteImage(String entityName, final Long clientId) {
        Object owner = null;
        Image image = null;
        if (EntityType.CLIENT.getDisplayName().equalsIgnoreCase(entityName)) {
            owner = this.clientRepositoryWrapper.findOneWithNotFoundDetectionAndLazyInitialize(clientId);
            Client client = (Client) owner;
            image = client.getImage();
            client.setImage(null);
            this.clientRepositoryWrapper.save(client);

        } else if (EntityType.STAFF.getDisplayName().equalsIgnoreCase(entityName)) {
            owner = this.staffRepositoryWrapper.findOneWithNotFoundDetectionAndLazyInitialize(clientId);
            Staff staff = (Staff) owner;
            image = staff.getImage();
            staff.setImage(null);
            this.staffRepositoryWrapper.save(staff);

        }
        // delete image from the file system
        if (image != null) {
            final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository(StorageType.fromInt(image
                    .getStorageType()));
            contentRepository.deleteImage(clientId, image.getLocation());
            this.imageRepository.delete(image);
        }

        return new CommandProcessingResult(clientId);
    }

    /**
     * @param entityName
     * @param entityId
     * @return
     */
    private Object deletePreviousImage(String entityName, final Long entityId) {
        Object owner = null;
        Image image = null;
        if (EntityType.CLIENT.getDisplayName().equalsIgnoreCase(entityName)) {
            Client client = this.clientRepositoryWrapper.findOneWithNotFoundDetectionAndLazyInitialize(entityId);
            image = client.getImage();
            owner = client;
        } else if (EntityType.STAFF.getDisplayName().equalsIgnoreCase(entityName)) {
            Staff staff = this.staffRepositoryWrapper.findOneWithNotFoundDetectionAndLazyInitialize(entityId);
            image = staff.getImage();
            owner = staff;
        }else if(EntityType.TASK.getDisplayName().equalsIgnoreCase(entityName)) {
        	Task task = this.taskRepositoryWrapper.findOneWithNotFoundDetection(entityId);
        	image = task.getImage();
        	owner = task;
        }
        if (image != null) {
            final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository(StorageType.fromInt(image
                    .getStorageType()));
            contentRepository.deleteImage(entityId, image.getLocation());
        }
        return owner;
    }

    private CommandProcessingResult updateImage(final Object owner, final String imageLocation, final StorageType storageType,
            final JsonCommand command) {
        Image image = null;
        if (owner instanceof Client) {
            Client client = (Client) owner;
            image = client.getImage();

            image = createImage(image, imageLocation, storageType, command);
            client.setImage(image);
            this.clientRepositoryWrapper.save(client);
        } else if (owner instanceof Staff) {
            Staff staff = (Staff) owner;
            image = staff.getImage();
            image = createImage(image, imageLocation, storageType, command);
            staff.setImage(image);
            this.staffRepositoryWrapper.save(staff);
        } else if (owner instanceof Task) {
            Task task = (Task) owner;
            image = task.getImage();
            image = createImage(image, imageLocation, storageType, command);
            task.setImage(image);
            this.taskRepositoryWrapper.save(task);
        } else {
            image = createImage(image, imageLocation, storageType, command);
        }

        return new CommandProcessingResult(this.imageRepository.save(image).getId());
    }

    private Image createImage(Image image, final String imageLocation, final StorageType storageType,final JsonCommand command) {
    	JsonElement json=command.parsedJson();
        if (image == null) {
            image = new Image(imageLocation, storageType,command);
        } else {
            image.setLocation(imageLocation);
            image.setStorageType(storageType.getValue());
        }
        
        if(json.getAsJsonObject().has(ImagesApiConstants.geoTagParam)){
            image.setGeoTag(GeoTag.from(json.getAsJsonObject().get(ImagesApiConstants.geoTagParam).getAsString()));
        }
        return image;
    }

}