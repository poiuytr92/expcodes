package exp.crawler.qq.bean;

import java.util.LinkedList;
import java.util.List;

import exp.crawler.qq.Config;
import exp.crawler.qq.utils.PicUtils;

/**
 * <PRE>
 * 相册对象
 * </PRE>
 * <B>PROJECT : </B> qzone-crawler
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Album {

	/** 相册编号 */
	private String id;
	
	/** 相册名称/描述 */
	private String name;
	
	/** 相册地址 */
	private String url;
	
	/** 相册页数 */
	private int pageNum;
	
	/** 相册照片总数 */
	private int totalPicNum;
	
	/** 相册照片�? */
	private List<Photo> photos;
	
	/**
	 * 构造函�?
	 * @param id
	 * @param name
	 * @param url
	 * @param totalPicNum
	 */
	public Album(String id, String name, String url, int totalPicNum) {
		this.id = id;
		this.name = name;
		this.url = url;
		this.totalPicNum = totalPicNum;
		this.pageNum = PicUtils.getPageNum(totalPicNum, Config.BATCH_LIMT);
		this.photos = new LinkedList<Photo>();
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
	
	public int PAGE_NUM() {
		return pageNum;
	}
	
	public int TOTAL_PIC_NUM() {
		return totalPicNum;
	}
	
	public int PIC_NUM() {
		return photos.size();
	}
	
	public List<Photo> getPhotos() {
		return photos;
	}
	
	public void addPhoto(Photo photo) {
		if(photo != null) {
			photos.add(photo);
		}
	}
	
	public void addPhotos(List<Photo> photos) {
		if(photos != null) {
			this.photos.addAll(photos);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("++++++++++++++++++++++++++++++++++++++++++++++++++++++\r\n");
		sb.append("+ [相册名称] : ").append(NAME()).append("\r\n");
		sb.append("+ [相册编号] : ").append(ID()).append("\r\n");
		sb.append("+ [相册地址] : ").append(URL()).append("\r\n");
		sb.append("+ [照片数量] : ").append(TOTAL_PIC_NUM()).append("\r\n");
		sb.append("++++++++++++++++++++++++++++++++++++++++++++++++++++++\r\n");
		return sb.toString();
	}
	
}
