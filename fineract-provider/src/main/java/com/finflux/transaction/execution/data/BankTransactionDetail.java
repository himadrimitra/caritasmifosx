package com.finflux.transaction.execution.data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import com.finflux.portfolio.bank.data.BankAccountDetailData;

/**
 * Created by dhirendra on 30/11/16.
 */
public class BankTransactionDetail {

    private final Long transactionId;
    private final BankAccountDetailData debiter;
    private final BankAccountDetailData beneficiary;
    private final EnumOptionData transferType;
    private final Integer entityTypeId;
    private final Long entityId;
    private final Long entityTxnId;
    private final BigDecimal amount;
    private final EnumOptionData status;
    private final String referenceNumber;
    private final String utrNumber;
    private final String poNumber;
    private final String errorCode;
    private final String errorMessage;
    private final Date transferDate;
    private List<EnumOptionData> supportedTransferTypes;
    private final Date createdDate;

    public BankTransactionDetail(Long transactionId, BankAccountDetailData debiter, BankAccountDetailData beneficiary, Integer entityTypeId,
            Long entityId, Long entityTxnId, BigDecimal amount, EnumOptionData transferType, EnumOptionData status, String referenceNumber,
            String utrNumber, String poNumber, String errorCode, String errorMessage, Date transferDate, Date createdDate) {
        this.debiter = debiter;
        this.beneficiary = beneficiary;
        this.transferType = transferType;
        this.entityTypeId = entityTypeId;
        this.entityId = entityId;
        this.entityTxnId = entityTxnId;
        this.amount = amount;
        this.status = status;
        this.transactionId = transactionId;
        this.referenceNumber = referenceNumber;
        this.utrNumber = utrNumber;
        this.poNumber = poNumber;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.transferDate = transferDate;
        this.createdDate = createdDate;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public BankAccountDetailData getDebiter() {
        return debiter;
    }

    public BankAccountDetailData getBeneficiary() {
        return beneficiary;
    }

    public EnumOptionData getTransferType() {
        return transferType;
    }

    public Integer getEntityTypeId() {
        return entityTypeId;
    }

    public Long getEntityId() {
        return entityId;
    }

    public Long getEntityTxnId() {
        return entityTxnId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public EnumOptionData getStatus() {
        return status;
    }

    public List<EnumOptionData> getSupportedTransferTypes() {
        return supportedTransferTypes;
    }

    public void setSupportedTransferTypes(List<EnumOptionData> supportedTransferTypes) {
        this.supportedTransferTypes = supportedTransferTypes;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public String getUtrNumber() {
        return utrNumber;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Date getTransferDate() {
        return transferDate;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

}
