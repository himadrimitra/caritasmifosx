package com.finflux.kyc.address.data;

import java.math.BigDecimal;
import java.util.Collection;

import com.finflux.infrastructure.gis.country.data.CountryData;
import com.finflux.infrastructure.gis.district.data.DistrictData;
import com.finflux.infrastructure.gis.state.data.StateData;

public class AddressData {

    private final Long addressId;
    private final String houseNo;
    private final String streetNo;
    private final String addressLineOne;
    private final String addressLineTwo;
    private final String landmark;
    private final String villageTown;
    private final String taluka;
    private final DistrictData districtData;
    private final StateData stateData;
    private final CountryData countryData;
    private final String postalCode;
    private final BigDecimal latitude;
    private final BigDecimal longitude;
    private final Collection<AddressEntityData> addressEntityData;

    private AddressData(final Long addressId, final String houseNo, final String streetNo, final String addressLineOne,
            final String addressLineTwo, final String landmark, final String villageTown, final String taluka,
            final DistrictData districtData, final StateData stateData, final CountryData countryData, final String postalCode,
            final BigDecimal latitude, final BigDecimal longitude, final Collection<AddressEntityData> addressEntityData) {
        this.addressId = addressId;
        this.houseNo = houseNo;
        this.streetNo = streetNo;
        this.addressLineOne = addressLineOne;
        this.addressLineTwo = addressLineTwo;
        this.landmark = landmark;
        this.villageTown = villageTown;
        this.taluka = taluka;
        this.districtData = districtData;
        this.stateData = stateData;
        this.countryData = countryData;
        this.postalCode = postalCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.addressEntityData = addressEntityData;
    }

    public static AddressData instance(final Long addressId, final String houseNo, final String streetNo, final String addressLineOne,
            final String addressLineTwo, final String landmark, final String villageTown, final String taluka,
            final DistrictData districtData, final StateData stateData, final CountryData countryData, final String postalCode,
            final BigDecimal latitude, final BigDecimal longitude, final Collection<AddressEntityData> addressEntityData) {
        return new AddressData(addressId, houseNo, streetNo, addressLineOne, addressLineTwo, landmark, villageTown, taluka, districtData,
                stateData, countryData, postalCode, latitude, longitude, addressEntityData);
    }

    public Long getAddressId() {
        return this.addressId;
    }

    public String getHouseNo() {
        return this.houseNo;
    }

    public String getStreetNo() {
        return this.streetNo;
    }

    public String getAddressLineOne() {
        return this.addressLineOne;
    }

    public String getAddressLineTwo() {
        return this.addressLineTwo;
    }

    public String getLandmark() {
        return this.landmark;
    }

    public String getVillageTown() {
        return this.villageTown;
    }

    public String getTaluka() {
        return this.taluka;
    }

    public DistrictData getDistrictData() {
        return this.districtData;
    }

    public StateData getStateData() {
        return this.stateData;
    }

    public CountryData getCountryData() {
        return this.countryData;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public BigDecimal getLatitude() {
        return this.latitude;
    }

    public BigDecimal getLongitude() {
        return this.longitude;
    }

    public Collection<AddressEntityData> getAddressEntityData() {
        return this.addressEntityData;
    }

}
