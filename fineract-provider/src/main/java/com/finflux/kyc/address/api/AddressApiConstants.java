package com.finflux.kyc.address.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.finflux.kyc.address.data.AddressData;

public class AddressApiConstants {

    public static final String ADDRESSES_RESOURCE_NAME = "addresses";

    /**
     * Template
     */
    public static final String addressTypeOptions = "AddressType";

    /**
     * Address Entity Types Enumerations
     */
    public static final String enumTypeClients = "CLIENTS";
    public static final String enumTypeGroups = "GROUPS";
    public static final String enumTypeCenters = "CENTERS";
    public static final String enumTypeOffices = "OFFICES";
    public static final String enumTypeBusinessCorrespondents = "BUSINESSCORRESPONDENTS";
    public static final String enumTypeVillages = "VILLAGES";

    /**
     * Address Parameters
     */

    public static final String addressesParamName = "addresses";
    public static final String addressIdParamName = "addressId";
    public static final String addressTypesParamName = "addressTypes";
    public static final String entityIdParamName = "entityId";
    public static final String entityTypeEnumParamName = "entityTypeEnum";
    public static final String houseNoParamName = "houseNo";
    public static final String streetNoParamName = "streetNo";
    public final static String addressLineOneParamName = "addressLineOne";
    public final static String addressLineTwoParamName = "addressLineTwo";
    public final static String landmarkParamName = "landmark";
    public final static String villageTownParamName = "villageTown";
    public final static String talukaIdParamName = "talukaId";
    public final static String districtIdParamName = "districtId";
    public final static String stateIdParamName = "stateId";
    public final static String countryIdParamName = "countryId";
    public final static String postalCodeParamName = "postalCode";
    public final static String latitudeParamName = "latitude";
    public final static String longitudeParamName = "longitude";
    public final static String documentIdParamName = "documentId";
    public final static String ekyc = "eKyc";
    /**
     * Common Parameters
     */
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";

    /**
     * Address Template Data Parameters
     */
    public static final Set<String> ADDRESS_TEMPLATE_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList());

    /**
     * Request Data Parameters
     */
    public static final Set<String> CREATE_ADDRESS_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(addressesParamName,
            addressTypesParamName, entityIdParamName, entityTypeEnumParamName, houseNoParamName, streetNoParamName, addressLineOneParamName,
            addressLineTwoParamName, landmarkParamName, villageTownParamName, talukaIdParamName, districtIdParamName, stateIdParamName,
            countryIdParamName, postalCodeParamName, latitudeParamName, longitudeParamName, localeParamName, dateFormatParamName));

    public static final Set<String> UPDATE_ADDRESS_REQUEST_DATA_PARAMETERS = new HashSet<>(
            Arrays.asList(addressesParamName, addressIdParamName, addressTypesParamName, entityIdParamName, entityTypeEnumParamName,
                    houseNoParamName, streetNoParamName, addressLineOneParamName, addressLineTwoParamName, landmarkParamName,
                    villageTownParamName, talukaIdParamName, districtIdParamName, stateIdParamName, countryIdParamName, postalCodeParamName,
                    latitudeParamName, longitudeParamName, localeParamName, dateFormatParamName));

    /**
     * These parameters will match the class level parameters of
     * {@link AddressData}. Where possible, we try to get response parameters to
     * match those of request parameters.
     */
    public static final Set<String> ADDRESS_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList());
}
