package exp.bilibli.plugin.cache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OnlineUserMgr {

	private Set<String> users;
	
	private static volatile OnlineUserMgr instance;
	
	private OnlineUserMgr() {
		this.users = new HashSet<String>();
	}
	
	public static OnlineUserMgr getInstn() {
		if(instance == null) {
			synchronized (OnlineUserMgr.class) {
				if(instance == null) {
					instance = new OnlineUserMgr();
				}
			}
		}
		return instance;
	}
	
	public void add(String username) {
		users.add(username);
	}
	
	public void del(String username) {
		users.remove(username);
	}
	
	public void clear() {
		users.clear();
	}
	
	public List<String> getAllUsers() {
		return new ArrayList<String>(users);
	}
	
}
