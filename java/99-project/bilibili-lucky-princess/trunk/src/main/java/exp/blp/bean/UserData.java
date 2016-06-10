package exp.blp.bean;

public class UserData {

	public final static String KEY_JOIN = "%参加抽奖";
	
	public final static String KEY_UNJOIN = "%退出抽奖";
	
	private String username;
	
	private int action;	// 在线有效行为次数
	
	private boolean isJoin;
	
	public UserData(String username) {
		this.username = username;
		this.action = 0;
		this.isJoin = false;
	}

	public String getUsername() {
		return username;
	}

	public int getAction() {
		return action;
	}

	public void addAction() {
		this.action++;
	}

	public boolean isJoin() {
		return isJoin;
	}

	public void setJoin(boolean isJoin) {
		this.isJoin = isJoin;
	}
	
}
