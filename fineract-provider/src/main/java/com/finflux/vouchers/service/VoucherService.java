package com.finflux.vouchers.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.accounting.glaccount.service.GLAccountReadPlatformService;
import org.apache.fineract.accounting.journalentry.data.JournalEntryAssociationParametersData;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetailRepository;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.paymenttype.service.PaymentTypeReadPlatformService;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.vouchers.data.VoucherTemplateData;
import com.finflux.vouchers.domain.Voucher;
import com.finflux.vouchers.domain.VoucherRepositoryWrapper;
import com.finflux.vouchers.service.impl.DefaultVoucherDataSerializer;

public abstract class VoucherService {

    private final String key;
    private final VoucherRepositoryWrapper voucherRepostiroyWrapper;
    private final PaymentDetailRepository paymentDetailsRepositoryWrapper;
    private final GLAccountReadPlatformService glAccountReadPlatformService;
    private final PaymentTypeReadPlatformService paymentTypeReadPlatformService;

    protected VoucherService(final String key, final VoucherRepositoryWrapper voucherRepostiroyWrapper,
            final PaymentDetailRepository paymentDetailsRepositoryWrapper, final GLAccountReadPlatformService glAccountReadPlatformService,
            final PaymentTypeReadPlatformService paymentTypeReadPlatformService) {
        this.key = key;
        this.voucherRepostiroyWrapper = voucherRepostiroyWrapper;
        this.paymentDetailsRepositoryWrapper = paymentDetailsRepositoryWrapper;
        this.glAccountReadPlatformService = glAccountReadPlatformService;
        this.paymentTypeReadPlatformService = paymentTypeReadPlatformService;
    }

    public String getKey() {
        return this.key;
    }

    @Transactional
    public CommandProcessingResult createVoucher(String apiDataAsJson) {
        List<Voucher> vouchers = this.getVoucherDataSerializer().validateAndCreateVouchers(apiDataAsJson);
        this.voucherRepostiroyWrapper.save(vouchers);
        if (vouchers.size() > 1) {
            Voucher fromVoucher = vouchers.get(0);
            Voucher toVoucher = vouchers.get(1);
            toVoucher.setRelatedVoucherId(fromVoucher.getId());
            this.voucherRepostiroyWrapper.save(toVoucher);
        }
        return buildCreateResponse(vouchers);
    }

    public CommandProcessingResult updateVoucher(final Long voucherId, final String apiDataAsJson) {
        Voucher voucher = this.voucherRepostiroyWrapper.findVoucher(voucherId);
        final Long paymentDetailsId = voucher.getPaymentDetailsId();
        PaymentDetail paymentDetails = null;
        if (paymentDetailsId != null) {
            paymentDetails = this.paymentDetailsRepositoryWrapper.findOne(paymentDetailsId);
        }
        Map<String, Object> changes = this.getVoucherDataSerializer().updateVoucher(voucher, paymentDetails, apiDataAsJson);
        if (!changes.isEmpty()) {
            this.paymentDetailsRepositoryWrapper.save(paymentDetails);
        }
        return new CommandProcessingResultBuilder() //
                .withEntityId(voucher.getId()) //
                .with(changes) //
                .build();
    }

    public abstract DefaultVoucherDataSerializer getVoucherDataSerializer();

    public CommandProcessingResult buildCreateResponse(List<Voucher> voucher) {
        return new CommandProcessingResultBuilder() //
                .withEntityId(voucher.get(0).getId()) //
                .withTransactionId(voucher.get(0).getVoucherNumber()) //
                .withSubEntityId(new Long(voucher.get(0).getVoucherType())) //
                .build();
    }

    public abstract VoucherTemplateData getTemplate();

    public List<GLAccountData> retrieveAllGLAccounts(final Integer glClassificationType) {
        final Integer accountClassification = null;
        final String searchParam = null;
        final Integer usage = null;
        final Boolean manualTransactionsAllowed = null;
        final Boolean disabled = null;
        final boolean transactionDetailsRequired = false;
        final boolean runningBalanceRequired = true;
        final JournalEntryAssociationParametersData associationParametersData = new JournalEntryAssociationParametersData(
                transactionDetailsRequired, runningBalanceRequired);
        return this.glAccountReadPlatformService.retrieveAllGLAccounts(accountClassification, searchParam, usage, manualTransactionsAllowed,
                disabled, associationParametersData, glClassificationType);
    }

    public Collection<PaymentTypeData> retrieveAllPaymentTypes() {
        return this.paymentTypeReadPlatformService.retrieveAllPaymentTypes();
    }
}
