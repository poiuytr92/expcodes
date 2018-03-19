package exp.qw.bean;

import exp.libs.utils.io.FileUtils;
import exp.libs.utils.num.IDUtils;
import exp.libs.utils.other.StrUtils;


public class Photo {

	private String name;
	
	private String desc;
	
	private String date;
	
	private String url;
	
	public Photo(String desc, String date, String url) {
		this.name = "";
		this.desc = (desc == null ? desc : "");
		this.date = (date == null ? date : "");
		this.url = (url == null ? url : "");
	}
	
	public String getFileName() {
		if(StrUtils.isEmpty(name)) {
			name = StrUtils.concat("[D", IDUtils.getTimeID(), "]-[U", date, "] ", desc);
			name = FileUtils.delForbidCharInFileName(name, "");
			name = StrUtils.showSummary(name);
		}
		return name;
	}
	
	public String DESC() {
		return desc;
	}
	
	public String DATE() {
		return date;
	}
	
	public String URL() {
		return url;
	}
	
}
