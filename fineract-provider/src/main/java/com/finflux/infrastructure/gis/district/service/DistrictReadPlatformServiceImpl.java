package com.finflux.infrastructure.gis.district.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.staff.data.StaffData;
import org.apache.fineract.portfolio.village.data.VillageData;
import org.apache.fineract.portfolio.village.data.VillageTimelineData;
import org.apache.fineract.portfolio.village.domain.VillageTypeEnumerations;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.infrastructure.gis.district.data.DistrictData;
import com.finflux.infrastructure.gis.district.domain.DistrictStatus;
import com.finflux.infrastructure.gis.taluka.data.TalukaData;
import com.finflux.infrastructure.gis.taluka.services.TalukaReadPlatformServices;
import com.finflux.task.configuration.service.TaskConfigurationUtils;
import com.finflux.task.data.TaskConfigEntityType;
import com.finflux.task.data.TaskEntityType;

@Service
public class DistrictReadPlatformServiceImpl implements DistrictReadPlatformService {

    @SuppressWarnings("unused")
    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    private final TalukaReadPlatformServices talukaReadPlatformService;
    private final TaskConfigurationUtils taskConfigurationUtils;

    @Autowired
    public DistrictReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final TalukaReadPlatformServices talukaReadPlatformService, final TaskConfigurationUtils taskConfigurationUtils) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.talukaReadPlatformService = talukaReadPlatformService;
        this.taskConfigurationUtils = taskConfigurationUtils;
    }

    @Override
    public DistrictData retrieveOne(final Long districtId, final boolean isTemplateRequired) {
        try {
            if (districtId != null && districtId > 0) {
                Collection<TalukaData> talukaDatas= null;
                if(isTemplateRequired){
                talukaDatas = this.talukaReadPlatformService.retrieveAllTalukaDataByDistrictId(districtId);
                }
                final Boolean isWorkflowEnabled = this.taskConfigurationUtils.isWorkflowEnabled(TaskConfigEntityType.DISTRICTONBOARDING);
                final DistrictDataMapper dataMapper = new DistrictDataMapper(talukaDatas, isWorkflowEnabled);
                final String sql = "SELECT " + dataMapper.schema() + " WHERE d.id = ? ";
                return this.jdbcTemplate.queryForObject(sql, dataMapper, new Object[] { TaskEntityType.DISTRICT, districtId });
            }
        } catch (final EmptyResultDataAccessException e) {}
        return null;
    }

    @Override
    public Collection<DistrictData> retrieveAllDistrictDataByDistrictIds(final List<Long> districtIds, final boolean isTemplateRequired) {
        try {
            if (districtIds != null && !districtIds.isEmpty()) {
                final String districtIdsStr = StringUtils.join(districtIds, ',');
                Collection<TalukaData> talukaDatas = null;
                if(isTemplateRequired){
                talukaDatas = this.talukaReadPlatformService.retrieveAllTalukaDataByDistrictIds(districtIds);
                }
                final Boolean isWorkflowEnabled = this.taskConfigurationUtils.isWorkflowEnabled(TaskConfigEntityType.DISTRICTONBOARDING);
                final DistrictDataMapper dataMapper = new DistrictDataMapper(talukaDatas, isWorkflowEnabled);
                final String sql = "SELECT " + dataMapper.schema() + " WHERE d.id IN (" + districtIdsStr + ") ";
                return this.jdbcTemplate.query(sql, dataMapper, new Object[] { TaskEntityType.DISTRICT });
            }
        } catch (final EmptyResultDataAccessException e) {}
        return null;
    }

    @Override
    public Collection<DistrictData> retrieveAllDistrictDataByStateId(final Long stateId) {
        if (stateId != null && stateId > 0) {
            final String districtIdsSql = "SELECT s.id FROM f_district s WHERE s.state_id = " + stateId + "";
            final List<Long> districtIds = this.jdbcTemplate.queryForList(districtIdsSql, Long.class);
            final Collection<TalukaData> talukaDatas = this.talukaReadPlatformService.retrieveAllTalukaDataByDistrictIds(districtIds);
            final Boolean isWorkflowEnabled = this.taskConfigurationUtils.isWorkflowEnabled(TaskConfigEntityType.DISTRICTONBOARDING);
            final DistrictDataMapper dataMapper = new DistrictDataMapper(talukaDatas, isWorkflowEnabled);
            final String sql = "SELECT " + dataMapper.schema() + " WHERE d.state_id = ? ";
            return this.jdbcTemplate.query(sql, dataMapper, new Object[] { TaskEntityType.DISTRICT, stateId });
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<DistrictData> retrieveAllDistrictDataByStateIds(final List<Long> stateIds) {
        try {
            if (stateIds != null && !stateIds.isEmpty()) {
                final String stateIdsStr = StringUtils.join(stateIds, ',');
                final String districtIdsSql = "SELECT s.id FROM f_district s WHERE s.state_id IN( " + stateIdsStr + " ) ";
                final List<Long> districtIds = this.jdbcTemplate.queryForList(districtIdsSql, Long.class);
                final Collection<TalukaData> talukaDatas = this.talukaReadPlatformService.retrieveAllTalukaDataByDistrictIds(districtIds);
                final Boolean isWorkflowEnabled = this.taskConfigurationUtils.isWorkflowEnabled(TaskConfigEntityType.DISTRICTONBOARDING);
                final DistrictDataMapper dataMapper = new DistrictDataMapper(talukaDatas, isWorkflowEnabled);
                final String sql = "SELECT " + dataMapper.schema() + " WHERE d.state_id IN(" + stateIdsStr + ") ";
                return this.jdbcTemplate.query(sql, dataMapper, new Object[] { TaskEntityType.DISTRICT });
            }
        } catch (final EmptyResultDataAccessException e) {}
        return null;
    }

    private static final class DistrictDataMapper implements RowMapper<DistrictData> {

        private final String schema;
        private final Collection<TalukaData> talukaDatas;
        private final Boolean isWorkflowEnabled;

        public DistrictDataMapper(final Collection<TalukaData> talukaDatas, final Boolean isWorkflowEnabled) {
            this.talukaDatas = talukaDatas;
            this.isWorkflowEnabled = isWorkflowEnabled;
            
            final StringBuilder builder = new StringBuilder(200);
            builder.append("d.id As districtId, d.state_id AS stateId, d.iso_district_code AS isoDistrictCode, ");
            builder.append("d.district_name As districtName, d.status_enum as statusEnum, ");
            builder.append("d.activation_date as activationDate, d.rejectedon_date as rejectedonDate, ");
            builder.append("task.id as workflowId ") ;
            builder.append("FROM f_district d ");
            builder.append("LEFT JOIN f_task task ON task.entity_type=? and task.parent_id is null and task.entity_id = d.id ");
            this.schema = builder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public DistrictData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long districtId = rs.getLong("districtId");
            final Long stateId = rs.getLong("stateId");
            final String isoDistrictCode = rs.getString("isoDistrictCode");
            final String districtName = rs.getString("districtName");
            Collection<TalukaData> talukaDatas = new ArrayList<>();
            if (this.talukaDatas != null && this.talukaDatas.size() > 0) {
                for (final TalukaData talukaData : this.talukaDatas) {
                    if (talukaData.getDistrictId() == districtId) {
                        talukaDatas.add(talukaData);
                    }
                }
            }
            final Integer statusEnum = rs.getInt("statusEnum");
            final EnumOptionData status = DistrictStatus.fromInt(statusEnum).getEnumOptionData();
            final LocalDate activationDate = JdbcSupport.getLocalDate(rs, "activationDate");
            final LocalDate rejectedonDate = JdbcSupport.getLocalDate(rs, "rejectedonDate");
            final Long workflowId = JdbcSupport.getLong(rs, "workflowId");
            return DistrictData.instance(districtId, stateId, isoDistrictCode, districtName, talukaDatas, this.isWorkflowEnabled, status,
                    activationDate, rejectedonDate, workflowId);
        }
    }

    @Override
    public Collection<VillageData> retrieveVillages(final Long distictId, final Integer status) {
        final AppUser currentUser = this.context.authenticatedUser();
        final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySearchString = hierarchy + "%";
        final VillageDataMapper mapper = new VillageDataMapper();
        final String sql = "SELECT " + mapper.schema() + " WHERE fa.district_id = ? AND o.hierarchy like ? AND v.status = ? ";
        return this.jdbcTemplate.query(sql, mapper, new Object[] { distictId, hierarchySearchString, status });
    }

    private static final class VillageDataMapper implements RowMapper<VillageData> {

        private final String schema;;

        public VillageDataMapper() {
            final StringBuilder builder = new StringBuilder(200);
            builder.append(" v.id AS villageId, v.external_id AS externalId, v.office_id AS officeId, o.name AS officeName,");
            builder.append(" v.village_code AS villageCode, v.village_name AS villageName, v.counter AS counter, v.status AS status");
            builder.append(" FROM chai_villages v");
            builder.append(" JOIN m_office o ON o.id = v.office_id");
            builder.append(" JOIN f_address_entity fae ON fae.entity_id = v.id AND fae.entity_type_enum=6");
            builder.append(" JOIN f_address fa ON fa.id = fae.address_id");
            this.schema = builder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public VillageData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long villageId = rs.getLong("villageId");
            final String externalId = rs.getString("externalId");
            final Long officeId = rs.getLong("officeId");
            final String officeName = rs.getString("officeName");
            final String villageCode = rs.getString("villageCode");
            final String villageName = rs.getString("villageName");
            final Long counter = rs.getLong("counter");
            final Integer status = JdbcSupport.getInteger(rs, "status");
            final EnumOptionData statusName = VillageTypeEnumerations.status(status);
            final Long workflowId = null;
            final Boolean isWorkflowEnabled = null;
            final VillageTimelineData timeline = null;
            StaffData staff = null;
            return VillageData.instance(villageId, externalId, officeId, officeName, villageCode, villageName, counter, statusName,
                    timeline, workflowId, isWorkflowEnabled, staff);
        }
    }
}
