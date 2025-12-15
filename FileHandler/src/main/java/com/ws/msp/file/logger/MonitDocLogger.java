package com.ws.msp.file.logger;

import java.nio.file.Path;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ws.msp.file.util.WatchObject;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class MonitDocLogger {

    @Autowired
    @Qualifier("watchObject")
    private WatchObject watchObject;

    protected Timer resetPerSecond = new Timer();

    @PostConstruct
    public void startUp() {

        // Do log every 30 sec
        resetPerSecond.schedule(new TimerTask() {
            @Override
            public void run() {

                synchronized (watchObject) {
                    for (Path path : watchObject.getWatchDirs()) {
                        log.debug("[Monitor folders] = {}", path.toAbsolutePath().toString());
                    }
                }
            }
        }, 0, 30000);
    }
}
