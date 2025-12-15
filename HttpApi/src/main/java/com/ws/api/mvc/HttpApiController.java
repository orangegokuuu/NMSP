 package com.ws.api.mvc;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ws.emg.constant.SmppConstant;
import com.ws.emg.pojo.MessageObject;
import com.ws.emg.util.EmgParser;
import com.ws.msp.config.MspProperties;

@Controller
@RequestMapping("/api")
public class HttpApiController {

	private static Logger logger = LogManager.getLogger(HttpApiController.class);

	@Autowired
	private MspProperties properties;

//	@Autowired
//	@Qualifier("mtExcutor")
//	private TaskExecutor taskExecutor = null;


	@Autowired
	private EmgParser parser;

	// @RequestMapping(value = "/submit", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String demo(@RequestParam("xml") String xml) {

		String result = "";

		List<MessageObject> msgs = parser.parse(xml);

		for (MessageObject msg : msgs) {

			try {// create task and do enqueue job
				msg.setState(SmppConstant.RC_ACCEPTD);
				msg.setWsMessageId(RandomStringUtils.randomAlphanumeric(8).toUpperCase());
				createTask(msg);
			} catch (TaskRejectedException e) {
				// limited thread size so do nothing here
				msg.setState(SmppConstant.RC_REJECTD);
			} catch (Exception e) {
				msg.setState(SmppConstant.RC_REJECTD);
				logger.error("[HTTP API PRODUCER] runtime error : [{}]", e.getMessage());
			}
		}
		
		//TODO confirm sms resp and handle process

		return result;
	}

	private void createTask(MessageObject msg) throws Exception {

		//TODO
		/*
		Producer worker = workerProvider.get();
		worker.init(msg, properties.getDal().getJms().getMtQueueName());
		taskExecutor.execute(worker);
		*/
	}
}
