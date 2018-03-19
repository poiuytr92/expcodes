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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("++++++++++++++++++++++++++++++++++++++++++++++++++++++\r\n");
		sb.append("+ [相册名称] : ").append(NAME()).append("\r\n");
		sb.append("+ [相册编号] : ").append(ID()).append("\r\n");
		sb.append("+ [相册地址] : ").append(URL()).append("\r\n");
		sb.append("+ [照片数量] : ").append(NUM()).append("\r\n");
		sb.append("++++++++++++++++++++++++++++++++++++++++++++++++++++++\r\n");
		return sb.toString();
	}
	
}
