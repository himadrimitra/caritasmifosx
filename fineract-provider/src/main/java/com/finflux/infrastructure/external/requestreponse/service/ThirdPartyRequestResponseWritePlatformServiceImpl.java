package com.finflux.infrastructure.external.requestreponse.service;

import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.finflux.infrastructure.external.requestreponse.data.ThirdPartyRequestEntityType;
import com.finflux.infrastructure.external.requestreponse.domain.ThirdPartyRequestResponseLog;
import com.finflux.infrastructure.external.requestreponse.domain.ThirdPartyRequestResponseLogRepository;

@Service
public class ThirdPartyRequestResponseWritePlatformServiceImpl
		implements ThirdPartyRequestResponseWritePlatformService {

	private final PlatformSecurityContext context;
	private final ThirdPartyRequestResponseLogRepository requestResponseLogRepository;

	@Autowired
	private ThirdPartyRequestResponseWritePlatformServiceImpl(final PlatformSecurityContext context,
		  final ThirdPartyRequestResponseLogRepository requestResponseLogRepository) {
		this.context = context;
		this.requestResponseLogRepository = requestResponseLogRepository;
	}

	@Override
	public Long registerRequest(ThirdPartyRequestEntityType entityType, Long entityId,
								HttpMethod requestMethod, String url, String request) {
		ThirdPartyRequestResponseLog thirdPartyRequestResponseLog = new ThirdPartyRequestResponseLog(
				entityType.getValue(), entityId, requestMethod.name(),url, request);
		thirdPartyRequestResponseLog = requestResponseLogRepository.save(thirdPartyRequestResponseLog);
		return thirdPartyRequestResponseLog.getId();
	}

	@Override
	public void registerResponse(Long thirdPartyRequestId, String response, Long responseTimeInMs, Integer httpStatusCode) {
		ThirdPartyRequestResponseLog thirdPartyRequestResponseLog = requestResponseLogRepository.findOne(thirdPartyRequestId);
		if(thirdPartyRequestResponseLog!=null){
			thirdPartyRequestResponseLog.setResponse(response);
			thirdPartyRequestResponseLog.setResponseTimeInMs(responseTimeInMs);
			thirdPartyRequestResponseLog.setHttpStatusCode(httpStatusCode);
			requestResponseLogRepository.save(thirdPartyRequestResponseLog);
		}
	}
}
