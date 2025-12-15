package com.ws.msp.file.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchKey;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component("watchObject")
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class WatchObject {

    @Value("${file.watcher.skip_folders}")
    private List<String> skipDirs;

    @Value("${file.watcher.working_folder}")
    private String workingFolder;

    @Value("${file.watcher.sms_path}")
    private String smsRootPathString;

    @Value("${file.watcher.blacklist_path}")
    private String blackRootPathString;

    @Value("${file.watcher.spam_keyword_path}")
    private String spamPathString;

    @Value("${file.watcher.mnp_path}")
    private String mnpPathString;

    private List<Path> watchDirs = new CopyOnWriteArrayList<>();
    private List<WatchKey> watchKeys = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void initRoot(){ // added watch root

        watchDirs.add(Paths.get(smsRootPathString));
        watchDirs.add(Paths.get(blackRootPathString));
        watchDirs.add(Paths.get(spamPathString));
        watchDirs.add(Paths.get(mnpPathString));
    }
}
