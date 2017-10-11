package org.apache.fineract.portfolio.client.service;

import java.util.Map;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.portfolio.client.data.ClientLimitsDataValidator;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientAccountLimitDetails;
import org.apache.fineract.portfolio.client.domain.ClientAccountLimitDetailsRepository;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientAccountLimitsWritePlatformServiceImpl implements ClientAccountLimitsWritePlatformServeice {

    private final ClientAccountLimitDetailsRepository clientAccountLimitDetailsRepository;
    private final ClientRepositoryWrapper clientRepository;
    private final ClientLimitsDataValidator fromApiJsonDeserializer;

    @Autowired
    public ClientAccountLimitsWritePlatformServiceImpl(final ClientAccountLimitDetailsRepository clientAccountLimitDetailsRepository,
            final ClientRepositoryWrapper clientRepository, ClientLimitsDataValidator fromApiJsonDeserializer) {
        this.clientAccountLimitDetailsRepository = clientAccountLimitDetailsRepository;
        this.clientRepository = clientRepository;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
    }

    @Override
    public CommandProcessingResult createAccountLimits(final Long clientId, final JsonCommand command) {

        this.fromApiJsonDeserializer.validateAccountLimits(command);
        // get client from clientId
        final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);

        ClientAccountLimitDetails limits = ClientAccountLimitDetails.assembleFromJson(command, client);

        this.clientAccountLimitDetailsRepository.save(limits);
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(limits.getId()) //
                .build();
    }

    @Override
    public CommandProcessingResult updateAccountLimits(final Long limitId, final JsonCommand command) {

        this.fromApiJsonDeserializer.validateAccountLimits(command);
        // get ClientAccountLimitDetails from limitId
        final ClientAccountLimitDetails limitDetails = this.clientAccountLimitDetailsRepository.findOne(limitId);
        final Map<String, Object> changes = limitDetails.update(command);
        this.clientAccountLimitDetailsRepository.save(limitDetails);
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(limitId) //
                .with(changes) //
                .build();
    }

}
