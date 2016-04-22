package com.finflux.kyc.address.data;

import java.util.Collection;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import com.finflux.infrastructure.gis.country.data.CountryData;

public class AddressTemplateData {

    private final Collection<CodeValueData> addressTypeOptions;
    private final Collection<EnumOptionData> addressEntityTypeOptions;
    private final Collection<CountryData> countryDatas;

    private AddressTemplateData(final Collection<CodeValueData> addressTypeOptions, final Collection<EnumOptionData> addressEntityTypeOptions,
            final Collection<CountryData> countryDatas) {
        this.addressTypeOptions = addressTypeOptions;
        this.addressEntityTypeOptions = addressEntityTypeOptions;
        this.countryDatas = countryDatas;
    }

    public static AddressTemplateData template(final Collection<CodeValueData> addressTypeOptions,
            final Collection<EnumOptionData> entityTypeOptions, final Collection<CountryData> countryDatas) {
        return new AddressTemplateData(addressTypeOptions, entityTypeOptions, countryDatas);
    }

    public Collection<CodeValueData> getAddressTypeOptions() {
        return this.addressTypeOptions;
    }

    public Collection<EnumOptionData> getAddressEntityTypeOptions() {
        return this.addressEntityTypeOptions;
    }

    public Collection<CountryData> getCountryDatas() {
        return this.countryDatas;
    }
}
