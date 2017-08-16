package org.apache.fineract.infrastructure.configuration.data;

public class CibilAddressTypes {

	private final String addressTypeResidence;
	private final String addressTypepermanent;
	private final String addressTypeOffice;

	public CibilAddressTypes(String addressTypeResidence, String addressTypepermanent, String addressTypeOffice) {
		super();
		this.addressTypeResidence = addressTypeResidence;
		this.addressTypepermanent = addressTypepermanent;
		this.addressTypeOffice = addressTypeOffice;
	}

	public String getAddressTypeResidence() {
		return this.addressTypeResidence;
	}

	public String getAddressTypepermanent() {
		return this.addressTypepermanent;
	}

	public String getAddressTypeOffice() {
		return this.addressTypeOffice;
	}
}
