package com.finflux.kyc.address.domain;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.finflux.infrastructure.gis.country.domain.Country;
import com.finflux.infrastructure.gis.district.domain.District;
import com.finflux.infrastructure.gis.state.domain.State;
import com.finflux.infrastructure.gis.taluka.domain.Taluka;
import com.finflux.kyc.address.api.AddressApiConstants;

@Entity
@Table(name = "f_address")
public class Address extends AbstractAuditableCustom<AppUser, Long> {

    @Column(name = "house_no", length = 200, nullable = true)
    private String houseNo;

    @Column(name = "street_no", length = 200, nullable = true)
    private String streetNo;

    @Column(name = "address_line_one", length = 200, nullable = true)
    private String addressLineOne;

    @Column(name = "address_line_two", length = 200, nullable = true)
    private String addressLineTwo;

    @Column(name = "landmark", length = 100, nullable = true)
    private String landmark;

    @Column(name = "village_town", length = 100, nullable = true)
    private String villageTown;

    @ManyToOne
    @JoinColumn(name = "taluka_id")
    private Taluka taluka;
    
    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @Column(name = "postal_code", length = 10,nullable = true)
    private String postalCode;

    @Column(name = "latitude", scale = 6, precision = 19, nullable = true)
    private BigDecimal latitude;

    @Column(name = "longitude", scale = 6, precision = 19, nullable = true)
    private BigDecimal longitude;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "address", orphanRemoval = true)
    private Set<AddressEntity> addressEntities = new HashSet<>();
   
    @Column(name = "is_verified", nullable = true)
    private Boolean isVerified;
    
    @Column(name = "document_id", nullable = true)
    private Long documentId;
    
    @Column(name = "is_locked", nullable = false)
    private boolean isLocked = false;

    protected Address() {}

    private Address(final String houseNo, final String streetNo, final String addressLineOne, final String addressLineTwo,
            final String landmark, final String villageTown, final Taluka taluka, final District district, final State state,
            final Country country, final String postalCode, final BigDecimal latitude, final BigDecimal longitude, final Boolean isVerified, final Long documentId) {
        this.houseNo = houseNo;
        this.streetNo = streetNo;
        this.addressLineOne = addressLineOne;
        this.addressLineTwo = addressLineTwo;
        this.landmark = landmark;
        this.villageTown = villageTown;
        this.taluka = taluka;
        this.district = district;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isVerified = isVerified;
        this.documentId = documentId;
    }

    public static Address create(final String houseNo, final String streetNo, final String addressLineOne, final String addressLineTwo,
            final String landmark, final String villageTown, final Taluka taluka, final District district, final State state,
            final Country country, final String postalCode, final BigDecimal latitude, final BigDecimal longitude, Boolean isVerified, Long documentId) {
        return new Address(houseNo, streetNo, addressLineOne, addressLineTwo, landmark, villageTown, taluka, district, state, country,
                postalCode, latitude, longitude, isVerified, documentId);
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(10);

        if (command.isChangeInStringParameterNamed(AddressApiConstants.houseNoParamName, this.houseNo)) {
            final String newValue = command.stringValueOfParameterNamed(AddressApiConstants.houseNoParamName);
            actualChanges.put(AddressApiConstants.houseNoParamName, newValue);
            this.houseNo = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(AddressApiConstants.streetNoParamName, this.streetNo)) {
            final String newValue = command.stringValueOfParameterNamed(AddressApiConstants.streetNoParamName);
            actualChanges.put(AddressApiConstants.streetNoParamName, newValue);
            this.streetNo = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(AddressApiConstants.addressLineOneParamName, this.addressLineOne)) {
            final String newValue = command.stringValueOfParameterNamed(AddressApiConstants.addressLineOneParamName);
            actualChanges.put(AddressApiConstants.addressLineOneParamName, newValue);
            this.addressLineOne = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(AddressApiConstants.addressLineTwoParamName, this.addressLineTwo)) {
            final String newValue = command.stringValueOfParameterNamed(AddressApiConstants.addressLineTwoParamName);
            actualChanges.put(AddressApiConstants.addressLineTwoParamName, newValue);
            this.addressLineTwo = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(AddressApiConstants.landmarkParamName, this.landmark)) {
            final String newValue = command.stringValueOfParameterNamed(AddressApiConstants.addressLineTwoParamName);
            actualChanges.put(AddressApiConstants.addressLineTwoParamName, newValue);
            this.landmark = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(AddressApiConstants.villageTownParamName, this.villageTown)) {
            final String newValue = command.stringValueOfParameterNamed(AddressApiConstants.villageTownParamName);
            actualChanges.put(AddressApiConstants.villageTownParamName, newValue);
            this.villageTown = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (!command.parameterExists(AddressApiConstants.talukaIdParamName)) {
            this.taluka = null;
        } else {
            if (this.taluka != null) {
                if (command.isChangeInLongParameterNamed(AddressApiConstants.talukaIdParamName, this.taluka.getId())) {
                    final Long newValue = command.longValueOfParameterNamed(AddressApiConstants.talukaIdParamName);
                    actualChanges.put(AddressApiConstants.talukaIdParamName, newValue);
                }
            } else if (command.parameterExists(AddressApiConstants.talukaIdParamName)) {
                final Long newValue = command.longValueOfParameterNamed(AddressApiConstants.talukaIdParamName);
                actualChanges.put(AddressApiConstants.talukaIdParamName, newValue);
            }
        }

        if (!command.parameterExists(AddressApiConstants.districtIdParamName)) {
            this.district = null;
        } else {
            if (this.district != null) {
                if (command.isChangeInLongParameterNamed(AddressApiConstants.districtIdParamName, this.district.getId())) {
                    final Long newValue = command.longValueOfParameterNamed(AddressApiConstants.districtIdParamName);
                    actualChanges.put(AddressApiConstants.districtIdParamName, newValue);
                }
            } else if (command.parameterExists(AddressApiConstants.districtIdParamName)) {
                final Long newValue = command.longValueOfParameterNamed(AddressApiConstants.districtIdParamName);
                actualChanges.put(AddressApiConstants.districtIdParamName, newValue);
            }
        }

        if (!command.parameterExists(AddressApiConstants.stateIdParamName)) {
            this.state = null;
        } else {
            if (this.state != null) {
                if (command.isChangeInLongParameterNamed(AddressApiConstants.stateIdParamName, this.state.getId())) {
                    final Long newValue = command.longValueOfParameterNamed(AddressApiConstants.stateIdParamName);
                    actualChanges.put(AddressApiConstants.stateIdParamName, newValue);
                }
            } else if (command.parameterExists(AddressApiConstants.stateIdParamName)) {
                final Long newValue = command.longValueOfParameterNamed(AddressApiConstants.stateIdParamName);
                actualChanges.put(AddressApiConstants.stateIdParamName, newValue);
            }
        }

        if (!command.parameterExists(AddressApiConstants.countryIdParamName)) {
            this.country = null;
        } else {
            if (this.country != null) {
                if (command.isChangeInLongParameterNamed(AddressApiConstants.countryIdParamName, this.country.getId())) {
                    final Long newValue = command.longValueOfParameterNamed(AddressApiConstants.countryIdParamName);
                    actualChanges.put(AddressApiConstants.countryIdParamName, newValue);
                }
            } else if (command.parameterExists(AddressApiConstants.countryIdParamName)) {
                final Long newValue = command.longValueOfParameterNamed(AddressApiConstants.countryIdParamName);
                actualChanges.put(AddressApiConstants.countryIdParamName, newValue);
            }
        }

        if (command.isChangeInStringParameterNamed(AddressApiConstants.postalCodeParamName, this.postalCode)) {
            final String newValue = command.stringValueOfParameterNamed(AddressApiConstants.postalCodeParamName);
            actualChanges.put(AddressApiConstants.postalCodeParamName, newValue);
            this.postalCode = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInBigDecimalParameterNamed(AddressApiConstants.latitudeParamName, this.latitude)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(AddressApiConstants.latitudeParamName);
            actualChanges.put(AddressApiConstants.latitudeParamName, newValue);
            this.latitude = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(AddressApiConstants.longitudeParamName, this.longitude)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(AddressApiConstants.longitudeParamName);
            actualChanges.put(AddressApiConstants.longitudeParamName, newValue);
            this.longitude = newValue;
        }
        
        if (command.isChangeInLongParameterNamed(AddressApiConstants.documentIdParamName, this.documentId)) {
            final Long newValue = command.longValueOfParameterNamed(AddressApiConstants.documentIdParamName);
            actualChanges.put(AddressApiConstants.documentIdParamName, newValue);
            this.documentId = newValue;
        }
        
        return actualChanges;
    }

    public void addAddressEntities(final AddressEntity addressEntities) {
        this.addressEntities.add(addressEntities);
    }

    public void addAllAddressEntities(final Set<AddressEntity> addressEntities) {
        this.addressEntities.clear();
        if (addressEntities != null && addressEntities.size() > 0) {
            this.addressEntities.addAll(addressEntities);
        }
    }

    public Set<AddressEntity> getAddressEntities() {
        return this.addressEntities;
    }
    
    public Taluka getTaluka() {
        return this.taluka;
    }

    public Long getTalukaId() {
        Long talukaId = null;
        if (this.district != null) {
            talukaId = this.taluka.getId();
        }
        return talukaId;
    }

    public District getDistrict() {
        return this.district;
    }

    public Long getDistrictId() {
        Long districtId = null;
        if (this.taluka != null) {
            districtId = this.district.getId();
        }
        return districtId;
    }
    
    public State getState() {
        return this.state;
    }

    public Long getStateId() {
        Long stateId = null;
        if (this.country != null) {
            stateId = this.state.getId();
        }
        return stateId;
    }

    public Country getCountry() {
        return this.country;
    }

    public Long getCountryId() {
        Long countryId = null;
        if (this.country != null) {
            countryId = this.country.getId();
        }
        return countryId;
    }
    public void updateTaluka(final Taluka taluka) {
        this.taluka = taluka;
    }

    public void updateDistrict(final District district) {
        this.district = district;
    }

    public void updateState(final State state) {
        this.state = state;
    }

    public void updateCountry(final Country country) {
        this.country = country;
    }

    
    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }
    
    public boolean isLocked() {
        return this.isLocked;
    }

}
