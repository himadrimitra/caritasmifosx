/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.scheduledjobs.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExecuteBatchUpdateTransactional {
    
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public ExecuteBatchUpdateTransactional(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    @Transactional
    public Map<String, Integer> executeBatchUpdate(List<String> insertStatement){
        Map<String, Integer> returnMap = new HashMap<>();
        int result = 0;
        StringBuilder errorMessage = new StringBuilder();
        int[] results = new int[0];
        if(!insertStatement.isEmpty()){
            try{
        results = this.jdbcTemplate.batchUpdate(insertStatement.toArray(new String[0]));
            }catch(Exception e){
                errorMessage.append(e.getMessage());
            }
        }
        for(int i:results){
            result += i;
        }
        returnMap.put(errorMessage.toString(), result);
        return returnMap;
    }
}
