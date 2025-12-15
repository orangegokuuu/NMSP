package com.ws.data.test;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.inject.Provider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.ws.msp.file.util.FileWatcher;
import com.ws.msp.legacy.LegacyConstant;
import com.ws.msp.pojo.ContentProvider;
import com.ws.msp.pojo.CpSourceAddress;
import com.ws.msp.service.ContentProviderManager;

import lombok.extern.log4j.Log4j2;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@Log4j2
public class DataMigration {

	@Autowired
	private ContentProviderManager contentProviderManager;
	
	@Autowired
	private Provider<FileWatcher> provider;

	@Test
	public void insertIBMCPDataFromFile() {
		String UTF8_BOM = "\uFEFF";
		String watchDir = "/data/temp/";
		String skipDir = "history";
		Path nowdir = null;
		FileInputStream inputStream = null;
		Scanner sc = null;
		int watchInterval = 3;
		log.info("watchDir:[{}]", watchDir);
		try {
			FileWatcher watcher = provider.get();
			// watcher.init(Paths.get(watchDir));
			while (true) {
				WatchKey watchKey = null;
				// WatchKey watchKey = watcher.getWatchService().take();
				// Path nowdir = watcher.getKeys().get(watchKey);
				// nowdir = watcher.getKeys().get(watchKey);
				log.info("watchDir:[{}]", watchDir);
				if (nowdir == null) {
					log.info("watch folder is null,key:[{}]", watchKey);
					continue;
				}
				for (WatchEvent<?> event : watchKey.pollEvents()) {
					WatchEvent.Kind<?> eventType = event.kind();
					if (eventType == ENTRY_CREATE) {
						Thread.sleep(1000 * watchInterval);
						Path child = nowdir.resolve((Path) event.context());
						log.info("file path:[{}]", child.toAbsolutePath().toString());

						inputStream = new FileInputStream(child.toFile());
						sc = new Scanner(inputStream, "UTF-8");
						int count = 1;
						int cpZone = 0;
						while (sc.hasNextLine()) {
							String line = sc.nextLine();

							if (count == 1) {
								if (line.startsWith(UTF8_BOM)) {
									line = line.substring(1);
									cpZone = LegacyConstant.QM_MAP.get(line);
									log.info("@@@@ cpZone:[{}]", cpZone);
								} else {
									cpZone = LegacyConstant.QM_MAP.get(line);
									log.info("@@@@ cpZone:[{}]", cpZone);
								}
							}
							if (line != null && !line.equals("") && count > 1) {
								log.debug("line:[{}]", line);
								String sysId = "";
								String[] array = line.split(",");
								ContentProvider cp = new ContentProvider();
								cp.setCpType(ContentProvider.CP_TYPE_MQ);
								cp.setStatus(ContentProvider.STATUS_ACTIVE);
								cp.setDaLimit(-1);
								cp.setCpZone(cpZone);
								cp.setDrRequestFl(true);
								cp.setApiVersion("1");
								cp.setBlacklistCheckFl(false);
								cp.setSpamCheckFl(false);
								cp.setPushDrFl(true);
								cp.setMoSmsFl(true);
								cp.setCreateBy("WSAdmin");
								cp.setCreateDate(LocalDateTime.now());

								for (String data : array) {
									try {
										if (!data.equals("")) {
											String[] value = data.split("=");
											if ("legacy".equals(value[0])) {
												cp.setLegacy(true);
											}
											if ("maxSizePerReplyMessageForDR".equals(value[0])) {
												cp.setMaxSizePerReplyMessageForDR(
														Integer.valueOf(value[1]));
											}
											if ("maxSizePerReplyMessageForMO".equals(value[0])) {
												cp.setMaxSizePerReplyMessageForMO(
														Integer.valueOf(value[1]));
											}
											if ("maxThread".equals(value[0])) {
												cp.setMaxThread(Integer.valueOf(value[1]));
											}
											if ("maxWaitingTimeForDR".equals(value[0])) {
												cp.setMaxWaitingTimeForDR(
														Integer.valueOf(value[1]));
											}
											if ("maxWaitingTimeForMO".equals(value[0])) {
												cp.setMaxWaitingTimeForMO(
														Integer.valueOf(value[1]));
											}
											if ("plyQueueName".equals(value[0])) {
												cp.setMqRespQName(value[1]);
											}
											if ("reqQueueName".equals(value[0])) {
												cp.setMqReqQName(value[1]);
											}
											if ("splitterWaitingTime".equals(value[0])) {
												cp.setSplitterWaitingTime(
														Integer.valueOf(value[1]));
											}
											if ("sysId".equals(value[0])) {
												cp.setCpId(value[1]);
												cp.setCpName(value[1]);
												sysId = value[1];
											}
											if ("trusted".equals(value[0])) {
												cp.setTrusted(true);
											}
											if ("validSourceAddress".equals(value[0])) {
												String oaArry[] = value[1].split(";");
												List<CpSourceAddress> cpsaMap =
														new ArrayList<CpSourceAddress>();
												for (String oa : oaArry) {
													if (oa != null && !oa.equals("")) {
														CpSourceAddress sa = new CpSourceAddress();
														sa.setCpId(sysId);
														sa.setSourceAddress(oa);
														cpsaMap.add(sa);
													}

												}
												if (cpsaMap.size() > 0) {
													cp.setCpsaMap(cpsaMap);
												}
											}
											if ("waterLevel".equals(value[0])) {
												cp.setSmsLimit(Integer.valueOf(value[1]));
												// cp.setWaterLevel(value[1]);
											}
											if ("prepaid".equals(value[0])) {
												cp.setPrepaidFl(true);
											}
										}
									} catch (Exception e) {
										log.error("data error, sysId:[{}]", sysId);
									}
								}
								// insert data
								if (count > 1) {
									try {
										contentProviderManager.save(ContentProvider.class, cp);
									} catch (Exception e) {
										log.error("insert error, sysId:[{}]", sysId);
										e.printStackTrace();
									}
								}
							}
							count++;
						}
						log.info("@@@ insert END @@@");
					}
				}
			}
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (sc != null) {
				sc.close();
			}
		}
	}

	@Test
	public void insertHTTPDataFromFile() {
		String UTF8_BOM = "\uFEFF";
		String watchDir = "/data/temp/";
		String skipDir = "history";
		Path nowdir = null;
		FileInputStream inputStream = null;
		Scanner sc = null;
		int watchInterval = 3;
		log.info("watchDir:[{}]", watchDir);
		try {
			FileWatcher watcher = provider.get();
			// watcher.init(Paths.get(watchDir));
			while (true) {
				WatchKey watchKey = null;
				// WatchKey watchKey = watcher.getWatchService().take();
				// Path nowdir = watcher.getKeys().get(watchKey);
				// nowdir = watcher.getKeys().get(watchKey);
				log.info("watchDir:[{}]", watchDir);
				if (nowdir == null) {
					log.info("watch folder is null,key:[{}]", watchKey);
					continue;
				}
				for (WatchEvent<?> event : watchKey.pollEvents()) {
					WatchEvent.Kind<?> eventType = event.kind();
					if (eventType == ENTRY_CREATE) {
						Thread.sleep(1000 * watchInterval);
						Path child = nowdir.resolve((Path) event.context());
						log.info("file path:[{}]", child.toAbsolutePath().toString());

						inputStream = new FileInputStream(child.toFile());
						sc = new Scanner(inputStream, "UTF-8");
						int cpZone = 0;
						while (sc.hasNextLine()) {
							String line = sc.nextLine();

							if (line != null && !line.equals("")) {
								log.debug("line:[{}]", line);
								String sysId = "";
								String[] array = line.split(",");
								log.debug("array.length:[{}]", array.length);

								ContentProvider cp = new ContentProvider();
								cp.setCpType(ContentProvider.CP_TYPE_HTTP);
								cp.setDaLimit(-1);
								cp.setCpZone(cpZone);
								cp.setDrRequestFl(true);
								cp.setApiVersion("1");
								cp.setBlacklistCheckFl(true);
								cp.setCreateBy("WSAdmin");
								cp.setCreateDate(LocalDateTime.now());

								int count = 0;
								for (String s : array) {
									if (count == 0 && s != null && !"".equals(s)) {
										if (s.equals("1")) {
											cp.setStatus(ContentProvider.STATUS_ACTIVE);
										} else {
											cp.setStatus(ContentProvider.STATUS_INACTIVE);
										}
									}
									if (count == 1 && s != null && !"".equals(s)) {
										cp.setCpId(s);
										cp.setCpName(s);
										sysId = s;
									}

									if (count == 2 && s != null && !"".equals(s)) {
										String oaArry[] = s.split(";");
										List<CpSourceAddress> cpsaMap =
												new ArrayList<CpSourceAddress>();
										for (String oa : oaArry) {
											if (oa != null && !oa.equals("")) {
												CpSourceAddress sa = new CpSourceAddress();
												sa.setCpId(sysId);
												sa.setSourceAddress(oa);
												cpsaMap.add(sa);
											}

										}
										if (cpsaMap.size() > 0) {
											cp.setCpsaMap(cpsaMap);
										}
									}
									if (count == 3 && s != null && !"".equals(s)) {
										if (s.equals("1")) {
											cp.setThroughPSA(true);
										} else {
											cp.setThroughPSA(false);
										}
									}
									if (count == 4 && s != null && !"".equals(s)) {
										if (s.equals("1")) {
											cp.setPushDrFl(true);
										} else {
											cp.setPushDrFl(false);
										}
									}
									if (count == 5 && s != null && !"".equals(s)) {
										if (s.equals("1")) {
											cp.setSpamCheckFl(true);
										} else {
											cp.setSpamCheckFl(false);
										}
									}
									if (count == 6 && s != null && !"".equals(s)) {
										if (s.equals("1")) {
											cp.setBlockPromotionFl(true);
										} else {
											cp.setBlockPromotionFl(false);
										}
									}
									if (count == 7 && s != null && !"".equals(s)) {
										if (s.equals("1")) {
											cp.setMoSmsFl(true);
										} else {
											cp.setMoSmsFl(false);
										}
									}

									if (count == 8 && s != null && !"".equals(s)) {
										cp.setPushDrUrl(s.trim());
									}

									if (count == 9 && s != null && !"".equals(s)) {
										cp.setDeliverSmUrl(s.trim());
									}
									count++;
								}
								// insert data
								try {
									contentProviderManager.save(ContentProvider.class, cp);
								} catch (Exception e) {
									log.error("insert error, sysId:[{}]", sysId);
									e.printStackTrace();
								}
							}
						}
						log.info("@@@ insert END @@@");
					}
				}
			}
		} catch (IOException | InterruptedException e) {
			log.debug(e.getMessage());
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.debug(e.getMessage());
				}
			}
			if (sc != null) {
				sc.close();
			}
		}
	}

	@Test
	public void insertFileDataFromFile() {
		String UTF8_BOM = "\uFEFF";
		String watchDir = "/data/temp/";
		String skipDir = "history";
		Path nowdir = null;
		FileInputStream inputStream = null;
		Scanner sc = null;
		int watchInterval = 3;
		log.info("watchDir:[{}]", watchDir);
		try {
			FileWatcher watcher = provider.get();
			// watcher.init(Paths.get(watchDir));
			while (true) {
				WatchKey watchKey = null;
				// WatchKey watchKey = watcher.getWatchService().take();
				// Path nowdir = watcher.getKeys().get(watchKey);
				// nowdir = watcher.getKeys().get(watchKey);
				log.info("watchDir:[{}]", watchDir);
				if (nowdir == null) {
					log.info("watch folder is null,key:[{}]", watchKey);
					continue;
				}
				for (WatchEvent<?> event : watchKey.pollEvents()) {
					WatchEvent.Kind<?> eventType = event.kind();
					if (eventType == ENTRY_CREATE) {
						Thread.sleep(1000 * watchInterval);
						Path child = nowdir.resolve((Path) event.context());
						log.info("file path:[{}]", child.toAbsolutePath().toString());

						inputStream = new FileInputStream(child.toFile());
						sc = new Scanner(inputStream, "UTF-8");
						int cpZone = 1;
						while (sc.hasNextLine()) {
							String line = sc.nextLine();

							if (line != null && !line.equals("")) {
								log.debug("line:[{}]", line);
								String sysId = "";
								String[] array = line.split(",");
								log.debug("array.length:[{}]", array.length);

								ContentProvider cp = new ContentProvider();
								cp.setCpType(ContentProvider.CP_TYPE_FILE);
								cp.setDaLimit(-1);
								cp.setCpZone(cpZone);
								cp.setDrRequestFl(false);
								cp.setApiVersion("1");
								cp.setBlacklistCheckFl(false);
								cp.setStatus(ContentProvider.STATUS_ACTIVE);
								cp.setCreateBy("WSAdmin");
								cp.setThroughPSA(false);
								cp.setPushDrFl(false);
								cp.setSpamCheckFl(false);
								cp.setBlockPromotionFl(false);
								cp.setMoSmsFl(false);
								cp.setCreateDate(LocalDateTime.now());

								int count = 0;
								for (String s : array) {
									if (count == 0 && s != null && !"".equals(s)) {
										cp.setCpId(s);
										cp.setCpName(s);
										sysId = s;
									}

									if (count == 1 && s != null && !"".equals(s)) {
										String oaArry[] = s.split(";");
										List<CpSourceAddress> cpsaMap =
												new ArrayList<CpSourceAddress>();
										for (String oa : oaArry) {
											if (oa != null && !oa.equals("")) {
												CpSourceAddress sa = new CpSourceAddress();
												sa.setCpId(sysId);
												sa.setSourceAddress(oa);
												cpsaMap.add(sa);
											}

										}
										if (cpsaMap.size() > 0) {
											cp.setCpsaMap(cpsaMap);
										}
									}
									count++;
								}
								// insert data
								try {
									contentProviderManager.save(ContentProvider.class, cp);
								} catch (Exception e) {
									log.error("insert error, sysId:[{}]", sysId);
								}
							}
						}
						log.info("@@@ insert END @@@");
					}
				}
			}
		} catch (IOException | InterruptedException e) {
			log.debug(e.getMessage());
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.debug(e.getMessage());
				}
			}
			if (sc != null) {
				sc.close();
			}
		}
	}
}
