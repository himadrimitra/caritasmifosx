package com.finflux.infrastructure.common.httpaccess;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalHttpConnectivity {

	public <T> T performServerPost(final String postURL, final T body, Class<T> rerurnType) throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		T response = restTemplate.postForObject(postURL, body, rerurnType);
		return response;
	}

}
