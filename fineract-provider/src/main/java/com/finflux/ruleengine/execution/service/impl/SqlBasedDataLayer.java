package com.finflux.ruleengine.execution.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.finflux.ruleengine.execution.data.DataLayerKey;
import com.finflux.ruleengine.execution.exception.SqlNotFoundException;
import com.finflux.ruleengine.execution.service.DataLayer;

public class SqlBasedDataLayer implements DataLayer {

    private final Map<String, Object> keyValueMap;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    // private final
    private final Map<String, Object> paramMap;

    public SqlBasedDataLayer(final RoutingDataSource dataSource, Map<String, Object> paramMap) {
        this.keyValueMap = new HashMap<>();
        this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.paramMap = paramMap;
    }

    @Override
    public Map<String, Object> getValues(List<String> keys) {
        Map<String, Object> myMap = new HashMap<>();
        for (String key : keys) {
            myMap.put(key, keyValueMap.get(key));
        }
        return myMap;
    }

    @Override
    public Object getValue(String key) {
        return keyValueMap.get(key);
    }

    @Override
    public void build(Map<String, Object> dataLayerEntities) {
        keyValueMap.clear();
        long ruleId = (long) dataLayerEntities.get(DataLayerKey.RULE_ID.getValue());
        String sql = null;
        try {
            final Map<String, Object> params = new HashMap<>(1);
            params.put("ruleId", ruleId);
            final String sqlSearch = "select ruleSql.sql_query from f_risk_rule_sql ruleSql where ruleSql.rule_id = :ruleId";
            sql = this.namedJdbcTemplate.queryForObject(sqlSearch, params, String.class);
        } catch (final EmptyResultDataAccessException e) {
            throw new SqlNotFoundException(ruleId);
        }
        this.keyValueMap.putAll(this.namedJdbcTemplate.queryForMap(sql, this.paramMap));
        this.keyValueMap.putAll(dataLayerEntities);
    }

    @Override
    public Map<String, Object> getParamsMap() {
        return keyValueMap;
    }

}
