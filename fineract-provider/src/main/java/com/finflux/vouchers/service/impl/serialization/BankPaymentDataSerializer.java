package com.finflux.vouchers.service.impl.serialization;

import java.util.ArrayList;
import java.util.List;

import org.apache.fineract.accounting.closure.domain.GLClosureRepository;
import org.apache.fineract.accounting.financialactivityaccount.domain.FinancialActivityAccountRepositoryWrapper;
import org.apache.fineract.accounting.glaccount.domain.GLAccountRepositoryWrapper;
import org.apache.fineract.accounting.journalentry.domain.JournalEntry;
import org.apache.fineract.infrastructure.configuration.domain.GlobalConfigurationRepositoryWrapper;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.office.domain.OfficeRepositoryWrapper;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetailRepository;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentTypeRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.vouchers.constants.VoucherType;
import com.finflux.vouchers.domain.Voucher;
import com.finflux.vouchers.domain.VoucherRepositoryWrapper;
import com.finflux.vouchers.service.impl.DefaultVoucherDataSerializer;

@Component
public class BankPaymentDataSerializer extends DefaultVoucherDataSerializer {

    private final PaymentDetailRepository paymentDetailRepository;

    private final static String VOUCHER_PREFIX = "BP" ;
    
    @Autowired
    public BankPaymentDataSerializer(final FromJsonHelper fromApiJsonHelper, final GLAccountRepositoryWrapper glAccountRepositoryWrapper,
            final PlatformSecurityContext context, final VoucherRepositoryWrapper voucherRepository,
            final PaymentTypeRepositoryWrapper paymentTyperepositoryWrapper, final PaymentDetailRepository paymentDetailRepository, final GLClosureRepository glClosureRepository,
            final FinancialActivityAccountRepositoryWrapper financialActivityAccountRepository,
            final OfficeRepositoryWrapper officeRepositoryWrapper, final GlobalConfigurationRepositoryWrapper globalConfigurationRepositoryWrapper) {
        super(fromApiJsonHelper, glAccountRepositoryWrapper, context, voucherRepository, paymentTyperepositoryWrapper,
                glClosureRepository, financialActivityAccountRepository, officeRepositoryWrapper, globalConfigurationRepositoryWrapper);
        this.paymentDetailRepository = paymentDetailRepository;
    }

    @Override
    public List<Voucher> validateAndCreateVouchers(final String json) {
        JournalEntry entry = retrieveJournalEntry(json, supportedParameters_withPaymentDetails);
        PaymentDetail paymentDetail = retrievePaymentDetails(json);
        paymentDetail = this.paymentDetailRepository.save(paymentDetail);
        entry.setPaymentDetailId(paymentDetail.getId());
        final String voucherNumber = generateVoucherNumber(VoucherType.BANK_PAYMENT.getValue(), entry, false);
        final Long relatedVoucherId = null ;
        Voucher voucher = new Voucher(VoucherType.BANK_PAYMENT.getValue(), voucherNumber, entry, getJournalEntryAmount(entry), getCurrentFinancialYear(entry.getTransactionDate()), relatedVoucherId);
        List<Voucher> vouchers = new ArrayList<>() ;
        vouchers.add(voucher) ;
        return vouchers ;
    }

    @Override
    protected String getVoucherPrefixKey() {
        return VOUCHER_PREFIX ;
    }

}
