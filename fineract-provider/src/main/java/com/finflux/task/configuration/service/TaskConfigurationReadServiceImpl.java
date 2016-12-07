package com.finflux.task.configuration.service;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class TaskConfigurationReadServiceImpl implements TaskConfigurationReadService {

    private final static Logger logger = LoggerFactory.getLogger(TaskConfigurationReadServiceImpl.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TaskConfigurationReadServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

}