package com.ws.msp.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ws.hibernate.GenericDataManagerImpl;
import com.ws.mc.pojo.AuthenticateException;
import com.ws.mc.pojo.ErrorCode;
import com.ws.msp.dao.SubConsoleUserDao;
import com.ws.msp.pojo.SubConsoleUser;
import com.ws.util.CryptUtil;

import lombok.extern.log4j.Log4j2;

@Service(value = "subConsoleUserManager")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
@Log4j2
public class SubConsoleUserManagerImpl extends GenericDataManagerImpl implements SubConsoleUserManager{
	
	@Autowired
	private SubConsoleUserDao userDao = null;
	
	@Transactional
	public SubConsoleUser getUser(String id) {
		return userDao.get(id);
	}

	@Transactional
	public List<SubConsoleUser> getUsers() {
		DetachedCriteria criteria = userDao.createDetachedCriteria();
		criteria.addOrder(Order.asc("userId"));
		return userDao.findByCriteria(criteria);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean loginUser(String id, String password) throws AuthenticateException {
		boolean success = userDao.loginUser(id, password);
		return success;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void removeUser(String id) {
		userDao.deleteByKey(id);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void saveUser(SubConsoleUser user) {
		if (user.getCreateDate() == null) {
			user.setCreateDate(LocalDateTime.now());
		}
		userDao.saveOrUpdate(user);
	}


	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public SubConsoleUser updatePassword(String userId, String oldPassword, String newPassword)
	        throws AuthenticateException {

		SubConsoleUser user = this.userDao.get(userId);
		if (user == null) {
			throw new AuthenticateException(ErrorCode.USER_NOT_FOUND, "User " + userId + " not found");
		}
		if(!user.getStatus().equals(SubConsoleUser.STATUS_ACTIVE)){
			if (user.getStatus().equals(SubConsoleUser.STATUS_SUSPEND)) {
				throw new AuthenticateException(ErrorCode.USER_SUSPENDED, "User " + userId + " suspended");
			}
			if (user.getStatus().equals(SubConsoleUser.STATUS_EXPIRE)) {
				throw new AuthenticateException(ErrorCode.USER_EXPIRED, "User " + userId + " expired");
			}
			throw new AuthenticateException(ErrorCode.RUNTIME_ERROR, "Unknown runtime error");
		}
		String encPass = null;
		try {
			encPass = CryptUtil.encrypt(oldPassword);
		} catch (Exception e) {
			log.warn("Encryption Error", e);
			throw new AuthenticateException(ErrorCode.RUNTIME_ERROR, "Old Password Encryption");
		}
		if (!encPass.equals(user.getPassword())) {
			throw new AuthenticateException(ErrorCode.PWD_INCORRECT, "Incorrect password");
		}

		try {
			user.setPassword(CryptUtil.encrypt(newPassword));
		} catch (Exception e) {
			log.warn("Encryption Error", e);
			throw new AuthenticateException(ErrorCode.RUNTIME_ERROR, "New Password Encryption");
		}
		
		user.setUpdateBy(userId);
		user.setUpdateDate(LocalDateTime.now());

		user = userDao.saveOrUpdate(user);
		return user;
	}

	
}
