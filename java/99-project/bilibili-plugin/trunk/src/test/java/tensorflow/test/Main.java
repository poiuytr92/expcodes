package tensorflow.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.bean.ldm.BiliCookie;
import exp.bilibili.plugin.utils.ImageUtils;
import exp.bilibili.protocol.envm.BiliCmdAtrbt;
import exp.libs.envm.FileType;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.num.IDUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.HttpURLUtils;
import exp.libs.warp.net.http.HttpUtils;

public class Main {

	/** 直播服务器主机 */
	protected final static String LIVE_HOST = Config.getInstn().LIVE_HOST();
	
	/** 直播首页 */
	private final static String LIVE_HOME = Config.getInstn().LIVE_HOME();
	
	/** 获取节奏风暴验证码URL */
	private final static String STORM_CODE_URL = Config.getInstn().STORM_CODE_URL();
	
	/** 图片缓存目录 */
	private final static String IMG_DIR = "./storm/";
	
	private final static int TOTAL = 112 * 32;
	
	/**
	 * 获取节奏风暴验证码图片并将其二值化
	 * @param args
	 */
	public static void main(String[] args) {
//		CookiesMgr.getInstn().load(CookieType.VEST);
//		BiliCookie cookie = CookiesMgr.VEST();
//		for(int i = 0; i < 76; i++) {
//			String imgPath = getStormCaptcha(cookie);
//			String binaryPath = toBinary(imgPath);
//			FileUtils.delete(imgPath);
//			
//			if(StrUtils.isEmpty(binaryPath)) {
//				i--;
//			}
//			ThreadUtils.tSleep(100);
//		}
		
		File dir = new File(IMG_DIR);
		File[] files = dir.listFiles();
		for(File file : files) {
			BufferedImage img = ImageUtils.read(file.getAbsolutePath());
			img = ImageUtils.toBinary(img);	// 单通道图像
			ImageUtils.write(img, file.getAbsolutePath(), FileType.PNG);
		}
		
		
	}
	
	private static String getStormCaptcha(BiliCookie cookie) {
		Map<String, String> header = GET_HEADER(cookie.toNVCookie(), "");
		Map<String, String> request = _getRequest();
		String response = HttpURLUtils.doGet(STORM_CODE_URL, header, request);
		
		String savePath = "";
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				JSONObject data = JsonUtils.getObject(json, BiliCmdAtrbt.data);
				String token = JsonUtils.getStr(data, BiliCmdAtrbt.token);
				String image = JsonUtils.getStr(data, BiliCmdAtrbt.image);
				savePath = HttpUtils.convertBase64Img(image, IMG_DIR, "storm");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return savePath;
	}
	
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
		header.put(HttpUtils.HEAD.KEY.USER_AGENT, HttpUtils.HEAD.VAL.USER_AGENT);
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
	 * 获取节奏风暴验证码参数
	 * @return
	 */
	private static Map<String, String> _getRequest() {
		Map<String, String> request = new HashMap<String, String>();
		request.put(BiliCmdAtrbt.underline, String.valueOf(System.currentTimeMillis()));
		request.put(BiliCmdAtrbt.width, "112");
		request.put(BiliCmdAtrbt.height, "32");
		return request;
	}
	
	private static String toBinary(String imgPath) {
		BufferedImage img = ImageUtils.read(imgPath);
		img = ImageUtils.toBinary(img);	// 单通道图像
		
		final int W = img.getWidth();
		final int H = img.getHeight();
		
		int blackCnt = 0;
		for(int w = 0; w < W; w++) {
			for(int h = 0; h < H; h++) {
				int RGB = img.getRGB(w, h);
				blackCnt += (RGB == ImageUtils.RGB_BLACK ? 1 : 0);
			}
		}
		
		// 总像素点为112*32, 在此范围外的黑色像素不是过少就是过多，不便于机器学习辨识
		String savePath = "";
		if(blackCnt >= 200 && blackCnt <= 800) {
			savePath = StrUtils.concat(IMG_DIR, IDUtils.getMillisID(), FileType.PNG.EXT);
			ImageUtils.write(img, savePath, FileType.PNG);
			
			System.out.println(savePath + ":" + blackCnt + "/" + TOTAL);
		}
		return savePath;
	}
	
}

