package com.finflux.workflow.configuration.service;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class WorkFlowConfigurationReadServiceImpl implements WorkFlowConfigurationReadService {

    private final static Logger logger = LoggerFactory.getLogger(WorkFlowConfigurationReadServiceImpl.class);
    
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public WorkFlowConfigurationReadServiceImpl(final RoutingDataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

}
