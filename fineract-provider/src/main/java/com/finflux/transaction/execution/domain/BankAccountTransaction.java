package com.finflux.transaction.execution.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;

@Entity
@Table(name = "f_bank_account_transaction")
public class BankAccountTransaction
		extends
			AbstractAuditableCustom<AppUser, Long> {

	@Column(name = "internal_reference_id")
	private String internalReferenceId;

	@Column(name = "entity_type")
	private Integer entityType;

	@Column(name = "entity_id")
	private Long entityId;

	@Column(name = "entity_transaction_id")
	private Long entityTransactionId;

	@Column(name = "external_service_id")
	private Long externalServiceId;

	private Integer status;

	@Column(name = "transfer_type")
	private Integer transferType;

	@Column(name = "debit_account")
	private Long debitAccount;

	@Column(name = "beneficiary_account")
	private Long beneficiaryAccount;

	private BigDecimal amount;

	@Column(name = "reference_number")
	private String referenceNumber;

	@Column(name = "utr_number")
	private String utrNumber;

	@Column(name = "po_number")
	private String poNumber;

	private String rrn;

	@Column(name = "error_code")
	private String errorCode;

	@Column(name = "error_message")
	private String errorMessage;

	@Column(name = "transaction_date")
	private Date transactionDate;

	@Column(name = "reason")
	private String reason;

	public BankAccountTransaction(Integer entityType, Long entityId,
			Long entityTransactionId, Integer status, Long debitAccount,
			Long beneficiaryAccount, BigDecimal amount, Integer transferType,
			Long externalServiceId, String reason) {
		this.entityType = entityType;
		this.entityId = entityId;
		this.entityTransactionId = entityTransactionId;
		this.status = status;
		this.debitAccount = debitAccount;
		this.beneficiaryAccount = beneficiaryAccount;
		this.amount = amount;
		this.transferType = transferType;
		this.externalServiceId = externalServiceId;
		this.reason = reason;
	}

	public BankAccountTransaction() {
	}

	public Integer getEntityType() {
		return entityType;
	}

	public void setEntityType(Integer entityType) {
		this.entityType = entityType;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public Long getEntityTransactionId() {
		return entityTransactionId;
	}

	public void setEntityTransactionId(Long entityTransactionId) {
		this.entityTransactionId = entityTransactionId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getTransferType() {
		return transferType;
	}

	public void setTransferType(Integer transferType) {
		this.transferType = transferType;
	}

	public Long getDebitAccount() {
		return debitAccount;
	}

	public void setDebitAccount(Long debitAccount) {
		this.debitAccount = debitAccount;
	}

	public Long getBeneficiaryAccount() {
		return beneficiaryAccount;
	}

	public void setBeneficiaryAccount(Long beneficiaryAccount) {
		this.beneficiaryAccount = beneficiaryAccount;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getUtrNumber() {
		return utrNumber;
	}

	public void setUtrNumber(String utrNumber) {
		this.utrNumber = utrNumber;
	}

	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public String getRrn() {
		return rrn;
	}

	public void setRrn(String rrn) {
		this.rrn = rrn;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Long getExternalServiceId() {
		return externalServiceId;
	}

	public void setExternalServiceId(Long externalServiceId) {
		this.externalServiceId = externalServiceId;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getInternalReferenceId() {
		return internalReferenceId;
	}

	public void setInternalReferenceId(String internalReferenceId) {
		this.internalReferenceId = internalReferenceId;
	}
}