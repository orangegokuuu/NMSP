package com.ws.msp.mq.sac.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ws.msp.mq.sac.interceptor.annotation.Permission;
import com.ws.msp.mq.sac.pojo.RestResult;
import com.ws.msp.mq.sac.pojo.SubUserSession;

@Controller
public class HomeController {
	
	@Autowired
	SubUserSession userSession = null;
	
//	@Permission
//	@RequestMapping("/")
//	public ModelAndView homepage() {
//		return new ModelAndView("home");
//	}
	
	@Permission
	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String homepage(Model model) {
		return "home";
	}
	
	@Permission
	@RequestMapping(value = "/session", method = RequestMethod.GET)
	public @ResponseBody RestResult<SubUserSession> getSession() {
		RestResult<SubUserSession> result = new RestResult<SubUserSession>();

		// Jackson Mapping fail for session scope
		SubUserSession clone = new SubUserSession();
		BeanUtils.copyProperties(userSession, clone);

		result.setSuccess(true);
		result.setData(clone);
		return result;
	}
	
	@RequestMapping("/logout")
	public @ResponseBody ModelAndView logout(HttpServletRequest request) throws Exception  {
		request.getSession().invalidate();
		ModelAndView modelAndView = new ModelAndView("redirect:/login");
		return modelAndView;
	}
	
}
