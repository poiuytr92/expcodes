package exp.crawler.qq.bean;

import java.util.LinkedList;
import java.util.List;

import exp.libs.utils.other.StrUtils;
import exp.libs.utils.time.TimeUtils;

/**
 * <PRE>
 * 说说对象
 * </PRE>
 * <B>PROJECT : </B> qzone-crawler
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Mood {

	/**
	 * 此条说说所在的页码.
	 *  
	 *  QQ空间的说说每页最�?20�?, 但是数量不是固定�?20.
	 *  原因是说说被删除�?, 原有的说说依然保有其自身的索�?, 虽然总的排序没有变化.
	 *  
	 *  这就导致每条说说所在的页码是相对固�?.
	 */
	private String page;
	
	/** 说说内容 */
	private String content;
	
	/** 说说的创建时�? */
	private long createTime;
	
	/**
	 * 说说中的相关图片地址.
	 * 	(包括说说自身�? �? 转发�?)
	 */
	private List<String> picURLs;
	
	/**
	 * 构造函�?
	 * @param page
	 * @param content
	 * @param createTime
	 */
	public Mood(int page, String content, long createTime) {
		this.page = StrUtils.leftPad(String.valueOf(page), '0', 4);
		this.createTime = (createTime < 0 ? 0 : createTime);
		
		// 处理换行�? �? @某人 的内�? (@某人 的原文是json)
		this.content = (content == null ? "" : content.replaceAll("[\r\n]", "").
				replaceAll("@\\{.*?nick:(.*?),who.*?\\}", "@$1")
		);
		if(StrUtils.isTrimEmpty(this.content)) {
			this.content = TimeUtils.toStr(createTime);
		}
		
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
	
	public int PIC_NUM() {
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
		sb.append("[图片数量] : ").append(PIC_NUM()).append("\r\n");
		sb.append("[图片列表] : \r\n");
		for(String url : picURLs) {
			sb.append("   ").append(url).append("\r\n");
		}
		sb.append("======================================================\r\n");
		return sb.toString();
	}
	
}
