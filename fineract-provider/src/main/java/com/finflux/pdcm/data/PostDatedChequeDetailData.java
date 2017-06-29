package com.finflux.pdcm.data;

import java.math.BigDecimal;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
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
    private final String loanProductName;
    private final String loanAccountNumber;

    private PostDatedChequeDetailData(final Long id, final String bankName, final String branchName, final String accountNumber,
            final String ifscCode, final BigDecimal chequeAmount, final EnumOptionData chequeType, final String chequeNumber,
            final LocalDate chequeDate, final EnumOptionData presentStatus, final EnumOptionData previousStatus,
            final LocalDate presentedDate, final LocalDate bouncedDate, final LocalDate clearedDate, final LocalDate cancelledDate,
            final LocalDate returnedDate, final String officeName, final String clientName, final String loanProductName,
            final String loanAccountNumber) {
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
        this.loanProductName = loanProductName;
        this.loanAccountNumber = loanAccountNumber;
    }

    public static PostDatedChequeDetailData instance(final Long id, final String bankName, final String branchName,
            final String accountNumber, final String ifscCode, final BigDecimal chequeAmount, final EnumOptionData chequeType,
            final String chequeNumber, final LocalDate chequeDate, final EnumOptionData presentStatus, final EnumOptionData previousStatus,
            final LocalDate presentedDate, final LocalDate bouncedDate, final LocalDate clearedDate, final LocalDate cancelledDate,
            final LocalDate returnedDate) {
        final String officeName = null;
        final String clientName = null;
        final String loanProductName = null;
        final String loanAccountNumber = null;
        return new PostDatedChequeDetailData(id, bankName, branchName, accountNumber, ifscCode, chequeAmount, chequeType, chequeNumber,
                chequeDate, presentStatus, previousStatus, presentedDate, bouncedDate, clearedDate, cancelledDate, returnedDate,
                officeName, clientName, loanProductName, loanAccountNumber);
    }

    public static PostDatedChequeDetailData searchDataInstance(final Long id, final String bankName, final String branchName,
            final String accountNumber, final String ifscCode, final BigDecimal chequeAmount, final EnumOptionData chequeType,
            final String chequeNumber, final LocalDate chequeDate, final EnumOptionData presentStatus, final EnumOptionData previousStatus,
            final LocalDate presentedDate, final LocalDate chequeBouncedDate, final LocalDate clearedDate, final LocalDate cancelledDate,
            final LocalDate returnedDate, final String officeName, final String clientName, final String loanProductName,
            final String loanAccountNumber) {
        return new PostDatedChequeDetailData(id, bankName, branchName, accountNumber, ifscCode, chequeAmount, chequeType, chequeNumber,
                chequeDate, presentStatus, previousStatus, presentedDate, chequeBouncedDate, clearedDate, cancelledDate, returnedDate,
                officeName, clientName, loanProductName, loanAccountNumber);
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

    public String getLoanProductName() {
        return this.loanProductName;
    }

    public String getLoanAccountNumber() {
        return this.loanAccountNumber;
    }

    public void setMappingData(final PostDatedChequeDetailMappingData mappingData) {
        this.mappingData = mappingData;
    }

}