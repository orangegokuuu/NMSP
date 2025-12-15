package com.ws.msp.mq.sac.controller;

import java.io.IOException;
import java.util.List;

import org.apache.commons.exec.ExecuteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ws.msp.mq.sac.dao.SystemManager;
import com.ws.msp.mq.sac.pojo.RestResult;

@Controller
public class MQAuthController {

	@Autowired
	private SystemManager sm = null;

	@Value("${sac.url.path}")
	private String sacPath = null;

	@Value("${mqsac.mqm.name}")
	private String mqmName = null;

	@RequestMapping(value = "/getSacPath", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody String getSacPath() {
		return "{\"sacpath\":\"" + sacPath + "\"}";
	}

	@RequestMapping(value = "/getMqmName", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody String getMqmName() {
		return "{\"mqmName\":\"" + mqmName + "\"}";
	}

	@RequestMapping(value = "/getMQList", method = RequestMethod.GET)
	public @ResponseBody List<String> getMQList() {
		List<String> result = null;
		// result.setData(sm.getMQList());
		return result;
	}

	@RequestMapping(value = "/createOsUser/{qManager}/{cpId}", method = RequestMethod.POST)
	public @ResponseBody RestResult<String> createOsUser(@PathVariable String qManager, @PathVariable String cpId)
			throws ExecuteException, IOException {

		if (qManager.matches("[a-zA-Z0-9]+") && cpId.matches("[a-zA-Z0-9]+")) {
			return sm.createOsUser(qManager, cpId);
		}
		return null;
	}

	@RequestMapping(value = "/createQueue/{qManager}/{cpId}", method = RequestMethod.POST)
	public @ResponseBody RestResult<String> createQueue(@PathVariable String qManager, @PathVariable String cpId)
			throws ExecuteException, IOException {
		if (qManager.matches("[a-zA-Z0-9]+") && cpId.matches("[a-zA-Z0-9]+")) {
			return sm.createQueue(qManager, cpId);
		}
		return null;
	}

	@RequestMapping(value = "/deleteOsUser/{qManager}/{cpId}", method = RequestMethod.POST)
	public @ResponseBody RestResult<String> deleteOsUser(@PathVariable String qManager, @PathVariable String cpId)
			throws ExecuteException, IOException {
		if (qManager.matches("[a-zA-Z0-9]+") && cpId.matches("[a-zA-Z0-9]+")) {
			return sm.deleteOsUser(qManager, cpId);
		}
		return null;
	}

	@RequestMapping(value = "/deleteQueue/{qManager}/{cpId}", method = RequestMethod.POST)
	public @ResponseBody RestResult<String> deleteQueue(@PathVariable String qManager, @PathVariable String cpId)
			throws ExecuteException, IOException {
		if (qManager.matches("[a-zA-Z0-9]+") && cpId.matches("[a-zA-Z0-9]+")) {
			return sm.deleteQueue(qManager, cpId);
		}
		return null;
	}
}
