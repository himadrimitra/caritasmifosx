package com.finflux.risk.profilerating.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.organisation.office.service.OfficeReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.finflux.risk.profilerating.data.ComputeProfileRatingTemplateData;
import com.finflux.risk.profilerating.data.ProfileRatingType;
import com.finflux.risk.profilerating.data.ScopeEntityType;

@Service
public class ComputeProfileRatingReadPlatformServiceImpl implements ComputeProfileRatingReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final OfficeReadPlatformService officeReadPlatformService;

    @Autowired
    public ComputeProfileRatingReadPlatformServiceImpl(final RoutingDataSource dataSource,
            final OfficeReadPlatformService officeReadPlatformService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.officeReadPlatformService = officeReadPlatformService;
    }

    @Override
    public ComputeProfileRatingTemplateData retrieveTemplate() {
        final Collection<EnumOptionData> scopeEntityTypeOptions = ScopeEntityType.options();
        final Collection<EnumOptionData> entityTypeOptions = ProfileRatingType.options();
        final Collection<OfficeData> officeOptions = this.officeReadPlatformService.retrieveAllOfficesForDropdown();
        return ComputeProfileRatingTemplateData.template(scopeEntityTypeOptions, entityTypeOptions, officeOptions);
    }

    @Override
    public List<Map<String, Object>> getAllClientIdsFromOffice(final Long officeId) {
        try {
            final StringBuilder sb = new StringBuilder(200);
            sb.append("SELECT c.id AS clientId FROM m_client c JOIN m_office o ON o.id = " + officeId
                    + " AND o.id = c.office_id WHERE c.status_enum = 300");
            return this.jdbcTemplate.queryForList(sb.toString());
        } catch (final EmptyResultDataAccessException e) {}
        return null;
    }
    
    @Override
    public List<Map<String, Object>> getAllCenterIdsFromOffice(final Long officeId) {
        try {
            final StringBuilder sb = new StringBuilder(200);
            sb.append("SELECT c.id AS centerId FROM m_group c WHERE c.level_id = 1 AND c.office_id = " + officeId + "");
            return this.jdbcTemplate.queryForList(sb.toString());
        } catch (final EmptyResultDataAccessException e) {}
        return null;
    }
}
