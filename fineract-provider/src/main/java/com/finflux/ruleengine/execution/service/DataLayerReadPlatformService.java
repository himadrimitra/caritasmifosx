package com.finflux.ruleengine.execution.service;

import java.util.Map;

/**
 * Created by dhirendra on 15/09/16.
 */
public interface DataLayerReadPlatformService {

    Map<String,Object> getAllClientMatrix(Long clientId);

    Map<String,Object> getAllLoanApplicationMatrix(Long loanApplicationId);
}
