package com.finflux.ruleengine.execution.service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.service.BusinessEventListner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.finflux.ruleengine.execution.data.DataLayerKey;
import com.finflux.ruleengine.execution.service.impl.SqlBasedDataLayer;
import com.finflux.ruleengine.lib.data.RuleResult;
import com.finflux.ruleengine.lib.data.ValueType;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Component
public class SqlBasedBusinessEventListner implements BusinessEventListner {

    private final RoutingDataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final RuleExecutionService ruleExecutionService;
    private final FromJsonHelper fromApiJsonHelper;
    private final String MEMBER_SEPARATOR = "\\.";
    private final String sql = "select berm.rule_id as ruleId,berm.min_output_value_for_validation as minOutput,berm.validation_exception_code as exceptionCode,"
            + "berm.input_param_detail as paramDetail  from f_business_event_rule_mapping berm where berm.business_event = ?";

    @Autowired
    public SqlBasedBusinessEventListner(final RoutingDataSource dataSource, final RuleExecutionService ruleExecutionService,
            final FromJsonHelper fromApiJsonHelper) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.ruleExecutionService = ruleExecutionService;
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    @Override
    public void businessEventToBeExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
        try {
            BUSINESS_EVENTS event = (BUSINESS_EVENTS) businessEventEntity.get(BUSINESS_ENTITY.BUSINESS_EVENT);

            final Map<String, Object> ruleDataMap = this.jdbcTemplate.queryForMap(sql, event.getValue());
            if (ruleDataMap.get("ruleId") != null) {
                Long ruleId = (Long) ruleDataMap.get("ruleId");
                final Map<String, Object> paramMap = new HashMap<>();
                if (ruleDataMap.get("paramDetail") != null) {
                    String paramDetail = (String) ruleDataMap.get("paramDetail");
                    final JsonElement element = this.fromApiJsonHelper.parse(paramDetail);
                    JsonArray inputparams = this.fromApiJsonHelper.extractJsonArrayNamed("inputValues", element);
                    for (int i = 0; i < inputparams.size(); i++) {
                        final JsonObject jsonObject = inputparams.get(i).getAsJsonObject();
                        String beanName = this.fromApiJsonHelper.extractStringNamed("entity", jsonObject);
                        JsonArray properties = this.fromApiJsonHelper.extractJsonArrayNamed("properties", jsonObject);
                        updateParamMap(paramMap, beanName, properties, businessEventEntity);
                    }
                }
                SqlBasedDataLayer dataLayer = new SqlBasedDataLayer(this.dataSource, paramMap);
                final Map<DataLayerKey, Long> dataLayerKeyLongMap = new HashMap<>();
                dataLayerKeyLongMap.put(DataLayerKey.RULE_ID, ruleId);
                dataLayer.build(dataLayerKeyLongMap);
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

    private void updateParamMap(final Map<String, Object> paramMap, String beanName, final JsonArray properties,
            Map<BUSINESS_ENTITY, Object> businessEventEntity) {

        switch (beanName) {
            case "json_command":
                JsonCommand jsonCommand = (JsonCommand) businessEventEntity.get(BUSINESS_ENTITY.JSON_COMMAND);
                final Map<String, Object> commandAsJsonMap = jsonCommand.extractObjectMap();

                for (int i = 0; i < properties.size(); i++) {
                    final JsonObject jsonObject = properties.get(i).getAsJsonObject();
                    String feildName = this.fromApiJsonHelper.extractStringNamed("feildName", jsonObject);
                    String sqlParamName = this.fromApiJsonHelper.extractStringNamed("sqlParamName", jsonObject);
                    paramMap.put(sqlParamName, commandAsJsonMap.get(feildName));
                }
            break;
            default:
                Object object = businessEventEntity.get(BUSINESS_ENTITY.from(beanName));

                for (int i = 0; i < properties.size(); i++) {
                    try {
                        final JsonObject jsonObject = properties.get(i).getAsJsonObject();
                        String feildName = this.fromApiJsonHelper.extractStringNamed("feildName", jsonObject);
                        String[] members = feildName.split(MEMBER_SEPARATOR);
                        Object value = object;
                        for (String member : members) {
                            Field feild = FieldUtils.getField(object.getClass(), member, true);
                            value = FieldUtils.readField(feild, value, true);
                        }
                        String sqlParamName = this.fromApiJsonHelper.extractStringNamed("sqlParamName", jsonObject);
                        paramMap.put(sqlParamName, value);
                    } catch (IllegalAccessException e) {

                    }
                }

            break;

        }
    }

    @Override
    public void businessEventWasExecuted(@SuppressWarnings("unused") Map<BUSINESS_ENTITY, Object> businessEventEntity) {
        // TODO Auto-generated method stub

    }

}
