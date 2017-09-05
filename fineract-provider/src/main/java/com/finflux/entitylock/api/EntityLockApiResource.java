package com.finflux.entitylock.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.portfolio.common.domain.EntityType;
import org.apache.fineract.portfolio.common.exception.EntityTypeNotSupportedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.commands.service.CommandWrapperBuilder;
import com.finflux.common.constant.CommonConstants;

@Path("locks/{entityType}/{entityId}")
@Component
@Scope("singleton")
public class EntityLockApiResource {

    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    @SuppressWarnings("rawtypes")
    @Autowired
    public EntityLockApiResource(final DefaultToApiJsonSerializer toApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService) {
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;

    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String lockOrUnlockEntity(@PathParam(CommonConstants.entityTypeParam) final String entityType,
            @PathParam(CommonConstants.entityIdParam) final Long entityId,
            @QueryParam(CommonConstants.commandParam) final String commandParam, final String apiRequestBodyAsJson) {
        final Long entityTypeId = validateEntityTypeAndGetEntityTypeId(entityType);
        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);
        CommandProcessingResult result = null;
        CommandWrapper commandRequest = null;
        if (is(commandParam, EntityLockApiConstants.ACTION_LOCK.toLowerCase())) {
            commandRequest = builder.lockEntity(entityTypeId, entityType, entityId, commandParam).build();
        } else if (is(commandParam, EntityLockApiConstants.ACTION_UNLOCK.toLowerCase())) {
            commandRequest = builder.unlockEntity(entityTypeId, entityType, entityId, commandParam).build();
        } else {
            return null;
        }
        result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }

    private Long validateEntityTypeAndGetEntityTypeId(final String entityType) {
        final EntityType entityTypeEnum = EntityType.getEntityType(entityType);
        if (entityTypeEnum == null || entityTypeEnum.isInvalid()) { throw new EntityTypeNotSupportedException(entityType); }
        return entityTypeEnum.getValue().longValue();
    }
}
