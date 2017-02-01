package com.finflux.infrastructure.external.requestreponse.service;

import org.springframework.http.HttpMethod;

import com.finflux.infrastructure.external.requestreponse.data.ThirdPartyRequestEntityType;

public interface ThirdPartyRequestResponseWritePlatformService {
	Long registerRequest(ThirdPartyRequestEntityType entityType, Long entityId,
						 HttpMethod requestMethod, String url, String request);

	void registerResponse(Long thirdPartyRequestId, String response, Long responseTimeInMs,
						 Integer httpStatusCode);
}
