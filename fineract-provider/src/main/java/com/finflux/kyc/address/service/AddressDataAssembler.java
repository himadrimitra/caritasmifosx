package com.finflux.kyc.address.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.infrastructure.gis.country.domain.Country;
import com.finflux.infrastructure.gis.country.domain.CountryRepositoryWrapper;
import com.finflux.infrastructure.gis.district.domain.District;
import com.finflux.infrastructure.gis.district.domain.DistrictRepositoryWrapper;
import com.finflux.infrastructure.gis.state.domain.State;
import com.finflux.infrastructure.gis.state.domain.StateRepositoryWrapper;
import com.finflux.kyc.address.api.AddressApiConstants;
import com.finflux.kyc.address.domain.Address;
import com.finflux.kyc.address.domain.AddressEntity;
import com.finflux.kyc.address.domain.AddressEntityRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class AddressDataAssembler {

    private final FromJsonHelper fromApiJsonHelper;
    private final CodeValueRepositoryWrapper codeValueRepository;
    private final AddressEntityRepository addressEntityRepository;
    private final CountryRepositoryWrapper countryRepository;
    private final StateRepositoryWrapper stateRepository;
    private final DistrictRepositoryWrapper districtRepository;

    @Autowired
    public AddressDataAssembler(final FromJsonHelper fromApiJsonHelper, final CodeValueRepositoryWrapper codeValueRepository,
            final AddressEntityRepository addressEntityRepository, final CountryRepositoryWrapper countryRepository,
            final StateRepositoryWrapper stateRepository, final DistrictRepositoryWrapper districtRepository) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.codeValueRepository = codeValueRepository;
        this.addressEntityRepository = addressEntityRepository;
        this.countryRepository = countryRepository;
        this.stateRepository = stateRepository;
        this.districtRepository = districtRepository;
    }

    /**
     * Assemble Create Address objects
     * 
     * @param entityTypeEnum
     * @param entityId
     * @param command
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<Address> assembleCreateForm(final Integer entityTypeEnum, final Long entityId, final JsonCommand command) {

        final List<Address> addresses = new ArrayList();

        final JsonElement parentElement = command.parsedJson();
        final JsonObject parentElementObj = parentElement.getAsJsonObject();
        if (parentElement.isJsonObject() && !command.parameterExists(AddressApiConstants.addressesParamName)) {
            final Address address = assembleCreateFormEachObject(entityId, entityTypeEnum, parentElement.getAsJsonObject());
            addresses.add(address);
        } else if (command.parameterExists(AddressApiConstants.addressesParamName)) {
            final JsonArray array = parentElementObj.get(AddressApiConstants.addressesParamName).getAsJsonArray();
            if (array != null && array.size() > 0) {
                for (int i = 0; i < array.size(); i++) {
                    final JsonObject element = array.get(i).getAsJsonObject();
                    final Address address = assembleCreateFormEachObject(entityId, entityTypeEnum, element);
                    addresses.add(address);
                }
            }
        }
        return addresses;
    }

    @SuppressWarnings("null")
    public Map<String, Object> assembleUpdateForm(final Address address, final Long entityId, final Integer entityTypeEnum,
            final JsonCommand command) {

        final Map<String, Object> actualChanges = address.update(command);
        Country country = null;
        State state = null;
        District district = null;
        
        if (command.parameterExists(AddressApiConstants.countryIdParamName)) {

        	 final Long countryId = command.longValueOfParameterNamed(AddressApiConstants.countryIdParamName);
        	 if(countryId != null){
        		 country = this.countryRepository.findOneWithNotFoundDetection(countryId);
        	 }
        	 address.updateCountry(country);
        		 
        	 }
        	 
             if (command.parameterExists(AddressApiConstants.stateIdParamName)) {
            	 final Long stateId = command.longValueOfParameterNamed(AddressApiConstants.stateIdParamName);
            	 if(stateId != null){
            	 if(address.getCountryId()!= null){
            		 state = this.stateRepository.findOneWithNotFoundDetection(stateId);
            	 validateStateWithCountryAndGetCountryObject(state, address.getCountryId());
            	 }else{
            		 state = this.stateRepository.findOneWithNotFoundDetection(stateId); 
            	 }
             }
            	 address.updateState(state);
          
             if (command.parameterExists(AddressApiConstants.districtIdParamName)) {
            	 final Long districtId = command.longValueOfParameterNamed(AddressApiConstants.districtIdParamName);
            	 if(districtId != null){
            	 if(address.getStateId()!= null){
            	 district = this.districtRepository.findOneWithNotFoundDetection(districtId);
            	 validateDistrictWithStateAndGetStateObject(district, address.getStateId());
            	 }else{
            		 district = this.districtRepository.findOneWithNotFoundDetection(districtId);
            	}
            	 address.updateDistrict(district);
             }
           }
        }
        
        final JsonElement element = command.parsedJson();
        final String[] addressTypes = this.fromApiJsonHelper.extractArrayNamed(AddressApiConstants.addressTypesParamName, element);

        final Set<AddressEntity> addressEntities = constructAddressEntityObjects(address, addressTypes, entityId, entityTypeEnum);

        if (address != null && addressEntities != null && addressEntities.size() > 0) {
            address.addAllAddressEntities(addressEntities);
        }

        return actualChanges;
    }

    private Address assembleCreateFormEachObject(final Long entityId, final Integer entityTypeEnum, final JsonObject element) {

        final JsonObject topLevelJsonElement = element.getAsJsonObject();
        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final Address address = constructAddressObject(element, locale);

        final String[] addressTypes = this.fromApiJsonHelper.extractArrayNamed(AddressApiConstants.addressTypesParamName, element);

        final Set<AddressEntity> addressEntities = constructAddressEntityObjects(address, addressTypes, entityId, entityTypeEnum);

        if (address != null && addressEntities != null && addressEntities.size() > 0) {
            address.addAllAddressEntities(addressEntities);
        }
        return address;
    }

    @SuppressWarnings("null")
    private Address constructAddressObject(final JsonObject element, final Locale locale) {

        final String houseNo = this.fromApiJsonHelper.extractStringNamed(AddressApiConstants.houseNoParamName, element);

        final String streetNo = this.fromApiJsonHelper.extractStringNamed(AddressApiConstants.streetNoParamName, element);

        final String addressLineOne = this.fromApiJsonHelper.extractStringNamed(AddressApiConstants.addressLineOneParamName, element);

        final String addressLineTwo = this.fromApiJsonHelper.extractStringNamed(AddressApiConstants.addressLineTwoParamName, element);

        final String landmark = this.fromApiJsonHelper.extractStringNamed(AddressApiConstants.landmarkParamName, element);

        final String villageTown = this.fromApiJsonHelper.extractStringNamed(AddressApiConstants.villageTownParamName, element);

        final String taluka = this.fromApiJsonHelper.extractStringNamed(AddressApiConstants.talukaParamName, element);

        final Long districtId = this.fromApiJsonHelper.extractLongNamed(AddressApiConstants.districtIdParamName, element);
        District district = null;
        if (districtId != null) {
            district = this.districtRepository.findOneWithNotFoundDetection(districtId);
        }
        final Long stateId = this.fromApiJsonHelper.extractLongNamed(AddressApiConstants.stateIdParamName, element);
        State state = null;
        if (stateId != null) {
            state = validateDistrictWithStateAndGetStateObject(district, stateId);
        } else if (district != null && district.getState() != null && state == null) {
            /**
             * Not as part of the requirement. If you want to auto set remove
             * the commented code
             */
            // state =
            // this.stateRepository.findOneWithNotFoundDetection(district.getStateId());
        }

        final Long countryId = this.fromApiJsonHelper.extractLongNamed(AddressApiConstants.countryIdParamName, element);
        Country country = null;
        if (countryId != null) {
            country = validateStateWithCountryAndGetCountryObject(state, countryId);
        } else if (state != null && state.getCountry() != null && country == null) {
            /**
             * Not as part of the requirement. If you want to auto set remove
             * the commented code
             */
            // country =
            // this.countryRepository.findOneWithNotFoundDetection(state.getCountryId());
        }

        final String postalCode = this.fromApiJsonHelper.extractStringNamed(AddressApiConstants.postalCodeParamName, element);

        final BigDecimal latitude = this.fromApiJsonHelper.extractBigDecimalNamed(AddressApiConstants.latitudeParamName, element, locale);

        final BigDecimal longitude = this.fromApiJsonHelper.extractBigDecimalNamed(AddressApiConstants.longitudeParamName, element, locale);

        return Address.create(houseNo, streetNo, addressLineOne, addressLineTwo, landmark, villageTown, taluka, district, state, country,
                postalCode, latitude, longitude);
    }

    private Country validateStateWithCountryAndGetCountryObject(final State state, final Long countryId) {
        final Country country = this.countryRepository.findOneWithNotFoundDetection(countryId);
        if (state != null && state.getCountryId() != null
                && state.getCountryId() != countryId) { throw new GeneralPlatformDomainRuleException(
                        "error.msg.address.state.does.not.belongs.to.country",
                        "" + state.getStateName() + " state does not belongs to " + country.getCountryName() + " country",
                        state.getStateName(), country.getCountryName()); }
        return country;
    }

    private State validateDistrictWithStateAndGetStateObject(final District district, final Long stateId) {
        final State state = this.stateRepository.findOneWithNotFoundDetection(stateId);
        if (district != null && district.getStateId() != null
                && district.getStateId() != stateId) { throw new GeneralPlatformDomainRuleException(
                        "error.msg.address.district.does.not.belongs.to.state",
                        "" + district.getDistrictName() + " district does not belongs to " + state.getStateName() + " state",
                        district.getDistrictName(), state.getStateName()); }
        return state;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Set<AddressEntity> constructAddressEntityObjects(final Address address, final String[] addressTypes, final Long entityId,
            final Integer entityTypeEnum) {

        if (address != null && address.getId() != null) {
            final Set<AddressEntity> existingAddressEntities = address.getAddressEntities();
            for (final AddressEntity addressEntity : existingAddressEntities) {
                if (addressEntity.isActive()) {
                    addressEntity.makeInActive();
                }
            }
        }
        final Set<AddressEntity> addressEntities = new HashSet();

        if (address != null && addressTypes != null) {
            final Set<String> addressTypesSet = Arrays.stream(addressTypes).collect(Collectors.toSet());
            int i = 0;
            for (final String id : addressTypesSet) {
                final Long addressTypeId = Long.parseLong(id);
                final CodeValue addressType = this.codeValueRepository.findOneWithNotFoundDetection(addressTypeId);
                AddressEntity addressEntity = findByAddressTypeAndEntityIdAndEntityTypeEnum(addressType, entityId, entityTypeEnum);
                CodeValue parentAddressType = null;
                if (i == 0) {
                    if (addressEntity != null) {
                        addressEntity.assignAddressAndMakeItActive(address, parentAddressType);
                    } else {
                        addressEntity = AddressEntity.create(address, addressType, entityId, entityTypeEnum, parentAddressType);
                    }
                } else {
                    parentAddressType = this.codeValueRepository.findOneWithNotFoundDetection(Long.parseLong(addressTypes[0]));
                    if (addressEntity != null) {
                        addressEntity.assignAddressAndMakeItActive(address, parentAddressType);
                    } else {
                        addressEntity = AddressEntity.create(address, addressType, entityId, entityTypeEnum, parentAddressType);
                    }
                }
                if (addressEntity != null) {
                    addressEntities.add(addressEntity);
                } else {
                    /**
                     * Through error message while constructing the address
                     * entity with address type
                     */
                }
                i++;
            }
        } else {
            /**
             * Through error message for address object is null
             */
        }
        return addressEntities;
    }

    private AddressEntity findByAddressTypeAndEntityIdAndEntityTypeEnum(final CodeValue addressType, final Long entityId,
            final Integer entityTypeEnum) {
        return this.addressEntityRepository.findByAddressTypeAndEntityIdAndEntityTypeEnum(addressType, entityId, entityTypeEnum);
    }
}
