package com.finflux.transaction.execution.provider.rbl.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dhirendra on 24/11/16.
 */
public class RBLSinglePaymentStatusRequest {

	@JsonProperty("Header")
	private Header header;

	@JsonProperty("Body")
	private Body body;

	@JsonProperty("Signature")
	private Signature signature;

	public RBLSinglePaymentStatusRequest(final Header header, final Body body,
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

		@JsonProperty("RefNo")
		private String referenceNumber;

		public Body(String referenceNumber) {
			this.referenceNumber = referenceNumber;
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
