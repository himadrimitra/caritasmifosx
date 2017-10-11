package com.finflux.portfolio.loan.mandate.service;

import com.finflux.portfolio.loan.mandate.api.MandateApiConstants;
import com.finflux.portfolio.loan.mandate.data.MandateData;
import com.finflux.portfolio.loan.mandate.data.MandateDataValidator;
import com.finflux.portfolio.loan.mandate.domain.Mandate;
import com.finflux.portfolio.loan.mandate.domain.MandateRepository;
import com.finflux.portfolio.loan.mandate.domain.MandateStatusEnum;
import com.finflux.portfolio.loan.mandate.exception.DoesNotMatchActiveMandateUMRNException;
import com.finflux.portfolio.loan.mandate.exception.InvalidMandateStateForOperationException;
import com.finflux.portfolio.loan.mandate.exception.NoActiveMandateFoundException;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.portfolio.products.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

@Service
public class MandateWritePlatformServiceImpl implements MandateWritePlatformService {

        private final PlatformSecurityContext context;
        private final MandateReadPlatformService mandateReadPlatformService;
        private final MandateRepository mandateRepository;
        private final MandateDataValidator mandateDataValidator;
        private final LoanReadPlatformService loanReadPlatformService;

        @Autowired
        public MandateWritePlatformServiceImpl(final PlatformSecurityContext context,
                final MandateReadPlatformService mandateReadPlatformService,
                final MandateRepository mandateRepository,
                final MandateDataValidator mandateDataValidator,
                final LoanReadPlatformService loanReadPlatformService){

                this.context = context;
                this.mandateReadPlatformService = mandateReadPlatformService;
                this.mandateRepository = mandateRepository;
                this.mandateDataValidator = mandateDataValidator;
                this.loanReadPlatformService = loanReadPlatformService;
        }

        @Override
        public CommandProcessingResult createMandate(final JsonCommand command) {
                this.mandateDataValidator.validate(command.json(), false);
                this.loanReadPlatformService.retrieveOne(command.getLoanId()); //just validating loan exists

                Collection<MandateData> mandates = this.mandateReadPlatformService.retrieveMandatesWithStatus(command.getLoanId(),
                        new Integer[] {MandateStatusEnum.ACTIVE.getValue(),
                                        MandateStatusEnum.CREATE_REQUESTED.getValue(),
                                        MandateStatusEnum.CREATE_INPROCESS.getValue(),
                                        MandateStatusEnum.UPDATE_REQUESTED.getValue(),
                                        MandateStatusEnum.UPDATE_INPROCESS.getValue(),
                                        MandateStatusEnum.CANCEL_REQUESTED.getValue(),
                                        MandateStatusEnum.CANCEL_INPROCESS.getValue()});

                if(null != mandates && mandates.size() > 0){
                        MandateData data = mandates.iterator().next();
                        throw new InvalidMandateStateForOperationException("CREATE",
                                MandateStatusEnum.fromInt(data.getMandateStatus().getId().intValue()));
                }

                Mandate mandate = Mandate.withStatus(command, MandateStatusEnum.CREATE_REQUESTED);
                this.mandateRepository.save(mandate);
                return new CommandProcessingResultBuilder() //
                        .withCommandId(command.commandId()) //
                        .withEntityId(mandate.getId())
                        .build();
        }

        @Override
        public CommandProcessingResult updateMandate(final JsonCommand command) {
                this.mandateDataValidator.validate(command.json(), true);
                this.loanReadPlatformService.retrieveOne(command.getLoanId()); //just validating loan exists

                Collection<MandateData> mandates = this.mandateReadPlatformService.retrieveMandatesWithStatus(command.getLoanId(),
                        new Integer[] {MandateStatusEnum.ACTIVE.getValue(),
                                MandateStatusEnum.CREATE_REQUESTED.getValue(),
                                MandateStatusEnum.CREATE_INPROCESS.getValue(),
                                MandateStatusEnum.UPDATE_REQUESTED.getValue(),
                                MandateStatusEnum.UPDATE_INPROCESS.getValue(),
                                MandateStatusEnum.CANCEL_REQUESTED.getValue(),
                                MandateStatusEnum.CANCEL_INPROCESS.getValue()});

                if(null == mandates || mandates.size() == 0 || !containsActiveMandate(mandates)){
                        throw new NoActiveMandateFoundException();
                } 
                MandateStatusEnum status = containsRequestedOrInProcessMandate(mandates);
                if(!status.hasStateOf(MandateStatusEnum.INVALID)){
                    throw new InvalidMandateStateForOperationException("UPDATE", status);
                }

                final String umrn = command.stringValueOfParameterNamed(MandateApiConstants.umrn);
                if(null == umrn || !umrn.equalsIgnoreCase(getActiveMandate(mandates).getUmrn())){
                        throw new DoesNotMatchActiveMandateUMRNException(umrn);
                }

                Mandate mandate = Mandate.withStatus(command, MandateStatusEnum.UPDATE_REQUESTED);
                this.mandateRepository.save(mandate);
                return new CommandProcessingResultBuilder() //
                        .withCommandId(command.commandId()) //
                        .withEntityId(mandate.getId())
                        .build();
        }

        @Override
        public CommandProcessingResult cancelMandate(final JsonCommand command) {
                this.mandateDataValidator.validate(command.json(), true);
                this.loanReadPlatformService.retrieveOne(command.getLoanId()); //just validating loan exists

                Collection<MandateData> mandates = this.mandateReadPlatformService.retrieveMandatesWithStatus(command.getLoanId(),
                        new Integer[] {MandateStatusEnum.ACTIVE.getValue(),
                                MandateStatusEnum.CREATE_REQUESTED.getValue(),
                                MandateStatusEnum.CREATE_INPROCESS.getValue(),
                                MandateStatusEnum.UPDATE_REQUESTED.getValue(),
                                MandateStatusEnum.UPDATE_INPROCESS.getValue(),
                                MandateStatusEnum.CANCEL_REQUESTED.getValue(),
                                MandateStatusEnum.CANCEL_INPROCESS.getValue()});

                if(null == mandates || mandates.size() == 0 || !containsActiveMandate(mandates)){
                        throw new NoActiveMandateFoundException();
                } else {
                        MandateStatusEnum status = containsRequestedOrInProcessMandate(mandates);
                        if(!status.hasStateOf(MandateStatusEnum.INVALID)){
                                throw new InvalidMandateStateForOperationException("CANCEL", status);
                        }
                }

                final String umrn = command.stringValueOfParameterNamed(MandateApiConstants.umrn);
                if(null == umrn || !umrn.equalsIgnoreCase(getActiveMandate(mandates).getUmrn())){
                        throw new DoesNotMatchActiveMandateUMRNException(umrn);
                }

                Mandate mandate = Mandate.withStatus(command, MandateStatusEnum.CANCEL_REQUESTED);
                this.mandateRepository.save(mandate);
                return new CommandProcessingResultBuilder() //
                        .withCommandId(command.commandId()) //
                        .withEntityId(mandate.getId())
                        .build();
        }

        @Override
        public CommandProcessingResult editMandate(final JsonCommand command) {
                this.mandateDataValidator.validate(command.json(), false);
                final Mandate mandateToEdit = this.mandateRepository.findOneByIdAndLoanId(command.entityId(), command.getLoanId());
                if(null == mandateToEdit){
                        throw new ResourceNotFoundException();
                }else{
                        final MandateStatusEnum status = MandateStatusEnum.fromInt(mandateToEdit.getMandateStatusEnum());
                        if(!(status.hasStateOf(MandateStatusEnum.CREATE_REQUESTED)
                                || status.hasStateOf(MandateStatusEnum.UPDATE_REQUESTED)
                                || status.hasStateOf(MandateStatusEnum.CANCEL_REQUESTED))){

                                throw new InvalidMandateStateForOperationException("EDIT", status);
                        }
                }

                Map<String, Object> changes = mandateToEdit.edit(command);
                this.mandateRepository.save(mandateToEdit);
                return new CommandProcessingResultBuilder() //
                        .withCommandId(command.commandId()) //
                        .withEntityId(command.entityId())
                        .with(changes)
                        .build();
        }

        @Override
        public CommandProcessingResult deleteMandate(final JsonCommand command) {
                final Mandate mandateToDelete = this.mandateRepository.findOneByIdAndLoanId(command.entityId(), command.getLoanId());
                if(null == mandateToDelete){
                        throw new ResourceNotFoundException();
                }else{
                        final MandateStatusEnum status = MandateStatusEnum.fromInt(mandateToDelete.getMandateStatusEnum());
                        if(!(status.hasStateOf(MandateStatusEnum.CREATE_REQUESTED)
                                || status.hasStateOf(MandateStatusEnum.UPDATE_REQUESTED)
                                || status.hasStateOf(MandateStatusEnum.CANCEL_REQUESTED))){

                                throw new InvalidMandateStateForOperationException("DELETE", status);
                        }
                }

                this.mandateRepository.delete(mandateToDelete);
                return new CommandProcessingResultBuilder() //
                        .withCommandId(command.commandId()) //
                        .withEntityId(command.entityId())
                        .build();
        }

        private MandateStatusEnum containsRequestedOrInProcessMandate(final Collection<MandateData> mandates) {
                if(null == mandates || mandates.size() < 1){
                        return MandateStatusEnum.INVALID;
                }

                for(MandateData data : mandates){
                        MandateStatusEnum status = MandateStatusEnum.fromInt(data.getMandateStatus().getId().intValue());
                        if(status.hasStateOf(MandateStatusEnum.CREATE_REQUESTED)
                                || status.hasStateOf(MandateStatusEnum.CREATE_INPROCESS)
                                || status.hasStateOf(MandateStatusEnum.UPDATE_REQUESTED)
                                || status.hasStateOf(MandateStatusEnum.UPDATE_INPROCESS)
                                || status.hasStateOf(MandateStatusEnum.CANCEL_REQUESTED)
                                || status.hasStateOf(MandateStatusEnum.CANCEL_INPROCESS)){
                                return status;
                        }
                }
                return MandateStatusEnum.INVALID;
        }

        private boolean containsActiveMandate(final Collection<MandateData> mandates) {
                if(null == mandates || mandates.size() < 1){
                        return false;
                }

                for(MandateData data : mandates){
                        if(MandateStatusEnum.fromInt(data.getMandateStatus().getId().intValue())
                                .hasStateOf(MandateStatusEnum.ACTIVE)){
                                return true;
                        }
                }
                return false;
        }

        private MandateData getActiveMandate(final Collection<MandateData> mandates) {
                if(null == mandates || mandates.size() < 1){
                        return null;
                }

                for(MandateData data : mandates){
                        if(MandateStatusEnum.fromInt(data.getMandateStatus().getId().intValue())
                                .hasStateOf(MandateStatusEnum.ACTIVE)){
                                return data;
                        }
                }
                return null;
        }
}
