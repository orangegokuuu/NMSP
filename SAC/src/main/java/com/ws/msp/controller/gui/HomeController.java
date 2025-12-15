package com.ws.msp.controller.gui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.LocaleResolver;

import com.ws.logging.LogAction;
import com.ws.mc.ConsoleActions;
import com.ws.mc.config.MCProperties;
import com.ws.mc.controller.AbstractAnnotateController;
import com.ws.mc.interceptor.annotation.Permission;
import com.ws.mc.legacy.LoginCommand;
import com.ws.mc.pojo.AuthenticateException;
import com.ws.mc.pojo.ConsolePrivilege;
import com.ws.mc.pojo.ConsolePrivilegeLabel;
import com.ws.mc.pojo.ConsolePrivilegeType;
import com.ws.mc.pojo.ConsolePrivilegeTypeLabel;
import com.ws.mc.pojo.ConsolePrivilegeTypeLabelKey;
import com.ws.mc.pojo.ConsoleRight;
import com.ws.mc.pojo.ConsoleUser;
import com.ws.mc.pojo.ErrorCode;
import com.ws.mc.pojo.MenuItem;
import com.ws.mc.pojo.PwdHistory;
import com.ws.mc.pojo.RestResult;
import com.ws.mc.service.ConsoleUserManager;
import com.ws.mc.util.PrivilegeUtil;
import com.ws.util.CryptUtil;

import lombok.extern.log4j.Log4j2;

@Controller("mspHomeController")
@Log4j2
public class HomeController extends AbstractAnnotateController {
	@Autowired
	private ConsoleUserManager consoleUserManager = null;

	@Autowired
	private MCProperties mc = null;

	@Permission
	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String homepage(Model model) {
		model.addAttribute("menuItems", getMenuItems());

		return "home";
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logoutPage(Model model) {
		return "logout";
	}

	@Autowired
	private LocaleResolver localeResolver = null;

	public List<MenuItem> getMenuItems() {
		List<ConsoleRight> userRights = userSession.getRights().entrySet().stream().map(x -> x.getValue())
				.collect(Collectors.toList());

		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		for (ConsolePrivilegeType privilegeType : consoleUserManager.getPrivilegeTypes(true)) {
			MenuItem menuItem = new MenuItem();
			Locale locale = LocaleContextHolder.getLocale();
			List<MenuItem> children = new ArrayList<MenuItem>();

			Map<String, ConsolePrivilegeTypeLabel> typeLabels = privilegeType.getLabels();
			ConsolePrivilegeTypeLabelKey typeKey = new ConsolePrivilegeTypeLabelKey();
			typeKey.setLocale(locale.toLanguageTag());
			typeKey.setPrivilegeType(privilegeType.getPrivilegeType());

			log.debug("Current Locale = [{}] Type=[{}] Label=[{}]", locale, privilegeType, typeLabels);
			menuItem.setCode(privilegeType.getPrivilegeType());
			menuItem.setLabel(typeLabels.get(locale.toLanguageTag()).getTypeName());
			menuItem.setIcon(privilegeType.getIcon());

			for (ConsolePrivilege privilege : privilegeType.getPrivileges()) {
				if (privilege.isShowOnMenu()) {
					log.debug("Current Privilege = [{}]", privilege);
					MenuItem child = new MenuItem();
					Map<String, ConsolePrivilegeLabel> labels = privilege.getLabels();
					child.setCode(privilege.getPrivilegeId());
					child.setLabel(labels.get(locale.toLanguageTag()).getPrivilegeName());
					child.setPrivilege(privilege);
					child.setIcon(privilege.getIcon());
					child.setUrl(privilege.getUrl());

					if (PrivilegeUtil.canGrantRead(userRights, privilege)) {
						children.add(child);
					}
				}
			}
			menuItem.setChild(children);
			menuItems.add(menuItem);
		}

		log.debug("MenuItems = [{}]", menuItems);
		return menuItems;
	}

	@Permission
	@RequestMapping(value = "/profile/password", method = RequestMethod.POST)
	@LogAction(type = ConsoleActions.UPD_PWD, message = "User[{1.username}] change password")
	public @ResponseBody RestResult<ConsoleUser> updatePassword(@RequestBody LoginCommand cmd)
			throws AuthenticateException {
		ConsoleUser existing = consoleUserManager.getUser(cmd.getUsername());
		RestResult<ConsoleUser> result = new RestResult<ConsoleUser>();

		String newEncPass = null;
		/* New Password Checking start */
		try {
			if (existing == null) {
				throw new AuthenticateException(ErrorCode.USER_NOT_FOUND, "User " + cmd.getUsername() + " not found");
			}
			if (!existing.getStatus().equals(ConsoleUser.STATUS_ACTIVE)) {
				throw new AuthenticateException(ErrorCode.USER_SUSPENDED, "User " + cmd.getUsername() + " suspended");
			}
			try {
				newEncPass = CryptUtil.encrypt(cmd.getNewPassword());
			} catch (Exception e) {
				log.warn("Encryption Error", e);
				throw new AuthenticateException(ErrorCode.RUNTIME_ERROR, "New Password Encryption");
			}

			if (existing.getPassword().matches(newEncPass)) {
				log.warn("New Password equal to Old Password. Update failed.");
				throw new AuthenticateException(ErrorCode.PWD_INCORRECT, "Repeated password");
			}
			log.debug("Config = " + mc.getSession().getPasswordHistory());
			if (mc.getSession().getPasswordHistory() > 0) {
				if (consoleUserManager.checkPwdHist(cmd.getUsername(), newEncPass,
						mc.getSession().getPasswordHistory()) == true) {
					log.info("Password History test failed. Password used before");
					throw new AuthenticateException(ErrorCode.PWD_INCORRECT, "Repeated password");
				}

				PwdHistory pwdHistory = new PwdHistory();
				pwdHistory.setUserId(cmd.getUsername());
				pwdHistory.setPWDRecord(existing.getPassword()); // save old encrypted password
				consoleUserManager.savePwdHist(pwdHistory);
			} else {
				log.debug("Password History Recognition Disabled.");
			}
		} catch (AuthenticateException e) {
			result.setSuccess(false);
			result.setCode(e.getErrorCode());
			return result;
		}
		/* New Password Checking end */

		existing.setLastPwdChange(new Date());
		existing.setPassword(newEncPass);

		consoleUserManager.saveOrUpdate(ConsoleUser.class, existing);

		result.setData(existing);
		log.debug("Password History test Passed. Password updated");
		return result;
	}

	@RequestMapping("/lang/{locale}")
	public void changeLanguage(@PathVariable String locale, HttpServletRequest request, HttpServletResponse response) {
		localeResolver.setLocale(request, response, parseLocaleValue(locale));
	}

	protected Locale parseLocaleValue(String locale) {
		return Locale.forLanguageTag(locale);
	}
}
