package exp.bilibili.plugin.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <PRE>
 * 在线用户管理器
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class OnlineUserMgr {

	/** 在线用户 */
	private Set<String> users;
	
	/** 被举报用户 -> 去报群众列表 */
	private Map<String, Set<String>> blacks;
	
	private static volatile OnlineUserMgr instance;
	
	private OnlineUserMgr() {
		this.users = new HashSet<String>();
		this.blacks = new HashMap<String, Set<String>>();
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
	
	public void clear() {
		users.clear();
		blacks.clear();
	}
	
	public void add(String username) {
		users.add(username);
	}
	
	public void del(String username) {
		users.remove(username);
	}
	
	public List<String> getAllUsers() {
		return new ArrayList<String>(users);
	}
	
	/**
	 * 举报
	 * @param accuser 举报人
	 * @param accused 被举报人
	 * @return 被举报次数
	 */
	public int complaint(String accuser, String accused) {
		Set<String> accusers = blacks.get(accused);
		if(accusers == null) {
			accusers = new HashSet<String>();
			blacks.put(accused, accusers);
		}
		accusers.add(accuser);
		return accusers.size();
	}
	
	/**
	 * 撤销举报
	 * @param accused 被举报人
	 */
	public void cancel(String accused) {
		blacks.remove(accused);
	}
	
}
