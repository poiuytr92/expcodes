package exp.crawler.qq.bean;

import exp.crawler.qq.utils.PicUtils;
import exp.libs.utils.other.StrUtils;


public class Photo {

	private String name;
	
	private String desc;
	
	private String time;
	
	private String url;
	
	public Photo(String desc, String time, String url) {
		this.name = "";
		this.desc = (desc == null ? "" : desc.replaceAll("[\r\n]", ""));
		this.time = (time == null ? "" : time);
		this.url = (url == null ? "" : url);
	}
	
	public String getPicName() {
		if(StrUtils.isEmpty(name)) {
			name = PicUtils.getPicName(time, desc);
		}
		return name;
	}
	
	public String DESC() {
		return desc;
	}
	
	public String TIME() {
		return time;
	}
	
	public String URL() {
		return url;
	}
	
	public String toString(boolean isDownload) {
		StringBuilder sb = new StringBuilder();
		sb.append("[下载状态] : ").append(isDownload).append("\r\n");
		sb.append("[上传时间] : ").append(TIME()).append("\r\n");
		sb.append("[照片描述] : ").append(DESC()).append("\r\n");
		sb.append("[照片路径] : ").append(URL()).append("\r\n");
		sb.append("======================================================\r\n");
		return sb.toString();
	}
	
}
