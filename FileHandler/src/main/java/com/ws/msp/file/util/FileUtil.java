package com.ws.msp.file.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.ws.api.util.HttpApiUtils;
import com.ws.api.util.XmlUtils;
import com.ws.hibernate.exception.DataAccessException;
import com.ws.httpapi.pojo.SMS;
import com.ws.msp.config.MspProperties;
import com.ws.msp.file.controller.BlackListFileHandlerController;
import com.ws.msp.file.controller.MnpFileHandlerController;
import com.ws.msp.file.controller.SmsFileHandlerController;
import com.ws.msp.file.controller.SpamFileHandlerController;
import com.ws.msp.pojo.BlackList;
import com.ws.msp.pojo.ContentProvider;
import com.ws.msp.pojo.MnpApiPhoneroutinginfo;
import com.ws.msp.pojo.SpamKeyWord;
import com.ws.msp.service.BlackListManager;
import com.ws.msp.service.ContentProviderManager;
import com.ws.msp.service.FetPrefixManager;
import com.ws.msp.service.MnpApiPhoneroutinginfoManager;
import com.ws.msp.service.SpamKeyWordManager;
import com.ws.util.CommonFileUtil;

import lombok.Getter;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileUtil extends CommonFileUtil {

	private static Logger bl_logger = LogManager.getLogger(BlackListFileHandlerController.class);
	private static Logger sms_logger = LogManager.getLogger(SmsFileHandlerController.class);
	private static Logger keyword_logger = LogManager.getLogger(SpamFileHandlerController.class);
	private static Logger mnp_logger = LogManager.getLogger(MnpFileHandlerController.class);

	public static final String SUBMIT_API_SUCCESS = "200";

	@Autowired
	private XmlUtils xmlUtils;

	@Autowired
	private MspProperties properties;

	@Autowired
	private BlackListManager blackListManager;

	@Autowired
	private ContentProviderManager contentProviderManager;

	@Autowired
	private SpamKeyWordManager spamKeyWordManager;

	@Autowired
	private MnpApiPhoneroutinginfoManager mnpApiPhoneroutinginfoManager;

	@Autowired
	private FetPrefixManager fetPrefixManager;

	@Getter
	@Value("${file.watcher.file_extension1}")
	private String smsExtension1;

	@Getter
	@Value("${file.watcher.file_extension2}")
	private String smsExtension2;

	@Value("${file.watcher.submitsms_url}")
	private String submitUrl;

	@Getter
	@Value("${file.watcher.error_path}")
	private String errorPath;

	@Getter
	@Value("${file.watcher.working_folder}")
	private String workingPath;

	@Getter
	@Value("${file.watcher.sms_backup_path}")
	private String backupPath;

	public synchronized boolean checkBlackListFileIsDone(String fileName, Path path) {
		return checkFileIsDone(fileName, path, FileWatcher.BLACK_LIST_FILE);
	}

	public synchronized boolean checkSmsFileIsDone(String fileName, Path path) {
		return checkFileIsDone(fileName, path, FileWatcher.SMS_FILE);
	}

	public synchronized boolean checkSpamKeyFileIsDone(String fileName, Path path) {
		return checkFileIsDone(fileName, path, FileWatcher.SPAM_KEYWORD_FILE);
	}

	public synchronized boolean checkMnpFileIsDone(String fileName, Path path) {
		return checkFileIsDone(fileName, path, FileWatcher.MNP_FILE);
	}

	synchronized boolean checkFileIsDone(String fileName, Path path, String type) {

		boolean isDone = false;
		Path file = null;

		if (StringUtils.isNotBlank(fileName) && !fileName.startsWith(".")) { // hidden file not process
			try {
				if (StringUtils.isNotBlank(type) && FileWatcher.SMS_FILE.equals(type)) { // sms need check extension
					if (fileName.endsWith(smsExtension1)) {
						file = path.resolve(fileName.replace(smsExtension1, smsExtension2));
					} else if (fileName.endsWith(smsExtension2)) {
						file = path.resolve(fileName.replace(smsExtension2, smsExtension1));
					}
				} else { // others no extension
					file = path.resolve(fileName);
				}

				if (file != null && Files.exists(file) && !Files.isHidden(path)) {
					isDone = true;
				}

				if (StringUtils.isNotBlank(type) && type.equals(FileWatcher.SMS_FILE)) {
					sms_logger.debug("check [{}] pair file [{}] is done ?, [{}] ", fileName, file.getFileName(),
							isDone);
				} else if (StringUtils.isNotBlank(type) && type.equals(FileWatcher.BLACK_LIST_FILE)) {
					bl_logger.debug("file is exist:[{}] ", isDone);
				} else if (StringUtils.isNotBlank(type) && type.equals(FileWatcher.MNP_FILE)) {
					mnp_logger.debug("file is exist:[{}] ", isDone);
				} else if (StringUtils.isNotBlank(type) && type.equals(FileWatcher.SPAM_KEYWORD_FILE)) {
					keyword_logger.debug("file is exist:[{}] ", isDone);
				}
			} catch (Exception e) {
				// ignore
			}
		}
		return isDone;

	}

	public String checkBlackListData(String line) {
		String data = "";
		try {
			if (line.startsWith("-") || line.startsWith("+") || line.startsWith("++")) {
				line = line.substring(1);
			}
			if (xmlUtils.checkNumberPrefix(line)) {
				data = resolveMsisdn(line);
			} else {
				bl_logger.info("Black List File data format error, number:[{}]", line);
			}
		} catch (Exception e) {
			bl_logger.error("[RUNTIME] Black List File check data failed ,error message:[{}]", e.getMessage());
			bl_logger.error(e, e);
		}
		return data;
	}

	public void processBlackListFile(Path path) {
		bl_logger.info("==== blacklist file process start ====");
		List<String> lines = new ArrayList<String>();
		List<String> delLines = new ArrayList<String>();
		FileInputStream inputStream = null;
		Scanner sc = null;
		bl_logger.info("processBlackListFile load file, file:[{}]", path.toAbsolutePath());
		try {
			inputStream = new FileInputStream(path.toFile());
			sc = new Scanner(inputStream, "UTF-8");
			boolean firstLine = true;
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (firstLine) {
					firstLine = false;
					// String UTF8_BOM = "\uFEFF";
					if (line.startsWith(FileUtil.UTF16BE_BOM) || line.startsWith(FileUtil.UTF16LE_BOM)
							|| line.startsWith(FileUtil.UTF8_BOM)) {
						line = line.substring(1);
					}
				}
				String data = checkBlackListData(line);
				if (!data.equals("") && line.startsWith("+")) {
					lines.add(data);
				} else if (!data.equals("") && line.startsWith("-")) {
					delLines.add(data);
				}
			}

			if (sc.ioException() != null) {
				bl_logger.error("[RUNTIME] processBlackListFile Scanner ioException:[{}]",
						sc.ioException().getMessage());
				throw sc.ioException();
			}
		} catch (FileNotFoundException e) {
			bl_logger.error("[RUNTIME] processBlackListFile FileNotFoundException:[{}]", e.getMessage());
		} catch (IOException e) {
			bl_logger.error("[RUNTIME] processBlackListFile IOException:[{}]", e.getMessage());
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
		bl_logger.info("processBlackListFile load file, insert size:[{}] ,delete size:[{}]", lines.size(),
				delLines.size());
		if (lines != null && lines.size() > 0) {
			// modify by matthew 2018-04-19
			try {
				// del
				bl_logger.info("processBlackListFile delete data to db and remove cache");
				int count = 1;
				for (String number : delLines) {
					BlackList bl = new BlackList();
					bl.setDestNumber(number);
					bl.setCreateBy("SYSTEM");
					bl.setCreateDate(new Date());
					try {
						blackListManager.deleteCacheAndDb(bl);
					} catch (Exception e) {
						bl_logger.warn("[RUNTIME] delete dest_number:[{}] ,error message:[{}] ", number,
								e.getMessage());
					}
					if (count % 100000 == 0) {
						bl_logger.info("processBlackListFile delete data to db and remove cache,count[{}]", count);
					}
					count++;
				}

				bl_logger.info("processBlackListFile insert data to db and load to cache");
				count = 1;
				for (String number : lines) {
					BlackList bl = new BlackList();
					bl.setDestNumber(number);
					bl.setCreateBy("SYSTEM");
					bl.setCreateDate(new Date());
					try {
						blackListManager.addAndSave(bl);
						bl_logger.debug("save blacklist record [{}]", bl.getDestNumber());
					} catch (Exception e) {
						bl_logger.warn("[RUNTIME] insert dest_number:[{}] ,error message:[{}] ", number,
								e.getMessage());
					}
					if (count % 100000 == 0) {
						bl_logger.info("processBlackListFile insert data to db and load to cache,count[{}]", count);
					}
					count++;
				}

			} catch (DataAccessException e) {
				bl_logger.error("[DB] delete or save error ,message:[{}]", e.getMessage());
				bl_logger.error(e, e);
			} catch (Exception e) {
				bl_logger.error("[RUNTIME] error message:[{}]", e.getMessage());
				bl_logger.error(e, e);
			} finally {
				lines.clear();
				delLines.clear();
			}

		}
		bl_logger.info("==== blacklist file process end ====");
	}

	public SMS processSMSFile(String absoluteFilePath) throws Exception {

		Path source;
		String fileName;

		if (StringUtils.isBlank(absoluteFilePath)) {
			return null;
		}

		source = getFilePath(absoluteFilePath);
		fileName = getFileName(absoluteFilePath);

		return processSMSFile(source, fileName);
	}

	public SMS processSMSFile(Path sourceDir, String fileName) throws Exception {

		SMS sms = null;
		Path file = null;

		sms_logger.debug("fileName : [{}], Path :[{}]", fileName, sourceDir);

		if (fileName.endsWith(smsExtension1)) {
			file = sourceDir.resolve(fileName);
		} else if (fileName.endsWith(smsExtension2)) {
			file = sourceDir.resolve(fileName.replace(smsExtension2, smsExtension1));
		}
		if (file != null) {
			List<String> lines = readfile(file);
			if (lines != null && lines.size() > 0) {
				String sysId = fileName.substring(0, 7);
				String language = lines.get(1);
				String text = lines.get(0);
				// check cp is exist
				ContentProvider cp = contentProviderManager.get(ContentProvider.class, sysId);
				if (cp == null || cp.getCpsaMap() == null) {
					sms_logger.info("SYSID:[{}] ,DB not find ContentProvider data or CpSourceAddress is null ",
							sysId);
					return sms;
				}

				if (!checkContentLength(language, text.length())) { // check content length
					sms_logger.warn("file [{}], language:[{}] text too long, length:[{}]", fileName, language,
							text.length());
					return sms;
				} else if (lines.size() > properties.getFile().getTargetSize() + 2) { // check target size
					sms_logger.warn("file [{}], target size [{}] too much, now setting [{}]", fileName,
							lines.size() - 2,
							properties.getFile().getTargetSize());
					return sms;
				} else {
					String coding = "utf8";
					if ("B".equals(language)) {
						coding = "big5";
					} else if ("E".equals(language)) {
						coding = "ISO-8859-1";
					}
					sms = new SMS();
					SMS.Message message = new SMS.Message();
					sms.setSysId(sysId);
					message.setLanguage(language.trim());
					message.setSource(cp.getCpsaMap().get(0).getSourceAddress());
					message.setValidType("4");
					message.setText(HttpApiUtils.base64Encoded(text, coding));
					message.setDrFlag("false");
					if ("MSP2".indexOf(cp.getApiVersion()) > 0) {
						message.setLongSmsFlag("true");
					}
					for (int i = 2; i < lines.size(); i++) {
						String mdn = lines.get(i);
						if (StringUtils.isNotBlank(mdn)) {
							message.getTarget().add(removeNoneVisible(mdn)); // trim spaces
						}
					}
					sms.setMessage(message);
				}
			}
		}
		return sms;
	}

	public String submitSms(String sysId, SMS sms) {
		String result = "";
		try {
			String xml = XmlUtils.ObjectToXml(sms, SMS.class);
			RestTemplate rest = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
			map.add("xmlData", xml);

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map,
					headers);
			ResponseEntity<String> responseEntity = rest.exchange(submitUrl, HttpMethod.POST, request, String.class);

			sms_logger.debug("SYSID:[{}] submitSms StatusCode:[{}]", sysId, responseEntity.getStatusCode());
			if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
				result = SUBMIT_API_SUCCESS;
			} else {
				result = responseEntity.getStatusCode().toString();
			}
			sms_logger.info("SYSID:[{}], submit url:[{}], http status:[{}], request body:[{}], result body:[{}]", sysId,
					submitUrl, responseEntity.getStatusCode(), xml, responseEntity.getBody());
		} catch (HttpClientErrorException e) {
			sms_logger.error("[RUNTIME] submitSms HttpClientError:[{}]", e.getMessage());
		} catch (Exception e) {
			sms_logger.error("[RUNTIME] submitSms Error:[{}]", e.getMessage());
		}
		return result;
	}

	public String submitSms(SMS sms) {
		return submitSms(sms.getSysId(), sms);
	}

	public void moveSmsFile(Path sourceDir, Path targetDir, String fileName) {
		if (fileName.endsWith(smsExtension1)) {
			moveFile(sourceDir, targetDir, fileName);
			moveFile(sourceDir, targetDir, fileName.replace(smsExtension1, smsExtension2));
		} else if (fileName.endsWith(smsExtension2)) {
			moveFile(sourceDir, targetDir, fileName);
			moveFile(sourceDir, targetDir, fileName.replace(smsExtension2, smsExtension1));
		}
	}

	public void moveSmsFile2Inner(String absoluteFilePath, String backupDir) {

		if (StringUtils.isNotBlank(absoluteFilePath) && StringUtils.isNotBlank(backupDir)) {
			Path source = getFilePath(absoluteFilePath);
			String fileName = getFileName(absoluteFilePath);
			Path targetDir = source.resolve(backupDir);

			sms_logger.debug("move to target folder [{}]", targetDir);
			moveSmsFile(source, targetDir, fileName);
		} else {
			sms_logger.debug("absoluteFilePath = [{}], targetDir = [{}]", absoluteFilePath, backupDir);
		}
	}

	public void moveSmsFile(String absoluteFilePath, String backupDir) {

		if (StringUtils.isNotBlank(absoluteFilePath) && StringUtils.isNotBlank(backupDir)) {
			Path source = getFilePath(absoluteFilePath);
			String fileName = getFileName(absoluteFilePath);
			Path targetDir = Paths.get(backupDir);

			sms_logger.debug("move to target folder [{}]", targetDir.toAbsolutePath().toString());
			moveSmsFile(source, targetDir, fileName);
		} else {
			sms_logger.debug("absoluteFilePath = [{}], targetDir = [{}]", absoluteFilePath, backupDir);
		}
	}

	public List<String> checkSpamKeyData(List<String> lines) {
		List<String> list = new ArrayList<String>();
		try {
			for (String key : lines) {
				if (!key.trim().equals("")) {
					keyword_logger.info("Spam keyword:[{}]", key);
					list.add(key);
				}
			}
			keyword_logger.info("Spam keyword File lines:[{}]", lines.size());
			keyword_logger.info("Spam keyword File data count:[{}]", list.size());
		} catch (Exception e) {
			keyword_logger.error("[RUNTIME] Spam keyword File check data failed ,error message:[{}]", e.getMessage());
		}
		return list;
	}

	public void processSpamKeywordFile(List<String> lines) {
		try {
			if (lines != null && lines.size() > 0) {
				keyword_logger.info("delete db data");
				spamKeyWordManager.deleteAll(SpamKeyWord.class);
				spamKeyWordManager.clearCacheSpamKeyWord();
				keyword_logger.info("insert data to db");
				for (String key : lines) {
					SpamKeyWord skw = new SpamKeyWord();
					skw.setKey(key);
					skw.setStatus(SpamKeyWord.ACTIVE);
					// spamKeyWordManager.save(SpamKeyWord.class, skw);
					spamKeyWordManager.addAndSave(skw);
				}
			}
		} catch (Exception e) {
			keyword_logger.error("[RUNTIME] error message:[{}]", e.getMessage());
		}
	}

	public MnpApiPhoneroutinginfo checkMnpFileData(String line, String fileType) {
		MnpApiPhoneroutinginfo info = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			String routingNumber[] = properties.getApi().getFet().getRouting_number().split(",");
			// MnpApiPhoneroutinginfo mnp = new MnpApiPhoneroutinginfo();
			if (line != null && !line.equals("")) {
				String data[] = line.split(",");
				// check phone number is fet routing and prefix is fet
				if (data.length > 0) {
					boolean sw = false;
					String phoneNum = data[0];
					if (fileType.equals("weekly")) {
						if (phoneNum.startsWith("-") || phoneNum.startsWith("+")) {
							phoneNum = phoneNum.substring(1);
						}
					}
					if (phoneNum.startsWith("09") || phoneNum.startsWith("886")) {

						// check routing number
						for (String routing : routingNumber) {
							if (data[1] != null && !data[1].equals("") && data[1].equals(routing)) {
								sw = true;
							}
						}

						// check prefix
						if (!sw) {
							if (fetPrefixManager.checkFetPrefixInCache(phoneNum)) {
								sw = true;
							}
						}

						// create info
						if (sw) {
							info = new MnpApiPhoneroutinginfo();
							info.setPhoneNumber(data[0]);
							info.setSs7Rn(data[1]);
							if (fileType.equals("daily")) {
								info.setOperationType(data[2]);
								info.setModifiedDate(sdf.parse(data[3]));
							} else if (fileType.equals("weekly")) {
								info.setOperationType("");
								info.setModifiedDate(sdf.parse(data[6]));
							}
						} else {
							mnp_logger.debug(
									"phone number prefix and routing number not is fet, phone number:[{}],RN:[{}]",
									data[0], data[1]);
						}
					}
				}
			}

		} catch (Exception e) {
			mnp_logger.error("[RUNTIME] error message:[{}]", e.getMessage());
			mnp_logger.error(e, e);
		}
		return info;
	}

	public void processMnpFileData(Path path, String fileType) {
		mnp_logger.info("==== processMnpFileData start ====");
		List<MnpApiPhoneroutinginfo> list = new ArrayList<MnpApiPhoneroutinginfo>();
		FileInputStream inputStream = null;
		Scanner sc = null;
		int totalCount = properties.getFile().getWatcher().getDifference_count();
		mnp_logger.info("processMnpFileData load file , file:[{}] ,fileType:[{}]", path.toAbsolutePath(), fileType);
		mnp_logger.info("processMnpFileData load file , difference_count:[{}] ", totalCount);
		try {
			inputStream = new FileInputStream(path.toFile());
			sc = new Scanner(inputStream, "UTF-8");
			int count = 0;
			boolean firstLine = true;
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (firstLine) {
					firstLine = false;
					// String UTF8_BOM = "\uFEFF";
					if (line.startsWith(UTF8_BOM)) {
						line = line.substring(1);
					}
				}
				MnpApiPhoneroutinginfo info = checkMnpFileData(line, fileType);
				if (fileType.equals("daily") && info != null) {
					mnp_logger.info("daily file processMnpFileData load file , line:[{}] ", line);
					list.add(info);
				} else if (fileType.equals("weekly") && info != null) {
					if (count % 50000 == 0) {
						mnp_logger.info("weekly file processMnpFileData load file ,count:[{}] line:[{}] ", count, line);
					}
					if (count <= totalCount) {
						// modify by matthew 20191108
						count++;
						list.add(info);
					} else {
						list = new ArrayList<MnpApiPhoneroutinginfo>();
						mnp_logger.info("processMnpFileData weekly file difference too big ,count:[{}] ", count);
						break;
					}
				}
			}

			if (sc.ioException() != null) {
				mnp_logger.error("[RUNTIME] processMnpFileData Scanner ioException:[{}]",
						sc.ioException().getMessage());
				throw sc.ioException();
			}
		} catch (FileNotFoundException e) {
			mnp_logger.error("[RUNTIME] processMnpFileData FileNotFoundException:[{}]", e.getMessage());
		} catch (IOException e) {
			mnp_logger.error("[RUNTIME] processMnpFileData IOException:[{}]", e.getMessage());
		} catch (Exception e) {
			mnp_logger.error("[ERROR] processMnpFileData Exception:[{}]", e.getMessage());
			mnp_logger.error(e, e);
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

		try {
			if (fileType.equals("daily")) {
				mnp_logger.info("**** mnp daily file process start ****");
				mnp_logger.info("mnp daily file process , delete and save , process size:[{}]", list.size());
				for (MnpApiPhoneroutinginfo mnp : list) {
					try {
						MnpApiPhoneroutinginfo temp = null;
						if (mnp.getOperationType().equals("I")) {
							mnp_logger.info("daily file insert , data:[{}]", mnp.toString());
							// check db
							temp = mnpApiPhoneroutinginfoManager.get(MnpApiPhoneroutinginfo.class,
									mnp.getPhoneNumber());
							if (temp == null) {
								mnpApiPhoneroutinginfoManager.addAndSave(mnp);
							} else {
								mnpApiPhoneroutinginfoManager.updateCacheAndDb(mnp);
							}

						} else if (mnp.getOperationType().equals("C") || mnp.getOperationType().equals("U")) {
							mnp_logger.info("daily file update , data:[{}]", mnp.toString());
							temp = mnpApiPhoneroutinginfoManager.get(MnpApiPhoneroutinginfo.class,
									mnp.getPhoneNumber());
							if (temp == null) {
								mnpApiPhoneroutinginfoManager.addAndSave(mnp);
							} else {
								mnpApiPhoneroutinginfoManager.updateCacheAndDb(mnp);
							}
						} else if (mnp.getOperationType().equals("D") || mnp.getOperationType().equals("R")) {
							mnp_logger.info("daily file delete , data:[{}]", mnp.toString());
							mnpApiPhoneroutinginfoManager.deleteCacheAndDb(mnp);
						}
					} catch (Exception e) {
						mnp_logger.error("[RUNTIME] daily process error phone_number:[{}], error message:[{}]",
								mnp.getPhoneNumber(), e.getMessage());
						mnp_logger.error(e, e);
					}
				}
				mnp_logger.info("**** mnp daily file process end ****");
			} else if (fileType.equals("weekly")) {
				mnp_logger.info("**** mnp weekly file process start ****");
				mnp_logger.info("mnp weekly file process , delete and save , process size:[{}]", list.size());
				for (MnpApiPhoneroutinginfo mnp : list) {
					try {

						String phoneNum = mnp.getPhoneNumber();
						if (phoneNum.startsWith("-")) {
							phoneNum = phoneNum.substring(1);
							mnp.setPhoneNumber(phoneNum);
							mnp_logger.info("weekly delete data:[{}]", mnp.toString());
							mnpApiPhoneroutinginfoManager.deleteCacheAndDb(mnp);
						} else {
							phoneNum = phoneNum.substring(1);
							mnp.setPhoneNumber(phoneNum);
							mnp_logger.info("weekly insert data:[{}]", mnp.toString());
							mnpApiPhoneroutinginfoManager.addAndSave(mnp);
						}

					} catch (Exception e) {
						mnp_logger.error("[RUNTIME] weekly process error phone_number:[{}], error message:[{}]",
								mnp.getPhoneNumber(), e.getMessage());
						mnp_logger.error(e, e);
					}
				}
				mnp_logger.info("**** mnp weekly file process end ****");
			}
			mnp_logger.info("==== processMnpFileData end ====");
		} catch (Exception e) {
			mnp_logger.error("error message:[{}]", e.getMessage());
		} finally {
			list.clear();
		}

	}
}
