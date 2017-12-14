package exp.bilibli.plugin.bean.ldm;

public class User {

	private String id;
	
	/** 头衔 */
	private String title;
	
	/** 勋章（含等级） */
	private String medal;
	
	/** 用户等级 */
	private String level;
	
	private String username;
	
	public User() {
		this.id = "";
		this.title = "";
		this.medal = "";
		this.level = "";
		this.username = "";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMedal() {
		return medal;
	}

	public void setMedal(String medal) {
		this.medal = medal;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
}
