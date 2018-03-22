package exp.crawler.qq.bean;

import java.util.LinkedList;
import java.util.List;

import exp.libs.utils.other.StrUtils;
import exp.libs.utils.time.TimeUtils;

public class Mood {

	private String page;
	
	private String content;
	
	private long createTime;
	
	private List<String> picURLs;
	
	public Mood(int page, String content, long createTime) {
		this.page = StrUtils.leftPad(String.valueOf(page), '0', 4);
		this.content = (content == null ? "" : content.replaceAll("[\r\n]", "").
				replaceAll("@\\{.*?nick:(.*?),who.*?\\}", "@$1")	// @某人
		);
		this.createTime = (createTime < 0 ? 0 : createTime); 
		this.picURLs = new LinkedList<String>();
	}
	
	public String PAGE() {
		return page;
	}
	
	public String CONTENT() {
		return content;
	}
	
	public String TIME() {
		return TimeUtils.toStr(createTime);
	}
	
	public int PIC_SIZE() {
		return picURLs.size();
	}
	
	public List<String> getPicURLs() {
		return picURLs;
	}
	
	public void addPicURL(String url) {
		if(StrUtils.isNotTrimEmpty(url)) {
			picURLs.add(url);
		}
	}
	
	public String toString(boolean isDownload) {
		StringBuilder sb = new StringBuilder();
		sb.append("[下载状态] : ").append(isDownload).append("\r\n");
		sb.append("[说说页码] : ").append(PAGE()).append("\r\n");
		sb.append("[说说内容] : ").append(CONTENT()).append("\r\n");
		sb.append("[图片数量] : ").append(getPicURLs().size()).append("\r\n");
		sb.append("[图片列表] : \r\n");
		for(String url : picURLs) {
			sb.append("   ").append(url).append("\r\n");
		}
		sb.append("======================================================\r\n");
		return sb.toString();
	}
	
}
