package com.finflux.vouchers.service.impl.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.accounting.glaccount.domain.GLClassificationType;
import org.apache.fineract.accounting.glaccount.service.GLAccountReadPlatformService;
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
import com.finflux.vouchers.service.impl.serialization.ContraEntryDataSerializer;

@Service
public class ContraVoucherService extends VoucherService {

    static final String key = VoucherType.CONTRA_ENTRY.getCode();

    private final ContraEntryDataSerializer contraEntryDataSerializer;

    @Autowired
    public ContraVoucherService(final ContraEntryDataSerializer contraEntryDataSerializer,
            final VoucherRepositoryWrapper voucherRepostiroyWrapper, final PaymentDetailRepository paymentDetailsRepository,
            final GLAccountReadPlatformService glAccountReadPlatformService,
            final PaymentTypeReadPlatformService paymentTypeReadPlatformService) {
        super(key, voucherRepostiroyWrapper, paymentDetailsRepository, glAccountReadPlatformService, paymentTypeReadPlatformService);
        this.contraEntryDataSerializer = contraEntryDataSerializer;
    }

    @Override
    public DefaultVoucherDataSerializer getVoucherDataSerializer() {
        return this.contraEntryDataSerializer;
    }

    @Override
    public VoucherTemplateData getTemplate() {
        List<GLAccountData> creditAccountingOptions = new ArrayList<>();
        creditAccountingOptions.addAll(retrieveAllGLAccounts(GLClassificationType.Cash.getValue()));
        creditAccountingOptions.addAll(retrieveAllGLAccounts(GLClassificationType.Bank.getValue()));
        List<GLAccountData> debitAccountingOptions = creditAccountingOptions;
        Collection<PaymentTypeData> paymentOptions  = retrieveAllPaymentTypes() ;
        return VoucherTemplateData.accountOptions(debitAccountingOptions, creditAccountingOptions, paymentOptions) ;
    }

}
