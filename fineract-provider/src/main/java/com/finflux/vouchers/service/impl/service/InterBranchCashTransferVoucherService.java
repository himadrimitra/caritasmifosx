package com.finflux.vouchers.service.impl.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.accounting.glaccount.domain.GLClassificationType;
import org.apache.fineract.accounting.glaccount.service.GLAccountReadPlatformService;
import org.apache.fineract.accounting.journalentry.domain.JournalEntryRepositoryWrapper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.office.domain.OfficeRepositoryWrapper;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetailRepository;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.paymenttype.service.PaymentTypeReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.vouchers.constants.VoucherType;
import com.finflux.vouchers.data.VoucherTemplateData;
import com.finflux.vouchers.domain.Voucher;
import com.finflux.vouchers.domain.VoucherRepositoryWrapper;
import com.finflux.vouchers.exception.VoucherUpdateNotSupportedException;
import com.finflux.vouchers.service.VoucherService;
import com.finflux.vouchers.service.impl.DefaultVoucherDataSerializer;
import com.finflux.vouchers.service.impl.serialization.InterBranchCashTransferVoucherDataSerializer;

@Service
public class InterBranchCashTransferVoucherService extends VoucherService {

    static final String key = VoucherType.INTER_BRANCH_CASH_TRANSFER.getCode();

    private final InterBranchCashTransferVoucherDataSerializer interBranchCashTransferVoucherDataSerializer;
    private final OfficeRepositoryWrapper officeRepository;

    @Autowired
    public InterBranchCashTransferVoucherService(
            final InterBranchCashTransferVoucherDataSerializer interBranchCashTransferVoucherDataSerializer,
            final VoucherRepositoryWrapper voucherRepostiroyWrapper, final OfficeRepositoryWrapper officeRepository,
            final PaymentDetailRepository paymentDetailsRepositoryWrapper,
            final GLAccountReadPlatformService glAccountReadPlatformService,
            final PaymentTypeReadPlatformService paymentTypeReadPlatformService,
            final PlatformSecurityContext context, final JournalEntryRepositoryWrapper  journalEntryRepositoryWrapper) {
        super(key, voucherRepostiroyWrapper, paymentDetailsRepositoryWrapper, glAccountReadPlatformService, paymentTypeReadPlatformService,
                context, journalEntryRepositoryWrapper);
        this.interBranchCashTransferVoucherDataSerializer = interBranchCashTransferVoucherDataSerializer;
        this.officeRepository = officeRepository;
    }

    @SuppressWarnings("unused")
    @Override
    public CommandProcessingResult updateVoucher(Long voucherId, String apiDataAsJson) {
        throw new VoucherUpdateNotSupportedException(VoucherType.INTER_BRANCH_CASH_TRANSFER.getCode()) ;
    }

    @Override
    public CommandProcessingResult buildCreateResponse(List<Voucher> vouchers) {
        return new CommandProcessingResultBuilder() //
                .withEntityId(vouchers.get(0).getId())//
                .with(constructChanges(vouchers)).build();
    }
    
    private Map<String, Object> constructChanges(List<Voucher> vouchers) {
        final Map<String, Object> returnMap = new LinkedHashMap<>();
        final List<Map<String, String>> listMap = new ArrayList<>();
        for (Voucher voucher : vouchers) {
            Long officeId = voucher.getOfficeId();
            final Office office = this.officeRepository.findOneWithNotFoundDetection(officeId);
            Map<String, String> voucherMap = new LinkedHashMap<>();
            voucherMap.put("voucherId", voucher.getId().toString());
            voucherMap.put("voucherNumber", voucher.getVoucherNumber());
            voucherMap.put("officeId", officeId.toString());
            voucherMap.put("officeName", office.getName());
            listMap.add(voucherMap);
        }
        returnMap.put("vouchers", listMap);
        return returnMap;
    }

    @Override
    public DefaultVoucherDataSerializer getVoucherDataSerializer() {
        return this.interBranchCashTransferVoucherDataSerializer ;
    }

    @Override
    public VoucherTemplateData getTemplate() {
        List<GLAccountData> creditAccountingOptions = retrieveAllGLAccounts(GLClassificationType.Cash.getValue());
        List<GLAccountData> debitAccountingOptions = creditAccountingOptions;
        Collection<PaymentTypeData> paymentOptions  = null ;
        return VoucherTemplateData.accountOptions(debitAccountingOptions, creditAccountingOptions, paymentOptions) ;
    }
}
