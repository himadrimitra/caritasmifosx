package com.finflux.vouchers.service.impl.service;

import java.util.Collection;
import java.util.List;

import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.accounting.glaccount.domain.GLClassificationType;
import org.apache.fineract.accounting.glaccount.service.GLAccountReadPlatformService;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
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
import com.finflux.vouchers.service.impl.serialization.CashPaymentDataSerializer;

@Service
public class CashPaymentVoucherService extends VoucherService {

    static final String key = VoucherType.CASH_PAYMENT.getCode();

    private final CashPaymentDataSerializer cashPaymentDataSerializer;

    @Autowired
    public CashPaymentVoucherService(final CashPaymentDataSerializer cashPaymentDataSerializer,
            final VoucherRepositoryWrapper voucherRepostiroyWrapper, final PaymentDetailRepository paymentDetailsRepositoryWrapper,
            final GLAccountReadPlatformService glAccountReadPlatformService,
            final PaymentTypeReadPlatformService paymentTypeReadPlatformService) {
        super(key, voucherRepostiroyWrapper, paymentDetailsRepositoryWrapper, glAccountReadPlatformService, paymentTypeReadPlatformService);
        this.cashPaymentDataSerializer = cashPaymentDataSerializer;
    }

    @Override
    public DefaultVoucherDataSerializer getVoucherDataSerializer() {
        return this.cashPaymentDataSerializer;
    }

    @SuppressWarnings("unused")
    @Override
    public CommandProcessingResult updateVoucher(Long voucherId, String apiDataAsJson) {
        throw new RuntimeException("Voucher Update is not supported");
    }

    @Override
    public VoucherTemplateData getTemplate() {
        List<GLAccountData> debitAccountingOptions = retrieveAllGLAccounts(GLClassificationType.OtherJV.getValue()) ;
        List<GLAccountData> creditAccountingOptions = retrieveAllGLAccounts(GLClassificationType.Cash.getValue());
        Collection<PaymentTypeData> paymentOptions  = null ;
        return VoucherTemplateData.accountOptions(debitAccountingOptions, creditAccountingOptions, paymentOptions) ;
    }

}
