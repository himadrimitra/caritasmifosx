package com.finflux.vouchers.service.impl.service;

import java.util.Collection;
import java.util.List;

import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.accounting.glaccount.domain.GLClassificationType;
import org.apache.fineract.accounting.glaccount.service.GLAccountReadPlatformService;
import org.apache.fineract.accounting.journalentry.domain.JournalEntryRepositoryWrapper;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetailRepository;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.paymenttype.service.PaymentTypeReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.vouchers.constants.VoucherType;
import com.finflux.vouchers.data.VoucherTemplateData;
import com.finflux.vouchers.domain.VoucherRepositoryWrapper;
import com.finflux.vouchers.service.VoucherService;
import com.finflux.vouchers.service.impl.DefaultVoucherDataSerializer;
import com.finflux.vouchers.service.impl.serialization.BankPaymentDataSerializer;

@Service
public class BankPaymentVoucherService extends VoucherService {

    static final String key = VoucherType.BANK_PAYMENT.getCode();

    private final BankPaymentDataSerializer bankPaymentDataSerializer;
    
    @Autowired
    public BankPaymentVoucherService(final BankPaymentDataSerializer bankPaymentDataSerializer,
            final VoucherRepositoryWrapper voucherRepostiroyWrapper, final PaymentDetailRepository paymentDetailsRepositoryWrapper,
            final GLAccountReadPlatformService glAccountReadPlatformService, final PaymentTypeReadPlatformService paymentTypeReadPlatformService,
            final PlatformSecurityContext context, final JournalEntryRepositoryWrapper  journalEntryRepositoryWrapper) {
        super(key, voucherRepostiroyWrapper, paymentDetailsRepositoryWrapper, glAccountReadPlatformService, paymentTypeReadPlatformService,
                context, journalEntryRepositoryWrapper);
        this.bankPaymentDataSerializer = bankPaymentDataSerializer;
    }

    @Override
    public DefaultVoucherDataSerializer getVoucherDataSerializer() {
        return this.bankPaymentDataSerializer ;
    }

    @Override
    public VoucherTemplateData getTemplate() {
        List<GLAccountData> debitAccountingOptions = retrieveAllGLAccounts(GLClassificationType.OtherJV.getValue()) ;
        List<GLAccountData> creditAccountingOptions = retrieveAllGLAccounts(GLClassificationType.Bank.getValue());
        Collection<PaymentTypeData> paymentOptions  = retrieveAllPaymentTypes() ;
        return VoucherTemplateData.accountOptions(debitAccountingOptions, creditAccountingOptions, paymentOptions) ;
    }

}
