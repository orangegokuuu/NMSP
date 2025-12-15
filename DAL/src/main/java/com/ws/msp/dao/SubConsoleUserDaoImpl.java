package com.ws.msp.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ws.hibernate.GenericDaoImpl;
import com.ws.mc.pojo.AuthenticateException;
import com.ws.mc.pojo.ErrorCode;
import com.ws.msp.pojo.SubConsoleUser;
import com.ws.util.CryptUtil;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class SubConsoleUserDaoImpl extends GenericDaoImpl<SubConsoleUser, String> implements SubConsoleUserDao {
	@Autowired(required = true)
	public void setSessionFactory(SessionFactory mcFactory) {
		super.setSessionFactory(mcFactory);
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.syniverse.sbm.console.dao.ConsoleUserDao#loginUser(java.lang.String,
	 * java.lang.String)
	 */
	public boolean loginUser(String id, String password)
			throws AuthenticateException {
		SubConsoleUser user = get(id);
		boolean success = false;
		log.debug("Retrieve user profile id = " + id + " found user " + user);

		if ((user != null) && (user.getUserId().equals(id))) {
			log.debug("user.getUserId() = " + user.getUserId());
			String cryptPass = null;
			try {
				cryptPass = CryptUtil.encrypt(password);
				log.debug("Input : [" + cryptPass + "]");
			} catch (Exception e) {
				throw new AuthenticateException(ErrorCode.RUNTIME_ERROR, "Password Encrypt Error");
			}
			log.debug("DB : [" + user.getPassword() + "]");

			log.debug("Console User Status : [" + user.getStatus() + "]");
			if (SubConsoleUser.STATUS_ACTIVE.equals(user.getStatus())) {
				if (cryptPass.equals(user.getPassword())) {
					success = true;
				} else {
					success = false;
				}
			} else if (SubConsoleUser.STATUS_SUSPEND.equals(user.getStatus())) {
				log.debug("Console User Suspended");
				throw new AuthenticateException(ErrorCode.USER_SUSPENDED, "User suspended");
			}
		} else {
			throw new AuthenticateException(ErrorCode.USER_NOT_FOUND, "User not exist");
		}

		return success;
	}
}
