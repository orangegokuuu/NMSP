package com.ws.msp.file.thread;

import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ws.httpapi.pojo.SMS;
import com.ws.msp.file.util.FileQueue;
import com.ws.msp.file.util.FileUtil;
import com.ws.msp.file.util.FileWatcher;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Log4j2
public class FileWorker implements Runnable {

    @Setter
    private FileQueue fileQueue = null;

    @Autowired
    FileUtil fileUtil;

    String fileAbsolutePath = "";
    String fileWorkingPath = "";
    String fileName = "";
    String queueType = "";
    String errorPath = "";
    String backupPathString = "";

    public boolean submitSMS4FTP(String fileAbsolutePath) throws Exception {

        boolean result = false;

        if (StringUtils.isBlank(fileAbsolutePath)) {
            return result;
        }

        // process file
        SMS sms = fileUtil.processSMSFile(fileAbsolutePath);
        if (sms != null) {
            log.info("==== SMS File [{}] Start ====", fileAbsolutePath);
            // sms submit
            String httpStatus = fileUtil.submitSms(sms);
            if (httpStatus.equals(FileUtil.SUBMIT_API_SUCCESS)) {
                result = true;
            } else {
                log.info("[{}] may have problem that sending fail, http status [{}]", fileAbsolutePath, httpStatus);
            }

            try {
                if (fileQueue.getFilePath() != null) {
                    if (result) {
                        // move sms file to history
                        backupPathString = fileQueue.getFilePath().resolve(fileUtil.getBackupPath()).toAbsolutePath()
                                .toString();

                    } else {
                        // move sms file to error
                        backupPathString = fileQueue.getFilePath().resolve(fileUtil.getBackupPath())
                                .resolve(fileUtil.getErrorPath()).toAbsolutePath()
                                .toString();
                    }
                    fileUtil.moveSmsFile(fileAbsolutePath, backupPathString);
                }

            } catch (Exception e) {
                log.warn("move [{}] to history folder fail, please check", fileAbsolutePath);
            }
            log.info("==== SMS File [{}] end ====", fileAbsolutePath);
        } else { // file content might have problem, move to error path
            backupPathString = fileQueue.getFilePath().resolve(fileUtil.getBackupPath())
                    .resolve(fileUtil.getErrorPath()).toAbsolutePath()
                    .toString();

            fileUtil.moveSmsFile(fileAbsolutePath, backupPathString);
        }
        return result;
    }

    public void doBlackListFile(String fileAbsolutePath) throws Exception {

        if (StringUtils.isNotBlank(fileAbsolutePath)) {
            log.info("==== Black List File [{}] start ====", fileAbsolutePath);
            // process black list file
            fileUtil.processBlackListFile(Paths.get(fileAbsolutePath));

            // move black list file to history
            String historyPathString = fileQueue.getFilePath().resolve(fileUtil.getBackupPath()).toAbsolutePath()
                    .toString();
            fileUtil.moveFile(historyPathString, Paths.get(fileAbsolutePath).toFile());
            log.info("==== Black List File [{}] process end ====", fileAbsolutePath);
        }
    }

    public void doSpamFile(String fileAbsolutePath) throws Exception {

        if (StringUtils.isNotBlank(fileAbsolutePath)) {
            log.info("==== SPAM File [{}] start ====", fileAbsolutePath);
            // process spam file
            List<String> lines = fileUtil.checkSpamKeyData(fileUtil.readfile(Paths.get(fileAbsolutePath)));
            fileUtil.processSpamKeywordFile(lines);

            // move spam file to history
            String historyPathString = fileQueue.getFilePath().resolve(fileUtil.getBackupPath()).toAbsolutePath()
                    .toString();
            fileUtil.moveFile(historyPathString, Paths.get(fileAbsolutePath).toFile());
            log.info("==== SPAM File [{}] process end ====", fileAbsolutePath);
        }
    }

    public void doMnpFile(String fileAbsolutePath) throws Exception {

        if (StringUtils.isNotBlank(fileAbsolutePath)) {
            log.info("==== MNP File [{}] start ====", fileAbsolutePath);

            String folderType;

            if (fileQueue.isMNPDaily()) {
                folderType = "daily";
            } else {
                folderType = "weekly";
            }

            // process mnp file
            fileUtil.processMnpFileData(Paths.get(fileAbsolutePath), folderType);

            // move mnp file to history
            String historyPathString = fileQueue.getFilePath().resolve(fileUtil.getBackupPath()).toAbsolutePath()
                    .toString();
            fileUtil.moveFile(historyPathString, Paths.get(fileAbsolutePath).toFile());
            log.info("==== MNP File [{}] end ====", fileAbsolutePath);
        }
    }

    @Override
    public void run() {
        if (fileQueue != null && StringUtils.isNotBlank(fileQueue.getAbsoluteFilePath())
                && StringUtils.isNotBlank(fileQueue.getFileType())) {

            log.debug(" ######### Worker start ######### [{}], type [{}]", fileQueue.getAbsoluteFilePath(),
                    fileQueue.getFileType());
            fileAbsolutePath = fileQueue.getAbsoluteFilePath();
            fileWorkingPath = fileUtil.getWorkingPath();
            fileName = fileQueue.getFileName();
            queueType = fileQueue.getFileType();
            // error path in original upload folder > history > error
            errorPath = fileQueue.getFilePath().resolve(fileUtil.getBackupPath()).resolve(fileUtil.getErrorPath())
                    .toAbsolutePath().toString();

            // real file exist in working folder
            String realWorkingFileAbsolutePath = fileWorkingPath + fileName;

            try {
                if (queueType.equals(FileWatcher.SMS_FILE)) {
                    submitSMS4FTP(realWorkingFileAbsolutePath);
                } else if (queueType.equals(FileWatcher.BLACK_LIST_FILE)) {
                    doBlackListFile(realWorkingFileAbsolutePath);
                } else if (queueType.equals(FileWatcher.SPAM_KEYWORD_FILE)) {
                    doSpamFile(realWorkingFileAbsolutePath);
                } else if (queueType.equals(FileWatcher.MNP_FILE)) {
                    doMnpFile(realWorkingFileAbsolutePath);
                }
            } catch (NullPointerException ne) {
                // ignore
            } catch (Exception e) {
                log.info("[File Worker] [{}] may have problem, cause [{}], move to error folder",
                        fileAbsolutePath, e.getMessage());
                log.error(e, e);

                // move file to history folder
                if (queueType.equals(FileWatcher.SMS_FILE)) {
                    fileUtil.moveSmsFile(realWorkingFileAbsolutePath, errorPath);
                } else {
                    fileUtil.moveFile(errorPath, Paths.get(realWorkingFileAbsolutePath).toFile());
                }

            }
        }
    }
}
