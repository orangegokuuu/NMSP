package com.ws.msp.file.thread;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.inject.Provider;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ws.msp.config.MspProperties;
import com.ws.msp.file.controller.QueueController;
import com.ws.msp.file.util.FileQueue;
import com.ws.msp.file.util.FileUtil;
import com.ws.msp.file.util.FileWatcher;
import com.ws.msp.file.util.WatchObject;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class FileProcessor {

	@Autowired
	MspProperties properties;

	@Autowired
	QueueController queueController;

	@Autowired
	FileUtil fileUtil;

	@Autowired
	@Qualifier("watchObject")
	WatchObject watchObject;

	@Autowired
	Provider<FileQueue> fileQueueProvider;
	FileQueue fileQueue;

	protected Timer resetPerSecond = new Timer();

	Path nowdir;
	String fileName;

	@PostConstruct
	public synchronized void startProcessor() {

		try {
			resetPerSecond.schedule(new TimerTask() {
				@Override
				public void run() {

					if (watchObject != null && watchObject.getWatchDirs() != null) {

						for (Path watchFolder : watchObject.getWatchDirs()) {
							log.debug("******** process folder : [{}]", watchFolder);
							if (Files.exists(watchFolder)) {
								nowdir = watchFolder;
								if (watchFolder.toFile().exists() && watchFolder.toFile().isDirectory()) {
									for (File file : watchFolder.toFile().listFiles()) {
										if (!file.isDirectory()) {
											fileName = file.getName();
											log.debug("***** Now scan file : [{}]",
													file.getAbsolutePath().toString());
											if (StringUtils.contains(file.getAbsolutePath().toString(),
													properties.getFile().getWatcher().getSms_path())) {
												if (fileUtil.checkSmsFileIsDone(fileName, nowdir)) {
													// 20230411 YC modify, only put .txt in queue
													if (fileName.endsWith(fileUtil.getSmsExtension2())) {
														fileName = fileName.replace(fileUtil.getSmsExtension2(),
																fileUtil.getSmsExtension1());
													}
													String absolutePath = nowdir + File.separator + fileName;
													processFile(absolutePath, FileWatcher.SMS_FILE);
												}
											} else if (StringUtils.contains(file.getAbsolutePath().toString(),
													properties.getFile().getWatcher().getBlacklist_path())) {
												if (fileUtil.checkBlackListFileIsDone(fileName, nowdir)) {
													String absolutePath = nowdir + File.separator + fileName;
													processFile(absolutePath, FileWatcher.BLACK_LIST_FILE);
												}
											} else if (StringUtils.contains(file.getAbsolutePath().toString(),
													(properties.getFile().getWatcher().getSpam_keyword_path()))) {
												if (fileUtil.checkSpamKeyFileIsDone(fileName, nowdir)) {
													String absolutePath = nowdir + File.separator + fileName;
													processFile(absolutePath, FileWatcher.SPAM_KEYWORD_FILE);
												}
											} else if (StringUtils.contains(file.getAbsolutePath().toString(),
													(properties.getFile().getWatcher().getMnp_path()))) {

												if (fileUtil.checkMnpFileIsDone(fileName, nowdir)) {

													if (StringUtils.contains(file.getAbsolutePath().toString(),
															"daily")) {
														String absolutePath = nowdir + File.separator + fileName;
														processMnpFile(absolutePath, FileWatcher.MNP_FILE, true);
													} else if (StringUtils.contains(file.getAbsolutePath().toString(),
															"weekly")) {
														String absolutePath = nowdir + File.separator + fileName;
														processMnpFile(absolutePath, FileWatcher.MNP_FILE, false);
													}
												}
											}
										}
									}
								}
							} else {
								log.warn("watch folder is null, folder:[{}]", watchFolder);
							}
						}
					} else {
						log.debug("no monit object detected");
					}
				}
			}, 0, 1000);
		} catch (Exception e) {
			log.error("[RUNTIME] process fileName : [{}] error:[{}], ", fileName, e.getMessage());
			log.error(e, e);

			// move error files to error path
			if (nowdir != null && StringUtils.isNotBlank(fileUtil.getBackupPath())
					&& StringUtils.isNotBlank(fileUtil.getErrorPath()) && StringUtils.isNotBlank(fileName)) {
				fileUtil.moveFile(nowdir, nowdir.resolve(fileUtil.getBackupPath()).resolve(fileUtil.getErrorPath()),
						fileName);
			}
		}

	}

	void processFile(String absolutePath, String fileType) {
		processMnpFile(absolutePath, fileType, false);
	}

	void processMnpFile(String absolutePath, String fileType, boolean mnpDaily) {

		fileQueue = fileQueueProvider.get();
		fileQueue.setAbsoluteFilePath(absolutePath);
		fileQueue.setFileType(fileType);

		if (fileType.equals(FileWatcher.MNP_FILE)) {
			fileQueue.setMNPDaily(mnpDaily);
		}

		// insert queue
		queueController.putQueue(fileQueue);
		// move file to working folder
		if (fileType.equals(FileWatcher.SMS_FILE)) {
			fileUtil.moveSmsFile(absolutePath, watchObject.getWorkingFolder());
		} else {
			fileUtil.moveFile(nowdir, Paths.get(watchObject.getWorkingFolder()),
					fileName);
		}
	}
}