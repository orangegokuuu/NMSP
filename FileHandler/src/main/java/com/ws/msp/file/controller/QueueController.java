package com.ws.msp.file.controller;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ws.msp.file.util.FileQueue;
import com.ws.msp.file.util.FileWatcher;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class QueueController {

    private Queue<FileQueue> queues = new LinkedList<>();

    public synchronized int getMessageCount() {
        return getCount(FileWatcher.SMS_FILE);
    }

    public synchronized int getSpamCount() {
        return getCount(FileWatcher.SPAM_KEYWORD_FILE);
    }

    public synchronized int getBlackListCount() {
        return getCount(FileWatcher.BLACK_LIST_FILE);
    }

    public synchronized int getMnpCount() {
        return getCount(FileWatcher.MNP_FILE);
    }

    public synchronized int getTotalQueueCount() {
        return getCount("");
    }

    private synchronized int getCount(String type) {

        int result = 0;

        try {
            if (StringUtils.isNotBlank(type)) {
                for (FileQueue fq : queues) {
                    if (fq != null && StringUtils.isNotBlank(fq.getFileType()) && (type.equals(fq.getFileType()))) {
                        result += 1;
                    }
                }
            } else { // get total queue count case
                result = queues.size();
                // log.debug("get total queue size [{}]", result);
            }
        } catch (Exception e) {
            log.warn("Something wrong with get queue count, type = [{}]", type);
            log.warn(e, e);
        }

        return result;
    }

    /**
     * @return first data in message queue and remove it from queue list
     */
    public synchronized FileQueue getMessageQueue() {
        return queues.poll();
    }

    /**
     * added data if it isn't exist in message queue list
     * 
     * @param
     */
    public synchronized boolean putQueue(FileQueue fileQueue) throws IllegalStateException {

        boolean result = false;
        String filePath = fileQueue.getFilePath() + fileQueue.getFileName();

        log.debug("Input File [{}]", filePath);

        if (queues.contains(fileQueue)) {
            result = true;
        } else {
            result = queues.add(fileQueue);
        }

        log.debug("Now total Queue count : [{}]", getTotalQueueCount());

        return result;
    }
}
