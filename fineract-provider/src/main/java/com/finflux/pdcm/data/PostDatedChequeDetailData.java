package com.finflux.pdcm.data;

import java.math.BigDecimal;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.loanaccount.data.LoanAccountData;
import org.joda.time.LocalDate;

public class PostDatedChequeDetailData {

    private final Long id;
    private final String bankName;
    private final String branchName;
    private final String accountNumber;
    private final String ifscCode;
    private final BigDecimal chequeAmount;
    private final EnumOptionData chequeType;
    private final String chequeNumber;
    private final LocalDate chequeDate;
    private final EnumOptionData presentStatus;
    private final EnumOptionData previousStatus;
    private final LocalDate presentedDate;
    private final LocalDate bouncedDate;
    private final LocalDate clearedDate;
    private final LocalDate cancelledDate;
    private final LocalDate returnedDate;
    private PostDatedChequeDetailMappingData mappingData;
    private final String officeName;
    private final String clientName;
    private final LoanAccountData loanAccountData;

    private PostDatedChequeDetailData(final Long id, final String bankName, final String branchName, final String accountNumber,
            final String ifscCode, final BigDecimal chequeAmount, final EnumOptionData chequeType, final String chequeNumber,
            final LocalDate chequeDate, final EnumOptionData presentStatus, final EnumOptionData previousStatus,
            final LocalDate presentedDate, final LocalDate bouncedDate, final LocalDate clearedDate, final LocalDate cancelledDate,
            final LocalDate returnedDate, final String officeName, final String clientName, final LoanAccountData loanAccountData) {
        this.id = id;
        this.bankName = bankName;
        this.branchName = branchName;
        this.accountNumber = accountNumber;
        this.ifscCode = ifscCode;
        this.chequeAmount = chequeAmount;
        this.chequeType = chequeType;
        this.chequeNumber = chequeNumber;
        this.chequeDate = chequeDate;
        this.presentStatus = presentStatus;
        this.previousStatus = previousStatus;
        this.presentedDate = presentedDate;
        this.bouncedDate = bouncedDate;
        this.clearedDate = clearedDate;
        this.cancelledDate = cancelledDate;
        this.returnedDate = returnedDate;
        this.officeName = officeName;
        this.clientName = clientName;
        this.loanAccountData = loanAccountData;
    }

    public static PostDatedChequeDetailData instance(final Long id, final String bankName, final String branchName,
            final String accountNumber, final String ifscCode, final BigDecimal chequeAmount, final EnumOptionData chequeType,
            final String chequeNumber, final LocalDate chequeDate, final EnumOptionData presentStatus, final EnumOptionData previousStatus,
            final LocalDate presentedDate, final LocalDate bouncedDate, final LocalDate clearedDate, final LocalDate cancelledDate,
            final LocalDate returnedDate, final LoanAccountData loanAccountData) {
        final String officeName = null;
        final String clientName = null;
        return new PostDatedChequeDetailData(id, bankName, branchName, accountNumber, ifscCode, chequeAmount, chequeType, chequeNumber,
                chequeDate, presentStatus, previousStatus, presentedDate, bouncedDate, clearedDate, cancelledDate, returnedDate,
                officeName, clientName, loanAccountData);
    }

    public static PostDatedChequeDetailData searchDataInstance(final Long id, final String bankName, final String branchName,
            final String accountNumber, final String ifscCode, final BigDecimal chequeAmount, final EnumOptionData chequeType,
            final String chequeNumber, final LocalDate chequeDate, final EnumOptionData presentStatus, final EnumOptionData previousStatus,
            final LocalDate presentedDate, final LocalDate chequeBouncedDate, final LocalDate clearedDate, final LocalDate cancelledDate,
            final LocalDate returnedDate, final String officeName, final String clientName, final LoanAccountData loanAccountData) {
        return new PostDatedChequeDetailData(id, bankName, branchName, accountNumber, ifscCode, chequeAmount, chequeType, chequeNumber,
                chequeDate, presentStatus, previousStatus, presentedDate, chequeBouncedDate, clearedDate, cancelledDate, returnedDate,
                officeName, clientName, loanAccountData);
    }

    public Long getId() {
        return this.id;
    }

    public String getBankName() {
        return this.bankName;
    }

    public String getBranchName() {
        return this.branchName;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public String getIfscCode() {
        return this.ifscCode;
    }

    public BigDecimal getChequeAmount() {
        return this.chequeAmount;
    }

    public EnumOptionData getChequeType() {
        return this.chequeType;
    }

    public String getChequeNumber() {
        return this.chequeNumber;
    }

    public LocalDate getChequeDate() {
        return this.chequeDate;
    }

    public EnumOptionData getPresentStatus() {
        return this.presentStatus;
    }

    public EnumOptionData getPreviousStatus() {
        return this.previousStatus;
    }

    public LocalDate getPresentedDate() {
        return this.presentedDate;
    }

    public LocalDate getBouncedDate() {
        return this.bouncedDate;
    }

    public LocalDate getClearedDate() {
        return this.clearedDate;
    }

    public LocalDate getCancelledDate() {
        return this.cancelledDate;
    }

    public LocalDate getReturnedDate() {
        return this.returnedDate;
    }

    public PostDatedChequeDetailMappingData getMappingData() {
        return this.mappingData;
    }

    public String getOfficeName() {
        return this.officeName;
    }

    public String getClientName() {
        return this.clientName;
    }

    public LoanAccountData getLoanAccountData() {
        return this.loanAccountData;
    }

    public void setMappingData(final PostDatedChequeDetailMappingData mappingData) {
        this.mappingData = mappingData;
    }

}