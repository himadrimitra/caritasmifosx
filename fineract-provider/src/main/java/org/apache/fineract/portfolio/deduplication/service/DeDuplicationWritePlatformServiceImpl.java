package org.apache.fineract.portfolio.deduplication.service;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.client.domain.LegalForm;
import org.apache.fineract.portfolio.deduplication.api.DeduplicationApiConstants;
import org.apache.fineract.portfolio.deduplication.domain.DeduplicationWeightage;
import org.apache.fineract.portfolio.deduplication.domain.DeduplicationWeightageRepository;
import org.apache.fineract.portfolio.products.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DeDuplicationWritePlatformServiceImpl implements DeDuplicationWritePlatformService {

        private final DeduplicationWeightageRepository repository;
        private final FromJsonHelper fromApiJsonHelper;

        @Autowired
        public DeDuplicationWritePlatformServiceImpl(final DeduplicationWeightageRepository repository,
                final FromJsonHelper fromApiJsonHelper){
                this.repository = repository;
                this.fromApiJsonHelper = fromApiJsonHelper;
        }

        @Override
        public CommandProcessingResult updateDedupWeightage(JsonCommand command) {
                validateForUpdate(command);
                DeduplicationWeightage weightage = this.repository.findByLegalForm(command.entityId().intValue());
                if(weightage == null){
                        throw new ResourceNotFoundException();
                }

                Map<String, Object> changes = weightage.update(command);
                this.repository.save(weightage);

                return new CommandProcessingResultBuilder() //
                        .withCommandId(command.commandId()) //
                        .withEntityId(command.entityId())
                        .with(changes)
                        .build();
        }

        private void validateForUpdate(JsonCommand command) {
                String json = command.json();
                if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

                final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
                if(command.entityId().equals(LegalForm.PERSON.getValue().longValue())){
                        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                                DeduplicationApiConstants.LEGAL_FORM_PERSON_PARAMS);
                }else if(command.entityId().equals(LegalForm.ENTITY.getValue().longValue())){
                        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                                DeduplicationApiConstants.LEGAL_FORM_ENTITY_PARAMS);
                }else{
                        throw new ResourceNotFoundException();
                }

                final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
                final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                        .resource(DeduplicationApiConstants.RESOURCE_NAME);
                final JsonElement element = this.fromApiJsonHelper.parse(json);

                if(this.fromApiJsonHelper.parameterExists(DeduplicationApiConstants.firstnameExact, element)){
                        final Integer firstnameExact = this.fromApiJsonHelper
                                .extractIntegerSansLocaleNamed(DeduplicationApiConstants.firstnameExact, element);
                        baseDataValidator.reset().parameter(DeduplicationApiConstants.firstnameExact).value(firstnameExact)
                                .notNull().integerZeroOrGreater();
                }

                if(this.fromApiJsonHelper.parameterExists(DeduplicationApiConstants.firstnameLike, element)){
                        final Integer firstnameLike = this.fromApiJsonHelper
                                .extractIntegerSansLocaleNamed(DeduplicationApiConstants.firstnameLike, element);
                        baseDataValidator.reset().parameter(DeduplicationApiConstants.firstnameLike).value(firstnameLike)
                                .notNull().integerZeroOrGreater();
                }

                if(this.fromApiJsonHelper.parameterExists(DeduplicationApiConstants.middlenameExact, element)){
                        final Integer middlenameExact = this.fromApiJsonHelper
                                .extractIntegerSansLocaleNamed(DeduplicationApiConstants.middlenameExact, element);
                        baseDataValidator.reset().parameter(DeduplicationApiConstants.middlenameExact).value(middlenameExact)
                                .notNull().integerZeroOrGreater();
                }

                if(this.fromApiJsonHelper.parameterExists(DeduplicationApiConstants.middlenameLike, element)){
                        final Integer middlenameLike = this.fromApiJsonHelper
                                .extractIntegerSansLocaleNamed(DeduplicationApiConstants.middlenameLike, element);
                        baseDataValidator.reset().parameter(DeduplicationApiConstants.middlenameLike).value(middlenameLike)
                                .notNull().integerZeroOrGreater();
                }

                if(this.fromApiJsonHelper.parameterExists(DeduplicationApiConstants.lastnameExact, element)){
                        final Integer lastnameExact = this.fromApiJsonHelper
                                .extractIntegerSansLocaleNamed(DeduplicationApiConstants.lastnameExact, element);
                        baseDataValidator.reset().parameter(DeduplicationApiConstants.lastnameExact).value(lastnameExact)
                                .notNull().integerZeroOrGreater();
                }

                if(this.fromApiJsonHelper.parameterExists(DeduplicationApiConstants.lastnameLike, element)){
                        final Integer lastnameLike = this.fromApiJsonHelper
                                .extractIntegerSansLocaleNamed(DeduplicationApiConstants.lastnameLike, element);
                        baseDataValidator.reset().parameter(DeduplicationApiConstants.lastnameLike).value(lastnameLike)
                                .notNull().integerZeroOrGreater();
                }

                if(this.fromApiJsonHelper.parameterExists(DeduplicationApiConstants.fullnameExact, element)){
                        final Integer fullnameExact = this.fromApiJsonHelper
                                .extractIntegerSansLocaleNamed(DeduplicationApiConstants.fullnameExact, element);
                        baseDataValidator.reset().parameter(DeduplicationApiConstants.fullnameExact).value(fullnameExact)
                                .notNull().integerZeroOrGreater();
                }

                if(this.fromApiJsonHelper.parameterExists(DeduplicationApiConstants.fullnameLike, element)){
                        final Integer fullnameLike = this.fromApiJsonHelper
                                .extractIntegerSansLocaleNamed(DeduplicationApiConstants.fullnameLike, element);
                        baseDataValidator.reset().parameter(DeduplicationApiConstants.fullnameLike).value(fullnameLike)
                                .notNull().integerZeroOrGreater();
                }

                if(this.fromApiJsonHelper.parameterExists(DeduplicationApiConstants.mobileNo, element)){
                        final Integer mobileNo = this.fromApiJsonHelper
                                .extractIntegerSansLocaleNamed(DeduplicationApiConstants.mobileNo, element);
                        baseDataValidator.reset().parameter(DeduplicationApiConstants.mobileNo).value(mobileNo)
                                .notNull().integerZeroOrGreater();
                }

                if(this.fromApiJsonHelper.parameterExists(DeduplicationApiConstants.dateOfBirth, element)){
                        final Integer dateOfBirth = this.fromApiJsonHelper
                                .extractIntegerSansLocaleNamed(DeduplicationApiConstants.dateOfBirth, element);
                        baseDataValidator.reset().parameter(DeduplicationApiConstants.dateOfBirth).value(dateOfBirth)
                                .notNull().integerZeroOrGreater();
                }

                if(this.fromApiJsonHelper.parameterExists(DeduplicationApiConstants.genderCvId, element)){
                        final Integer genderCvId = this.fromApiJsonHelper
                                .extractIntegerSansLocaleNamed(DeduplicationApiConstants.genderCvId, element);
                        baseDataValidator.reset().parameter(DeduplicationApiConstants.genderCvId).value(genderCvId)
                                .notNull().integerZeroOrGreater();
                }

                if(this.fromApiJsonHelper.parameterExists(DeduplicationApiConstants.incorpNo, element)){
                        final Integer incorpNo = this.fromApiJsonHelper
                                .extractIntegerSansLocaleNamed(DeduplicationApiConstants.incorpNo, element);
                        baseDataValidator.reset().parameter(DeduplicationApiConstants.incorpNo).value(incorpNo)
                                .notNull().integerZeroOrGreater();
                }

                if(this.fromApiJsonHelper.parameterExists(DeduplicationApiConstants.clientIdentifier, element)){
                        final Integer clientIdentifier = this.fromApiJsonHelper
                                .extractIntegerSansLocaleNamed(DeduplicationApiConstants.clientIdentifier, element);
                        baseDataValidator.reset().parameter(DeduplicationApiConstants.clientIdentifier).value(clientIdentifier)
                                .notNull().integerZeroOrGreater();
                }

                throwExceptionIfValidationWarningsExist(dataValidationErrors);

        }

        private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
                if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                        "Validation errors exist.", dataValidationErrors); }
        }
}
