package com.ws.msp.mq.sac.handler;

import java.io.IOException;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.exec.ExecuteException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.ws.msp.mq.sac.pojo.AuthenticateException;
import com.ws.msp.mq.sac.pojo.RestResult;


@ControllerAdvice
public class MCExceptionHandler {
	private static Logger logger = LogManager.getLogger(MCExceptionHandler.class);

	@Value("${mqsac.error.page.401}")
	private String errorPage401 = null;

	@ExceptionHandler({ AuthenticateException.class })
	@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
	public ModelAndView handleUnauthorize(AuthenticateException ex) throws IOException {
		logger.debug("Handling AuthenticateException reason[{}] redirect to page[{}]", ex.getMessage(), errorPage401);

		ModelAndView view = new ModelAndView(errorPage401);
		view.addObject("detail", new ErrorDetail(HttpStatus.UNAUTHORIZED, ex.getMessage(), ex));

		return view;
	}

//	@ExceptionHandler({ AuthenticateException.class })
//	@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
//	public @ResponseBody RestResult<AuthenticateException> handleUnauthorize(AuthenticateException e) {
//		logger.debug("Fail to access resource due to[{}]", e.getMessage());
//		RestResult<AuthenticateException> result = new RestResult<AuthenticateException>();
//		result.setCode(HttpStatus.UNAUTHORIZED.value());
//		result.setSuccess(false);
//		result.setMessage(e.getMessage());
//
//		AuthenticateException clone = new AuthenticateException();
//		BeanUtils.copyProperties(e, clone);
//
//		result.setData(clone);
//		return result;
//	}

	@ExceptionHandler({ DataIntegrityViolationException.class })
	@ResponseStatus(value = HttpStatus.CONFLICT)
	public @ResponseBody RestResult<DataIntegrityViolationException> handleDataIntegrityViolation(
	        DataIntegrityViolationException e, HttpServletRequest req, HttpServletResponse res) {
		logger.debug("Fail to execute updatet due to[{}]", e.getMessage(), e);
		RestResult<DataIntegrityViolationException> result = new RestResult<DataIntegrityViolationException>();
		if (req.getMethod().equals("DELETE")) {
			result.setCode(HttpStatus.NOT_ACCEPTABLE.value());
		} else {
			result.setCode(HttpStatus.CONFLICT.value());
		}
		result.setSuccess(false);
		result.setMessage(e.getMessage());

		result.setData(e);
		return result;
	}

	@ExceptionHandler({ EntityNotFoundException.class })
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public @ResponseBody RestResult<EntityNotFoundException> handleNotFound(EntityNotFoundException e) {
		logger.debug("Fail to get Object due to[{}]", e.getMessage(), e);
		RestResult<EntityNotFoundException> result = new RestResult<EntityNotFoundException>();
		result.setCode(HttpStatus.NOT_FOUND.value());
		result.setSuccess(false);
		result.setMessage(e.getMessage());
		result.setData(e);
		return result;
	}
	
	@ExceptionHandler({ IOException.class })
	@ResponseStatus(value = HttpStatus.CONFLICT)
	public @ResponseBody RestResult<IOException> handleIO(IOException e) {
		logger.debug("Fail to get Object due to[{}]", e.getMessage(), e);
		RestResult<IOException> result = new RestResult<IOException>();
		result.setCode(HttpStatus.CONFLICT.value());
		result.setSuccess(false);
		result.setMessage(e.getMessage());
		result.setData(e);
		return result;
	}
	
	@ExceptionHandler({ ExecuteException.class })
	@ResponseStatus(value = HttpStatus.CONFLICT)
	public @ResponseBody RestResult<ExecuteException> handleExecute(ExecuteException e) {
		logger.debug("Fail to excute Object due to[{}]", e.getMessage(), e);
		RestResult<ExecuteException> result = new RestResult<ExecuteException>();
		result.setCode(HttpStatus.CONFLICT.value());
		result.setSuccess(false);
		result.setMessage(e.getMessage());
		result.setData(e);
		return result;
	}
	
	@ExceptionHandler({ Exception.class })
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody RestResult<Exception> handleError(Exception e) {
		logger.debug("Unknown Failure", e);

		RestResult<Exception> result = new RestResult<Exception>();
		result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		result.setSuccess(false);
		result.setMessage(e.getMessage());

		// In case Exception is generated from proxy
		Exception clone = new Exception();
		BeanUtils.copyProperties(e, clone);
		result.setData(clone);
		return result;
	}

}
