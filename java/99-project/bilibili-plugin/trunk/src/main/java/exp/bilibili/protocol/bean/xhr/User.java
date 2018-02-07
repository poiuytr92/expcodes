package exp.bilibili.protocol.bean.xhr;

public class User {

	public final static User NULL = new User("", "");
	
	private String id;
	
	private String name;
	
	public User(String id, String name) {
		this.id = (id == null ? "" : id);
		this.name = (name == null ? "" : name);
	}
	
	public String ID() {
		return id;
	}
	
	public String NAME() {
		return name;
	}
	
}
