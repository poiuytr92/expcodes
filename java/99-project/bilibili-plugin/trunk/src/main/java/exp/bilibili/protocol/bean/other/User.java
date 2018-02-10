package exp.bilibili.protocol.bean.other;

public class User {

	public final static User NULL = new User("", "", 0);
	
	private String id;
	
	private String name;
	
	private int level;
	
	public User(String id, String name) {
		this(id, name, 0);
	}
	
	public User(String id, String name, int level) {
		this.id = (id == null ? "" : id);
		this.name = (name == null ? "" : name);
		this.level = (level < 0 ? 0 : level);
	}
	
	public String ID() {
		return id;
	}
	
	public String NAME() {
		return name;
	}
	
	public int LV() {
		return level;
	}
	
}
