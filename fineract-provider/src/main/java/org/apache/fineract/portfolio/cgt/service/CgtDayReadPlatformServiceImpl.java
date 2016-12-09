package org.apache.fineract.portfolio.cgt.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.staff.data.StaffData;
import org.apache.fineract.organisation.staff.service.StaffReadPlatformService;
import org.apache.fineract.portfolio.cgt.data.CgtDayClientData;
import org.apache.fineract.portfolio.cgt.data.CgtDayData;
import org.apache.fineract.portfolio.cgt.domain.CgtDayClientAttendanceStatusType;
import org.apache.fineract.portfolio.cgt.domain.CgtDayStatusType;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.note.data.NoteData;
import org.apache.fineract.portfolio.note.domain.NoteType;
import org.apache.fineract.portfolio.note.service.NoteReadPlatformService;
import org.apache.fineract.useradministration.data.AppUserData;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class CgtDayReadPlatformServiceImpl implements CgtDayReadPlatformService {

    private final StaffReadPlatformService staffReadPlatformService;
    private final PlatformSecurityContext context;
    private final NoteReadPlatformService noteReadPlatformService;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    protected CgtDayReadPlatformServiceImpl(final StaffReadPlatformService staffReadPlatformService, final PlatformSecurityContext context,
            final NoteReadPlatformService noteReadPlatformService, final RoutingDataSource dataSource) {
        this.staffReadPlatformService = staffReadPlatformService;
        this.context = context;
        this.noteReadPlatformService = noteReadPlatformService;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public CgtDayData retrievetCgtDayDataById(final Long cgtDayId) {

        this.context.authenticatedUser();
        CgtDayDataMapper rm = new CgtDayDataMapper();
        final String sql = "select " + rm.getSchema() + " WHERE cd.id = ?";
        return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { cgtDayId });
        
    }

    public List<EnumOptionData> retrieveAttendanceTypeOptions() {
        List<EnumOptionData> cgtDayClientAttendanceStatusTypes = new ArrayList<>(CgtDayClientAttendanceStatusType.values().length);
        for (CgtDayClientAttendanceStatusType cgtDayClientAttendanceStatusType : CgtDayClientAttendanceStatusType.values()) {
            cgtDayClientAttendanceStatusTypes.add(new EnumOptionData(cgtDayClientAttendanceStatusType.getValue().longValue(),
                    cgtDayClientAttendanceStatusType.getCode(), cgtDayClientAttendanceStatusType.name()));
        }
        return cgtDayClientAttendanceStatusTypes;
    }

    
    private final class CgtDayDataMapper implements RowMapper<CgtDayData> {

        private final String schema;

        public CgtDayDataMapper() {
            final StringBuilder sqlBuilder = new StringBuilder(500);
            sqlBuilder.append(" cd.id as cgtDayId, cd.cgt_id as cgtId, cd.scheduled_date as scheduledDate, cd.loan_officer_id as loanOfficerId, ms.display_name as loanOfficerName, ");
            sqlBuilder.append(" cd.cgt_day_status as cgtDayStatus, cd.cgt_day_name as cgtDayName, cd.location as location, ");
            sqlBuilder.append(" cd.completed_date as completedDate from f_cgt_day cd left join m_staff ms on cd.loan_officer_id = ms.id ");

            this.schema = sqlBuilder.toString();
        }
        
        public String getSchema() {
            return this.schema;
        }

        @Override
        public CgtDayData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "cgtDayId");
            final Long cgtId = JdbcSupport.getLong(rs, "cgtId");
            final String cgtDayName = rs.getString("cgtDayName");
            final Date scheduledDate = rs.getDate("scheduledDate");
            final String location = rs.getString("location");
            final Date completedDate = rs.getDate("completedDate");
            final Integer cgtDayStatus = rs.getInt("cgtDayStatus");
            final Long loanOfficerId = rs.getLong("loanOfficerId");
            final String loanOfficerName = rs.getString("loanOfficerName");
            final StaffData cgtDayprimaryLoanOfficer = StaffData.lookup(loanOfficerId, loanOfficerName);
            CgtDayStatusType cgtDayStatusData = CgtDayStatusType.fromInt(cgtDayStatus);
            final LocalDate createdDate = null;
            final LocalDate updatedDate = null;
            final AppUserData createdBy = null;
            final AppUserData updatedBy = null;
            final Collection<ClientData> clientMembers = null;
            final List<EnumOptionData> attendanceTypeOptions = retrieveAttendanceTypeOptions();
            final Collection<StaffData> loanOfficerOptions = getstaffDatasOptions();
            final Collection<NoteData> notesDatas = getNotesData(id);
            Collection<CgtDayClientData> cgtDayClients = getCgtDayClient(id);

            return new CgtDayData(id, cgtId, cgtDayName, location, notesDatas, new LocalDate(scheduledDate), new LocalDate(completedDate), cgtDayStatusData, createdDate,
                    updatedDate, createdBy, updatedBy, cgtDayprimaryLoanOfficer, loanOfficerOptions, clientMembers, attendanceTypeOptions,
                    cgtDayClients);

        }
    }
    
    public Collection<StaffData> getstaffDatasOptions() {
        final AppUser currentUser = this.context.authenticatedUser();
        final Collection<StaffData> staffDatas = this.staffReadPlatformService.retrieveAllLoanOfficersInOfficeById(currentUser.getOffice()
                .getId());
        return staffDatas;
    }
    
    public Collection<NoteData> getNotesData(Long cgtDayId) {
        final Collection<NoteData> noteDatas = this.noteReadPlatformService.retrieveNotesByResource(cgtDayId, NoteType.CGT_DAY.getValue());
        return noteDatas;
    }
    
    @Override
    public Collection<CgtDayClientData> getCgtDayClient(Long cgtDayId) {

        CgtDayClientDataMapper rm = new CgtDayClientDataMapper();

        final String sql = "select " + rm.getSchema() + " WHERE cd.cgt_day_id = ?";

        return this.jdbcTemplate.query(sql, rm, new Object[] { cgtDayId });

    }
   
   private final class CgtDayClientDataMapper implements RowMapper<CgtDayClientData> {

       private final String schema;

       public CgtDayClientDataMapper() {
           final StringBuilder sqlBuilder = new StringBuilder(200);
           sqlBuilder.append(" cd.id as id, cd.cgt_day_id as cgtDayId, cd.client_id as clientId, mc.display_name as clientName, cd.attendance as attendance ");
           sqlBuilder.append(" from f_cgt_day_client cd left join m_client mc on cd.client_id = mc.id ");

           this.schema = sqlBuilder.toString();
       }
       
       public String getSchema() {
           return this.schema;
       }

       @Override
       public CgtDayClientData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

           final Long id = JdbcSupport.getLong(rs, "id");
           final Long cgtDayId = JdbcSupport.getLong(rs, "cgtDayId");
           final Long clientId = JdbcSupport.getLong(rs, "clientId");
           final String clientName = rs.getString("clientName");
           final int attendanceType = rs.getInt("attendance");
           EnumOptionData attendance =  CgtDayClientAttendanceStatusType.CgtDayClientAttendanceStatusTypeEnumDatafromInt(attendanceType);
           ClientData clientData = ClientData.formClientData(clientId, clientName);

           return new CgtDayClientData(id, cgtDayId, clientData, attendance);

       }
   }

}
