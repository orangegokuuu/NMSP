package com.ws.msp.file.controller;

import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
public class ApiController {

    // @Autowired
    // @Qualifier("watcherKeys")
    // private Map<WatchKey, Path> totalWatchKeys;

    // // @RequestMapping(value = "/file/monitDoc", method = { RequestMethod.GET, RequestMethod.POST })
    // public String getMonitDoc() {

    //     String result = "";
        
    //     result += "<ul>";
    //     for (Map.Entry<WatchKey, Path> entry : totalWatchKeys.entrySet()) {
    //         log.debug("[Monitor folders] = {}", entry.getValue());
    //         result += "<li>"+entry.getValue()+"</li>";
    //     }
    //     result += "</ul>";

    //     return result;
    // }

}
