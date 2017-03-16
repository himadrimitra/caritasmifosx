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
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
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
import com.finflux.infrastructure.gis.taluka.domain.Taluka;
import com.finflux.infrastructure.gis.taluka.domain.TalukaRepositoryWrapper;
import com.finflux.kyc.address.api.AddressApiConstants;
import com.finflux.kyc.address.data.AddressEntityTypeEnums;
import com.finflux.kyc.address.domain.Address;
import com.finflux.kyc.address.domain.AddressEntity;
import com.finflux.kyc.address.domain.AddressEntityRepository;
import com.finflux.kyc.address.exception.AddressTypeAlreadyExistsException;
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
    private final TalukaRepositoryWrapper talukaRepository;

    @Autowired
    public AddressDataAssembler(final FromJsonHelper fromApiJsonHelper, final CodeValueRepositoryWrapper codeValueRepository,
            final AddressEntityRepository addressEntityRepository, final CountryRepositoryWrapper countryRepository,
            final StateRepositoryWrapper stateRepository, final DistrictRepositoryWrapper districtRepository,final TalukaRepositoryWrapper talukaRepository) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.codeValueRepository = codeValueRepository;
        this.addressEntityRepository = addressEntityRepository;
        this.countryRepository = countryRepository;
        this.stateRepository = stateRepository;
        this.districtRepository = districtRepository;
        this.talukaRepository = talukaRepository;
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

    public Map<String, Object> assembleUpdateForm(final Address address, final Long entityId, final Integer entityTypeEnum,
            final JsonCommand command) {

        final Map<String, Object> actualChanges = address.update(command);

        if (actualChanges.containsKey(AddressApiConstants.countryIdParamName)) {
            final Long countryId = (Long) actualChanges.get(AddressApiConstants.countryIdParamName);
            final Country country = this.countryRepository.findOneWithNotFoundDetection(countryId);
            address.updateCountry(country);
        }

        if (actualChanges.containsKey(AddressApiConstants.stateIdParamName)) {
            final Long stateId = (Long) actualChanges.get(AddressApiConstants.stateIdParamName);
            final State state = this.stateRepository.findOneWithNotFoundDetection(stateId);
            if (address.getCountryId() != null) {
                validateStateWithCountryAndGetCountryObject(state, address.getCountryId());
            }
            address.updateState(state);
        }

        if (actualChanges.containsKey(AddressApiConstants.districtIdParamName)) {
            final Long districtId = (Long) actualChanges.get(AddressApiConstants.districtIdParamName);
            final District district = this.districtRepository.findOneWithNotFoundDetection(districtId);
            if (address.getStateId() != null) {
                validateDistrictWithStateAndGetStateObject(district, address.getStateId());
            }
            address.updateDistrict(district);
        }
        if (actualChanges.containsKey(AddressApiConstants.talukaIdParamName)) {
            final Long talukaId = (Long) actualChanges.get(AddressApiConstants.talukaIdParamName);
            final Taluka taluka = this.talukaRepository.findOneWithNotFoundDetection(talukaId);
            if (address.getDistrictId() != null) {
                validateTalukaWithDistrictAndGetDistrictObject(taluka, address.getDistrictId());
            }
            address.updateTaluka(taluka);
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

        validateAddressTypeAlreadyExistsOrNot(addressTypes,entityId, entityTypeEnum);
        
        final Set<AddressEntity> addressEntities = constructAddressEntityObjects(address, addressTypes, entityId, entityTypeEnum);

        if (address != null && addressEntities != null && addressEntities.size() > 0) {
            address.addAllAddressEntities(addressEntities);
        }
        return address;
    }
    
    private void validateAddressTypeAlreadyExistsOrNot(final String[] addressTypes, final Long entityId,
			final Integer entityTypeEnum) {
		if (addressTypes != null) {
			final Set<String> addressTypesSet = Arrays.stream(addressTypes).collect(Collectors.toSet());
			for (final String id : addressTypesSet) {
				final Long addressTypeId = Long.parseLong(id);
				final CodeValue addressType = this.codeValueRepository.findOneWithNotFoundDetection(addressTypeId);
				final AddressEntity addressEntity = findByAddressTypeAndEntityIdAndEntityTypeEnum(addressType, entityId,
						entityTypeEnum);
				if (addressEntity != null) {
					final EnumOptionData addressEntityData = AddressEntityTypeEnums.addressEntity(entityTypeEnum);
					throw new AddressTypeAlreadyExistsException(addressType.label(), addressEntityData.getValue(),
							entityId);
				}
			}
		}
	}

    private Address constructAddressObject(final JsonObject element, final Locale locale) {

        final String houseNo = this.fromApiJsonHelper.extractStringNamed(AddressApiConstants.houseNoParamName, element);

        final String streetNo = this.fromApiJsonHelper.extractStringNamed(AddressApiConstants.streetNoParamName, element);

        final String addressLineOne = this.fromApiJsonHelper.extractStringNamed(AddressApiConstants.addressLineOneParamName, element);

        final String addressLineTwo = this.fromApiJsonHelper.extractStringNamed(AddressApiConstants.addressLineTwoParamName, element);

        final String landmark = this.fromApiJsonHelper.extractStringNamed(AddressApiConstants.landmarkParamName, element);

        final String villageTown = this.fromApiJsonHelper.extractStringNamed(AddressApiConstants.villageTownParamName, element);

        final Long talukaId = this.fromApiJsonHelper.extractLongNamed(AddressApiConstants.talukaIdParamName, element);
        Taluka taluka = null;
        if (talukaId != null) {
            taluka = this.talukaRepository.findOneWithNotFoundDetection(talukaId);
        }

        final Long districtId = this.fromApiJsonHelper.extractLongNamed(AddressApiConstants.districtIdParamName, element);
        District district = null;
        if (districtId != null) {
            district = validateTalukaWithDistrictAndGetDistrictObject(taluka, districtId);
        }else if(taluka !=null && taluka.getDistrict()!= null && district == null){
            //district = this.districtRepository.findOneWithNotFoundDetection(taluka.getDistrictId());
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
                && !state.getCountryId().equals(countryId)) { throw new GeneralPlatformDomainRuleException(
                        "error.msg.address.state.does.not.belongs.to.country",
                        "" + state.getStateName() + " state does not belongs to " + country.getCountryName() + " country",
                        state.getStateName(), country.getCountryName()); }
        return country;
    }

    private State validateDistrictWithStateAndGetStateObject(final District district, final Long stateId) {
        final State state = this.stateRepository.findOneWithNotFoundDetection(stateId);
        if (district != null && district.getStateId() != null
                && !district.getStateId() .equals(stateId)) { throw new GeneralPlatformDomainRuleException(
                        "error.msg.address.district.does.not.belongs.to.state",
                        "" + district.getDistrictName() + " district does not belongs to " + state.getStateName() + " state",
                        district.getDistrictName(), state.getStateName()); }
        return state;
    }
    private District validateTalukaWithDistrictAndGetDistrictObject(final Taluka taluka, final Long districtId) {
        final District district = this.districtRepository.findOneWithNotFoundDetection(districtId);
        if (taluka != null && taluka.getDistrictId() != null
                && !taluka.getDistrictId() .equals(districtId)) { throw new GeneralPlatformDomainRuleException(
                        "error.msg.address.taluka.does.not.belongs.to.district",
                        "" + taluka.getTalukaName() + " taluka does not belongs to " + district.getDistrictName() + " district",
                        taluka.getTalukaName(), district.getDistrictName()); }
        return district;
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

        if (address != null) {
            if(addressTypes != null){
                final Set<String> addressTypesSet = Arrays.stream(addressTypes).collect(Collectors.toSet());
                int i = 0;
                for (final String id : addressTypesSet) {
                    final Long addressTypeId = Long.parseLong(id);
                    CodeValue parentAddressType = null;
                    if(i > 0){
                        parentAddressType = this.codeValueRepository.findOneWithNotFoundDetection(Long.parseLong(addressTypes[0]));
                    }
                    final CodeValue addressType = this.codeValueRepository.findOneWithNotFoundDetection(addressTypeId);
                    constructAddressEntity(addressEntities,address,addressType,parentAddressType,entityId, entityTypeEnum, i);
                    i++;
                }
            }else{
                constructAddressEntity(addressEntities, address, null, null, entityId, entityTypeEnum, 0);
            }
        }
        return addressEntities;
    }

    private void constructAddressEntity(Set<AddressEntity> addressEntities, Address address, CodeValue addressType,
            CodeValue parentAddressType, Long entityId, Integer entityTypeEnum, int i) {
        AddressEntity addressEntity = findByAddressTypeAndEntityIdAndEntityTypeEnum(addressType, entityId, entityTypeEnum);
        if (i == 0) {
            if (addressEntity != null) {
                addressEntity.assignAddressAndMakeItActive(address, parentAddressType);
            } else {
                addressEntity = AddressEntity.create(address, addressType, entityId, entityTypeEnum, parentAddressType);
            }
        } else {
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
             * Through error message while constructing the address entity with
             * address type
             */
        }
    }

    private AddressEntity findByAddressTypeAndEntityIdAndEntityTypeEnum(final CodeValue addressType, final Long entityId,
            final Integer entityTypeEnum) {
        return this.addressEntityRepository.findByAddressTypeAndEntityIdAndEntityTypeEnum(addressType, entityId, entityTypeEnum);
    }
}
