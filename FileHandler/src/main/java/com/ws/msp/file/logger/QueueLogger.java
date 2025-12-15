package com.ws.msp.file.logger;

import java.util.Timer;
import java.util.TimerTask;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ws.msp.file.controller.QueueController;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class QueueLogger {

	@Autowired
	private QueueController queueController;

	protected Timer resetPerSecond = new Timer();

	@PostConstruct
	public void startUp() {

		// Do log every 30 sec
		resetPerSecond.schedule(new TimerTask() {
			@Override
			public void run() {
				log.debug("Queue [SMS] count = {}", queueController.getMessageCount());
				log.debug("Queue [BlackList] count = {}", queueController.getBlackListCount());
				log.debug("Queue [MNP] count = {}", queueController.getMnpCount());
				log.debug("Queue [SPAM] count = {}", queueController.getSpamCount());
			}
		}, 0, 30000);
	}
}
