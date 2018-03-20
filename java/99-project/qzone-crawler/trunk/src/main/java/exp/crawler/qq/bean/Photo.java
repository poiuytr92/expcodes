package exp.crawler.qq.bean;

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
		this.desc = (desc == null ? "" : desc.replaceAll("[\r\n]", ""));
		this.date = (date == null ? "" : date);
		this.url = (url == null ? "" : url);
	}
	
	public String getFileName() {
		if(StrUtils.isEmpty(name)) {
			name = StrUtils.concat("[D", IDUtils.getTimeID(), "]-[U", date, "] ", desc);
			name = FileUtils.delForbidCharInFileName(name, "");
			name = StrUtils.showSummary(name);
			name = name.concat(".png");
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
	
	public String toString(boolean isDownload) {
		StringBuilder sb = new StringBuilder();
		sb.append("[下载状态] : ").append(isDownload).append("\r\n");
		sb.append("[照片路径] : ").append(URL()).append("\r\n");
		sb.append("[照片描述] : ").append(DESC()).append("\r\n");
		sb.append("[上传时间] : ").append(DATE()).append("\r\n");
		sb.append("======================================================\r\n");
		return sb.toString();
	}
	
}
