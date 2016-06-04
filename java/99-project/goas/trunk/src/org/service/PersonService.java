package org.service;

import javax.annotation.Resource;

import org.manager.UserManager;
import org.model.User;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

	@Resource
	private UserManager userManager;
	
	public User getUser(Integer id) {
		return userManager.getUser(id);
	}
	
	public Boolean updateUser(User user) {
		return userManager.updateUser(user);
	}
}
