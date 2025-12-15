/**
 * 
 */
package com.ws.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ws.msp.config.MspProperties;
import com.ws.pojo.GenericBean;

import lombok.extern.log4j.Log4j2;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Log4j2
public class CommonFileUtil extends GenericBean {

	public static final String UTF16BE_BOM = "\uFEFF";
	public static final String UTF8_BOM = "\uEFBB";
	public static final String UTF16LE_BOM = "\uFFFE";

	@Autowired
	private MspProperties properties;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2011955171977498831L;

	public List<String> readfile(File file) {

		List<String> fileContent = new ArrayList<>();
		BufferedReader br = null;
		boolean firstLine = true;
		String logResult = "";
		String originalResult = "";

		if (file.exists() && file.canRead()) {
			try {
				br = new BufferedReader(new FileReader(file));
				String line = null;
				while ((line = br.readLine()) != null) {

					if (firstLine) { // remove file BOM
						firstLine = false;
						log.debug("header line: [{}]", line);
						if (line.startsWith(UTF8_BOM)) {
							line = line.replaceFirst(UTF8_BOM, "");
						} else if (line.startsWith(UTF16BE_BOM)) {
							line = line.replaceFirst(UTF16BE_BOM, "");
						} else if (line.startsWith(UTF16LE_BOM)) {
							line = line.replaceFirst(UTF16LE_BOM, "");
						}
					}
					// read file content
					if (StringUtils.isNotBlank(line)) { // remove empty line
						fileContent.add(line);
						logResult += line + "\n";
					}
					originalResult += line + "\n";
				}

				log.debug("[{}] content:[{}]", file.getAbsolutePath(), logResult);
				log.debug("[{}] original content:[{}]", file.getAbsolutePath(), originalResult);
			} catch (Exception e) {
				log.warn("Read file [{}] fail", file.getAbsoluteFile(), e.getMessage());
				log.warn(e, e);
				return null;
			}
		} else {
			log.debug("File [{}] is empty or can not read", file.getAbsoluteFile());
		}

		return fileContent;
	}

	public List<String> readfile(Path path) throws NullPointerException {
		if (path != null) {
			return readfile(path.toFile());
		}
		return null;
	}

	/**
	 * return all content into one string
	 * 
	 * @param file
	 * @return
	 */
	public String readFile2String(File file) {

		String result = null;
		StringBuilder sb = new StringBuilder();

		for (String line : readfile(file)) {
			if (StringUtils.isNotBlank(line)) { // remove empty lines
				sb.append(line);
				sb.append(System.lineSeparator());
			}
		}

		result = sb.toString();
		return result;
	}

	/**
	 * Move file to ${targetPath}
	 * 
	 * @param targetPath
	 * @param file
	 * @return
	 */
	public boolean moveFile(String path, File file) {

		boolean isSuccess = false;

		if (StringUtils.isNotBlank(path) && file != null) {

			try {
				File pFile = new File(path);
				if (pFile.exists() && pFile.isDirectory()) {
					if (file.renameTo(Paths.get(path).resolve(file.getName()).toFile())) {
						log.debug("move file[{}] to [{}] successful!", file, path);
						isSuccess = true;
					} else {
						log.debug("move file[{}] to [{}] failed", file.getName(), path);
						isSuccess = false;
					}
				} else {
					log.warn("[{}] does not exist or a directiry, ", path);
				}
			} catch (Exception e) {
				log.error("move file occur exception cause [{}]", e.getMessage());
			}
		}
		return isSuccess;
	}

	public boolean checkExtension(File file, String fileExtension) {

		boolean go = false;

		if (!file.isHidden() && file.isFile()) {
			int dotPos = file.getName().lastIndexOf(".");
			String extension = file.getName().substring(dotPos + 1, file.getName().length());
			if (extension.toLowerCase().equals(fileExtension)) {
				go = true;
			}
		} else {
			log.debug("[{}] is not a file or hidden", file.getAbsolutePath());
		}
		return go;
	}

	public boolean checkExtension(String absolutePath, String fileExtension) {

		boolean go = false;

		File file = new File(absolutePath);
		if (file.exists()) {
			go = checkExtension(file, fileExtension);
		}
		return go;
	}

	public boolean removefile(String fileName, String Path) throws Exception {

		boolean success = true;

		File file = new File(resolvePath(Path, fileName));

		if (file.exists()) {
			success = file.delete();
		}

		log.debug("[REMOVE FILE] [{}]", Path + fileName);

		return success;
	}

	/**
	 * Create a file
	 * 
	 * @param fileName
	 *                 : file name
	 * @param filePath
	 *                 : file path
	 * @param param
	 *                 : oa / da / startTime...
	 * @return : create file success or not
	 * @throws Exception
	 */
	public boolean createFile(String fileName, String filePath, String... param) {

		boolean result = false;

		try {

			if (!filePath.endsWith(File.separator)) {
				filePath = filePath + "File.separator";
			}

			if (fileName.startsWith(File.separator)) {
				fileName = fileName.substring(1);
			}

			File tempFile = new File(filePath + fileName);
			FileWriter fr = new FileWriter(tempFile);
			BufferedWriter bw = new BufferedWriter(fr);
			for (String s : param) {
				bw.write(s + "\r\n");
			}
			bw.flush();

			bw.close();
			fr.close();

			result = true;
		} catch (Exception e) {
			log.error("Create file failed due to {}", e.getMessage());
		}

		return result;
	}

	/**
	 * if file exist, return (String)fileName = [0] and (Path)filePath = [1]
	 * 
	 * @param fileAbsolutePath
	 * @return filename as String and filePath as Path
	 * @throws NullPointerException when file not correct or exist
	 */
	private List getFileInfo(String fileAbsolutePath) throws NullPointerException {

		List result = null;
		Path filePath = Paths.get(fileAbsolutePath);
		if (Files.exists(filePath)) {
			String parsedFileName = filePath.getFileName().toString();
			result = new ArrayList();
			result.add(parsedFileName);
			result.add(filePath.getParent());
		}
		return result;
	}

	public String getFileName(String fileAbsolutePath) {

		String result = "";
		if (StringUtils.isNotBlank(fileAbsolutePath)) {
			try {
				result = (String) getFileInfo(fileAbsolutePath).get(0);
			} catch (Exception e) {
				// do nothing
			}
		}
		return result;
	}

	public Path getFilePath(String fileAbsolutePath) {

		Path result = null;
		if (StringUtils.isNotBlank(fileAbsolutePath)) {
			try {
				result = (Path) getFileInfo(fileAbsolutePath).get(1);
			} catch (Exception e) {
				// do nothing
			}
		}
		return result;
	}

	private String resolvePath(String path, String fileName) {

		String result = "";
		if (StringUtils.isNotBlank(path)) {
			File file = new File(path);
			if (file.exists()) {
				if (file.isFile() && fileName.contains(file.getName())) {
					result = file.getAbsolutePath();
				} else {
					file = new File(path, fileName);
					result = file.getAbsolutePath();
				}
			}
		}
		return result;
	}

	/**
	 * resolve msisdn from muilticase prefix to 886
	 * 
	 * @param mdn prefix with 09 / +886 / +0 / + / 886
	 * @return prefix 886
	 *         e.g. 886987654321
	 */
	public String resolveMsisdn(String mdn) {

		String result = "";
		if (mdn != null && mdn.trim().length() == 10 && mdn.trim().startsWith("09")) {
			result = "886" + mdn.substring(1, mdn.length());
		} else if (mdn != null && mdn.trim().length() == 13 && mdn.trim().startsWith("+886")) {
			result = mdn.substring(1, mdn.trim().length());
		} else if (mdn != null && mdn.trim().startsWith("0")) {
			result = "886" + mdn.substring(1, mdn.length());
		} else if (mdn != null && mdn.trim().startsWith("+0")) {
			result = "886" + mdn.substring(2, mdn.trim().length());
		} else if (mdn != null && mdn.trim().startsWith("+")) {
			result = mdn.substring(1, mdn.trim().length());
		} else {
			result = mdn;
		}
		return result;
	}

	public void moveFile(Path sourceDir, Path targetDir, String fileName) {

		if (sourceDir != null && targetDir != null && StringUtils.isNotBlank(fileName)) {
			Path file = sourceDir.resolve(fileName);
			try {
				createDirectory(targetDir);
				file = sourceDir.resolve(fileName);
				if (file.toFile().exists() && !Files.isHidden(file)) {
					Files.copy(file, Paths.get(file.toString().replace(sourceDir.toString(), targetDir.toString())),
							StandardCopyOption.REPLACE_EXISTING);
					log.info("==== copy file from [{}] to [{}], fileName : [{}]",
							sourceDir.toAbsolutePath(), targetDir.toAbsolutePath(), fileName);

					deleteFile(file);
				} else {
					log.warn("==== moveFile, file is not exists or hidden, input sourceDir [{}], file name [{}]",
							sourceDir.toAbsolutePath(), fileName);
				}

			} catch (FileAlreadyExistsException e) {
				// multi thread trigger then ignore
			} catch (Exception e) {
				log.error("==== moveFile => copy file [{}] failed", file.toAbsolutePath());
				log.error(e, e);
			}
		}
	}

	public void createDirectory(Path path) {

		try {
			if (path != null && !path.toFile().exists()) {
				Files.createDirectory(path);
				log.info("create dir [{}] success", path.toAbsolutePath().toString());
			}
		} catch (NoSuchFileException e) {
			try {
				if (path.getParent() != null && !path.getParent().toFile().exists()) {
					Files.createDirectory(path.getParent());
					log.info("create dir [{}] success", path.getParent().toAbsolutePath().toString());
				}
			} catch (Exception ee) {
				log.error("create parent [{}] folder fail,", path.getParent().toAbsolutePath().toString());
				log.error(ee, ee);
			}
		} catch (Exception e) {

		}
	}

	public void deleteFile(Path file) {
		try {
			Files.delete(file);
			log.debug("delete file [{}] success", file.toAbsolutePath());
		} catch (Exception e) {
			log.error("delete file [{}] filed", file.toAbsolutePath());
			log.error(e, e);
		}
	}

	private int getMaxChineseLength() {
		return properties.getApi().getContent().getChinese().getMaxLength();
	}

	private int getMaxEnglishLength() {
		return properties.getApi().getContent().getEnglish().getMaxLength();
	}

	public boolean checkContentLength(String language, int contentLength) {

		boolean result = false;

		if (StringUtils.isBlank(language)) {
			log.warn("Get Language fail");
			return result;
		}

		if (language.equals("C") || language.equals("B") || language.equals("U")) {
			if (contentLength <= getMaxChineseLength()) {
				result = true;
			} else {
				log.warn("Content size [{}] with language [{}] too long ", contentLength, language);
			}
		} else if (language.equals("E")) {
			if (contentLength <= getMaxEnglishLength()) {
				result = true;
			} else {
				log.warn("Content size [{}] with language [{}] too long ", contentLength, language);
			}
		} else {
			log.warn("Please consider file Language [{}] is correct or not", language);
		}

		return result;
	}

	/**
	 * remove white space from String
	 * 
	 * @param content
	 * @return
	 */
	public String removeNoneVisible(String content) {

		if (StringUtils.isNotBlank(content)) {
			content = content.replaceAll("\\s+", "");
		}
		return content;
	}

	/**
	 * remove Linefeed in the end of string
	 * @param text
	 * @return
	 */
	public static String removeLinefeedInTheEnd(String text) {

		String result = "";

		log.debug("[Before] Remove Linefeed : [{}]", text);

		if (StringUtils.isNotBlank(text)) {
			result = text.replaceAll("[\n\r]+$", "");
		}

		log.debug("[After] Remove Linefeed : [{}]", result);

		return result;
	}
}
