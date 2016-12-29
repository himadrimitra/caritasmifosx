package com.finflux.infrastructure.common.httpaccess;

import java.net.ConnectException;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.finflux.infrastructure.external.authentication.aadhar.exception.ConnectionFailedException;

@Service
public class ExternalHttpConnectivity {

	public <T> T performServerPost(final String postURL, final T body, Class<T> rerurnType){
		T response = null;
		try{
			RestTemplate restTemplate = new RestTemplate();
			response = restTemplate.postForObject(postURL, body, rerurnType);
		}catch(Exception ce){
			throw new ConnectionFailedException(
					"Unable to communicate with Aadhaar server. Please Conntact your Support team.");
		}
		return response;
	}

}
