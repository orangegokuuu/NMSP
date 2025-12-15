package com.ws.msp.file.util;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileQueue {

    @Autowired
    FileUtil fileUtil;

    private String fileName;
    private String filePathString;
    private Path filePath;
    private String fileType;
    private String absoluteFilePath;
    private boolean isMNPDaily;

    public void setAbsoluteFilePath(String absoluteFilePath) {
        this.absoluteFilePath = absoluteFilePath;
        fileName = fileUtil.getFileName(absoluteFilePath);
        filePath = fileUtil.getFilePath(absoluteFilePath);
        if (filePath != null) {
            filePathString = filePath.toAbsolutePath().toString();
        }
    }
}
