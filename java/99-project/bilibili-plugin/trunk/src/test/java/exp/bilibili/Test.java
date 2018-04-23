package exp.bilibili;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import exp.bilibili.plugin.Config;
import exp.bilibili.protocol.envm.BiliCmdAtrbt;
import exp.libs.envm.FileType;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.img.ImageUtils;
import exp.libs.warp.net.http.HttpURLUtils;
import exp.libs.warp.net.http.HttpUtils;

public class Test {

	/** 直播服务器主机 */
	protected final static String LIVE_HOST = Config.getInstn().LIVE_HOST();
	
	/** 直播首页 */
	private final static String LIVE_HOME = Config.getInstn().LIVE_HOME();
	
	/** 个人Link中心服务器主机 */
	protected final static String LINK_HOST = Config.getInstn().LINK_HOST();
	
	/** 个人Link中心首页 */
	protected final static String LINK_HOME = Config.getInstn().LINK_HOME();
	
	public static void main(String[] args) {
//		CookiesMgr.getInstn().load(CookieType.MAIN);
//		for(int i = 0; i < 100; i++) {
//			calculateAnswer(GET_HEADER(CookiesMgr.MAIN().toNVCookie(), "390480"), 
//					StrUtils.concat("img-", i));
//			ThreadUtils.tSleep(10);
//		}
		
		File dir = new File("./log/vercode/");
		File[] files = dir.listFiles();
		for(File file : files) {
			if(file.isDirectory()) {
				continue;
			}
			
			BufferedImage image = ImageUtils.read(file.getAbsolutePath());
			final int W = image.getWidth();
			final int H = image.getHeight();
			for (int i = 0; i < W; i++) {
				for (int j = 0; j < H; j++) {
					int RGB = image.getRGB(i, j);
					if(RGB == -15326125) {	// 干扰线
						image.setRGB(i, j, -1);
						
					}
//					else if(RGB > -7000000) { // 噪点
//						image.setRGB(i, j, -1);
//					}
				}
			}
			ImageUtils.write(image, file.getAbsolutePath().replace("jpeg", "png"), FileType.PNG);
		}
		
//		for (int j = 0; j < H; j++) {
//			for (int i = 0; i < W; i++) {
//				int RGB = image.getRGB(i, j);
//				if(RGB == -15326125) {	// 干扰线
//					RGB = -1;
//				}
//				RGB *= -1;
//				System.out.print(StrUtils.leftPad(String.valueOf(RGB), '0', 8) + ",");
//			}
//			System.out.println();
//		}
		
	}
	
	private static int calculateAnswer(Map<String, String> header, String name) {
		Map<String, String> request = new HashMap<String, String>();
		request.put("ts", String.valueOf(System.currentTimeMillis()));
		String response = HttpURLUtils.doGet(Config.getInstn().MATH_CODE_URL(), header, request);
		
		int answer = -1;
		try {
			JSONObject json = JSONObject.fromObject(response);
			JSONObject data = JsonUtils.getObject(json, BiliCmdAtrbt.data);
			String img = JsonUtils.getStr(data, BiliCmdAtrbt.img);
			String imgPath = HttpUtils.convertBase64Img(img, "./log/vercode", name);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return answer;
	}
	
	protected final static Map<String, String> GET_HEADER(String cookie, String uri) {
		Map<String, String> header = GET_HEADER(cookie);
		header.put(HttpUtils.HEAD.KEY.HOST, LIVE_HOST);
		header.put(HttpUtils.HEAD.KEY.ORIGIN, LIVE_HOME);
		header.put(HttpUtils.HEAD.KEY.REFERER, LIVE_HOME.concat(uri));
		return header;
	}
	
	protected final static Map<String, String> GET_HEADER(String cookie) {
		Map<String, String> header = new HashMap<String, String>();
		header.put(HttpUtils.HEAD.KEY.ACCEPT, "application/json, text/plain, */*");
		header.put(HttpUtils.HEAD.KEY.ACCEPT_ENCODING, "gzip, deflate, sdch");
		header.put(HttpUtils.HEAD.KEY.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8,en;q=0.6");
		header.put(HttpUtils.HEAD.KEY.CONNECTION, "keep-alive");
		header.put(HttpUtils.HEAD.KEY.COOKIE, cookie);
		header.put(HttpUtils.HEAD.KEY.USER_AGENT, HttpUtils.HEAD.VAL.USER_AGENT);
		return header;
	}
	
}
