/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

package com.finflux.email.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class BusinessEventEmailConfigurationReadPaltformServiceImpl implements BusinessEventEmailConfigurationReadPaltformService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BusinessEventEmailConfigurationReadPaltformServiceImpl(RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Map<String, Object> retrieveOneWithBuisnessEvent(final BUSINESS_EVENTS businessEvent) {
        try{
        HashMap<String, String> map = new HashMap<String, String>();
        String sql = "select bemc.buisness_event as businessEvent, bemc.center_display_name as centerDisplayName, bemc.attachment_type as attachmentType,"
                + "bemc.report_name as reportName from f_business_events_email_config  bemc where bemc.buisness_event = ? ";
        return this.jdbcTemplate.queryForMap(sql, businessEvent.getValue());
        
        }catch(final EmptyResultDataAccessException e) {
            return null;
        }
    }
}  
