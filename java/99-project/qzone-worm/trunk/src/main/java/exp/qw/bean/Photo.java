package exp.qw.bean;

public class Photo {

	private String name;
	
	private String date;
	
	private String url;
	
	public Photo(String name, String date, String url) {
		this.name = remove(name);
		this.date = date;
		this.url = url;
	}
	
	private String remove(String name) {
		// FIXME 移除特殊字符
		return name;
	}
	
	public String NAME() {
		return name;
	}
	
	public String DATE() {
		return date;
	}
	
	public String URL() {
		return url;
	}
	
}
