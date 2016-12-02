package com.finflux.transaction.execution.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.finflux.transaction.execution.data.BankTransactionEntityType;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.bank.data.BankAccountDetailData;
import com.finflux.portfolio.bank.domain.BankAccountDetailStatus;
import com.finflux.transaction.execution.data.BankTransactionDetail;
import com.finflux.transaction.execution.data.TransactionStatus;
import com.finflux.transaction.execution.data.TransferType;

@Service
public class BankTransactionReadPlatformServiceImpl
		implements
		BankTransactionReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final AccountTransactionDetailMapper transactionDetailMapper = new AccountTransactionDetailMapper();

	@Autowired
	public BankTransactionReadPlatformServiceImpl(
			final PlatformSecurityContext context,
			final RoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public BankTransactionDetail getAccountTransactionDetails(
			Long transactionId) {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("select ");
			sb.append(transactionDetailMapper.schema());
			sb.append(" where bat.id=?");

			return this.jdbcTemplate.queryForObject(sb.toString(),
					transactionDetailMapper, transactionId);
		} catch (final EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<BankTransactionDetail> getAccountTransactionsByEntity(
			BankTransactionEntityType entityType, Long entityId) {
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		sb.append(transactionDetailMapper.schema());
		sb.append(" where bat.entity_type=? and bat.entity_id=?");

		return this.jdbcTemplate.query(sb.toString(), transactionDetailMapper,
				entityType.getValue(), entityId);
	}

	@Override
	public List<BankTransactionDetail> getAccountTransactionsByStatus(
			TransactionStatus status) {
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		sb.append(transactionDetailMapper.schema());
		sb.append(" where bat.status=? order by bat.external_service_id asc");

		return this.jdbcTemplate.query(sb.toString(), transactionDetailMapper,
				status.getValue());

	}

	private static final class AccountTransactionDetailMapper
			implements
				RowMapper<BankTransactionDetail> {

		public String schema() {
			StringBuilder sb = new StringBuilder();
			sb.append(" bat.id as transactionId,bat.entity_type as entityType,bat.entity_id as entityId, ");
			sb.append(" bat.entity_transaction_id as entityTxnId,bat.amount as amount,bat.status as status, ");
			sb.append(" bat.transfer_type as transferType, ");
			sb.append(" debbad.id as debitAccountid, debbad.name as debitAccountName, ");
			sb.append(" debbad.account_number as debitAccountNumber,  debbad.ifsc_code as debitIfscCode, ");
			sb.append(" debbad.mobile_number as debitMobile, debbad.email as debitEmail, debbad.status_id as debitStatus, ");
			sb.append(" benbad.id as benAccountid, benbad.name as benAccountName, ");
			sb.append(" benbad.account_number as benAccountNumber,  benbad.ifsc_code as benIfscCode, ");
			sb.append(" benbad.mobile_number as benMobile, benbad.email as benEmail, benbad.status_id as benStatus, ");
			sb.append(" bat.reference_number as referenceNumber ");
			sb.append(" from f_bank_account_transaction bat ");
			sb.append(" left join f_bank_account_details debbad on bat.debit_account = debbad.id ");
			sb.append(" left join f_bank_account_details benbad on bat.beneficiary_account = benbad.id ");
			return sb.toString();
		}

		@Override
		public BankTransactionDetail mapRow(final ResultSet rs,
											@SuppressWarnings("unused") final int rowNum)
				throws SQLException {
			final Long transactionId = rs.getLong("transactionId");
			final Integer entityType = rs.getInt("entityType");
			final Long entityId = rs.getLong("entityId");
			final Long entityTxnId = rs.getLong("entityTxnId");
			final BigDecimal amount = rs.getBigDecimal("amount");
			final Integer status = rs.getInt("status");
			final Integer transferType = rs.getInt("transferType");

			final Long debitAccountid = rs.getLong("debitAccountid");
			final String debitAccountName = rs.getString("debitAccountName");
			final String debitAccountNumber = rs
					.getString("debitAccountNumber");
			final String debitIfscCode = rs.getString("debitIfscCode");
			final String debitMobile = rs.getString("debitMobile");
			final String debitEmail = rs.getString("debitEmail");
			final Integer debitStatus = rs.getInt("debitStatus");

			final BankAccountDetailData debitAccount = new BankAccountDetailData(
					debitAccountid, debitAccountName, debitAccountNumber,
					debitIfscCode, debitMobile, debitEmail,
					BankAccountDetailStatus
							.bankAccountDetailStatusEnumDate(debitStatus));

			final Long benAccountid = rs.getLong("benAccountid");
			final String benAccountName = rs.getString("benAccountName");
			final String benAccountNumber = rs.getString("benAccountNumber");
			final String benIfscCode = rs.getString("benIfscCode");
			final String benMobile = rs.getString("benMobile");
			final String benEmail = rs.getString("benEmail");
			final Integer benStatus = rs.getInt("benStatus");

			final BankAccountDetailData beneficiaryAccount = new BankAccountDetailData(
					benAccountid, benAccountName, benAccountNumber,
					benIfscCode, benMobile, benEmail,
					BankAccountDetailStatus
							.bankAccountDetailStatusEnumDate(benStatus));

			BankTransactionDetail accountTransactionDetail = new BankTransactionDetail(
					transactionId, debitAccount, beneficiaryAccount,
					entityType, entityId, entityTxnId, amount, TransferType
							.fromInt(transferType).getEnumOptionData(),
					TransactionStatus.fromInt(status).getEnumOptionData());

			return accountTransactionDetail;
		}
	}
}
