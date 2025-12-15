package com.ws.msp.mq.sac.dao;

import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ws.msp.mq.sac.pojo.RestResult;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class SystemManager {

	@Value("${mqsac.cmd.entry}")
	private String entryPath = null;

	@Value("${mqsac.cmd.q.create}")
	private String createQPath = null;

	@Value("${mqsac.cmd.q.delete}")
	private String deleteQPath = null;

	@Value("${mqsac.cmd.user.create}")
	private String createUserPath = null;

	@Value("${mqsac.cmd.user.delete}")
	private String deleteUserPath = null;

	public RestResult<String> createOsUser(String qManager, String cpId) throws ExecuteException, IOException {
		RestResult<String> result = new RestResult<String>();
		int exitValue = -1;
		CommandLine cmd = CommandLine.parse("sh -x " + entryPath);
		cmd.addArgument(createUserPath);
		cmd.addArgument(qManager);
		cmd.addArgument(cpId);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
		DefaultExecutor exec = new DefaultExecutor();
		PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);
		exec.setStreamHandler(streamHandler);
		log.info("Execute command: {}", cmd.toString());

		try {
			exitValue = exec.execute(cmd);
		} catch (ExecuteException e) {
			log.debug("Catch ExecuteException: {}", errorStream.toString());
			throw e;
		} catch (IOException e) {
			log.debug("Catch IOException: {}", errorStream.toString());
			throw e;
		}
		log.info("Result = {}", outputStream.toString());
		if (exitValue == 0) {
			result.setSuccess(true);
			result.setData(outputStream.toString());
			result.setCode(HttpStatus.OK.value());
		} else {
			result.setSuccess(false);
			result.setData(errorStream.toString());
			result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return result;
	}

	public RestResult<String> deleteOsUser(String qManager, String cpId) throws ExecuteException, IOException {
		RestResult<String> result = new RestResult<String>();
		int exitValue = -1;
		CommandLine cmd = CommandLine.parse("sh -x " + entryPath);
		cmd.addArgument("sh");
		cmd.addArgument(deleteUserPath);
		cmd.addArgument(qManager);
		cmd.addArgument(cpId);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
		DefaultExecutor exec = new DefaultExecutor();
		PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);
		exec.setStreamHandler(streamHandler);

		log.info("Execute command: {}", cmd.toString());
		try {
			exitValue = exec.execute(cmd);
		} catch (ExecuteException e) {
			log.debug("Catch ExecuteException: {}", errorStream.toString());
			throw e;
		} catch (IOException e) {
			log.debug("Catch IOException: {}", errorStream.toString());
			throw e;
		}
		log.info("Result = {}", outputStream.toString());
		if (exitValue == 0) {
			result.setSuccess(true);
			result.setData(outputStream.toString());
			result.setCode(HttpStatus.OK.value());
		} else {
			result.setSuccess(false);
			result.setData(errorStream.toString());
			result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return result;
	}

	public RestResult<String> createQueue(String qManager, String cpId) throws ExecuteException, IOException {
		RestResult<String> result = new RestResult<String>();
		int exitValue = -1;
		CommandLine cmd = CommandLine.parse("sh -x " + entryPath);
		cmd.addArgument(createQPath);
		cmd.addArgument(qManager);
		cmd.addArgument(cpId);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
		DefaultExecutor exec = new DefaultExecutor();
		PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);
		exec.setStreamHandler(streamHandler);

		log.info("Execute command: {}", cmd.toString());
		try {
			exitValue = exec.execute(cmd);
		} catch (ExecuteException e) {
			log.debug("Catch ExecuteException: {}", errorStream.toString());
			throw e;
		} catch (IOException e) {
			log.debug("Catch IOException: {}", errorStream.toString());
			throw e;
		}
		log.info("Result = {}", outputStream.toString());
		if (exitValue == 0) {
			result.setSuccess(true);
			result.setData(outputStream.toString());
			result.setCode(HttpStatus.OK.value());
		} else {
			result.setSuccess(false);
			result.setData(errorStream.toString());
			result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return result;
	}

	public RestResult<String> deleteQueue(String qManager, String cpId) throws ExecuteException, IOException {
		RestResult<String> result = new RestResult<String>();
		int exitValue = -1;
		CommandLine cmd = CommandLine.parse("sh -x " + entryPath);
		cmd.addArgument("sh");
		cmd.addArgument(deleteQPath);
		cmd.addArgument(qManager);
		cmd.addArgument(cpId);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
		DefaultExecutor exec = new DefaultExecutor();
		PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);
		exec.setStreamHandler(streamHandler);

		log.info("Execute command: {}", cmd.toString());
		try {
			exitValue = exec.execute(cmd);
		} catch (ExecuteException e) {
			log.debug("Catch ExecuteException: {}", errorStream.toString());
			throw e;
		} catch (IOException e) {
			log.debug("Catch IOException: {}", errorStream.toString());
			throw e;
		}
		log.info("Result = {}", outputStream.toString());
		if (exitValue == 0) {
			result.setSuccess(true);
			result.setData(outputStream.toString());
			result.setCode(HttpStatus.OK.value());
		} else {
			result.setSuccess(false);
			result.setData(errorStream.toString());
			result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return result;
	}

}
