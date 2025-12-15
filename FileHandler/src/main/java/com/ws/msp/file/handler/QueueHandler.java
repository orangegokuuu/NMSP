package com.ws.msp.file.handler;

import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.inject.Provider;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.ws.msp.file.controller.QueueController;
import com.ws.msp.file.thread.FileWorker;
import com.ws.msp.file.util.FileQueue;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class QueueHandler {

	@Autowired
	private Provider<FileWorker> provider;

	@Autowired
	private QueueController queueController;

	@Autowired
	@Qualifier("fileExecutor")
	private ThreadPoolTaskExecutor taskExecutor;

	private boolean retry = false;
	protected Timer resetPerSecond = new Timer();

	@PostConstruct
	public void startUp() {
		resetPerSecond.schedule(new TimerTask() {
			@Override
			public void run() {
				if (getQueueCount() > 0) {
					for (int i = 0; i < getQueueCount(); i++) {
						FileQueue fq = queueController.getMessageQueue();
						if (fq != null && StringUtils.isNotBlank(fq.getAbsoluteFilePath())) {
							log.info("##### Start processing file [{}] ... #####", fq.getAbsoluteFilePath());
							doJob(fq);
						}
					}
				}
			}
		}, 0, 1000);
	}

	public void doJob(FileQueue fileQueue) {

		String fileAbsolutePath = "";
		try {
			// create thread to process file
			if (fileQueue != null && fileQueue.getFilePath() != null
					&& StringUtils.isNotBlank(fileQueue.getFilePath().toAbsolutePath().toString())
					&& StringUtils.isNotBlank(fileQueue.getFileName())) {
				fileAbsolutePath = fileQueue.getAbsoluteFilePath();
				// use file processor to do job
				FileWorker worker = provider.get();
				worker.setFileQueue(fileQueue);
				taskExecutor.execute(worker);
			}
		} catch (TaskRejectedException e) {
			log.trace("Task exceed, pending file : [{}]", fileAbsolutePath);
			waitForCountinue(500);
			// set back to queue again
			retry = !queueController.putQueue(fileQueue);
		} catch (Exception e) {
			log.error("[File Worker] process file [{}] error", fileAbsolutePath);
			log.error(e, e);
		}

		if (retry) {
			for (int i = 1; i < 3; i++) {
				if (queueController.putQueue(fileQueue)) {
					retry = false;
					break;
				}
				waitForCountinue(500);
			}
			if (retry) {
				log.error("[SMS Queue] Put [{}] back in queue fail.", fileAbsolutePath);
			}
		}
	}

	/**
	 * Wait ${wait} ms to continue process
	 * 
	 * @param wait
	 *             : millisecond
	 */
	private void waitForCountinue(long wait) {
		try {
			log.debug("Waiting {}ms...", wait);
			Thread.sleep(wait);
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	private int getQueueCount() {
		return queueController.getTotalQueueCount();
	}
}
