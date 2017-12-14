package exp.bilibli.plugin.core.peep;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import exp.bilibli.plugin.bean.ldm.Action;
import exp.bilibli.plugin.bean.ldm.User;
import exp.bilibli.plugin.utils.WebUtils;
import exp.libs.utils.other.StrUtils;

public class UserMgr {

	private Set<Integer> tabus;
	
	/**
	 * 用户管理器.
	 *  id/name -> user
	 */
	private Map<String, User> users;
	
	/** 用户行为记录器 */
	private List<Action> actions;
	
	private static volatile UserMgr instance;
	
	private UserMgr() {
		this.tabus = new HashSet<Integer>();
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
		if(tabus.contains(chatItem.hashCode())) {
			return;
		}
		tabus.add(chatItem.hashCode());
		
		User user = null;
		String action = "";
		
		// 投喂信息
		if(chatItem.getAttribute("class").contains("gift-item")) {
			String username = chatItem.findElement(By.className("username")).getText();
			action = StrUtils.concat(chatItem.findElement(By.className("action")).getText(), 
					" ", chatItem.findElement(By.className("gift-count")).getText());
			
			user = users.get(username);
			
		// 版聊信息
		} else {
			String uid = chatItem.getAttribute("data-uid");
			action = chatItem.getAttribute("data-danmaku");
			
			if(users.containsKey(uid)) {
				user = users.get(uid);
				
			} else {
				user = new User();
				user.setId(uid);
				user.setUsername(chatItem.getAttribute("data-uname"));
				user.setLevel(chatItem.findElement(By.className("user-level-icon")).getText());
				
				WebElement medal = chatItem.findElement(By.className("fans-medal-item")); {
					String label = medal.findElement(By.className("label")).getText();
					String level = medal.findElement(By.className("level")).getText();
					user.setMedal(StrUtils.concat(label, "(", level, ")"));
				}
				
				String title = ""; {
//					if(WebUtils.exist(chatItem, By.className("vip-year-color"))) {
//						title = "年费老爷";
//					} else if(WebUtils.exist(chatItem, By.className("vip-color"))) {
//						title = "月费老爷";
//					}
//					
//					if(WebUtils.exist(chatItem, By.className("guard-level-3"))) {
//						title = title.concat(" 舰长");
//					} else if(WebUtils.exist(chatItem, By.className("guard-level-2"))) {
//						title = title.concat(" 提督");
//					} else if(WebUtils.exist(chatItem, By.className("guard-level-1"))) {
//						title = title.concat(" 总督");
//					}
				}
				user.setTitle(title);
				
				users.put(user.getId(), user);
				users.put(user.getUsername(), user);
			}
			
		}
		
		if(user != null) {
			actions.add(new Action(user.getId(), action));
		}
	}
	
	public List<List<String>> getChatMsgs() {
		List<List<String>> chatMsgs = new LinkedList<List<String>>();
		for(Action action : actions) {
			List<String> chatMsg = new LinkedList<String>();
			User user = users.get(action.getUId());
			
			chatMsg.add(user.getId());
			chatMsg.add(user.getTitle());
			chatMsg.add(user.getMedal());
			chatMsg.add(user.getLevel());
			chatMsg.add(user.getUsername());
			chatMsg.add(action.getAction());
			chatMsgs.add(chatMsg);
		}
		return chatMsgs;
	}
	
}
