/**
 * RESTful service controller to show MNPD KPI information
 */
package com.ws.api.mvc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoRestController {

	private static Logger logger = LogManager.getLogger(DemoRestController.class);

	@RequestMapping(value = "/Demo/{data}", method = { RequestMethod.GET, RequestMethod.POST })
	public String demo(@PathVariable("data") String data) {
		
		String result;
//		logger.info("Receive Param [{}]", data);
//		result = "Hello " + data + " !!!";
		
		result = "{\n" + 
				" \"modes\" : [\"direct\", \"selection\"],\n" + 
				" \"expiration\" : [duration of validity of requests],\n" + 
				" \"inputs\" : {\n" + 
				" \"date\" : \"scalar\",\n" + 
				" \"contact\" : \"object\",\n" + 
				" \"amount\" : \"scalar\",\n" + 
				" \"order\" : \"array\"\n" + 
				" },\n" + 
				" \"outputs\" : {\n" + 
				" \"personid\" : \"\",\n" + 
				" \"contact\" : { \"lastname\" : \"\", \"firstname\" : \"\" },\n" + 
				" \"shoppingcart\" : [ { \"itemname\" : \"\", \"reference\" : \"\", \"quantity\" : \"\", \"unitprice\" :\"\" } ]\n" + 
				" }\n" + 
				"}";
		
		return result;
	}
}

