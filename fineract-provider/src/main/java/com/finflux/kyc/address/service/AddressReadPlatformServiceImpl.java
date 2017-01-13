package com.finflux.kyc.address.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.infrastructure.gis.country.data.CountryData;
import com.finflux.infrastructure.gis.country.service.CountryReadPlatformService;
import com.finflux.infrastructure.gis.district.data.DistrictData;
import com.finflux.infrastructure.gis.district.service.DistrictReadPlatformService;
import com.finflux.infrastructure.gis.state.data.StateData;
import com.finflux.infrastructure.gis.state.service.StateReadPlatformService;
import com.finflux.infrastructure.gis.taluka.data.TalukaData;
import com.finflux.infrastructure.gis.taluka.services.TalukaReadPlatformServices;
import com.finflux.kyc.address.api.AddressApiConstants;
import com.finflux.kyc.address.data.AddressData;
import com.finflux.kyc.address.data.AddressEntityData;
import com.finflux.kyc.address.data.AddressEntityTypeEnums;
import com.finflux.kyc.address.data.AddressTemplateData;
import com.finflux.kyc.address.domain.Address;
import com.finflux.kyc.address.domain.AddressRepositoryWrapper;
import com.finflux.kyc.address.exception.AddressEntityTypeNotSupportedException;
import com.finflux.kyc.address.exception.AddressNotFoundException;

@Service
public class AddressReadPlatformServiceImpl implements AddressReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final AddressEntityDataMapper addressEntityDataMapper;
    private final CodeValueReadPlatformService codeValueReadPlatformService;
    private final AddressRepositoryWrapper addressRepository;
    private final DistrictReadPlatformService districtReadPlatformService;
    private final StateReadPlatformService stateReadPlatformService;
    private final CountryReadPlatformService countryReadPlatformService;
    private final TalukaReadPlatformServices talukaReadPlatformService;

    @Autowired
    public AddressReadPlatformServiceImpl(final RoutingDataSource dataSource,
            final CodeValueReadPlatformService codeValueReadPlatformService, final AddressRepositoryWrapper addressRepository,
            final DistrictReadPlatformService districtReadPlatformService, final StateReadPlatformService stateReadPlatformService,
            final CountryReadPlatformService countryReadPlatformService, final TalukaReadPlatformServices talukaReadPlatformService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.addressEntityDataMapper = new AddressEntityDataMapper();
        this.codeValueReadPlatformService = codeValueReadPlatformService;
        this.addressRepository = addressRepository;
        this.districtReadPlatformService = districtReadPlatformService;
        this.stateReadPlatformService = stateReadPlatformService;
        this.countryReadPlatformService = countryReadPlatformService;
        this.talukaReadPlatformService = talukaReadPlatformService;
    }

    @Override
    public AddressTemplateData retrieveTemplate() {
        final List<CodeValueData> addressTypeOptions = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode(AddressApiConstants.addressTypeOptions));
        final Collection<EnumOptionData> addressEntityTypeOptions = AddressEntityTypeEnums.entityTypeOptions();
        final Collection<CountryData> countryDatas = this.countryReadPlatformService.retrieveAll();
        return AddressTemplateData.template(addressTypeOptions, addressEntityTypeOptions, countryDatas);
    }

    @Override
    public AddressData retrieveOne(final String entityType, final Long entityId, final Long addressId) {
        try {
            final Address address = this.addressRepository.findOneWithNotFoundDetection(addressId);
            final Collection<TalukaData> talukaDatas = getTalukaDatas(address, null);
            final Collection<DistrictData> districtDatas = getDistrictDatas(address, null);
            final Collection<StateData> stateDatas = getStateDatas(address, null);
            final Collection<CountryData> countryDatas = getCountryDatas(address, null);
            final AddressEntityTypeEnums addressEntityType = AddressEntityTypeEnums.getEntityType(entityType);
            if (addressEntityType == null) { throw new AddressEntityTypeNotSupportedException(entityType); }
            final Integer entityTypeEnum = addressEntityType.getValue();
            // this.addressBusinessValidators.validateAddressEntityIdAndEntityType(entityTypeEnum,
            // entityId);
            final Collection<AddressEntityData> addressEntityDatas = retrieveAddressEntityData(entityId, entityTypeEnum, addressId);
            if (addressEntityDatas != null) {
                final AddressDataMapper dataMapper = new AddressDataMapper(districtDatas, stateDatas, countryDatas, addressEntityDatas,
                        talukaDatas);
                final String sql = "SELECT " + dataMapper.schema() + " WHERE a.id = ? ";
                return this.jdbcTemplate.queryForObject(sql, dataMapper, new Object[] { addressId });
            }
        } catch (final EmptyResultDataAccessException e) {
            throw new AddressNotFoundException(addressId);
        }
        return null;
    }

    @SuppressWarnings("unused")
    private Collection<CountryData> getCountryDatas(final Address address,
            final List<Map<String, Object>> listOfMapForCountryStateDistrictIds) {
        Collection<CountryData> countryDatas = null;
        if (listOfMapForCountryStateDistrictIds != null && listOfMapForCountryStateDistrictIds.size() > 0) {
            final List<Long> countryIds = new ArrayList<Long>();
            for (final Map<String, Object> mapIds : listOfMapForCountryStateDistrictIds) {
                if (mapIds.containsKey("countryId") && mapIds.get("countryId") != null) {
                    final Long countryId = Long.parseLong(mapIds.get("countryId").toString());
                    if (!countryIds.contains(countryId)) {
                        countryIds.add(countryId);
                    }
                }
            }
            if (!countryIds.isEmpty()) {
                countryDatas = this.countryReadPlatformService.retrieveAllCountryDataByCountryIds(countryIds);
            }
        } else if (address != null && address.getCountry() != null) {
            final Long countryId = address.getCountry().getId();
            final CountryData countryData = this.countryReadPlatformService.retrieveOne(countryId);
            if (countryData != null) {
                countryDatas = new ArrayList<CountryData>();
                countryDatas.add(countryData);
            }
        }
        return countryDatas;
    }

    @SuppressWarnings("unused")
    private Collection<StateData> getStateDatas(final Address address, final List<Map<String, Object>> listOfMapForCountryStateDistrictIds) {
        Collection<StateData> stateDatas = null;
        if (listOfMapForCountryStateDistrictIds != null && listOfMapForCountryStateDistrictIds.size() > 0) {
            final List<Long> stateIds = new ArrayList<Long>();
            for (final Map<String, Object> mapIds : listOfMapForCountryStateDistrictIds) {
                if (mapIds.containsKey("stateId") && mapIds.get("stateId") != null) {
                    final Long stateId = Long.parseLong(mapIds.get("stateId").toString());
                    if (!stateIds.contains(stateId)) {
                        stateIds.add(stateId);
                    }
                }
            }
            if (!stateIds.isEmpty()) {
                stateDatas = this.stateReadPlatformService.retrieveAllStateDataByStateIds(stateIds);
            }
        } else if (address != null && address.getState() != null) {
            final Long stateId = address.getState().getId();
            final StateData stateData = this.stateReadPlatformService.retrieveOne(stateId);
            if (stateData != null) {
                stateDatas = new ArrayList<StateData>();
                stateDatas.add(stateData);
            }
        }
        return stateDatas;
    }

    @SuppressWarnings("unused")
    private Collection<DistrictData> getDistrictDatas(final Address address,
            final List<Map<String, Object>> listOfMapForCountryStateDistrictIds) {
        Collection<DistrictData> districtDatas = null;
        if (listOfMapForCountryStateDistrictIds != null && listOfMapForCountryStateDistrictIds.size() > 0) {
            final List<Long> districtIds = new ArrayList<Long>();
            for (final Map<String, Object> mapIds : listOfMapForCountryStateDistrictIds) {
                if (mapIds.containsKey("districtId") && mapIds.get("districtId") != null) {
                    final Long districtId = Long.parseLong(mapIds.get("districtId").toString());
                    if (!districtIds.contains(districtId)) {
                        districtIds.add(districtId);
                    }
                }
            }
            if (!districtIds.isEmpty()) {
                districtDatas = this.districtReadPlatformService.retrieveAllDistrictDataByDistrictIds(districtIds);
            }
        } else if (address != null && address.getDistrict() != null) {
            final Long districtId = address.getDistrict().getId();
            final DistrictData districtData = this.districtReadPlatformService.retrieveOne(districtId);
            if (districtData != null) {
                districtDatas = new ArrayList<DistrictData>();
                districtDatas.add(districtData);
            }
        }
        return districtDatas;
    }

    @SuppressWarnings("unused")
    private Collection<TalukaData> getTalukaDatas(final Address address, final List<Map<String, Object>> listOfMapForCountryStateDistrictIds) {
        Collection<TalukaData> talukaDatas = null;
        if (listOfMapForCountryStateDistrictIds != null && listOfMapForCountryStateDistrictIds.size() > 0) {
            final List<Long> talukaIds = new ArrayList<Long>();
            for (final Map<String, Object> mapIds : listOfMapForCountryStateDistrictIds) {
                if (mapIds.containsKey("talukaId") && mapIds.get("talukaId") != null) {
                    final Long talukaId = Long.parseLong(mapIds.get("talukaId").toString());
                    if (!talukaIds.contains(talukaId)) {
                        talukaIds.add(talukaId);
                    }
                }
            }
            if (!talukaIds.isEmpty()) {
                talukaDatas = this.talukaReadPlatformService.retrieveAllTalukaDataByTalukaIds(talukaIds);
            }
        } else if (address != null && address.getTaluka() != null) {
            final Long talukaId = address.getTaluka().getId();
            final TalukaData talukaData = this.talukaReadPlatformService.retrieveOne(talukaId);
            if (talukaData != null) {
                talukaDatas = new ArrayList<TalukaData>();
                talukaDatas.add(talukaData);
            }
        }
        return talukaDatas;
    }

    @SuppressWarnings({ "unused" })
    @Override
    public Collection<AddressData> retrieveAddressesByEntityTypeAndEntityId(final String entityType, final Long entityId) {
        try {
            final AddressEntityTypeEnums addressEntityType = AddressEntityTypeEnums.getEntityType(entityType);
            if (addressEntityType == null) { throw new AddressEntityTypeNotSupportedException(entityType); }
            final Integer entityTypeEnum = addressEntityType.getValue();
            // this.addressBusinessValidators.validateAddressEntityIdAndEntityType(entityTypeEnum,
            // entityId);
            final Collection<AddressEntityData> addressEntityDatas = retrieveAddressEntityData(entityId, entityTypeEnum, null);
            if (addressEntityDatas != null) {
                Set<Long> addressIds = new HashSet<Long>();
                for (final AddressEntityData addressEntityData : addressEntityDatas) {
                    addressIds.add(addressEntityData.getAddressId());
                }
                if (!addressIds.isEmpty()) {
                    final String addressIdsAsString = StringUtils.join(addressIds, ',');
                    final String sqlForCountryStateDistrictIds = "SELECT a.taluka_id AS talukaId, a.district_id AS districtId, a.state_id AS stateId,a.country_id AS countryId FROM f_address a WHERE a.id IN ("
                            + addressIdsAsString + ") ";
                    final List<Map<String, Object>> listOfMapForCountryStateDistrictIds = this.jdbcTemplate
                            .queryForList(sqlForCountryStateDistrictIds);
                    final Collection<TalukaData> talukaDatas = getTalukaDatas(null, listOfMapForCountryStateDistrictIds);
                    final Collection<DistrictData> districtDatas = getDistrictDatas(null, listOfMapForCountryStateDistrictIds);
                    final Collection<StateData> stateDatas = getStateDatas(null, listOfMapForCountryStateDistrictIds);
                    final Collection<CountryData> countryDatas = getCountryDatas(null, listOfMapForCountryStateDistrictIds);
                    final AddressDataMapper dataMapper = new AddressDataMapper(districtDatas, stateDatas, countryDatas, addressEntityDatas,
                            talukaDatas);
                    final String sql = "SELECT " + dataMapper.schema() + " WHERE a.id IN (" + addressIdsAsString + ") ";
                    return this.jdbcTemplate.query(sql, dataMapper);
                }
            }
        } catch (final EmptyResultDataAccessException e) {

        }
        return new ArrayList<AddressData>();
    }

    @Override
    public Long countOfAddressByEntityTypeAndEntityId(AddressEntityTypeEnums entityType, Long entityId) {
        AddressCountMapper addressCountMapper = new AddressCountMapper();
        final Integer entityTypeEnum = entityType.getValue();
        final String sql = "SELECT " + addressCountMapper.schema() + " where ae.entity_id = ? AND ae.entity_type_enum = ? ";
        Long addressCount =  this.jdbcTemplate.queryForObject(sql, addressCountMapper, new Object[] { entityId, entityTypeEnum });
        if(addressCount==null){
            addressCount = 0L;
        }
        return addressCount;
    }

    private static final class AddressDataMapper implements RowMapper<AddressData> {

        private final String schema;
        private final Collection<TalukaData> talukaDatas;
        private final Collection<DistrictData> districtDatas;
        private final Collection<StateData> stateDatas;
        private final Collection<CountryData> countryDatas;
        private final Collection<AddressEntityData> addressEntityDatas;

        public AddressDataMapper(final Collection<DistrictData> districtDatas, final Collection<StateData> stateDatas,
                final Collection<CountryData> countryDatas, final Collection<AddressEntityData> addressEntityDatas,
                final Collection<TalukaData> talukaDatas) {
            this.districtDatas = districtDatas;
            this.stateDatas = stateDatas;
            this.countryDatas = countryDatas;
            this.addressEntityDatas = addressEntityDatas;
            this.talukaDatas = talukaDatas;

            final StringBuilder sqlBuilder = new StringBuilder(200);
            sqlBuilder.append("a.id AS addressId, a.house_no AS houseNo, a.street_no AS streetNo, ");
            sqlBuilder.append("a.address_line_one AS addressLineOne, a.address_line_two AS addressLineTwo, a.landmark AS landmark, ");
            sqlBuilder.append("a.village_town AS villageTown,a.taluka_id AS talukaId, ");
            sqlBuilder.append("a.district_id AS districtId,a.state_id AS stateId,a.country_id AS countryId, ");
            sqlBuilder.append("a.postal_code AS postalCode, a.latitude AS latitude,a.longitude AS longitude ");
            sqlBuilder.append("FROM f_address a ");
            this.schema = sqlBuilder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @SuppressWarnings({ "unused" })
        @Override
        public AddressData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

            final Long addressId = rs.getLong("addressId");
            final String houseNo = rs.getString("houseNo");
            final String streetNo = rs.getString("streetNo");
            final String addressLineOne = rs.getString("addressLineOne");
            final String addressLineTwo = rs.getString("addressLineTwo");
            final String landmark = rs.getString("landmark");
            final String villageTown = rs.getString("villageTown");

            final Long talukaId = rs.getLong("talukaId");
            TalukaData talukaData = null;
            if (this.talukaDatas != null && this.talukaDatas.size() > 0) {
                for (final TalukaData tt : this.talukaDatas) {
                    if (tt.getTalukaId().equals(talukaId)) {
                        talukaData = tt;
                        break;
                    }
                }
            }

            final Long districtId = rs.getLong("districtId");
            DistrictData districtData = null;
            if (this.districtDatas != null && this.districtDatas.size() > 0) {
                for (final DistrictData dd : this.districtDatas) {
                    if (dd.getDistrictId().equals(districtId)) {
                        districtData = dd;
                        break;
                    }
                }
            }

            final Long stateId = rs.getLong("stateId");
            StateData stateData = null;
            if (this.stateDatas != null && this.stateDatas.size() > 0) {
                for (final StateData sd : this.stateDatas) {
                    if (sd.getStateId().equals(stateId)) {
                        stateData = sd;
                        break;
                    }
                }
            }

            final Long countryId = rs.getLong("countryId");
            CountryData countryData = null;
            if (this.countryDatas != null && this.countryDatas.size() > 0) {
                for (final CountryData cd : this.countryDatas) {
                    if (cd.getCountryId().equals(countryId)) {
                        countryData = cd;
                        break;
                    }
                }
            }

            final String postalCode = rs.getString("postalCode");
            final BigDecimal latitude = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "latitude");
            final BigDecimal longitude = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "longitude");

            final Collection<AddressEntityData> addressEntityData = new ArrayList<AddressEntityData>();
            if (this.addressEntityDatas != null && this.addressEntityDatas.size() > 0) {
                for (AddressEntityData a : this.addressEntityDatas) {
                    if (a.getAddressId().equals(addressId)) {
                        addressEntityData.add(a);
                    }
                }
            }

            return AddressData.instance(addressId, houseNo, streetNo, addressLineOne, addressLineTwo, landmark, villageTown, talukaData,
                    districtData, stateData, countryData, postalCode, latitude, longitude, addressEntityData);
        }
    }

    private Collection<AddressEntityData> retrieveAddressEntityData(final Long entityId, final Integer entityTypeEnum, final Long addressId) {
        if (addressId != null && addressId > 0) {
            final String sql = "SELECT " + this.addressEntityDataMapper.schema()
                    + " where ae.address_id = ? AND ae.entity_id = ? AND ae.entity_type_enum = ? ";
            return this.jdbcTemplate.query(sql, this.addressEntityDataMapper, new Object[] { addressId, entityId, entityTypeEnum });
        } else if (entityId != null && entityTypeEnum != null) {
            final String sql = "SELECT " + this.addressEntityDataMapper.schema() + " where ae.entity_id = ? AND ae.entity_type_enum = ? ";
            return this.jdbcTemplate.query(sql, this.addressEntityDataMapper, new Object[] { entityId, entityTypeEnum });
        }
        return null;
    }

    private static final class AddressEntityDataMapper implements RowMapper<AddressEntityData> {

        private final String schema;

        public AddressEntityDataMapper() {
            final StringBuilder sqlBuilder = new StringBuilder(200);
            sqlBuilder.append("ae.id AS id, ae.address_id AS addressId, ");
            sqlBuilder
                    .append("ae.address_type AS addressTypeId,addressTypeCv.code_value AS addressTypeName,addressTypeCv.is_active AS addressTypeIsActive, ");
            sqlBuilder.append("ae.entity_id AS entityId, ae.entity_type_enum AS entityTypeEnum, ae.is_active AS isActive, ");
            sqlBuilder
                    .append("ae.parent_address_type AS parentAddressTypeId, paddressTypeCv.code_value AS parentAddressTypeName,paddressTypeCv.is_active AS parentAddressTypeIsActive ");
            sqlBuilder.append("FROM f_address_entity ae ");
            sqlBuilder.append("LEFT JOIN m_code_value addressTypeCv ON addressTypeCv.id = ae.address_type ");
            sqlBuilder.append("LEFT JOIN m_code_value paddressTypeCv ON paddressTypeCv.id = ae.parent_address_type ");
            this.schema = sqlBuilder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public AddressEntityData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final Long addressId = rs.getLong("addressId");
            final Long addressTypeId = rs.getLong("addressTypeId");
            final String addressTypeName = rs.getString("addressTypeName");
            final boolean addressTypeIsActive = rs.getBoolean("addressTypeIsActive");
            CodeValueData addressType = null;
            if (addressTypeId != null && addressTypeId > 0) {
                addressType = CodeValueData.instance(addressTypeId, addressTypeName, addressTypeIsActive);
            }
            final Long entityId = rs.getLong("entityId");
            final Integer entityTypeEnum = rs.getInt("entityTypeEnum");
            final EnumOptionData entityType = AddressEntityTypeEnums.addressEntity(entityTypeEnum);
            final boolean isActive = rs.getBoolean("isActive");

            final Long parentAddressTypeId = rs.getLong("parentAddressTypeId");
            final String parentAddressTypeName = rs.getString("parentAddressTypeName");
            final boolean parentAddressTypeIsActive = rs.getBoolean("parentAddressTypeIsActive");
            CodeValueData parentAddressType = null;
            if (parentAddressTypeId != null && parentAddressTypeId > 0) {
                parentAddressType = CodeValueData.instance(parentAddressTypeId, parentAddressTypeName, parentAddressTypeIsActive);
            }
            return AddressEntityData.instance(id, addressId, addressType, entityId, entityType, isActive, parentAddressType);
        }
    }


    private static final class AddressCountMapper implements RowMapper<Long> {

        private final String schema;

        public AddressCountMapper() {
            final StringBuilder sqlBuilder = new StringBuilder(200);
            sqlBuilder.append("count(1) AS count ");
            sqlBuilder.append("FROM f_address_entity ae ");
            this.schema = sqlBuilder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public Long mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long count = rs.getLong("count");
            return count;
        }
    }
}