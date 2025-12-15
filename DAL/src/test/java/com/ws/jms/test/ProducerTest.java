package com.ws.jms.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.log4j.Log4j2;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, MongoDataAutoConfiguration.class,
		MongoAutoConfiguration.class, HibernateJpaAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class })
@ContextConfiguration(classes = JmsConfig.class)
@Log4j2
public class ProducerTest {

	@Autowired
	private JmsTemplate jmsTemplate = null;

	private int index = 0;
	private int count = 0;

	@Test
	public void test1() throws InterruptedException {

		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

		executor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				log.info("TX Rate = {}", count);
				count = 0;
			}
		}, 1L, 1, TimeUnit.SECONDS);

		List<Thread> pool = new ArrayList<Thread>();
		for (int i = 0; i < 50; i++) {
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						try {
							jmsTemplate.convertAndSend(new String("Test " + index));
							index++;
							count++;
						} catch (JmsException e) {
							log.warn("Exception during send JMS", e);
						}
					}
				}
			});
			pool.add(t);
			t.start();
		}

		while (true) {
			Thread.sleep(1000L);
		}
	}
}
