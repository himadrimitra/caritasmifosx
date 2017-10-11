package com.finflux.ruleengine.execution.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.service.BusinessEventListner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.finflux.ruleengine.execution.data.DataLayerKey;
import com.finflux.ruleengine.execution.service.impl.DataFieldUtils;
import com.finflux.ruleengine.execution.service.impl.SqlBasedDataLayer;
import com.finflux.ruleengine.lib.data.RuleResult;
import com.finflux.ruleengine.lib.data.ValueType;

@Component
public class SqlBasedBusinessEventListner implements BusinessEventListner {

    private final RoutingDataSource dataSource;
    private final DataFieldUtils dataFieldUtils;
    private final JdbcTemplate jdbcTemplate;
    private final RuleExecutionService ruleExecutionService;
    private final String sql = "select berm.rule_id as ruleId,berm.min_output_value_for_validation as minOutput,berm.validation_exception_code as exceptionCode,"
            + "berm.input_param_detail as paramDetail  from f_business_event_rule_mapping berm where berm.business_event = ?";

    @Autowired
    public SqlBasedBusinessEventListner(final RoutingDataSource dataSource, final RuleExecutionService ruleExecutionService,final DataFieldUtils dataFieldUtils) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.ruleExecutionService = ruleExecutionService;
        this.dataFieldUtils=dataFieldUtils;
    }

    @Override
    public void businessEventToBeExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
        try {
            BUSINESS_EVENTS event = (BUSINESS_EVENTS) businessEventEntity.get(BUSINESS_ENTITY.BUSINESS_EVENT);
            Map<String,Object> fieldParams=new HashMap<>();
            for(BUSINESS_ENTITY businessEntity:businessEventEntity.keySet())
            {
                fieldParams.put(businessEntity.getValue(),businessEventEntity.get(businessEntity));
            }

            final Map<String, Object> ruleDataMap = this.jdbcTemplate.queryForMap(sql, event.getValue());
            if (ruleDataMap.get("ruleId") != null) {
                Long ruleId = (Long) ruleDataMap.get("ruleId");
                final Map<String, Object> paramMap = new HashMap<>();
                if (ruleDataMap.get("paramDetail") != null) {
                    String paramDetail = (String) ruleDataMap.get("paramDetail");
                    this.dataFieldUtils.updateParamMap(paramMap, paramDetail, fieldParams);
                }
                SqlBasedDataLayer dataLayer = new SqlBasedDataLayer(this.dataSource, paramMap);
                fieldParams.put(DataLayerKey.RULE_ID.getValue(), ruleId);
                dataLayer.build(fieldParams);
                final RuleResult ruleResult = this.ruleExecutionService.executeARule(ruleId, dataLayer);
                if (ValueType.NUMBER.equals(ruleResult.getOutput().getType())) {
                    Integer ruleOutput = Integer.valueOf(ruleResult.getOutput().getValue());
                    if (ruleOutput <= ((Integer) ruleDataMap.get("minOutput"))) { throw new GeneralPlatformDomainRuleException(
                            (String) ruleDataMap.get("exceptionCode"), (String) ruleDataMap.get("exceptionCode")); }
                }

            }
        } catch (final EmptyResultDataAccessException e) {

        }
    }
    @Override
    public void businessEventWasExecuted(@SuppressWarnings("unused") Map<BUSINESS_ENTITY, Object> businessEventEntity) {
        // TODO Auto-generated method stub

    }

}
