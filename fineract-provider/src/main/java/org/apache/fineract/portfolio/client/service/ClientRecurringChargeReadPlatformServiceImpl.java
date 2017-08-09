package org.apache.fineract.portfolio.client.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.portfolio.charge.service.ChargeEnumerations;
import org.apache.fineract.portfolio.client.data.ClientRecurringChargeData;
import org.apache.fineract.portfolio.client.domain.ClientStatus;
import org.apache.fineract.portfolio.client.exception.ClientRecurringChargeNotFoundException;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ClientRecurringChargeReadPlatformServiceImpl implements ClientRecurringChargeReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final ClientRecurringChargeMapper clientRecurringChargeMapper;
    private final ClientRecurringChargeJobMapper clientRecurringChargeJobMapper;

    @Autowired
    public ClientRecurringChargeReadPlatformServiceImpl(PlatformSecurityContext context, final RoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.clientRecurringChargeMapper = new ClientRecurringChargeMapper();
        this.clientRecurringChargeJobMapper = new ClientRecurringChargeJobMapper();
    }
    
    public static final class ClientRecurringChargeJobMapper implements RowMapper<ClientRecurringChargeData> {

        @Override
        public ClientRecurringChargeData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Long id = rs.getLong("id");
            final Long officeId = rs.getLong("officeId");
            final String officeName = null;
            final OfficeData officeData = OfficeData.lookup(officeId, officeName);
            final LocalDate chargeDueDate = new LocalDate(rs.getDate("chargeduedate"));
            final Integer chargeTimeEnum = rs.getInt("chargeTimeEnum");
            final EnumOptionData chargeTimeType = ChargeEnumerations.chargeTimeType(chargeTimeEnum);
            final Integer feeInterval = rs.getInt("feeInterval");
            final Boolean isSyncMeeting = rs.getBoolean("issynchmeeting");
            final Integer countOfExistingFutureInstallments = JdbcSupport.getInteger(rs, "countOfExistingFutureInstallments");
            return new ClientRecurringChargeData(id, officeData, chargeDueDate, chargeTimeType, feeInterval, isSyncMeeting,
                    countOfExistingFutureInstallments);
        }

        public String jobSchema(final LocalDate currentDate) {
            final StringBuilder sql = new StringBuilder(400);
            sql.append("select mcrc.id as id ");
            sql.append(",c.office_id as officeId ");
            sql.append(",mcrc.charge_due_date as chargeduedate ");
            sql.append(",mcrc.is_synch_meeting as issynchmeeting ");
            sql.append(",mcrc.charge_time_enum as chargeTimeEnum ");
            sql.append(",mcrc.fee_interval as feeInterval ");
            sql.append(",count(cc.id) as countOfExistingFutureInstallments ");
            sql.append("from m_client_recurring_charge mcrc ");
            sql.append("join m_client c on c.status_enum = ? and c.id = mcrc.client_id ");
            sql.append("left join m_client_charge cc on cc.client_recurring_charge_id = mcrc.id ");
            sql.append("and cc.is_active = 1 and cc.waived = 0 and cc.is_paid_derived = 0 and cc.charge_actual_due_date > ");
            sql.append("'");
            sql.append(currentDate);
            sql.append("' ");
            sql.append("where mcrc.charge_due_date = ");
            sql.append("'");
            sql.append(currentDate);
            sql.append("' ");
            sql.append("and mcrc.is_active = 1 group by mcrc.id ");
            return sql.toString();
        }
    }

    public static final class ClientRecurringChargeMapper implements RowMapper<ClientRecurringChargeData> {

        @Override
        public ClientRecurringChargeData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Long id = rs.getLong("id");
            final Long clientId = rs.getLong("clientid");
            final Long chargeId = rs.getLong("chargeid");
            final String chargeName = rs.getString("chargename");
            final LocalDate chargeDueDate = new LocalDate(rs.getDate("chargeduedate"));
            final String currencyCode = rs.getString("currencycode");
            final String currencyName = rs.getString("currencyname");
            final Integer currencyDecimalPlaces = rs.getInt("currencydecimalplaces");
            final Integer currencyMultiplesOf = rs.getInt("currencymultiplesof");
            final String currencyDisplaySymbol = rs.getString("currencydisplaysymbol");
            final String currencyNameCode = rs.getString("currencynamecode");
            final CurrencyData currency = new CurrencyData(currencyCode, currencyName, currencyDecimalPlaces, currencyMultiplesOf,
                    currencyDisplaySymbol, currencyNameCode);
            final Integer chargeAppliesToEnum = rs.getInt("chargeappliesenum");
            final EnumOptionData chargeAppliesTo = ChargeEnumerations.chargeAppliesTo(chargeAppliesToEnum);
            final Integer chargeTimeEnum = rs.getInt("chargetimeenum");
            final EnumOptionData chargeTimeType = ChargeEnumerations.chargeTimeType(chargeTimeEnum);
            final Integer chargeCalculationEnum = rs.getInt("chargecalculationenum");
            final EnumOptionData chargeCalculationType = ChargeEnumerations.chargeCalculationType(chargeCalculationEnum);
            final Integer chargePaymentModeEnum = rs.getInt("chargepaymentmodeenum");
            final EnumOptionData chargePaymentMode = ChargeEnumerations.chargePaymentMode(chargePaymentModeEnum);
            final BigDecimal amount = rs.getBigDecimal("chargeamount");
            final Integer feeOnDay = rs.getInt("feeonday");
            final Integer feeInterval = rs.getInt("feeinterval");
            final Integer feeOnMonth = rs.getInt("feeonmonth");
            final Boolean isPenalty = rs.getBoolean("ispenalty");
            final Boolean isActive = rs.getBoolean("isactive");
            final Boolean isDeleted = rs.getBoolean("isdeleted");
            final Boolean isSyncMeeting = rs.getBoolean("issynchmeeting");
            final BigDecimal minCap = rs.getBigDecimal("mincap");
            final BigDecimal maxCap = rs.getBigDecimal("maxcap");
            final Integer feeFrequency = rs.getInt("feefrequency");
            final LocalDate inactivatedOnDate = new LocalDate(rs.getDate("inactivatedondate"));
            final Integer countOfExistingFutureInstallments = null;
            return new ClientRecurringChargeData(id, clientId, chargeId, chargeName, chargeDueDate, currency, chargeAppliesTo,
                    chargeTimeType, chargeCalculationType, chargePaymentMode, amount, feeOnDay, feeInterval, feeOnMonth, isPenalty,
                    isActive, isDeleted, isSyncMeeting, minCap, maxCap, feeFrequency, inactivatedOnDate, countOfExistingFutureInstallments);
        }

        public String schema() {
            return "crc.id as id,crc.client_id as clientid,crc.charge_id as chargeid,crc.name as chargename,crc.charge_due_date as chargeduedate,"
                    + "crc.currency_code as currencycode,oc.name as currencyname,oc.decimal_places as currencydecimalplaces,"
                    + "oc.currency_multiplesof as currencymultiplesof,oc.display_symbol as currencydisplaysymbol,"
                    + "oc.internationalized_name_code as currencynamecode,crc.charge_applies_to_enum as chargeappliesenum,"
                    + "crc.charge_time_enum as chargetimeenum,crc.charge_calculation_enum as chargecalculationenum,"
                    + "crc.charge_payment_mode_enum as chargepaymentmodeenum,crc.amount as chargeamount,"
                    + "crc.fee_on_day as feeonday,crc.fee_interval as feeinterval,crc.fee_on_month as feeonmonth,crc.is_penalty as ispenalty,"
                    + "crc.is_active as isactive,crc.is_deleted as isdeleted,crc.is_synch_meeting as issynchmeeting,"
                    + "crc.is_synch_meeting as issynchmeeting,crc.min_cap as mincap,crc.max_cap as maxcap,"
                    + "crc.fee_frequency as feefrequency,crc.inactivated_on_date as inactivatedondate  from m_client_recurring_charge crc"
                    + " inner join m_organisation_currency oc on crc.currency_code = oc.code";
        }
    }

    @Override
    public List<ClientRecurringChargeData> retrieveRecurringClientCharges(Long clientId) {
        try {
            this.context.authenticatedUser();
            final String sql = "select " + clientRecurringChargeMapper.schema() + " where crc.client_id = ?";
            return this.jdbcTemplate.query(sql, clientRecurringChargeMapper, new Object[] { clientId });
        } catch (final EmptyResultDataAccessException e) {
            throw new ClientRecurringChargeNotFoundException(clientId);
        }

    }

    @Override
    public ClientRecurringChargeData retriveRecurringClientCharge(Long clientId, Long recurringChargeId) {
        try {
            this.context.authenticatedUser();
            //final ClientRecurringChargeMapper clientRecurringChargeMapper = new ClientRecurringChargeMapper();
            final String sql = "select " + clientRecurringChargeMapper.schema() + " where crc.client_id = ? and crc.id = ?";
            return this.jdbcTemplate.queryForObject(sql, clientRecurringChargeMapper, new Object[] { clientId,recurringChargeId});
        } catch (final EmptyResultDataAccessException e) {
            throw new ClientRecurringChargeNotFoundException(clientId,recurringChargeId);
        }
    }

    @Override
    public List<ClientRecurringChargeData> retriveActiveRecurringChargesForJob(final LocalDate currentDate) {
        try {
            return this.jdbcTemplate.query(this.clientRecurringChargeJobMapper.jobSchema(currentDate), this.clientRecurringChargeJobMapper,
                    new Object[] { ClientStatus.ACTIVE.getValue() });
        } catch (final EmptyResultDataAccessException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
