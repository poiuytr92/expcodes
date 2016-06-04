package org.service;

import java.util.List;

import javax.annotation.Resource;

import org.manager.UserManager;
import org.model.User;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

	@Resource
	private UserManager userManager;
	
	public User getUser(String username, String password) {
		return userManager.getUser(username, password);
	}
}
