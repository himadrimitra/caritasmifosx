package com.finflux.portfolio.external.service;

import java.lang.reflect.Type;
import java.util.*;

import com.finflux.common.security.service.PlatformCryptoService;
import com.finflux.portfolio.external.data.ExternalServicePropertyData;
import com.finflux.portfolio.external.domain.*;
import org.apache.fineract.commands.domain.CommandSource;
import org.apache.fineract.commands.domain.CommandSourceRepository;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.task.data.*;
import com.finflux.task.domain.*;
import com.finflux.task.service.TaskExecutionService;
import com.finflux.task.service.TaskPlatformReadService;
import com.finflux.task.service.TaskPlatformWriteService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by dhirendra on 22/09/16.
 */
@Service
@Scope("singleton")
public class ExternalServiceWriteServiceImpl implements ExternalServiceWriteService {

    private final CommandSourceRepository commandSourceRepository;
    private final FromJsonHelper fromJsonHelper;
    private final PlatformSecurityContext context;
    private final PlatformCryptoService platformCryptoService;

    private final OtherExternalServiceRepository serviceRepository;
    private final OtherExternalServiceRepositoryWrapper serviceRepositoryWrapper;
    private final OtherExternalServicePropertyRepository servicePropertyRepository;
    private final OtherExternalServicePropertyRepositoryWrapper servicePropertyRepositoryWrapper;


    @Autowired
    public ExternalServiceWriteServiceImpl(final OtherExternalServiceRepository serviceRepository,
                                           final OtherExternalServicePropertyRepository servicePropertyRepository,
                                           final OtherExternalServiceRepositoryWrapper serviceRepositoryWrapper,
                                           final OtherExternalServicePropertyRepositoryWrapper servicePropertyRepositoryWrapper,
										   final FromJsonHelper fromJsonHelper, final PlatformSecurityContext context,
                                           final CommandSourceRepository commandSourceRepository,
                                           final PlatformCryptoService platformCryptoService) {
        this.serviceRepository = serviceRepository;
        this.servicePropertyRepository = servicePropertyRepository;
        this.serviceRepositoryWrapper = serviceRepositoryWrapper;
        this.servicePropertyRepositoryWrapper = servicePropertyRepositoryWrapper;
        this.commandSourceRepository = commandSourceRepository;
        this.fromJsonHelper = fromJsonHelper;
        this.context = context;
        this.platformCryptoService = platformCryptoService;
    }

    @Transactional
    @Override
    public CommandProcessingResult updateServiceProperties(Long serviceId, JsonCommand command) {
        this.context.authenticatedUser();
        Type type = new TypeToken<List<ExternalServicePropertyData>>() {}.getType();
        List<ExternalServicePropertyData> deltaServiceProperties = fromJsonHelper.getGsonConverter().fromJson(command.json(),type);
        OtherExternalService otherExternalService = serviceRepositoryWrapper.findOneWithNotFoundDetection(serviceId);
        if(deltaServiceProperties != null){
            for(ExternalServicePropertyData propertyData: deltaServiceProperties ){
                OtherExternalServiceProperty externalServiceProperty = servicePropertyRepositoryWrapper.findOneWithNotFoundDetection(serviceId,propertyData.getName());
                String newValue = propertyData.getValue();
                if(propertyData.getEncrypted() && propertyData.getValue()!=null) {
                    newValue = platformCryptoService.encrypt(propertyData.getValue());
                    externalServiceProperty.setEncrypted(true);
                }
                externalServiceProperty.setValue(newValue);
                servicePropertyRepository.save(externalServiceProperty);
            }
        }
        return new CommandProcessingResult(null);
    }
}
