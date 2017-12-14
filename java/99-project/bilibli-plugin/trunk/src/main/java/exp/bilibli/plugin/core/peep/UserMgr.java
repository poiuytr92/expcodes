package exp.bilibli.plugin.core.peep;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebElement;

import exp.bilibli.plugin.bean.ldm.Action;
import exp.bilibli.plugin.bean.ldm.User;

public class UserMgr {

	/**
	 * 用户管理器.
	 *  id -> user
	 */
	private Map<String, User> users;
	
	/** 用户行为记录器 */
	private List<Action> actions;
	
	private static volatile UserMgr instance;
	
	private UserMgr() {
		this.users = new HashMap<String, User>();
		this.actions = new LinkedList<Action>();
	}
	
	public static UserMgr getInstn() {
		if(instance == null) {
			synchronized (UserMgr.class) {
				if(instance == null) {
					instance = new UserMgr();
				}
			}
		}
		return instance;
	}
	
	public void analyse(WebElement chatItem) {
		
	}
	
}
