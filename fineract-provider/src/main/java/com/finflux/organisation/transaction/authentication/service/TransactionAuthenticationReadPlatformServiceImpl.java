package com.finflux.organisation.transaction.authentication.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.data.ClientIdentifierData;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductReadPlatformService;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.paymenttype.service.PaymentTypeReadPlatformService;
import org.apache.fineract.useradministration.domain.AppUser;
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
	private final LoanProductReadPlatformService loanProductReadPlatformService;
	private final CodeValueReadPlatformService codeValueReadPlatformService;
	private final TransactionAuthenticationServiceMapper transactionAuthenticationServiceMapper = new TransactionAuthenticationServiceMapper();
	private final TransactionAuthenticationDataMapper dataMapper = new TransactionAuthenticationDataMapper();
    private final PlatformSecurityContext context;

	@Autowired
	private TransactionAuthenticationReadPlatformServiceImpl(final RoutingDataSource dataSourc,
			final TransactionAuthenticationDropdownReadPlatformService transactionAuthenticationDropdownReadPlatformService,
			final ExternalAuthenticationServicesReadPlatformService externalAuthenticationServicesReadPlatformService,
			final PaymentTypeReadPlatformService paymentTypeReadPlatformService,
			final SecondaryAuthenticationServiceRepository secondaryAuthenticationServiceRepository,
			final LoanProductReadPlatformService loanProductReadPlatformService, final CodeValueReadPlatformService codeValueReadPlatformService,final PlatformSecurityContext context) {
		this.jdbcTemplate = new JdbcTemplate(dataSourc);
		this.transactionAuthenticationDropdownReadPlatformService = transactionAuthenticationDropdownReadPlatformService;
		this.externalAuthenticationServicesReadPlatformService = externalAuthenticationServicesReadPlatformService;
		this.paymentTypeReadPlatformService = paymentTypeReadPlatformService;
		this.loanProductReadPlatformService = loanProductReadPlatformService;
		this.codeValueReadPlatformService = codeValueReadPlatformService;
		this.context = context;
	}

	@Override
	public TransactionAuthenticationData retriveTemplate() {
		final Collection<PaymentTypeData> PaymentTypeData = this.paymentTypeReadPlatformService
				.retrieveAllPaymentTypes();
		final Collection<ExternalAuthenticationServiceData> authenticationData = this.externalAuthenticationServicesReadPlatformService
				.getOnlyActiveExternalAuthenticationServices();
		final Collection<EnumOptionData> appliesToOptions = this.transactionAuthenticationDropdownReadPlatformService
				.retrieveApplicableToTypes();
		final Collection<LoanProductData> productOptions = this.loanProductReadPlatformService.retrieveAllLoanProductsForLookup(true);
		final Collection<EnumOptionData> loansTypeOptions = this.transactionAuthenticationDropdownReadPlatformService
				.retrieveLoanTransactionTypes();
		final Collection<EnumOptionData> savingsTypeOptions = this.transactionAuthenticationDropdownReadPlatformService
				.retrieveSavingTransactionTypes();
        final Collection<CodeValueData> identificationTypeOptions = this.codeValueReadPlatformService.retrieveCodeValuesByCode("Customer Identifier");

		return TransactionAuthenticationData.retriveTemplate(appliesToOptions, PaymentTypeData, loansTypeOptions,
				savingsTypeOptions, productOptions, authenticationData,identificationTypeOptions);
	}

	@Override
	public List<TransactionAuthenticationData> findByPortfolioTypeAndTransactionTypeIdAndPaymentTypeIdAndAmount(
			final Integer portfolioType, final Integer transactionTypeId, final Long paymentTypeId,
			final BigDecimal amountGreaterThan, final Long productId) {
		try {
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("select " + this.dataMapper.schema());
			sqlBuilder.append(" from f_transaction_authentication a ");
			sqlBuilder.append("join m_code_value cv on a.identifier_type_id = cv.id ");
			sqlBuilder.append("where ");
			sqlBuilder.append("a.portfolio_type = ? ");
			sqlBuilder.append("and a.transaction_type_enum = ? and a.payment_type_id = ? ");
			sqlBuilder.append("and a.amount = ? and product_id = ?");
			String sql = sqlBuilder.toString();
			return this.jdbcTemplate.query(sql, this.dataMapper,
					new Object[] { portfolioType, transactionTypeId, paymentTypeId, amountGreaterThan, productId });
		} catch (final EmptyResultDataAccessException e) {
			return new ArrayList<TransactionAuthenticationData>();
		}
	}

	@Override
	public TransactionAuthenticationData retrieveOneById(Long id) {
		try {
			String sql = "select " + this.transactionAuthenticationServiceMapper.schema()
					+ " from f_transaction_authentication a "
					+ "join m_code_value cv on a.identifier_type_id = cv.id "
					+ " where a.id=? ;";
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
					"a.id as id, a.portfolio_type as portfolioType, a.transaction_type_enum as transactionTypeId, a.payment_type_id as PaymentTypeId, a.amount as amount, a.second_app_user_role_id as secondAppUserRoleId, a.is_second_app_user_enabled as isSecondAppUserEnabled, a.authentication_id as authenticationTypeId,");
			sqlBuilder.append(" product_id as productId, ");
			sqlBuilder.append("cv.code_value as identificationType, a.identifier_type_id as identificationTypeId ");
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
			final Long productId = JdbcSupport.getLong(rs, TransactionAuthenticationApiConstants.PRODUCT_ID);
            final Long identificationTypeId = JdbcSupport.getLong(rs, "identificationTypeId");
            final String identificationTypeName = rs.getString("identificationType");
			final CodeValueData identificationType = CodeValueData.instance(identificationTypeId, identificationTypeName);
			return TransactionAuthenticationData.instance(id, portfolioTypeId, productId, transactionTypeId, paymentTypeId, amount,
					secondAppUserRoleId, isSecondAppUserEnabled, authenticationTypeId,identificationType);
		}
	}

	@Override
	public List<TransactionAuthenticationData> findByPortfolioTypeAndTransactionTypeIdAndPaymentTypeIdAndAmountAndAuthenticationTypeId(
			Integer portfolioType, Integer transactionTypeId, Long paymentTypeId, BigDecimal amountGreaterThan,
			Long authenticationId, final Long productId) {
		try {
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("select " + this.dataMapper.schema());
			sqlBuilder.append(" from f_transaction_authentication a ");
			sqlBuilder.append("join m_code_value cv on a.identifier_type_id = cv.id ");
			sqlBuilder.append("where ");
			sqlBuilder.append("a.portfolio_type = ? ");
			sqlBuilder.append("and a.transaction_type_enum = ? and a.payment_type_id = ? ");
			sqlBuilder.append("and a.amount = ? ");
			sqlBuilder.append("and a.authentication_id = ? and product_id = ? ");
			String sql = sqlBuilder.toString();
			return this.jdbcTemplate.query(sql, this.dataMapper, new Object[] { portfolioType, transactionTypeId,
					paymentTypeId, amountGreaterThan, authenticationId, productId });
		} catch (final EmptyResultDataAccessException e) {
			return new ArrayList<TransactionAuthenticationData>();
		}
	}

	@Override
	public TransactionAuthenticationData retriveTransactionAuthenticationDetails(final Integer portfolioType,
			final Integer transactionTypeId, final Long paymentTypeId, final BigDecimal amount, final Long productId) {
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select " + this.dataMapper.schema());
		sqlBuilder.append(" from f_transaction_authentication a ");
		sqlBuilder.append("join m_code_value cv on a.identifier_type_id = cv.id ");
		sqlBuilder.append("where a.amount <= ? ");
		sqlBuilder.append("and a.portfolio_type = ? ");
		sqlBuilder.append("and a.transaction_type_enum = ? and a.payment_type_id = ? and a.product_id = ? ");
		sqlBuilder.append("and a.amount in ");
		sqlBuilder.append("( select max(fa.amount) from f_transaction_authentication fa where fa.amount <= ? ");
		sqlBuilder.append("and fa.portfolio_type = ? and ");
		sqlBuilder.append(" fa.transaction_type_enum = ? and fa.payment_type_id = ? and fa.product_id = ?);");
		String sql = sqlBuilder.toString();
		try {
			TransactionAuthenticationData transactionAuthenticationDatas = this.jdbcTemplate.queryForObject(sql,
					new Object[] { amount, portfolioType, transactionTypeId, paymentTypeId, productId, amount, portfolioType,
							transactionTypeId, paymentTypeId, productId },
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
					"a.id, a.portfolio_type as portfolioType, a.transaction_type_enum as transactionId, a.payment_type_id as paymentTypeId, a.amount, a.authentication_id as authenticationTypeId ,"
					+ "a.product_id as productId, cv.code_value as identificationType, a.identifier_type_id as identificationTypeId ");
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
			final Long productId = rs.getLong("productId");
			final Long identificationTypeId = JdbcSupport.getLong(rs, "identificationTypeId");
            final String identificationTypeName = rs.getString("identificationType");
			final CodeValueData identificationType = CodeValueData.instance(identificationTypeId, identificationTypeName);
			return TransactionAuthenticationData.instance(id, portfolioType, productId, transactionId, paymentTypeId, amount,
					authenticationTypeId,identificationType);
		}

	}

	@Override
	public Collection<TransactionAuthenticationData> retriveTransactionAuthenticationDetails(Integer productTypeId,
			Integer transactionTypeId, BigDecimal approvedPrincipal, final Long productId) {
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select " + this.dataMapper.schema());
		sqlBuilder.append(" from f_transaction_authentication a ");
		sqlBuilder.append("join m_code_value cv on a.identifier_type_id = cv.id ");
		sqlBuilder.append("where a.amount <= ? ");
		sqlBuilder.append("and a.portfolio_type = ? ");
		sqlBuilder.append("and a.transaction_type_enum = ? and a.product_id = ?;");
		String sql = sqlBuilder.toString();
		try {
			return this.jdbcTemplate.query(sql, this.dataMapper, new Object[] { approvedPrincipal, productTypeId,
					transactionTypeId, productId});
		} catch (EmptyResultDataAccessException e) {
			return new ArrayList<>();
		}
	}

	@Override
	public Collection<TransactionAuthenticationData> retiveTransactionAuthenticationDetailsForTemplate(
			Integer productTypeId, Integer transactionTypeId, BigDecimal approvedPrincipal, final Long loanId,
			final Long productId) {
		final Collection<TransactionAuthenticationData> transactionAuthenticationDatas = retriveTransactionAuthenticationDetails(
				productTypeId, transactionTypeId, approvedPrincipal, productId);
		final Collection<TransactionAuthenticationData> transactionAuthenticatioDatasForTemplare = new LinkedList<>();
		for (TransactionAuthenticationData transactionAuthenticationData : transactionAuthenticationDatas) {
			final ExternalAuthenticationServiceData externalAuthenticationServiceData = this.externalAuthenticationServicesReadPlatformService
					.retrieveOneExternalAuthenticationService(transactionAuthenticationData.getAuthenticationTypeId());

			TransactionAuthenticationData newData = TransactionAuthenticationData.instance(
					transactionAuthenticationData.getId(), transactionAuthenticationData.getPortfolioTypeId(),
					transactionAuthenticationData.getTransactionTypeId(),
					transactionAuthenticationData.getPaymentTypeId(), transactionAuthenticationData.getAmount(),
					externalAuthenticationServiceData.getName(),transactionAuthenticationData.getIdentificationType());
			transactionAuthenticatioDatasForTemplare.add(newData);
		}
		return transactionAuthenticatioDatasForTemplare;
	}

	@Override
	public Collection<TransactionAuthenticationData> retriveAllTransactionAuthenticationDeatails() {
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select " + this.dataMapper.schema());
		sqlBuilder.append(" from f_transaction_authentication a ");
		sqlBuilder.append("join m_code_value cv on a.identifier_type_id = cv.id ");
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
				final String productName = getTheProductData(aTransactionAuthenticationData);
				final CodeValueData identificationType = aTransactionAuthenticationData.getIdentificationType();
				final TransactionAuthenticationData transactionAuthenticationData = TransactionAuthenticationData
						.newInstance(id, portfolioType, productName, transactionType, amount, paymentType, authenticationType,identificationType);
				newDatas.add(transactionAuthenticationData);
			}
			return newDatas;
		} catch (EmptyResultDataAccessException e) {
			return new ArrayList<>();
		}
	}

	
	private String getTheProductData(final TransactionAuthenticationData transactionAuthenticationData) {
		Integer portfolioId = transactionAuthenticationData.getPortfolioTypeId();
		String productName = null;
		switch(portfolioId){
		case 1:
			productName = this.loanProductReadPlatformService
					.retrieveLoanProductNameById(transactionAuthenticationData.getProductId()).getName();
			break;
		}
		return productName;
	}
}
