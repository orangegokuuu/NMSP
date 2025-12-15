package com.ws.jms.test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.jms.annotation.JmsListener;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class Consumer {
	private int count = 0;

	public Consumer() {
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

		executor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				log.info("RX Rate = {}", count);
				count = 0;
			}
		}, 1L, 1, TimeUnit.SECONDS);

	}

	@JmsListener(destination = "testing.queue")
	public void receiveMessage(String msg) {
		count++;
		log.trace("Received [{}]", msg);
	}
}
