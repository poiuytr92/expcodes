package exp.crawler.qq.utils;

import exp.libs.utils.io.FileUtils;
import exp.libs.utils.num.IDUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 图片工具类
 * </PRE>
 * <B>PROJECT : </B> qzone-crawler
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2018-03-23
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class PicUtils {

	/** 图片后缀 */
	public final static String SUFFIX = ".png";
	
	/** 私有化构造函�? */
	protected PicUtils() {}
	
	/**
	 * 计算页数
	 * @param total 总数
	 * @param batch 分页�?
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
		name = FileUtils.delForbidCharInFileName(name, "");	// 移除无效的文件名字符
		name = StrUtils.showSummary(name);	// 避免文件名过�?
		name = name.concat(SUFFIX);
		return name;
	}
	
	/**
	 * 转换图片地址
	 * @param url 图片地址
	 * @return
	 */
	public static String convert(String picURL) {
		if(picURL != null) {
			picURL = picURL.replace("psbe?", "psb?");	// 去除权限加密
			picURL = picURL.replace("/m/", "/b/");		// 缩略图变成大�?
			picURL = picURL.replace("/c/", "/b/");		// 缩略图变成大�?
			
		} else {
			picURL = "";
		}
		return picURL;
	}
	
	/**
	 * 判定验证码是否为伪验证码.
	 * 
	 * 	伪验证码以感叹号开头，�?  !QWE
	 *  真实验证码则为字�?+数字组合，如 Q2R5
	 * @return
	 */
	public static boolean isFalsuVcode(String vcode) {
		return (vcode == null || vcode.startsWith("!"));
	}
	
}
