package org.test;

import javax.annotation.Resource;

import org.junit.runner.RunWith;
import org.manager.UserManager;
import org.model.User;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="applicationContext.xml")
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=false) 
@Transactional 
public class Test extends AbstractTransactionalJUnit4SpringContextTests {

	@Resource
	private UserManager userManager;
	
//	@Resource
//	private LoginService loginService;
	
	@org.junit.Test
	public void test() {
		User user1 = new User();
		user1.setName("zzzz");
		user1.setId(8);
		userManager.addUser(user1);
//		System.out.println(user1.getId());
//		List<User> userList = userManager.findAll();
//		for (User user : userList) {
//			System.out.println(user.getName());
//		}
		System.out.println("success");
	}
}
