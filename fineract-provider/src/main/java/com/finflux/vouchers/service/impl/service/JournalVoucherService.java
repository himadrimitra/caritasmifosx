package com.finflux.vouchers.service.impl.service;

import java.util.Collection;
import java.util.List;

import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.accounting.glaccount.domain.GLClassificationType;
import org.apache.fineract.accounting.glaccount.service.GLAccountReadPlatformService;
import org.apache.fineract.accounting.journalentry.domain.JournalEntryRepositoryWrapper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetailRepository;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.paymenttype.service.PaymentTypeReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.vouchers.constants.VoucherType;
import com.finflux.vouchers.data.VoucherTemplateData;
import com.finflux.vouchers.domain.VoucherRepositoryWrapper;
import com.finflux.vouchers.exception.VoucherUpdateNotSupportedException;
import com.finflux.vouchers.service.VoucherService;
import com.finflux.vouchers.service.impl.DefaultVoucherDataSerializer;
import com.finflux.vouchers.service.impl.serialization.JournalVoucherDataSerializer;

@Service
public class JournalVoucherService extends VoucherService {

    static final String key = VoucherType.JV_ENTRY.getCode() ;

    private final JournalVoucherDataSerializer journalVoucherDataSerializer;

    @Autowired
    public JournalVoucherService(JournalVoucherDataSerializer journalVoucherDataSerializer,
            final VoucherRepositoryWrapper voucherRepostiroyWrapper, final PaymentDetailRepository paymentDetailsRepositoryWrapper,
            final GLAccountReadPlatformService glAccountReadPlatformService,
            final PaymentTypeReadPlatformService paymentTypeReadPlatformService,
            final PlatformSecurityContext context, final JournalEntryRepositoryWrapper  journalEntryRepositoryWrapper) {
        super(key, voucherRepostiroyWrapper, paymentDetailsRepositoryWrapper, glAccountReadPlatformService, paymentTypeReadPlatformService,
                context, journalEntryRepositoryWrapper);
        this.journalVoucherDataSerializer = journalVoucherDataSerializer;
    }

    @Override
    public DefaultVoucherDataSerializer getVoucherDataSerializer() {
        return this.journalVoucherDataSerializer ;
    }
    
    @SuppressWarnings("unused")
    @Override
    public CommandProcessingResult updateVoucher(Long voucherId, String apiDataAsJson) {
        throw new VoucherUpdateNotSupportedException(VoucherType.JV_ENTRY.getCode()) ;
    }

    @Override
    public VoucherTemplateData getTemplate() {
        List<GLAccountData> debitAccountingOptions = retrieveAllGLAccounts(GLClassificationType.OtherJV.getValue()) ;
        List<GLAccountData> creditAccountingOptions =debitAccountingOptions ;
        Collection<PaymentTypeData> paymentOptions  = null ;
        return VoucherTemplateData.accountOptions(debitAccountingOptions, creditAccountingOptions, paymentOptions) ;
    }

}
