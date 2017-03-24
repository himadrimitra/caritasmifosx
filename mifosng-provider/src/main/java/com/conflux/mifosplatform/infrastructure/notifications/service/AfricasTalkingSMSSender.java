package com.conflux.mifosplatform.infrastructure.notifications.service;

import org.json.JSONArray;

import africastalking.sms.AfricasTalkingClient;

public class AfricasTalkingSMSSender extends AbstractSMSSender implements
		SMSSender {

	private final AfricasTalkingClient client ;
	
	protected AfricasTalkingSMSSender() {
		String user = getSMSConfig("africastalking.user");				
		String key =getSMSConfig("africastalking.key");
		client =  new AfricasTalkingClient(user, key) ;
	}

	@Override
	public JSONArray sendmsg(String to, String message) {
		// Uses Africa's talking JAVA classes to send SMS
		JSONArray response=null; 
		try {
			response=client.sendMessage(to, message);			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public void send(String to, String message) {
		// TODO Auto-generated method stub
		
	}

}
