package exp.crawler.qq.utils;

import exp.libs.utils.io.FileUtils;
import exp.libs.utils.num.IDUtils;
import exp.libs.utils.other.StrUtils;

public class PicUtils {

	public final static String SUFFIX = ".png";
	
	protected PicUtils() {}
	
	/**
	 * 计算页数
	 * @param total 总数
	 * @param batch 分页数
	 * @return 页数
	 */
	public static int getPageNum(int total, int batch) {
		total = (total < 0 ? 0 : total);
		batch = (batch <= 0 ? 1 : batch);
		
		int page = total / batch;
		if(total % batch != 0) {
			page += 1;	// 向上取整
		}
		return page;
	}
	
	/**
	 * 生成图片名称
	 * @param idx 图片索引
	 * @param desc 图片描述
	 * @return
	 */
	public static String getPicName(String idx, String desc) {
		String name = StrUtils.concat("[", IDUtils.getTimeID(), "]-[", idx, "] ", desc);
		name = FileUtils.delForbidCharInFileName(name, "");	// 取出无效的文件名字符
		name = StrUtils.showSummary(name);	// 避免文件名过长
		name = name.concat(SUFFIX);
		return name;
	}
	
	/**
	 * 转换图片地址
	 * @param url 图片地址
	 * @return
	 */
	public static String convert(String url) {
		if(url != null) {
			url = url.replace("psbe?", "psb?");	// 去除权限加密
			url = url.replace("/m/", "/b/");	// 缩略图变成大图
			url = url.replace("/c/", "/b/");	// 缩略图变成大图
			
		} else {
			url = "";
		}
		return url;
	}
	
}
