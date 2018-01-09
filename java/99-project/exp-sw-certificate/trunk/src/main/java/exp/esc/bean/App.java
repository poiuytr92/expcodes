package exp.esc.bean;

public class App {

	private String name;
	
	private String versions;
	
	private String time;
	
	private String blacklist;
	
	public App(String name, String versions, String time, String blacklist) {
		this.name = (name == null ? "" : name.trim());
		this.versions = (versions == null ? "" : versions.trim());
		this.time = (time == null ? "" : time.trim());
		this.blacklist = (blacklist == null ? "" : blacklist.trim());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersions() {
		return versions;
	}

	public void setVersions(String versions) {
		this.versions = versions;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getBlacklist() {
		return blacklist;
	}

	public void setBlacklist(String blacklist) {
		this.blacklist = blacklist;
	}
	
}
