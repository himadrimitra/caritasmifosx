package com.finflux.risk.creditbureau.provider.highmark.service;

import java.util.List;

import com.finflux.risk.creditbureau.provider.highmark.data.HighmarkData;

public interface HighmarkDataReadPlatformService {

    public List<HighmarkData> getHighmarkData(Long clientId);
}
