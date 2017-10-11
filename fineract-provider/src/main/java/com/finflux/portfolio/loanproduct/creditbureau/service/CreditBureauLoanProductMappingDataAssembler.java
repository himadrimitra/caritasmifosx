package com.finflux.portfolio.loanproduct.creditbureau.service;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.office.domain.OfficeRepository;
import org.apache.fineract.organisation.office.exception.OfficeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.portfolio.loanproduct.creditbureau.domain.CreditBureauLoanProductMapping;
import com.finflux.portfolio.loanproduct.creditbureau.domain.CreditBureauLoanProductOfficeMapping;
import com.finflux.portfolio.loanproduct.creditbureau.domain.CreditBureauLoanProductOfficeMappingRepository;
import com.finflux.risk.creditbureau.configuration.domain.CreditBureauProduct;
import com.finflux.risk.creditbureau.configuration.domain.CreditBureauProductRepositoryWrapper;
import com.finflux.risk.creditbureau.provider.api.CreditBureauApiConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Component
public class CreditBureauLoanProductMappingDataAssembler {

    private final FromJsonHelper fromApiJsonHelper;
    private final CreditBureauProductRepositoryWrapper creditBureauProductRepository;
    private final OfficeRepository officeRepository;
    private final CreditBureauLoanProductOfficeMappingRepository creditBureauLoanProductOfficeMappingRepository;

    @Autowired
    public CreditBureauLoanProductMappingDataAssembler(final FromJsonHelper fromApiJsonHelper,
            final CreditBureauProductRepositoryWrapper creditBureauProductRepository, final OfficeRepository officeRepository,
            final CreditBureauLoanProductOfficeMappingRepository creditBureauLoanProductOfficeMappingRepository) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.creditBureauProductRepository = creditBureauProductRepository;
        this.officeRepository = officeRepository;
        this.creditBureauLoanProductOfficeMappingRepository = creditBureauLoanProductOfficeMappingRepository;
    }

    public CreditBureauLoanProductMapping assembleCreateForm(final JsonCommand command) {
        final JsonElement element = command.parsedJson();
        final JsonObject topLevelJsonElement = element.getAsJsonObject();
        
        final Long creditBureauProductId = this.fromApiJsonHelper.extractLongNamed(CreditBureauApiConstants.CREDIT_BUREAU_PRODUCTID, element);
        final CreditBureauProduct creditBureauProduct = this.creditBureauProductRepository.findOneWithNotFoundDetection(creditBureauProductId);
        final Long loanProductId = this.fromApiJsonHelper.extractLongNamed(CreditBureauApiConstants.LOAN_PRODUCTID, element);
        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);
        final Boolean isCreditcheckMandatory = this.fromApiJsonHelper.extractBooleanNamed(CreditBureauApiConstants.IS_CREDIT_CHECK_MANDATORY, element);
        final Boolean skipCreditcheckInFailure = this.fromApiJsonHelper.extractBooleanNamed(CreditBureauApiConstants.SKIP_CREDIT_CHECK_IN_FAILURE, element);
        final Integer stalePeriod = this.fromApiJsonHelper.extractIntegerNamed(CreditBureauApiConstants.STALE_PERIOD, element, locale);
        Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(CreditBureauApiConstants.IS_ACTIVE, element);
        if (isActive == null) {
            isActive = false;
        }
        
        final CreditBureauLoanProductMapping creditBureauLoanProductMapping = CreditBureauLoanProductMapping.create(creditBureauProduct,
                isCreditcheckMandatory, skipCreditcheckInFailure, stalePeriod, isActive);

        JsonArray offices = command.arrayOfParameterNamed(CreditBureauApiConstants.OFFICES);
        Set<CreditBureauLoanProductOfficeMapping> mappingList = new HashSet<>();
        if (offices != null && offices.size() > 0) {
            for (JsonElement officeId : offices) {
                final Office office = this.officeRepository.findOne(officeId.getAsLong());
                if (office == null) { throw new OfficeNotFoundException(officeId.getAsLong()); }
                mappingList.add(
                        CreditBureauLoanProductOfficeMapping.create(creditBureauLoanProductMapping, loanProductId, officeId.getAsLong()));
            }
        } else {
            Long defaultOffice = null;
            mappingList.add(CreditBureauLoanProductOfficeMapping.create(creditBureauLoanProductMapping, loanProductId, defaultOffice));
        }
        creditBureauLoanProductMapping.setOffices(mappingList);
        return creditBureauLoanProductMapping;
    }

    public Map<String, Object> assembleUpdateForm(final CreditBureauLoanProductMapping creditBureauLoanProductMapping,
            final JsonCommand command) {
        final JsonElement element = command.parsedJson();
        final Map<String, Object> actualChanges = creditBureauLoanProductMapping.update(command);
        if (actualChanges.containsKey(CreditBureauApiConstants.CREDIT_BUREAU_PRODUCTID)) {
            final Long creditBureauProductId = this.fromApiJsonHelper.extractLongNamed(CreditBureauApiConstants.CREDIT_BUREAU_PRODUCTID, element);
            final CreditBureauProduct creditBureauProduct = this.creditBureauProductRepository.findOneWithNotFoundDetection(creditBureauProductId);
            creditBureauLoanProductMapping.updateCreditBureauProduct(creditBureauProduct);
        }
        final Long loanProductId = this.fromApiJsonHelper.extractLongNamed("loanProductId", element);
        if (actualChanges.containsKey(CreditBureauApiConstants.OFFICES)) {
            final String[] officeIds = (String[]) actualChanges.get(CreditBureauApiConstants.OFFICES);

            Set<CreditBureauLoanProductOfficeMapping> mappingList = new HashSet<>();
            if (officeIds.length > 0) {
                for (String officeId : officeIds) {

                    final Office office = this.officeRepository.findOne(Long.parseLong(officeId));
                    if (office == null) { throw new OfficeNotFoundException(Long.parseLong(officeId)); }

                    final CreditBureauLoanProductOfficeMapping creditBureauLoanProductOfficeMapping = this.creditBureauLoanProductOfficeMappingRepository
                            .findByLoanProductIdAndOfficeId(loanProductId, Long.valueOf(officeId));
                    if (creditBureauLoanProductOfficeMapping == null) {
                        mappingList.add(CreditBureauLoanProductOfficeMapping.create(creditBureauLoanProductMapping, loanProductId,
                                Long.parseLong(officeId)));
                    } else {
                        if (creditBureauLoanProductMapping.getId() != creditBureauLoanProductOfficeMapping
                                .getCreditBureauLoanProductMapping().getId()) {

                        throw new GeneralPlatformDomainRuleException(CreditBureauApiConstants.LOAN_PRODUCT_AND_OFFICE_COMBINATION,
                                "Loan Product and Office combination exists!!", office.getName()); }
                        mappingList.add(creditBureauLoanProductOfficeMapping);

                    }

                }
            }

            else {
                Long defaultOffice = null;
                mappingList.add(CreditBureauLoanProductOfficeMapping.create(creditBureauLoanProductMapping, loanProductId, defaultOffice));
            }
            creditBureauLoanProductMapping.updateOffices(mappingList);
        }

        return actualChanges;
    }
}