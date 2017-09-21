package africastalking.sms;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AfricasTalkingClient {

	private static final String SMSURLString      = "https://api.africastalking.com/version1/messaging";
	private static final int HTTP_CODE_OK = 200;
	private static final int HTTP_CODE_CREATED = 201;
	private static final boolean DEBUG = false;

	//Client Credentials
	private final String userName;
	private final String applicationKey;

	//DefaultDatamap
	private static final Logger logger = LoggerFactory.getLogger(AfricasTalkingClient.class);

	public AfricasTalkingClient(String userName, String applicationKey) {
		this.userName = userName;
		this.applicationKey = applicationKey;
	}

	public JSONArray sendMessage(final String mobileNumber, final String message) throws Exception {
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("username", userName);
		data.put("from", "CaritasNRB");
		data.put("to", mobileNumber);
		data.put("message", message);
		return postMessage(data);
	}

	private JSONArray postMessage(HashMap<String, String> dataMap) throws Exception {
		try {
			String data = new String();
			Iterator<Entry<String, String>> it = dataMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
				data += URLEncoder.encode(pairs.getKey().toString(), "UTF-8");
				data += "=" + URLEncoder.encode(pairs.getValue().toString(), "UTF-8");
				if (it.hasNext())
					data += "&";
			}
			URL url = new URL(SMSURLString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("apikey", applicationKey);
			conn.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
			writer.write(data);
			writer.flush();

			final int responseCode = conn.getResponseCode();

			BufferedReader reader;
			if (responseCode == HTTP_CODE_OK || responseCode == HTTP_CODE_CREATED) {
				reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			} else {
				reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}
			String response = reader.readLine();

			if (DEBUG) {
				logger.debug(response);
			}
			reader.close();
			conn.disconnect();
			if (responseCode == HTTP_CODE_CREATED || responseCode == HTTP_CODE_OK) { 
				JSONObject jsObject = new JSONObject(response);
	    		JSONArray  recipients = jsObject.getJSONObject("SMSMessageData").getJSONArray("Recipients");
	    		return recipients;	
			}
			throw new Exception(response);
		} catch (Exception e) {
			throw e;
		}
	}
}
