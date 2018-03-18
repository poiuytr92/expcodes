package exp.qw.bean;

public class Album {

	private String id;
	
	private String name;
	
	private String url;
	
	private int num;
	
	public Album(String id, String name, String url, int num) {
		this.id = id;
		this.name = name;
		this.url = url;
		this.num = num;
	}
	
	public String ID() {
		return id;
	}
	
	public String NAME() {
		return name;
	}
	
	public String URL() {
		return url;
	}
	
	public int NUM() {
		return num;
	}
	
}
