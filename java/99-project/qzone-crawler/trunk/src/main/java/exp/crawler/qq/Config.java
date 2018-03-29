package exp.crawler.qq;

import exp.libs.envm.Charset;

/**
 * <PRE>
 * 配置类
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-03-23
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Config {

	/** 默认编码 */
	public final static String CHARSET = Charset.UTF8;
	
	/** 下载数据保存目录 */
	public final static String DATA_DIR = "./data/";
	
	/** 登陆信息保存路径 */
	public final static String LOGIN_INFO_PATH = "./conf/account.dat";
	
	/** 验证码图片保存路径 */
	public final static String VCODE_IMG_PATH = "./conf/vcode.jpg";
	
	/** 行为休眠间隔 */
	public final static long SLEEP_TIME = 100;
	
	/** 请求超时 */
	public final static int TIMEOUT = 10000;
	
	/**
	 * 每次批量请求的数量限制
	 * 	(说说最多20, 相册是30, 此处取最小值)
	 */
	public final static int BATCH_LIMT = 20;
	
	/** 重试次数 */
	public final static int RETRY = 5;
	
}
