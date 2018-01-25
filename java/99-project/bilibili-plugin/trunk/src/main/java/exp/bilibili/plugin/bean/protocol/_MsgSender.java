package exp.bilibili.plugin.bean.protocol;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibili.plugin.Config;
import exp.libs.warp.net.http.HttpUtils;

/**
 * <PRE>
 * B站直播版聊消息发送器
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _MsgSender {

	protected final static Logger log = LoggerFactory.getLogger(_MsgSender.class);
	
	protected final static String SSL_HOST = Config.getInstn().SSL_HOST();
	
	private final static String LIVE_URL = Config.getInstn().LIVE_URL();
	
	protected final static String LINK_HOST = Config.getInstn().LINK_HOST();
	
	protected final static String LINK_URL = Config.getInstn().LINK_URL();
	
	/** 私有化构造函数 */
	protected _MsgSender() {}
	
	/**
	 * 生成POST方法的请求头参数
	 * @param cookie
	 * @param realRoomId
	 * @return
	 */
	protected static Map<String, String> toPostHeadParams(String cookie, String realRoomId) {
		Map<String, String> params = toPostHeadParams(cookie);
		params.put(HttpUtils.HEAD.KEY.HOST, SSL_HOST);
		params.put(HttpUtils.HEAD.KEY.ORIGIN, LIVE_URL);
		params.put(HttpUtils.HEAD.KEY.REFERER, LIVE_URL.concat(realRoomId));	// 发送/接收消息的直播间地址
		return params;
	}
	
	/**
	 * 生成POST方法的请求头参数
	 * @param cookie
	 * @return
	 */
	protected static Map<String, String> toPostHeadParams(String cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(HttpUtils.HEAD.KEY.ACCEPT, "application/json, text/javascript, */*; q=0.01");
		params.put(HttpUtils.HEAD.KEY.ACCEPT_ENCODING, "gzip, deflate, br");
		params.put(HttpUtils.HEAD.KEY.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8,en;q=0.6");
		params.put(HttpUtils.HEAD.KEY.CONNECTION, "keep-alive");
		params.put(HttpUtils.HEAD.KEY.CONTENT_TYPE, // POST的是表单
				HttpUtils.HEAD.VAL.POST_FORM.concat(Config.DEFAULT_CHARSET));
		params.put(HttpUtils.HEAD.KEY.COOKIE, cookie);
		params.put(HttpUtils.HEAD.KEY.USER_AGENT, Config.USER_AGENT);
		return params;
	}
	
	/**
	 * 生成GET方法的请求头参数
	 * @param cookie
	 * @param realRoomId
	 * @return
	 */
	protected static Map<String, String> toGetHeadParams(String cookie, String realRoomId) {
		Map<String, String> params = toGetHeadParams(cookie);
		params.put(HttpUtils.HEAD.KEY.HOST, SSL_HOST);
		params.put(HttpUtils.HEAD.KEY.ORIGIN, LIVE_URL);
		params.put(HttpUtils.HEAD.KEY.REFERER, LIVE_URL.concat(String.valueOf(realRoomId)));	// 发送/接收消息的直播间地址
		return params;
	}
	
	/**
	 * 生成GET方法的请求头参数
	 * @param cookie
	 * @return
	 */
	protected static Map<String, String> toGetHeadParams(String cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(HttpUtils.HEAD.KEY.ACCEPT, "application/json, text/plain, */*");
		params.put(HttpUtils.HEAD.KEY.ACCEPT_ENCODING, "gzip, deflate, sdch");
		params.put(HttpUtils.HEAD.KEY.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8,en;q=0.6");
		params.put(HttpUtils.HEAD.KEY.CONNECTION, "keep-alive");
		params.put(HttpUtils.HEAD.KEY.COOKIE, cookie);
		params.put(HttpUtils.HEAD.KEY.USER_AGENT, Config.USER_AGENT);
		return params;
	}
	
}
