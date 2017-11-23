package com.finflux.portfolio.investmenttracker.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.fineract.accounting.producttoaccountmapping.service.ProductToGLAccountMappingWritePlatformService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.portfolio.charge.domain.Charge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.investmenttracker.Exception.InvestmentProductNotFoundException;
import com.finflux.portfolio.investmenttracker.api.InvestmentProductApiconstants;
import com.finflux.portfolio.investmenttracker.data.InvestmentProductDataValidator;
import com.finflux.portfolio.investmenttracker.domain.InvestmentProduct;
import com.finflux.portfolio.investmenttracker.domain.InvestmentProductDataAssembler;
import com.finflux.portfolio.investmenttracker.domain.InvestmentProductRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Service
public class InvestmentProductWriteServiceImpl implements InvestmentProductWriteService {

    private final Logger logger;
    private final InvestmentProductDataValidator fromApiJsonDataValidator;
    private final InvestmentProductDataAssembler investmentProductDataAssembler;
    private final InvestmentProductRepository investmentProductRepository;
    private final ProductToGLAccountMappingWritePlatformService accountMappingWritePlatformService;

    @Autowired
    public InvestmentProductWriteServiceImpl(final InvestmentProductDataValidator fromApiJsonDataValidator,
            final InvestmentProductDataAssembler investmentProductDataAssembler,
            final InvestmentProductRepository investmentProductRepository,
            final ProductToGLAccountMappingWritePlatformService accountMappingWritePlatformService) {
        this.fromApiJsonDataValidator = fromApiJsonDataValidator;
        this.investmentProductDataAssembler = investmentProductDataAssembler;
        this.investmentProductRepository = investmentProductRepository;
        this.accountMappingWritePlatformService = accountMappingWritePlatformService;
        this.logger = LoggerFactory.getLogger(InvestmentProductWriteServiceImpl.class);
    }

    @Override
    public CommandProcessingResult createInvestmentProduct(JsonCommand command) {
        try {
            this.fromApiJsonDataValidator.validateForCreate(command.json());

            final InvestmentProduct product = this.investmentProductDataAssembler.createAssemble(command);

            this.investmentProductRepository.save(product);

            // save accounting mappings
            this.accountMappingWritePlatformService.createInvestmentProductToGLAccountMapping(product.getId(), command);

            return new CommandProcessingResultBuilder() //
                    .withEntityId(product.getId()) //
                    .build();
        } catch (final DataAccessException e) {
            handleDataIntegrityIssues(command, e);
            return CommandProcessingResult.empty();
        }
    }

    private void handleDataIntegrityIssues(final JsonCommand command, final DataAccessException dae) {

        final Throwable realCause = dae.getMostSpecificCause();
        if (realCause.getMessage().contains("unq_name")) {

            final String name = command.stringValueOfParameterNamed("name");
            throw new PlatformDataIntegrityException("error.msg.product.investment.duplicate.name", "Investment product with name `" + name
                    + "` already exists", "name", name);
        } else if (realCause.getMessage().contains("unq_short_name")) {

            final String shortName = command.stringValueOfParameterNamed("shortName");
            throw new PlatformDataIntegrityException("error.msg.product.investment.duplicate.short.name",
                    "Investment product with short name `" + shortName + "` already exists", "shortName", shortName);
        }

        logAsErrorUnexpectedDataIntegrityException(dae);
        throw new PlatformDataIntegrityException("error.msg.investmentproduct.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    private void logAsErrorUnexpectedDataIntegrityException(final DataAccessException dae) {
        this.logger.error(dae.getMessage(), dae);
    }

    @Override
    public CommandProcessingResult updateInvestmentProduct(Long investmentProductId, JsonCommand command) {
        try {
            final InvestmentProduct product = this.investmentProductRepository.findOne(investmentProductId);
            if (product == null) { throw new InvestmentProductNotFoundException(investmentProductId); }

            this.fromApiJsonDataValidator.validateForUpdate(command.json());
            final Collection<Long> requestedChargeIds = extractChargeIds(command);

            final Map<String, Object> changes = product.update(command);

            if (changes.containsKey(InvestmentProductApiconstants.chargesParamName)) {
                final Set<Charge> investmentProductCharges = this.investmentProductDataAssembler.assembleListOfInvestmentProductCharges(
                        command, product.currency().getCode());
                final boolean updated = product.update(investmentProductCharges);
                if (!updated) {
                    changes.remove(InvestmentProductApiconstants.chargesParamName);
                }
            }

            // accounting related changes
            final boolean accountingTypeChanged = changes.containsKey(InvestmentProductApiconstants.accountingTypeParamName);
            final Map<String, Object> accountingMappingChanges = this.accountMappingWritePlatformService
                    .updateInvestmentProductToGLAccountMapping(product.getId(), command, accountingTypeChanged, product.getAccountingType());
            changes.putAll(accountingMappingChanges);

            if (!changes.isEmpty()) {
                this.investmentProductRepository.saveAndFlush(product);
            }

            return new CommandProcessingResultBuilder() //
                    .withEntityId(product.getId()) //
                    .with(changes).build();
        } catch (final DataAccessException e) {
            handleDataIntegrityIssues(command, e);
            return CommandProcessingResult.empty();
        }

    }

    private Collection<Long> extractChargeIds(final JsonCommand command) {
        final Collection<Long> chargeIds = new ArrayList<>();
        final JsonArray charges = command.arrayOfParameterNamed("charges");
        if (null != charges && charges.size() > 0) {
            for (final JsonElement charge : charges) {
                chargeIds.add(charge.getAsJsonObject().get("id").getAsLong());
            }
        }
        return chargeIds;
    }
}
