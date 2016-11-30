package com.finflux.transaction.execution.service.provider.rbl.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dhirendra on 24/11/16.
 */
public class RBLSinglePaymentRequest {

	@JsonProperty("Header")
	private Header header;

	@JsonProperty("Body")
	private Body body;

	@JsonProperty("Signature")
	private Signature signature;

	public RBLSinglePaymentRequest(final Header header, final Body body,
			final Signature signature) {
		this.header = header;
		this.body = body;
		this.signature = signature;
	}

	public Header getHeader() {
		return header;
	}

	public Body getBody() {
		return body;
	}

	public Signature getSignature() {
		return signature;
	}

	public static class Header {

		@JsonProperty("TranID")
		private String transactionId;

		@JsonProperty("Corp_ID")
		private String corpId;

		@JsonProperty("Maker_ID")
		private String makerId;

		@JsonProperty("Checker_ID")
		private String checkerId;

		@JsonProperty("Approver_ID")
		private String approverId;

		public Header(String transactionId, String corpId, String makerId,
				String checkerId, String approverId) {
			this.transactionId = transactionId;
			this.corpId = corpId;
			if (makerId == null) {
				this.makerId = "";
			} else {
				this.makerId = makerId;
			}
			if (checkerId == null) {
				this.checkerId = "";
			} else {
				this.checkerId = checkerId;
			}
			if (approverId == null) {
				this.approverId = "";
			} else {
				this.approverId = approverId;
			}
		}

		public String getTransactionId() {
			return transactionId;
		}

		public String getCorpId() {
			return corpId;
		}

		public String getMakerId() {
			return makerId;
		}

		public String getCheckerId() {
			return checkerId;
		}

		public String getApproverId() {
			return approverId;
		}
	}

	public static class Body {

		@JsonProperty("Amount")
		private String amount;

		@JsonProperty("Debit_Acct_No")
		private String debitAccountNumber;

		@JsonProperty("Debit_Acct_Name")
		private String debitAccountName;

		@JsonProperty("Debit_IFSC")
		private String debitIFSC;

		@JsonProperty("Debit_Mobile")
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private String debitMobile;

		@JsonProperty("Debit_TrnParticulars")
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private String debitTxnParticulars;

		@JsonProperty("Debit_PartTrnRmks")
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private String debitPartTxnRemarks;

		@JsonProperty("Ben_IFSC")
		private String beneficiaryIFSC;

		@JsonProperty("Ben_Acct_No")
		private String beneficiaryAccountNumber;

		@JsonProperty("Ben_Name")
		private String beneficiaryName;

		@JsonProperty("Ben_Address")
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private String beneficiaryAddress;

		@JsonProperty("Ben_BankName")
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private String beneficiaryBankName;

		// Demand Draft purpose
		// @JsonProperty("Ben_BankCd")
		// private String beneficiaryBankCode;

		// Demand Draft purpose
		// @JsonProperty("Ben_BranchCd")
		// private String beneficiaryBranchCode;

		@JsonProperty("Ben_Email")
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private String beneficiaryEmail;

		@JsonProperty("Ben_Mobile")
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private String beneficiaryMobile;

		@JsonProperty("Ben_TrnParticulars")
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private String beneficiaryTxnParticulars;

		@JsonProperty("Ben_PartTrnRmks")
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private String beneficiaryTxnRemarks;

		// Demand Draft purpose
		// @JsonProperty("Issue_BranchCd")
		// private String issueBranchCd;

		@JsonProperty("Mode_of_Pay")
		private String modeOfPay;

		@JsonProperty("Remarks")
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private String remarks;

		@JsonProperty("RptCode")
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private String rptCode;

		public Body(String amount, String debitAccountNumber,
				String debitAccountName, String debitIFSC, String debitMobile,
				String debitTxnParticulars, String debitPartTxnRemarks,
				String beneficiaryIFSC, String beneficiaryAccountNumber,
				String beneficiaryName, String beneficiaryAddress,
				String beneficiaryBankName, String beneficiaryEmail, String beneficiaryMobile,
				String beneficiaryTxnParticulars, String beneficiaryTxnRemarks,
				String modeOfPay, String rptCode, String remarks) {
			this.amount = amount;
			this.debitAccountNumber = debitAccountNumber;
			this.debitAccountName = debitAccountName;
			this.debitIFSC = debitIFSC;
			this.debitMobile = debitMobile;
			this.debitTxnParticulars = debitTxnParticulars;
			this.debitPartTxnRemarks = debitPartTxnRemarks;
			this.beneficiaryIFSC = beneficiaryIFSC;
			this.beneficiaryAccountNumber = beneficiaryAccountNumber;
			this.beneficiaryName = beneficiaryName;
			this.beneficiaryAddress = beneficiaryAddress;
			this.beneficiaryBankName = beneficiaryBankName;
			this.beneficiaryEmail = beneficiaryEmail;
			this.beneficiaryMobile = beneficiaryMobile;
			this.beneficiaryTxnParticulars = beneficiaryTxnParticulars;
			this.beneficiaryTxnRemarks = beneficiaryTxnRemarks;
			this.modeOfPay = modeOfPay;
			this.rptCode = rptCode;
			this.remarks = remarks;
		}

		public String getAmount() {
			return amount;
		}

		public String getDebitAccountNumber() {
			return debitAccountNumber;
		}

		public String getDebitAccountName() {
			return debitAccountName;
		}

		public String getDebitIFSC() {
			return debitIFSC;
		}

		public String getDebitMobile() {
			return debitMobile;
		}

		public String getDebitTxnParticulars() {
			return debitTxnParticulars;
		}

		public String getDebitPartTxnRemarks() {
			return debitPartTxnRemarks;
		}

		public String getBeneficiaryIFSC() {
			return beneficiaryIFSC;
		}

		public String getBeneficiaryAccountNumber() {
			return beneficiaryAccountNumber;
		}

		public String getBeneficiaryName() {
			return beneficiaryName;
		}

		public String getBeneficiaryAddress() {
			return beneficiaryAddress;
		}

		public String getBeneficiaryBankName() {
			return beneficiaryBankName;
		}

		public String getBeneficiaryMobile() {
			return beneficiaryMobile;
		}

		public String getBeneficiaryTxnParticulars() {
			return beneficiaryTxnParticulars;
		}

		public String getBeneficiaryTxnRemarks() {
			return beneficiaryTxnRemarks;
		}

		public String getModeOfPay() {
			return modeOfPay;
		}

		public String getRptCode() {
			return rptCode;
		}

		public String getRemarks() {
			return remarks;
		}

		public String getBeneficiaryEmail() {
			return beneficiaryEmail;
		}
	}

	public static class Signature {

		@JsonProperty("Signature")
		private String signature;

		public Signature(String signature) {
			this.signature = signature;
		}

		public String getSignature() {
			return signature;
		}
	}
}
