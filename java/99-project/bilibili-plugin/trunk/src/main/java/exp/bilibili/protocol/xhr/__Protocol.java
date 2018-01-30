package exp.bilibili.protocol.xhr;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibili.plugin.Config;
import exp.libs.warp.net.http.HttpUtils;

/**
 * <PRE>
 * B站XHR协议
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class __Protocol {

	/** 日志器 */
	protected final static Logger log = LoggerFactory.getLogger(__Protocol.class);
	
	/** SSL服务器主机 */
	protected final static String LIVE_HOST = Config.getInstn().LIVE_HOST();
	
	/** 直播间URL */
	private final static String LIVE_HOME = Config.getInstn().LIVE_HOME();
	
	/** Link中心服务器主机 */
	protected final static String LINK_HOST = Config.getInstn().LINK_HOST();
	
	/** Link中心URL */
	protected final static String LINK_HOME = Config.getInstn().LINK_HOME();
	
	/** 私有化构造函数 */
	protected __Protocol() {}
	
	/**
	 * 生成GET方法的请求头参数
	 * @param cookie
	 * @return
	 */
	protected final static Map<String, String> GET_HEADER(String cookie) {
		Map<String, String> header = new HashMap<String, String>();
		header.put(HttpUtils.HEAD.KEY.ACCEPT, "application/json, text/plain, */*");
		header.put(HttpUtils.HEAD.KEY.ACCEPT_ENCODING, "gzip, deflate, sdch");
		header.put(HttpUtils.HEAD.KEY.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8,en;q=0.6");
		header.put(HttpUtils.HEAD.KEY.CONNECTION, "keep-alive");
		header.put(HttpUtils.HEAD.KEY.COOKIE, cookie);
		header.put(HttpUtils.HEAD.KEY.USER_AGENT, Config.USER_AGENT);
		return header;
	}
	
	/**
	 * 生成GET方法的请求头参数
	 * @param cookie
	 * @param uri
	 * @return
	 */
	protected final static Map<String, String> GET_HEADER(String cookie, String uri) {
		Map<String, String> header = GET_HEADER(cookie);
		header.put(HttpUtils.HEAD.KEY.HOST, LIVE_HOST);
		header.put(HttpUtils.HEAD.KEY.ORIGIN, LIVE_HOME);
		header.put(HttpUtils.HEAD.KEY.REFERER, LIVE_HOME.concat(uri));
		return header;
	}
	
	/**
	 * 生成POST方法的请求头参数
	 * @param cookie
	 * @return
	 */
	protected final static Map<String, String> POST_HEADER(String cookie) {
		Map<String, String> header = new HashMap<String, String>();
		header.put(HttpUtils.HEAD.KEY.ACCEPT, "application/json, text/javascript, */*; q=0.01");
		header.put(HttpUtils.HEAD.KEY.ACCEPT_ENCODING, "gzip, deflate, br");
		header.put(HttpUtils.HEAD.KEY.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8,en;q=0.6");
		header.put(HttpUtils.HEAD.KEY.CONNECTION, "keep-alive");
		header.put(HttpUtils.HEAD.KEY.CONTENT_TYPE, // POST的是表单
				HttpUtils.HEAD.VAL.POST_FORM.concat(Config.DEFAULT_CHARSET));
		header.put(HttpUtils.HEAD.KEY.COOKIE, cookie);
		header.put(HttpUtils.HEAD.KEY.USER_AGENT, Config.USER_AGENT);
		return header;
	}
	
	/**
	 * 生成POST方法的请求头参数
	 * @param cookie
	 * @param uri
	 * @return
	 */
	protected final static Map<String, String> POST_HEADER(String cookie, String uri) {
		Map<String, String> header = POST_HEADER(cookie);
		header.put(HttpUtils.HEAD.KEY.HOST, LIVE_HOST);
		header.put(HttpUtils.HEAD.KEY.ORIGIN, LIVE_HOME);
		header.put(HttpUtils.HEAD.KEY.REFERER, LIVE_HOME.concat(uri));
		return header;
	}
	
}
