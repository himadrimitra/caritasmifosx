package com.finflux.reconcilation.bankstatement.data;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionEnumData;
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;
import org.joda.time.LocalDate;

import com.finflux.reconcilation.bank.data.BankData;

@SuppressWarnings("unused")
public class BankStatementDetailsData {

    private final Long id;
    private Long bankStatementId;
    private String transactionId;
    private Date transactionDate;
    private String description;
    private BigDecimal amount;
    private String mobileNumber;
    private String clientAccountNumber;
    private String loanAccountNumber;
    private final Boolean isReconciled;
    private final Long loanTransaction;

    private final Long loanTransactionId;
    private final Long officeId;
    private final String officeName;
    private final LoanTransactionEnumData type;
    private final LocalDate date;
    private final PaymentDetailData paymentDetailData;
    private final BigDecimal transactionAmount;
    private final String externalId;
    private final LocalDate submittedOnDate;
    final String loanAccountNo;

    private Long branch;
    private String glAccount;
    private String accountingType;
    private String glCode;
    private final String branchName;
    private final Boolean isJournalEntry;
    private final BankData bankData;
    private final String transactionType;
    private final String branchExternalId;
    private final String groupExternalId;

    public BankStatementDetailsData(final Long id, final Long bankStatementId, final String transactionId, final Date transactionDate,
            final String description, final BigDecimal amount, final String mobileNumber, final String clientAccountNumber,
            final String loanAccountNumber, final Boolean isReconciled, final Long loanTransaction,
            final Long branch, final String glAccount, final String accountingType, final String glCode, final Boolean isJournalEntry,
            final BankData bankData, final String branchName, final String transactionType, final String branchExternalId, final String groupExternalId) {
        this.id = id;
        this.bankStatementId = bankStatementId;
        this.transactionId = transactionId;
        this.transactionDate = transactionDate;
        this.description = description;
        this.amount = amount;
        this.mobileNumber = mobileNumber;
        this.clientAccountNumber = clientAccountNumber;
        this.loanAccountNumber = loanAccountNumber;
        this.isReconciled = isReconciled;
        this.loanTransaction = loanTransaction;
        this.loanTransactionId = 0l;
        this.transactionAmount = null;
        this.paymentDetailData = null;
        this.officeId = null;
        this.transactionDate = null;
        this.date = null;
        this.type = null;
        this.officeName = null;
        this.externalId = null;
        this.submittedOnDate = null;
        this.loanAccountNo = null;
        this.branch = branch;
        this.glAccount = glAccount;
        this.accountingType = accountingType;
        this.glCode = glCode;
        this.isJournalEntry = isJournalEntry;
        this.bankData = bankData;
        this.branchName = branchName;
        this.transactionType = transactionType;
        this.branchExternalId = branchExternalId;
        this.groupExternalId =groupExternalId;
    }

    public BankStatementDetailsData(final Long id, final Long bankStatementId, final String transactionId, final Date transactionDate,
            final String description, final BigDecimal amount, final String mobileNumber, final String clientAccountNumber,
            final String loanAccountNumber, final Boolean isReconciled, final Long loanTransaction,
            final Long loanTransactionId, final Long officeId, final String officeName, final LoanTransactionEnumData type,
            final LocalDate date, final PaymentDetailData paymentDetailData, final BigDecimal transactionAmount, final String externalId,
            final LocalDate submittedOnDate, final String loanAccountNo, final Long branch, final String glAccount,
            final String accountingType, final String glCode, final Boolean isJournalEntry, final BankData bankData,
            final String branchName, final String transactionType, final String branchExternalId, final String groupExternalId) {
        this.id = id;
        this.bankStatementId = bankStatementId;
        this.transactionId = transactionId;
        this.transactionDate = transactionDate;
        this.description = description;
        this.amount = amount;
        this.mobileNumber = mobileNumber;
        this.clientAccountNumber = clientAccountNumber;
        this.loanAccountNumber = loanAccountNumber;
        this.isReconciled = isReconciled;
        this.loanTransaction = loanTransaction;
        this.loanTransactionId = loanTransactionId;
        this.officeId = officeId;
        this.officeName = officeName;
        this.type = type;
        this.date = date;
        this.paymentDetailData = paymentDetailData;
        this.transactionAmount = transactionAmount;
        this.externalId = externalId;
        this.submittedOnDate = submittedOnDate;
        this.loanAccountNo = loanAccountNo;
        this.branch = branch;
        this.glAccount = glAccount;
        this.accountingType = accountingType;
        this.glCode = glCode;
        this.isJournalEntry = isJournalEntry;
        this.bankData = bankData;
        this.branchName = branchName;
        this.transactionType = transactionType;
        this.branchExternalId = branchExternalId;
        this.groupExternalId =groupExternalId;
    }

    public static BankStatementDetailsData instance(final Long id, final Long bankStatementId, final String transactionId,
            final Date transactionDate, final String description, final BigDecimal amount, final String mobileNumber,
            final String clientAccountNumber, final String loanAccountNumber, final Boolean isReconciled,
            final Long loanTransaction, final Long branch, final String glAccount, final String accountingType, final String glCode,
            final Boolean isJournalEntry, final BankData bankData, final String branchName, final String transactionType, final String branchExternalId, final String groupExternalId) {

        return new BankStatementDetailsData(id, bankStatementId, transactionId, transactionDate, description, amount, mobileNumber,
                clientAccountNumber, loanAccountNumber, isReconciled, loanTransaction, branch, glAccount,
                accountingType, glCode, isJournalEntry, bankData, branchName, transactionType, branchExternalId, groupExternalId);
    }

    public static BankStatementDetailsData instance(final Long id, final Long bankStatementId, final String transactionId,
            final Date transactionDate, final String description, final BigDecimal amount, final String mobileNumber,
            final String clientAccountNumber, final String loanAccountNumber, final Boolean isReconciled,
            final Long loanTransaction, final Long loanTransactionId, final Long officeId, final String officeName,
            final LoanTransactionEnumData type, final LocalDate date, final PaymentDetailData paymentDetailData,
            final BigDecimal transactionAmount, final String externalId, final LocalDate submittedOnDate, final String loanAccountNo,
            final Long branch, final String glAccount, final String accountingType, final String glCode, final Boolean isJournalEntry,
            final BankData bankData, final String branchName, final String transactionType, final String branchExternalId, final String groupExternalId) {

        return new BankStatementDetailsData(id, bankStatementId, transactionId, transactionDate, description, amount, mobileNumber,
                clientAccountNumber, loanAccountNumber, isReconciled, loanTransaction, loanTransactionId, officeId,
                officeName, type, date, paymentDetailData, transactionAmount, externalId, submittedOnDate, loanAccountNo, branch,
                glAccount, accountingType, glCode, isJournalEntry, bankData, branchName, transactionType, branchExternalId, groupExternalId);
    }

    public Long getId() {
        return id;
    }

    public Long getBankStatementId() {
        return bankStatementId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getClientAccountNumber() {
        return clientAccountNumber;
    }

    public String getLoanAccountNumber() {
        return loanAccountNumber;
    }

    public String getGroupExternalId() {
        return groupExternalId;
    }

    public Boolean getIsReconciled() {
        return isReconciled;
    }

    public Long getLoanTransaction() {
        return loanTransaction;
    }

    public Long getLoanTransactionId() {
        return loanTransactionId;
    }

    public Long getOfficeId() {
        return officeId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public LoanTransactionEnumData getType() {
        return type;
    }

    public LocalDate getDate() {
        return date;
    }

    public PaymentDetailData getPaymentDetailData() {
        return paymentDetailData;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public String getExternalId() {
        return externalId;
    }

    public LocalDate getSubmittedOnDate() {
        return submittedOnDate;
    }

    public String getLoanAccountNo() {
        return loanAccountNo;
    }

    public Long getBranch() {
        return branch;
    }

    public String getGlAccount() {
        return glAccount;
    }

    public String getAccountingType() {
        return accountingType;
    }

    public String getGlCode() {
        return glCode;
    }

    public Boolean getIsJournalEntry() {
        return isJournalEntry;
    }

    public String getBranchName() {
        return branchName;
    }

    public BankData getBankData() {
        return bankData;
    }

    public String getTransactionType() {
        return transactionType;
    }
    
    public String getBranchExternalId() {
        return branchExternalId;
    }

}
