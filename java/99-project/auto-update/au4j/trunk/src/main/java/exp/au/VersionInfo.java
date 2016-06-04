package exp.au;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import exp.libs.utils.pub.ESCUtils;
import exp.libs.utils.pub.StrUtils;

public class VersionInfo {

	private String appName;
	
	private String description;
	
	private Version version;
	
	private String time;
	
	private String author;
	
	private String team;
	
	private String company;
	
	private List<String> updateContents;
	
	public VersionInfo() {
		this.updateContents = new LinkedList<String>();
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		this.version = version;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getUpdateContents(String lineIndent) {
		lineIndent = (lineIndent == null ? "" : lineIndent);
		StringBuilder sb = new StringBuilder();
		int cnt = 0;
		for(String content : updateContents) {
			String serialNum = StrUtils.leftPad(String.valueOf(++cnt), '0', 2);
			sb.append(lineIndent).append(serialNum).append(". ");
			sb.append(content).append(".\r\n");
		}
		return sb.toString();
	}

	public void addUpdateContent(String updateContent) {
		if(StrUtils.isNotEmpty(updateContent)) {
			this.updateContents.add(updateContent);
		}
	}
	
	@Override
	public String toString() {
		List<List<Object>> table = new ArrayList<List<Object>>();
		table.add(getRow("app-name", getAppName()));
		table.add(getRow("version", getVersion().getVersion()));
		table.add(getRow("time", getTime()));
		table.add(getRow("author", getAuthor()));
		table.add(getRow("company", StrUtils.concat(getCompany(), "/", getTeam())));
		table.add(getRow("descript", getDescription()));
		return StrUtils.concat(ESCUtils.toTXT(table, true), 
				"  updates:\r\n", getUpdateContents("    "));
	}
	
	private List<Object> getRow(String key, String val) {
		List<Object> row = new ArrayList<Object>(2);
		row.add(key);
		row.add(val);
		return row;
	}
	
}
