package exp.crawler.qq.bean;

import java.util.List;

import exp.libs.utils.other.StrUtils;

public class Mood {

	private String page;
	
	private String content;
	
	private List<String> picURLs;
	
	public Mood(int page, String content, List<String> picURLs) {
		this.page = StrUtils.leftPad(String.valueOf(page), '0', 4);
		this.content = (content == null ? "" : content.replaceAll("[\r\n]", ""));
		this.picURLs = picURLs;
	}
	
	public String PAGE() {
		return page;
	}
	
	public String CONTENT() {
		return content;
	}
	
	public List<String> PIC_URLS() {
		return picURLs;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("++++++++++++++++++++++++++++++++++++++++++++++++++++++\r\n");
		sb.append("+ [说说页码] : ").append(PAGE()).append("\r\n");
		sb.append("+ [说说内容] : ").append(CONTENT()).append("\r\n");
		sb.append("+ [照片数量] : ").append(PIC_URLS().size()).append("\r\n");
		sb.append("++++++++++++++++++++++++++++++++++++++++++++++++++++++\r\n");
		return sb.toString();
	}
	
}
