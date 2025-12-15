package com.ws.msp.mq.sac.dao;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ws.msp.mq.sac.pojo.AuthenticateException;
import com.ws.msp.mq.sac.pojo.RestResult;
import com.ws.msp.pojo.SubConsoleUser;
import com.ws.util.CryptUtil;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class RestDataManager {

	@Value("${sac.url.path}")
	private String sacPath = null;

	@Autowired
	private RestTemplate restTemplate = null;

	private HttpHeaders headers = null;

	public RestDataManager() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("AgentSignature", "WYm1Qsy1sdw=");
		this.headers = headers;
	}

//	public boolean loginSubConsoleUser(String username, String password) throws AuthenticateException, Exception {
//		ResponseEntity<RestResult<Boolean>> result = null;
//		String encpwd = CryptUtil.encrypt(password);
//		String apiUrl = sacPath + "/sms/subconsole/login/" + username + "/" + encpwd + "/";
//		log.debug("Execute REST[{}]", apiUrl);
//		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
//		try {
//			result = restTemplate
//					.exchange(apiUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<RestResult<Boolean>>() {
//					});
//			log.debug(result);
//		} catch (HttpClientErrorException exception) {
//			int statusCode = exception.getStatusCode().value();
//			if (statusCode == HttpStatus.UNAUTHORIZED.value()){
//				throw new AuthenticateException(result.getBody().getCode(), result.getBody().getMessage());
//			}
//		}
//		log.debug(result);
//		if(!result.getStatusCode().is2xxSuccessful()){
//			int statusCode = result.getStatusCode().value();
//			if (statusCode == HttpStatus.UNAUTHORIZED.value()){
//				throw new AuthenticateException(result.getBody().getCode(), result.getBody().getMessage());
//			}
//		}
//		
//		log.debug(result);
//		return result.getBody().getData().booleanValue();
//	}

	public boolean loginSubConsoleUser(String username, String password) throws AuthenticateException, Exception {
		ResponseEntity<RestResult<Boolean>> result = null;
		String encpwd = CryptUtil.encrypt(password);
		/* 20180214 Added by YC
		 * Set password to binary String due to "/" may cause httpAPI error
		 */
		encpwd = Arrays.toString(encpwd.getBytes());
		
		String apiUrl = sacPath + "/sms/subconsole/login/" + username + "/" + encpwd + "/";
		log.debug("Execute REST[{}]", apiUrl);
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
			result = restTemplate
					.exchange(apiUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<RestResult<Boolean>>() {
					});
		log.debug(result);
		if(!result.getBody().isSuccess()){
			throw new AuthenticateException(result.getBody().getCode(), result.getBody().getMessage());
		}
		return result.getBody().getData().booleanValue();
	}
	
	
	public SubConsoleUser getSubConsoleUser(String id) {
		String apiUrl = sacPath + "/sms/subconsole/get/" + id + "/";
		log.debug("Execute REST[{}]", apiUrl);

		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
		RestResult<SubConsoleUser> result = restTemplate
				.exchange(apiUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<RestResult<SubConsoleUser>>() {
				}).getBody();

		log.debug(result);

		return result.getData();
	}

}
