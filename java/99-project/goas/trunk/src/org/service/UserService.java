package org.service;

import java.util.List;

import javax.annotation.Resource;

import org.manager.UserManager;
import org.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	@Resource
	private UserManager userManager;
	
	public User getUser(Integer id) {
		return userManager.getUser(id);
	}
	
	public List<User> getUserList() {
		return userManager.getUserList();
	}
	
	public Boolean addUser(User user) {
		return userManager.addUser(user);
	}
	
	public Boolean updateUser(User user) {
		return userManager.updateUser(user);
	}
	
	public Boolean deleteUserList(List<Integer> idList) {
		return userManager.deleteUserList(idList);
	}
}
