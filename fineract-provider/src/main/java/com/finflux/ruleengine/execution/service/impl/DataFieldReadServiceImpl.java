package com.finflux.ruleengine.execution.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.finflux.ruleengine.execution.data.DataField;

@Service
public class DataFieldReadServiceImpl implements DataFieldReadService{

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DataFieldMapper dataFieldMapper=new DataFieldMapper();
    private final DataFieldUtils dataFieldUtils;
    @Autowired
    public DataFieldReadServiceImpl(final RoutingDataSource dataSource,final DataFieldUtils dataFieldUtils) {
        this.jdbcTemplate=new NamedParameterJdbcTemplate(dataSource);
        this.dataFieldUtils=dataFieldUtils;
    }
    
    @Override
    public Map<String, Object> getAllFieldValues(List<String> keys, Map<String, Object> fieldParams) {
        Map<String,Object> keyValueMap=new HashMap<>();
       if(keys!=null)
       {
           Map<String,Object> params = new HashMap<>();
           params.put("key", keys);
           final String sql = "select " + dataFieldMapper.schema() + " where f.uname in (:key)";
           List<DataField> fields = this.jdbcTemplate.query(sql, params,dataFieldMapper);
        for(DataField dataField:fields)
        {
            final String sqlQuery=dataField.getQuery();
            if(sqlQuery != null){
            String paramsJson=dataField.getParams();
            Map<String,Object> paramMap=new HashMap<>();
            this.dataFieldUtils.updateParamMap(paramMap, paramsJson, fieldParams);
            keyValueMap.putAll(this.jdbcTemplate.queryForMap(sqlQuery, paramMap));
            }
        }
       }
        return keyValueMap;
    }

private static final class DataFieldMapper implements RowMapper<DataField> {


    private final String schemaSql;

    public DataFieldMapper() {
        final StringBuilder sqlBuilder = new StringBuilder(100);
        sqlBuilder.append(" ");
        sqlBuilder.append("f.name as name, ");
        sqlBuilder.append("f.sql_query as query, ");
        sqlBuilder.append("f.input_params as params ");
        sqlBuilder.append("from f_risk_field as f ");
        this.schemaSql = sqlBuilder.toString();
    }

    public String schema() {
        return this.schemaSql;
    }

    @Override
    public DataField mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
        final String name = rs.getString("name");
        final String query = rs.getString("query");
        final String params=rs.getString("params");
        DataField dataField=new DataField(name,query,params);
        return dataField;
        }
    }       
}
