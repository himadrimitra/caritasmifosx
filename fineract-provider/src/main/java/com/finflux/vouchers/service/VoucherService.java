package com.finflux.vouchers.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.accounting.glaccount.domain.GLAccountUsage;
import org.apache.fineract.accounting.glaccount.service.GLAccountReadPlatformService;
import org.apache.fineract.accounting.journalentry.data.JournalEntryAssociationParametersData;
import org.apache.fineract.accounting.journalentry.domain.JournalEntry;
import org.apache.fineract.accounting.journalentry.domain.JournalEntryDetail;
import org.apache.fineract.accounting.journalentry.domain.JournalEntryRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetailRepository;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.paymenttype.service.PaymentTypeReadPlatformService;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.vouchers.data.VoucherTemplateData;
import com.finflux.vouchers.domain.Voucher;
import com.finflux.vouchers.domain.VoucherRepositoryWrapper;
import com.finflux.vouchers.exception.VoucherCannotBeReversedException;
import com.finflux.vouchers.service.impl.DefaultVoucherDataSerializer;

public abstract class VoucherService {

    private final String key;
    private final VoucherRepositoryWrapper voucherRepostiroyWrapper;
    private final PaymentDetailRepository paymentDetailsRepositoryWrapper;
    private final GLAccountReadPlatformService glAccountReadPlatformService;
    private final PaymentTypeReadPlatformService paymentTypeReadPlatformService;
    private final PlatformSecurityContext context;
    private final JournalEntryRepositoryWrapper  journalEntryRepositoryWrapper ;
    
    protected VoucherService(final String key, final VoucherRepositoryWrapper voucherRepostiroyWrapper,
            final PaymentDetailRepository paymentDetailsRepositoryWrapper, final GLAccountReadPlatformService glAccountReadPlatformService,
            final PaymentTypeReadPlatformService paymentTypeReadPlatformService,
            final PlatformSecurityContext context, final JournalEntryRepositoryWrapper  journalEntryRepositoryWrapper) {
        this.key = key;
        this.voucherRepostiroyWrapper = voucherRepostiroyWrapper;
        this.paymentDetailsRepositoryWrapper = paymentDetailsRepositoryWrapper;
        this.glAccountReadPlatformService = glAccountReadPlatformService;
        this.paymentTypeReadPlatformService = paymentTypeReadPlatformService;
        this.journalEntryRepositoryWrapper = journalEntryRepositoryWrapper ;
        this.context = context ;
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
            fromVoucher.setRelatedVoucherId(toVoucher.getId());
            this.voucherRepostiroyWrapper.save(toVoucher);
        }
        return buildCreateResponse(vouchers);
    }

    public CommandProcessingResult reverseVoucher(final Voucher voucher, final JsonCommand command) {
        String reversalComment = command.stringValueOfParameterNamed("reverseComments");
        validateRelatedVoucher(voucher) ;
        reverseVoucher(voucher, reversalComment);
        if(voucher.getRelatedVoucherId() != null) {
            final Voucher relatedVoucher = this.voucherRepostiroyWrapper.findVoucher(voucher.getRelatedVoucherId()) ;
            reverseVoucher(relatedVoucher, reversalComment); 
        }
        return buildReverseResponse(voucher) ;
    }
    
    protected void reverseVoucher(final Voucher voucher, final String comments) {
        final JournalEntry journalEntry = voucher.getJournalEntry() ;
        final JournalEntry reversalJournalEntryDetail = createReversalEntry(journalEntry, comments) ;
        this.journalEntryRepositoryWrapper.save(reversalJournalEntryDetail);
        journalEntry.setReversalJournalEntry(reversalJournalEntryDetail);
        this.journalEntryRepositoryWrapper.save(journalEntry);
    }
    protected void validateRelatedVoucher(final Voucher voucher) {
        final Long relatedVoucherId = voucher.getRelatedVoucherId() ;
        if(relatedVoucherId == null && this.voucherRepostiroyWrapper.findVoucherByRelatedVoucherId(voucher.getId()) != null) {
            throw new VoucherCannotBeReversedException("error.msg.vouchers.voucher.reserse.should.be.initiated.from.initiatator", voucher.getId()) ;
        }
    }
    
    protected JournalEntry createReversalEntry(final JournalEntry journalEntry, final String comments) {
        final boolean manualEntry = true;
        JournalEntry reversalJournalEntryDetail = JournalEntry.createNew(journalEntry.getOfficeId(), journalEntry.getPaymentDetailId(),
                journalEntry.getCurrencyCode(), generateTransactionId(journalEntry.getOfficeId()), manualEntry, journalEntry.getTransactionDate(),
                journalEntry.getValueDate(), journalEntry.getEffectiveDate(), comments, journalEntry.getEntityType(),
                journalEntry.getEntityId(), journalEntry.getReferenceNumber(), journalEntry.getEntityTransactionId());
        List<JournalEntryDetail> journalEntryDetails = journalEntry.getJournalEntryDetails();
        for (final JournalEntryDetail journalEntryDetail : journalEntryDetails) {
            JournalEntryDetail reversalJournalEntry = journalEntryDetail.reversalJournalEntry();
            reversalJournalEntryDetail.addJournalEntryDetail(reversalJournalEntry);
        }
        return reversalJournalEntryDetail ;
    }
    
    private String generateTransactionId(final Long officeId) {
        final AppUser user = this.context.authenticatedUser();
        final Long time = System.currentTimeMillis();
        final String uniqueVal = String.valueOf(time) + user.getId() + officeId;
        final String transactionId = Long.toHexString(Long.parseLong(uniqueVal));
        return transactionId;
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
                .withResourceIdAsString(voucher.get(0).getVoucherNumber()) 
                .withTransactionId(voucher.get(0).getTransactionId()) //
                .withSubEntityId(new Long(voucher.get(0).getVoucherType())) //
                .build();
    }

    public CommandProcessingResult buildReverseResponse(Voucher voucher) {
        return new CommandProcessingResultBuilder() //
                .withEntityId(voucher.getId()) //
                .withResourceIdAsString(voucher.getVoucherNumber()) 
                .withTransactionId(voucher.getTransactionId()) //
                .withSubEntityId(new Long(voucher.getVoucherType())) //
                .build();
    }
    
    public abstract VoucherTemplateData getTemplate();

    public List<GLAccountData> retrieveAllGLAccounts(final Integer glClassificationType) {
        final Integer accountClassification = null;
        final String searchParam = null;
        final Integer usage = GLAccountUsage.DETAIL.getValue() ;
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
