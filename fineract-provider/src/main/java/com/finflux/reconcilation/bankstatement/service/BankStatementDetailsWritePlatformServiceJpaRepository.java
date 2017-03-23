package com.finflux.reconcilation.bankstatement.service;

import java.util.Map;

import org.apache.fineract.accounting.glaccount.data.GLAccountDataForLookup;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.office.domain.OfficeRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.reconcilation.ReconciliationApiConstants;
import com.finflux.reconcilation.bankstatement.domain.BankStatementDetails;
import com.finflux.reconcilation.bankstatement.domain.BankStatementDetailsRepository;
import com.finflux.reconcilation.bankstatement.exception.BankStatementDetailNotFoundException;

@Service
public class BankStatementDetailsWritePlatformServiceJpaRepository implements BankStatementDetailsWritePlatformService {

    private final BankStatementDetailsRepository bankStatementDetailsRepository;
    private final OfficeRepositoryWrapper officeRepository;
    private final BankGLAccountReadPlatformService bankGLAccountReadPlatformService;

    @Autowired
    public BankStatementDetailsWritePlatformServiceJpaRepository(final BankStatementDetailsRepository bankStatementDetailsRepository,
            final OfficeRepositoryWrapper officeRepository,
            final BankGLAccountReadPlatformService bankGLAccountReadPlatformService) {
        this.bankStatementDetailsRepository = bankStatementDetailsRepository;
        this.officeRepository = officeRepository;
        this.bankGLAccountReadPlatformService = bankGLAccountReadPlatformService;
    }

    @Override
    public CommandProcessingResult updateBankStatementDetails(final Long bankStatementDetailId, final JsonCommand command) {

        BankStatementDetails bankStatementDetails = this.bankStatementDetailsRepository.findOne(bankStatementDetailId);
        if (bankStatementDetails == null) { throw new BankStatementDetailNotFoundException(bankStatementDetailId); }
        final Map<String, Object> changes = bankStatementDetails.update(command);
        if (changes.containsKey(ReconciliationApiConstants.officeIdParamName)) {
            final Long officeId = command.longValueOfParameterNamed(ReconciliationApiConstants.officeIdParamName);
            if (officeId != null) {
                Office office = officeRepository.findOneWithNotFoundDetection(officeId);
                bankStatementDetails.setBranchExternalId(office.getExternalId());
            }
        }
        
        if (changes.containsKey(ReconciliationApiConstants.glCodeParamName)) {
            final String glCode = command.stringValueOfParameterNamedAllowingNull(ReconciliationApiConstants.glCodeParamName);
            GLAccountDataForLookup GLAccount = this.bankGLAccountReadPlatformService.retrieveGLAccountByGLCode(glCode);
            changes.put(ReconciliationApiConstants.glAccountParamName , GLAccount.getName());
            bankStatementDetails.setGlAccount(GLAccount.getName());
        }

        if (!changes.isEmpty()) {
            //bankStatementDetails.setIsError(false);
            bankStatementDetailsRepository.save(bankStatementDetails);
        }
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(bankStatementDetailId) //
                .with(changes) //
                .build();

    }

}
