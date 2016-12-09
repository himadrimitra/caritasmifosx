package org.apache.fineract.portfolio.cgt.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.staff.data.StaffData;
import org.apache.fineract.organisation.staff.service.StaffReadPlatformService;
import org.apache.fineract.portfolio.cgt.data.CgtData;
import org.apache.fineract.portfolio.cgt.data.CgtDayData;
import org.apache.fineract.portfolio.cgt.domain.Cgt;
import org.apache.fineract.portfolio.cgt.domain.CgtDay;
import org.apache.fineract.portfolio.cgt.domain.CgtDayRepository;
import org.apache.fineract.portfolio.cgt.domain.CgtRepository;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.domain.ClientStatus;
import org.apache.fineract.portfolio.group.domain.Group;
import org.apache.fineract.portfolio.group.domain.GroupRepository;
import org.apache.fineract.portfolio.group.domain.GroupTypes;
import org.apache.fineract.portfolio.note.data.NoteData;
import org.apache.fineract.portfolio.note.domain.NoteType;
import org.apache.fineract.portfolio.note.service.NoteReadPlatformService;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class CgtReadPlatformServiceImpl implements CgtReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final ClientMembersOfEntityMapper clientMembersOfEntityMapper;
    private final StaffReadPlatformService staffReadPlatformService;
    private final GroupRepository groupRepository;
    private final CgtRepository cgtRepository;
    private final CgtDayRepository cgtDayRepository;
    private final NoteReadPlatformService noteReadPlatformService;

    @Autowired
    private CgtReadPlatformServiceImpl(final RoutingDataSource dataSource, PlatformSecurityContext context,
            final StaffReadPlatformService staffReadPlatformService, final GroupRepository groupRepository,
            final CgtRepository cgtRepository, final CgtDayRepository cgtDayRepository, final NoteReadPlatformService noteReadPlatformService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.context = context;
        this.clientMembersOfEntityMapper = new ClientMembersOfEntityMapper();
        this.staffReadPlatformService = staffReadPlatformService;
        this.groupRepository = groupRepository;
        this.cgtRepository = cgtRepository;
        this.cgtDayRepository = cgtDayRepository;
        this.noteReadPlatformService = noteReadPlatformService;
    }

    @Override
    public CgtData retrievetTemplateDataOfEntity(Long entityId) {

        final AppUser currentUser = this.context.authenticatedUser();
        final String sql = "select " + this.clientMembersOfEntityMapper.schema
                + " where (mg.parent_id = ? or mg.id = ?) and mg.level_id = ? and mc.status_enum = ? ";
        final Collection<ClientData> clientMembers = this.jdbcTemplate.query(sql, this.clientMembersOfEntityMapper, new Object[] {
                entityId, entityId, GroupTypes.GROUP.getId(), ClientStatus.ACTIVE.getValue() });
        final Group group = this.groupRepository.findOne(entityId);
        final StaffData staffData = StaffData.lookup(group.getStaff().getId(), group.getStaff().displayName());
        final Collection<StaffData> staffDatas = this.staffReadPlatformService.retrieveAllLoanOfficersInOfficeById(currentUser.getOffice()
                .getId());

        return CgtData.retriveTemplate(staffData, clientMembers, staffDatas);

    }

    private final class ClientMembersOfEntityMapper implements RowMapper<ClientData> {

        private final String schema;

        public ClientMembersOfEntityMapper() {
            final StringBuilder sqlBuilder = new StringBuilder(200);
            sqlBuilder.append(" mc.id as clientId, mc.display_name as displayName ");
            sqlBuilder.append("from m_group mg ");
            sqlBuilder.append("LEFT JOIN m_group_client mgc on mg.id = mgc.group_id LEFT JOIN m_client mc on mgc.client_id = mc.id ");

            this.schema = sqlBuilder.toString();
        }

        @Override
        public ClientData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final String displayName = rs.getString("displayName");
            final Long clientId = JdbcSupport.getLong(rs, "clientId");
            return ClientData.formClientData(clientId, displayName);

        }
    }

    @Override
    public CgtData retrievetCgtDataById(final Long cgtId) {
        final Cgt cgt = this.cgtRepository.findOne(cgtId);
        final Collection<CgtDay> CgtDays = this.cgtDayRepository.findByCgtId(cgtId);
        Collection<CgtDayData> cgtDayDatas = new ArrayList<>(CgtDays.size());
        Collection<NoteData> cgtNoteData = this.noteReadPlatformService.retrieveNotesByResource(cgt.getId(), NoteType.CGT.getValue());
        for (CgtDay cgtDay : CgtDays) {
            Collection<NoteData> cgtDayNoteData = this.noteReadPlatformService.retrieveNotesByResource(cgtDay.getId(), NoteType.CGT_DAY.getValue());
            cgtDayDatas.add(CgtDayData.retriveCgtDayDataFromEntity(cgtDay, cgtDayNoteData));
        }
        return CgtData.retriveCgtData(cgt, cgtDayDatas, cgtNoteData);
    }

    @Override
    public Collection<CgtData> retrievetAllCgtDataByEntityId(final Integer entityId) {
        final Collection<Cgt> cgts = this.cgtRepository.findByEntityTypeId(entityId);
        final Collection<CgtDayData> cgtDayDatas = null;
        List<CgtData> cgtDatas = new ArrayList<>(cgts.size());
        for (Cgt cgt : cgts) {
            Collection<NoteData> cgtNoteData = this.noteReadPlatformService.retrieveNotesByResource(cgt.getId(), NoteType.CGT.getValue());
            cgtDatas.add(CgtData.retriveCgtData(cgt, cgtDayDatas, cgtNoteData));
        }
        return cgtDatas;
    }

}
