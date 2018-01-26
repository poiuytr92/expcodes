package exp.bilibili.protocol.xhr;

import java.util.Map;

import exp.libs.utils.encode.CryptoUtils;
import exp.libs.warp.net.http.HttpURLUtils;
import exp.libs.warp.net.http.HttpUtils;

/**
 * <PRE>
 * 获取LINK中心的个人标签.
 * 	用于验证软件授权(备用方式)
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Certificate extends __Protocol {

	/**
	 * 软件授权页(Bilibili-备用)
	 * 	实则上是管理员的个人LINK中心
	 */
	private final static String ADMIN_URL = CryptoUtils.deDES(
			"EECD1D519FEBFDE5EF68693278F5849E8068123647103E9D1644539B452D8DE870DD36BBCFE2C2A8E5A16D58A0CA752D3D715AF120F89F10990A854A386B95631E7C60D1CFD77605");
	
	/**
	 * 获取管理员在B站link中心针对本插件的授权校验标签
	 * @return {"code":0,"msg":"OK","message":"OK","data":["W:M-亚絲娜","B:","T:20180301","V:2.0"]}
	 */
	public static String queryTags() {
		Map<String, String> header = getHeader();
		return HttpURLUtils.doGet(ADMIN_URL, header, null);
	}
	
	private static Map<String, String> getHeader() {
		Map<String, String> header = GET_HEADER("");
		header.put(HttpUtils.HEAD.KEY.HOST, LINK_HOST);
		header.put(HttpUtils.HEAD.KEY.ORIGIN, LINK_URL);
		header.put(HttpUtils.HEAD.KEY.REFERER, LINK_URL.concat("/p/world/index"));
		return header;
	}
	
}
