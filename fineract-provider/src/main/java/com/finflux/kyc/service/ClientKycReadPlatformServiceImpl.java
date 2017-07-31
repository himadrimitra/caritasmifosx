package com.finflux.kyc.service;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ClientKycReadPlatformServiceImpl implements ClientKycReadPlatformService {
    
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public ClientKycReadPlatformServiceImpl(final RoutingDataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    @Override
    public String retrieveResponseObjectWithClientId(Long clientId){
        try {
            final String sql = "select kyc.response_data from f_client_kyc_details kyc where kyc.client_id = ?";

            return this.jdbcTemplate.queryForObject(sql, new Object[] { clientId }, String.class);

        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

}
