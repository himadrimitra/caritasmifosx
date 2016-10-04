package com.finflux.organisation.transaction.authentication.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.paymenttype.service.PaymentTypeReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.infrastructure.external.authentication.data.ExternalAuthenticationServiceData;
import com.finflux.infrastructure.external.authentication.data.ExternalAuthenticationServicesDataConstants;
import com.finflux.infrastructure.external.authentication.domain.SecondaryAuthenticationServiceRepository;
import com.finflux.infrastructure.external.authentication.exception.ExternalAuthenticationServiceNotFoundException;
import com.finflux.infrastructure.external.authentication.service.ExternalAuthenticationServicesReadPlatformService;
import com.finflux.organisation.transaction.authentication.api.TransactionAuthenticationApiConstants;
import com.finflux.organisation.transaction.authentication.data.TransactionAuthenticationData;
import com.finflux.organisation.transaction.authentication.domain.SupportedAuthenticationPortfolioTypes;
import com.finflux.organisation.transaction.authentication.domain.SupportedTransactionTypeEnumerations;

@Service
public class TransactionAuthenticationReadPlatformServiceImpl implements TransactionAuthenticationReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final TransactionAuthenticationDropdownReadPlatformService transactionAuthenticationDropdownReadPlatformService;
	private final ExternalAuthenticationServicesReadPlatformService externalAuthenticationServicesReadPlatformService;
	private final PaymentTypeReadPlatformService paymentTypeReadPlatformService;
	private final TransactionAuthenticationServiceMapper transactionAuthenticationServiceMapper = new TransactionAuthenticationServiceMapper();
	private final TransactionAuthenticationDataMapper dataMapper = new TransactionAuthenticationDataMapper();

	@Autowired
	private TransactionAuthenticationReadPlatformServiceImpl(final RoutingDataSource dataSourc,
			final TransactionAuthenticationDropdownReadPlatformService transactionAuthenticationDropdownReadPlatformService,
			final ExternalAuthenticationServicesReadPlatformService externalAuthenticationServicesReadPlatformService,
			final PaymentTypeReadPlatformService paymentTypeReadPlatformService,
			final SecondaryAuthenticationServiceRepository secondaryAuthenticationServiceRepository) {
		this.jdbcTemplate = new JdbcTemplate(dataSourc);
		this.transactionAuthenticationDropdownReadPlatformService = transactionAuthenticationDropdownReadPlatformService;
		this.externalAuthenticationServicesReadPlatformService = externalAuthenticationServicesReadPlatformService;
		this.paymentTypeReadPlatformService = paymentTypeReadPlatformService;
	}

	@Override
	public TransactionAuthenticationData retriveTemplate() {
		final Collection<PaymentTypeData> PaymentTypeData = this.paymentTypeReadPlatformService
				.retrieveAllPaymentTypes();
		final Collection<ExternalAuthenticationServiceData> authenticationData = this.externalAuthenticationServicesReadPlatformService
				.getOnlyActiveExternalAuthenticationServices();
		final Collection<EnumOptionData> appliesToOptions = this.transactionAuthenticationDropdownReadPlatformService
				.retrieveApplicableToTypes();
		final Collection<EnumOptionData> loansTypeOptions = this.transactionAuthenticationDropdownReadPlatformService
				.retrieveLoanTransactionTypes();
		final Collection<EnumOptionData> savingsTypeOptions = this.transactionAuthenticationDropdownReadPlatformService
				.retrieveSavingTransactionTypes();
		return TransactionAuthenticationData.retriveTemplate(appliesToOptions, PaymentTypeData, loansTypeOptions,
				savingsTypeOptions, authenticationData);
	}

	@Override
	public List<TransactionAuthenticationData> findByPortfolioTypeAndTransactionTypeIdAndPaymentTypeIdAndAmount(
			final Integer portfolioType, final Integer transactionTypeId, final Long paymentTypeId,
			final BigDecimal amountGreaterThan) {
		try {
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("select " + this.dataMapper.schema());
			sqlBuilder.append(" from f_transaction_authentication a ");
			sqlBuilder.append("where ");
			sqlBuilder.append("a.portfolio_type = ? ");
			sqlBuilder.append("and a.transaction_type_enum = ? and a.payment_type_id = ? ");
			sqlBuilder.append("and a.amount = ?");
			String sql = sqlBuilder.toString();
			return this.jdbcTemplate.query(sql, this.dataMapper,
					new Object[] { portfolioType, transactionTypeId, paymentTypeId, amountGreaterThan });
		} catch (final EmptyResultDataAccessException e) {
			return new ArrayList<TransactionAuthenticationData>();
		}
	}

	@Override
	public TransactionAuthenticationData retrieveOneById(Long id) {
		try {
			String sql = "select " + this.transactionAuthenticationServiceMapper.schema()
					+ " from f_transaction_authentication a where a.id=? ;";
			TransactionAuthenticationData transactionAuthenticationData = this.jdbcTemplate.queryForObject(sql,
					this.transactionAuthenticationServiceMapper, new Object[] { id });
			return transactionAuthenticationData;
		} catch (final EmptyResultDataAccessException e) {
			throw new ExternalAuthenticationServiceNotFoundException(id);
		}

	}

	private static class TransactionAuthenticationServiceMapper implements RowMapper<TransactionAuthenticationData> {
		private final String schema;

		public String schema() {
			return schema;
		}

		public TransactionAuthenticationServiceMapper() {
			final StringBuilder sqlBuilder = new StringBuilder(200);
			sqlBuilder.append(
					"a.id as id, a.portfolio_type as portfolioType, a.transaction_type_enum as transactionTypeId, a.payment_type_id as PaymentTypeId, a.amount as amount, a.second_app_user_role_id as secondAppUserRoleId, a.is_second_app_user_enabled as isSecondAppUserEnabled, a.authentication_id as authenticationTypeId");
			this.schema = sqlBuilder.toString();
		}

		@Override
		public TransactionAuthenticationData mapRow(ResultSet rs, int rowNum) throws SQLException {
			final Long id = JdbcSupport.getLong(rs, TransactionAuthenticationApiConstants.ID);
			final Integer portfolioTypeId = rs.getInt(TransactionAuthenticationApiConstants.PORTFOLIO_TYPE);
			final Integer transactionTypeId = rs.getInt(TransactionAuthenticationApiConstants.TRANSACTION_TYPE_ID);
			final Long paymentTypeId = JdbcSupport.getLong(rs, TransactionAuthenticationApiConstants.PAYMENT_TYPE_ID);
			final boolean isSecondAppUserEnabled = rs
					.getBoolean(TransactionAuthenticationApiConstants.IS_SECOND_APP_USER_ENABLED);
			final BigDecimal amount = rs.getBigDecimal(TransactionAuthenticationApiConstants.AMOUNT);
			final Long authenticationTypeId = JdbcSupport.getLong(rs,
					TransactionAuthenticationApiConstants.AUTHENTICATION_TYPE_ID);
			final Long secondAppUserRoleId = JdbcSupport.getLong(rs,
					TransactionAuthenticationApiConstants.SECOUND_APP_USER_ROLE_ID);
			return TransactionAuthenticationData.instance(id, portfolioTypeId, transactionTypeId, paymentTypeId, amount,
					secondAppUserRoleId, isSecondAppUserEnabled, authenticationTypeId);
		}
	}

	@Override
	public List<TransactionAuthenticationData> findByPortfolioTypeAndTransactionTypeIdAndPaymentTypeIdAndAmountAndAuthenticationTypeId(
			Integer portfolioType, Integer transactionTypeId, Long paymentTypeId, BigDecimal amountGreaterThan,
			Long authenticationId) {
		try {
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("select " + this.dataMapper.schema());
			sqlBuilder.append(" from f_transaction_authentication a ");
			sqlBuilder.append("where ");
			sqlBuilder.append("a.portfolio_type = ? ");
			sqlBuilder.append("and a.transaction_type_enum = ? and a.payment_type_id = ? ");
			sqlBuilder.append("and a.amount = ? ");
			sqlBuilder.append("and a.authentication_id = ? ;");
			String sql = sqlBuilder.toString();
			return this.jdbcTemplate.query(sql, this.dataMapper, new Object[] { portfolioType, transactionTypeId,
					paymentTypeId, amountGreaterThan, authenticationId });
		} catch (final EmptyResultDataAccessException e) {
			return new ArrayList<TransactionAuthenticationData>();
		}
	}

	@Override
	public TransactionAuthenticationData retriveTransactionAuthenticationDetails(final Integer portfolioType,
			final Integer transactionTypeId, final Long paymentTypeId, final BigDecimal amount) {
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select " + this.dataMapper.schema());
		sqlBuilder.append(" from f_transaction_authentication a ");
		sqlBuilder.append("where a.amount <= ? ");
		sqlBuilder.append("and a.portfolio_type = ? ");
		sqlBuilder.append("and a.transaction_type_enum = ? and a.payment_type_id = ? ");
		sqlBuilder.append("and a.amount in ");
		sqlBuilder.append("( select max(fa.amount) from f_transaction_authentication fa where fa.amount <= ? ");
		sqlBuilder.append("and fa.portfolio_type = ? and ");
		sqlBuilder.append(" fa.transaction_type_enum = ? and fa.payment_type_id = ?);");
		String sql = sqlBuilder.toString();
		try {
			TransactionAuthenticationData transactionAuthenticationDatas = this.jdbcTemplate.queryForObject(sql,
					new Object[] { amount, portfolioType, transactionTypeId, paymentTypeId, amount, portfolioType,
							transactionTypeId, paymentTypeId },
					this.dataMapper);
			return transactionAuthenticationDatas;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static class TransactionAuthenticationDataMapper implements RowMapper<TransactionAuthenticationData> {

		private final String schema;

		public String schema() {
			return schema;
		}

		public TransactionAuthenticationDataMapper() {
			final StringBuilder sqlBuilder = new StringBuilder(200);
			sqlBuilder.append(
					"a.id, a.portfolio_type as portfolioType, a.transaction_type_enum as transactionId, a.payment_type_id as paymentTypeId, a.amount, a.authentication_id as authenticationTypeId ");
			this.schema = sqlBuilder.toString();
		}

		@Override
		public TransactionAuthenticationData mapRow(ResultSet rs, int rowNum) throws SQLException {
			final Long id = JdbcSupport.getLong(rs, ExternalAuthenticationServicesDataConstants.ID);
			final Integer portfolioType = rs.getInt("portfolioType");
			final Integer transactionId = rs.getInt("transactionId");
			final Long paymentTypeId = rs.getLong("paymentTypeId");
			final BigDecimal amount = rs.getBigDecimal("amount");
			final Long authenticationTypeId = rs.getLong("authenticationTypeId");

			return TransactionAuthenticationData.instance(id, portfolioType, transactionId, paymentTypeId, amount,
					authenticationTypeId);
		}

	}

	@Override
	public Collection<TransactionAuthenticationData> retriveTransactionAuthenticationDetails(Integer productTypeId,
			Integer transactionTypeId, BigDecimal approvedPrincipal) {
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select " + this.dataMapper.schema());
		sqlBuilder.append(" from f_transaction_authentication a ");
		sqlBuilder.append("where a.amount <= ? ");
		sqlBuilder.append("and a.portfolio_type = ? ");
		sqlBuilder.append("and a.transaction_type_enum = ? ");
		sqlBuilder.append("and a.amount in ");
		sqlBuilder.append("( select max(fa.amount) from f_transaction_authentication fa where fa.amount <= ? ");
		sqlBuilder.append("and fa.portfolio_type = ? and ");
		sqlBuilder.append(" fa.transaction_type_enum = ? );");
		String sql = sqlBuilder.toString();
		try {
			return this.jdbcTemplate.query(sql, this.dataMapper, new Object[] { approvedPrincipal, productTypeId,
					transactionTypeId, approvedPrincipal, productTypeId, transactionTypeId });
		} catch (EmptyResultDataAccessException e) {
			return new ArrayList<>();
		}
	}

	@Override
	public Collection<TransactionAuthenticationData> retiveTransactionAuthenticationDetailsForTemplate(
			Integer productTypeId, Integer transactionTypeId, BigDecimal approvedPrincipal) {
		Collection<TransactionAuthenticationData> transactionAuthenticationDatas = retriveTransactionAuthenticationDetails(
				productTypeId, transactionTypeId, approvedPrincipal);
		Collection<TransactionAuthenticationData> transactionAuthenticatioDatasForTemplare = new LinkedList<>();
		for (TransactionAuthenticationData transactionAuthenticationData : transactionAuthenticationDatas) {
			final ExternalAuthenticationServiceData externalAuthenticationServiceData = this.externalAuthenticationServicesReadPlatformService
					.retrieveOneExternalAuthenticationService(transactionAuthenticationData.getAuthenticationTypeId());

			TransactionAuthenticationData newData = TransactionAuthenticationData.instance(
					transactionAuthenticationData.getId(), transactionAuthenticationData.getPortfolioTypeId(),
					transactionAuthenticationData.getTransactionTypeId(),
					transactionAuthenticationData.getPaymentTypeId(), transactionAuthenticationData.getAmount(),
					externalAuthenticationServiceData.getName());
			transactionAuthenticatioDatasForTemplare.add(newData);
		}
		return transactionAuthenticatioDatasForTemplare;
	}

	@Override
	public Collection<TransactionAuthenticationData> retriveAllTransactionAuthenticationDeatails() {
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select " + this.dataMapper.schema());
		sqlBuilder.append(" from f_transaction_authentication a ;");
		String sql = sqlBuilder.toString();
		try {
			Collection<TransactionAuthenticationData> transactionAuthentications = this.jdbcTemplate.query(sql,
					this.dataMapper, new Object[] {});
			Collection<TransactionAuthenticationData> newDatas = new LinkedList<>();
			for (TransactionAuthenticationData aTransactionAuthenticationData : transactionAuthentications) {
				final Long id = aTransactionAuthenticationData.getId();
				final String portfolioType = SupportedAuthenticationPortfolioTypes
						.chargeAppliesTo(SupportedAuthenticationPortfolioTypes
								.fromInt(aTransactionAuthenticationData.getPortfolioTypeId()))
						.getValue();
				final String transactionType = SupportedTransactionTypeEnumerations
						.transactionType(aTransactionAuthenticationData.getTransactionTypeId()).getValue();
				final PaymentTypeData paymentType = this.paymentTypeReadPlatformService
						.retrieveOne(aTransactionAuthenticationData.getPaymentTypeId());
				final BigDecimal amount = aTransactionAuthenticationData.getAmount();
				final String authenticationType = this.externalAuthenticationServicesReadPlatformService
						.retrieveOneExternalAuthenticationService(
								aTransactionAuthenticationData.getAuthenticationTypeId())
						.getName();
				final TransactionAuthenticationData transactionAuthenticationData = TransactionAuthenticationData
						.newInstance(id, portfolioType, transactionType, amount, paymentType, authenticationType);
				newDatas.add(transactionAuthenticationData);
			}
			return newDatas;
		} catch (EmptyResultDataAccessException e) {
			return new ArrayList<>();
		}
	}

}
