package com.ws.ibm.mq.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ws.ibm.mq.handler.MQClient;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RequestMapping("/mqclient")
@RestController
public class SubmitController {

	@Autowired
	private MQClient mqClient = null;

	@RequestMapping(value = "/triggerQueue/{cpId}/")
	public void triggerTargetQueue(@PathVariable String cpId) {
		log.info("Trigger Queue for cp[{}]", cpId);
		mqClient.triggerThread(cpId);
	}

}
