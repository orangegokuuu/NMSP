package com.ws.msp.file.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class FileWatcher {

	public static final String SMS_FILE = "SMS_FILE";
	public static final String BLACK_LIST_FILE = "BLACK_LIST_FILE";
	public static final String SPAM_KEYWORD_FILE = "SPAM_KEYWORD_FILE";
	public static final String MNP_FILE = "MNP_FILE";

	@Autowired
	@Qualifier("watchObject")
	WatchObject watchObject;

	Timer resetPerSecond = new Timer();

	@PostConstruct
	public void init() {

		try {

			resetPerSecond.schedule(new TimerTask() {
				@Override
				public void run() {

					for (Path root : watchObject.getWatchDirs()) {
						log.debug("==== watchDir:[{}] skipDir:[{}]", root, watchObject.getSkipDirs().toString());
						registerAll(root);
					}
				}
			}, 5000, 1000);

		} catch (Exception e) {
			log.error(e, e);
		}
	}

	public void registerAll(Path root) {
		// register all subfolders
		try {
			log.debug("sacn root [{}]", root);
			Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					registerDirectory(dir);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
					// ignore and continue
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (NoSuchFileException e) {
			// cancel register
			cancelRegisterDirectory(root);
		} catch (Exception e) {
			log.error("error occur when register [{}]", root);
			log.error(e, e);
		}
	}

	private void registerDirectory(Path dir) {
		try {
			boolean sw = true;
			for (String skip : watchObject.getSkipDirs()) {
				log.debug("==== skipDir:[{}], now path [{}]", skip, dir);
				if (StringUtils.contains(dir.toAbsolutePath().toString(), skip)) {
					sw = false;
					break;
				}
			}
			if (sw) {
				log.debug("==== registerDirectory dir path:[{}]", dir.toAbsolutePath().toString());
				if (!watchObject.getWatchDirs().contains(dir)) {
					watchObject.getWatchDirs().add(dir);
				}
			}
		} catch (Exception e) {
			log.error("register [{}] fail", dir.toString());
			log.error(e, e);
		}
	}

	/**
	 * cancel watch service when directory not exist
	 * 
	 * @param path
	 */
	public void cancelRegisterDirectory(Path path) {
		try {
			log.debug("remove watcher folder [{}]", path);
			synchronized (watchObject.getWatchDirs()) {
				if (watchObject.getWatchDirs().contains(path)) {
					watchObject.getWatchDirs().remove(path);
				}
			}
		} catch (Exception e) {
			log.error("cancel register [{}] fail.", path.toString());
			log.error(e, e);
		}
	}

}
