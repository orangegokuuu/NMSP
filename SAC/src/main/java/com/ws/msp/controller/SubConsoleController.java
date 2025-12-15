package com.ws.msp.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.ws.logging.LogAction;
import com.ws.logging.LogEvent;
import com.ws.mc.ConsoleActions;
import com.ws.mc.ConsoleEvents;
import com.ws.mc.controller.AbstractRestController;
import com.ws.mc.interceptor.annotation.Permission;
import com.ws.mc.interceptor.annotation.PrivilegeLevel;
import com.ws.mc.pojo.AuthenticateException;
import com.ws.mc.pojo.RestResult;
import com.ws.msp.pojo.SubConsoleUser;
import com.ws.msp.service.SubConsoleUserManager;
import com.ws.pojo.PaginationResult;
import com.ws.pojo.SearchablePaging;
import com.ws.util.CryptUtil;
import com.ws.util.StringUtil;

@RestController
@RequestMapping("/sms/subconsole")
public class SubConsoleController extends AbstractRestController<SubConsoleUser, String> {

	@Autowired
	private SubConsoleUserManager subConsoleUserManager = null;

	@Autowired
	public SubConsoleController(SubConsoleUserManager subConsoleUserManager) {
		super(subConsoleUserManager);
		// this.setSuccessView("/success");
	}

	@RequestMapping(value = "/page", method = RequestMethod.POST)
	@Permission(id = "SMS_SERVICE_07", level = PrivilegeLevel.READ)
	public @ResponseBody PaginationResult<SubConsoleUser> page(@RequestBody SearchablePaging page) {
		return super.page(page);
	}

	@Override
	public void requiredPrivilege() {
		super.setPrivilege("SMS_RECORD_07");
	}

	//subconsole user login
	@RequestMapping(value = "/login/{username}/{password}/", method = RequestMethod.GET)
	@Permission(id = "SMS_SERVICE_07", level = PrivilegeLevel.READ)
	public @ResponseBody RestResult<Boolean> loginSubUser(@PathVariable String username, @PathVariable String password) throws IOException{
		RestResult<Boolean> result = new RestResult<Boolean>();
		
		/* 20180214 Added by YC
		 * Set password to binary String due to "/" may cause httpAPI error
		 */
		password = revertBinaryString(password);
		
		String paintPwd = CryptUtil.decrypt(password);
		
		try {
			boolean loginResult = subConsoleUserManager.loginUser(username, paintPwd);
			result.setData(new Boolean(loginResult));
			result.setSuccess(true);
		} catch (AuthenticateException e) {
			result.setCode(e.getErrorCode());
			result.setMessage(e.getMessage());
			result.setSuccess(false);
		}
		return result;
	}

	@RequestMapping(method = RequestMethod.POST)
	@LogAction(type = ConsoleActions.ADD_USER, message = "Add Sub SAC user[{1.userId}]")
	@LogEvent(type = ConsoleEvents.ADD_USER, message = "Add Sub SAC user[{1.userId}]", id1 = "{1.userId}")
	public @ResponseBody SubConsoleUser create(@RequestBody SubConsoleUser user) {
		user.setCreateBy(userSession.getUserId());
		user.setCreateDate(LocalDateTime.now());
		user.setPassword(CryptUtil.encrypt(user.getPassword()));
		return super.create(user);
	}

	@RequestMapping(value = "/get/{id}/", method = RequestMethod.GET)
	public @ResponseBody RestResult<SubConsoleUser> getSubUser(@PathVariable String id) {
		RestResult<SubConsoleUser> result = new RestResult<SubConsoleUser>();
		SubConsoleUser subConsoleUser = subConsoleUserManager.get(SubConsoleUser.class, id);
		result.setData(subConsoleUser);
		result.setSuccess(true);
		// return subConsoleUser;
		return result;
	}

	@RequestMapping(value = "/{id}/", method = RequestMethod.PUT)
	@LogAction(type = ConsoleActions.UPD_USER, message = "Update Sub SAC user[{1}]")
	@LogEvent(type = ConsoleEvents.UPD_USER, message = "Update Sub SAC user[{1}]", id1 = "{1}")
	public @ResponseBody SubConsoleUser update(@PathVariable String id, @RequestBody SubConsoleUser user) {
		SubConsoleUser exists = subConsoleUserManager.getUser(id);
		if (exists == null) {
			throw new EntityNotFoundException(String.format("ConsoleUser[%s] not found", id));
		}
		if (StringUtil.isEmpty(user.getPassword())) {
			SubConsoleUser exits = subConsoleUserManager.getUser(id);
			user.setPassword(exits.getPassword());
		} else {
			user.setPassword(CryptUtil.encrypt(user.getPassword()));
		}
		user.setUpdateBy(userSession.getUserId());
		user.setUpdateDate(LocalDateTime.now());
		return super.update(id, user);
	}

	@RequestMapping(value = "/{id}/", method = RequestMethod.DELETE)
	@LogAction(type = ConsoleActions.DEL_USER, message = "Delete Sub SAC user[{1}]")
	@LogEvent(type = ConsoleEvents.DEL_USER, message = "Delte Sub SAC user[{1}]", id1 = "{1}")
	public @ResponseBody SubConsoleUser delete(@PathVariable String id) {
		return super.delete(id);
	}

	public String revertBinaryString(String response) {
		
		String[] byteValues = response.substring(1, response.length() - 1).split(",");
		byte[] bytes = new byte[byteValues.length];

		for (int i=0, len=bytes.length; i<len; i++) {
		   bytes[i] = Byte.parseByte(byteValues[i].trim());     
		}

		String str = new String(bytes);
		return str;
	}
}
