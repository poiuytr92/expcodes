package exp.bilibili.plugin.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.bean.ldm.BiliCookie;
import exp.bilibili.protocol.envm.BiliCmdAtrbt;
import exp.libs.envm.FileType;
import exp.libs.utils.format.JsonUtils;
import exp.libs.warp.net.http.HttpURLUtils;
import exp.libs.warp.net.http.HttpUtils;

public class ImageUtils {

	private final static Logger log = LoggerFactory.getLogger(ImageUtils.class);
	
	/** 直播服务器主机 */
	protected final static String LIVE_HOST = Config.getInstn().LIVE_HOST();
	
	/** 直播首页 */
	private final static String LIVE_HOME = Config.getInstn().LIVE_HOME();
	
	/** 获取节奏风暴验证码URL */
	private final static String STORM_CODE_URL = Config.getInstn().STORM_CODE_URL();
	
	/** javax.imageio.ImageIO 的黑色RGB(应该是反码, 理论值应为0) */
	private final static int RGB_BLACK = -16777216;
	
	/** javax.imageio.ImageIO 的白色RGB值(应该是反码, 理论值应为16777215) */
	private final static int RGB_WHITE = -1;
	
	/** 二值化图片格式 */
	private final static String PNG = "png";
	
	/** 私有化构造函数 */
	private ImageUtils() {}
	
	public static void main(String[] args) {
		final String imgDir = "./tmp/";
		File dir = new File(imgDir);
		File[] files = dir.listFiles();
		for(File file : files) {
			String imgName = file.getName().replaceFirst("\\.jp.*$", "");
			String imgType = file.getName().replaceFirst("[^\\.]*\\.", "");
			_toDo(imgDir, imgName, imgType);
		}
		
//		CookiesMgr.getInstn().load(CookieType.VEST);
////		getStormCaptcha(CookiesMgr.VEST());
//		
//		final String imgDir = "./storm/";
//		toDo(imgDir, "storm", "jpeg");
	}
	
	public static void _toDo(String imgDir, String imgName, String imgType) {
		String imgPath = imgDir + imgName + "." + imgType;
		imgPath = toBinary(imgPath);
		BufferedImage image = read(imgPath);
		final int W = image.getWidth();
		final int H = image.getHeight();
		
		List<Integer> fres = new ArrayList<Integer>(W);	// 每一列的黑色像素个数
		for(int i = 0; i < W; i++) {
			int cnt = 0;
			for(int j = 0; j < H; j++) {
				cnt += (image.getRGB(i, j) == RGB_BLACK ? 1 : 0);
			}
			fres.add(cnt);
			System.out.print(cnt + " ");
		}
		System.out.println();
		
		int offset = 0;	// 有效区域偏移值
		for(int i = 0; i < W && fres.get(i) < 2; i++) {	// <2 是扔掉干扰线
			offset = i;
		}
		
		// 最后一条分割线位置
		int endIdx = 0;
		for(int i = W - 1; i >= 0 && fres.get(i) < 2; i--) {	// <2 是扔掉干扰线
			endIdx = i;
		}
		
		// 5个字符的 左右边界
		int[][] spIdxs = new int[][] {
			{ offset, -1 }, 
			{ -1, -1 },  
			{ -1, -1 }, 
			{ -1, -1 }, 
			{ -1, endIdx }, 
		};
		
		int avgWidth = (endIdx - offset) / 5;	// 单个字符的平均宽度
		System.out.println("平均字宽：" + avgWidth);
		
		// 定位5组边界
		for(int i = offset; i <= endIdx; i++) {
			
		}
	}
	
	public static void toDo(String imgDir, String imgName, String imgType) {
		String imgPath = imgDir + imgName + "." + imgType;
		imgPath = toBinary(imgPath);
		BufferedImage image = read(imgPath);
		final int W = image.getWidth();
		final int H = image.getHeight();
		
		List<Integer> fres = new ArrayList<Integer>(W);	// 每一列的黑色像素个数
		for(int i = 0; i < W; i++) {
			int cnt = 0;
			for(int j = 0; j < H; j++) {
				cnt += (image.getRGB(i, j) == RGB_BLACK ? 1 : 0);
			}
			fres.add(cnt);
			System.out.print(cnt + " ");
		}
		System.out.println();
		
		int[] splitIdx = { 0, -1, -1, -1, -1, (W - 1) };	// 6条分割线的位置
		
		// 第一条分割线位置
		for(int i = 0; i < W && fres.get(i) < 2; i++) {	// <2 是扔掉干扰线
			splitIdx[0] = i;
		}
		
		// 最后一条分割线位置
		for(int i = W - 1; i >= 0 && fres.get(i) < 2; i--) {	// <2 是扔掉干扰线
			splitIdx[5] = i;
		}
		
		int avgWidth = (splitIdx[5] - splitIdx[0]) / 5;	// 单个字符的平均宽度
		System.out.println("平均字宽：" + avgWidth);
		
		// 中间分割线位置
		for(int i = splitIdx[0] + 1; i < splitIdx[5]; i++) {
			if(fres.get(i) == 0) {
				int diff = (i - splitIdx[0]) / avgWidth;
				diff = (diff == 0 ? 1 : diff);
				splitIdx[diff] = i;
			}
		}
		
		// 处理黏连字符
		for(int i = 0; i < splitIdx.length; i++) {
			if(splitIdx[i] < 0) {
				int bgnIdx = splitIdx[i - 1];
				int cnt = 1;	// 此区域黏连的字符数
				int endIdx = bgnIdx;
				do {
					endIdx = splitIdx[i + (cnt++)];
				} while(endIdx < 0);
				
				// 模糊均分区域内黏连字符
				int avg = (endIdx - bgnIdx) / cnt;
				for(int j = 0; j < cnt - 1; j++) {
					splitIdx[i + j] = splitIdx[i + j - 1] + avg;
				}
			}
			System.out.println("第" + i + "条分割线：" + splitIdx[i]);
		}
		
		// 图像分割
		for(int i = 0; i < splitIdx.length - 1; i++) {
			int bgnIdx = splitIdx[i];
			int endIdx = splitIdx[i + 1];
			
			int weight = endIdx - bgnIdx;
			BufferedImage subImage = new BufferedImage(weight, H,
					BufferedImage.TYPE_BYTE_BINARY);
			
			for(int w = 0; w < weight; w++) {
				for(int h = 0; h < H; h++) {
					int rgb = image.getRGB((w + bgnIdx), h);
					subImage.setRGB(w, h, rgb);
				}
			}
			
			String subPath = imgDir + imgName + "-" + i + ".png";
			write(subImage, subPath, FileType.PNG);
		}
	}
	
	public static BufferedImage read(String imgPath) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(imgPath));
		} catch (Exception e) {
			log.error("读取图片失败: {}", imgPath, e);
		}
		return image;
	}
	
	public static boolean write(BufferedImage image, String savePath, FileType imageType) {
		boolean isOk = false;
		try {
			isOk = ImageIO.write(image, imageType.NAME, new File(savePath));
		} catch (Exception e) {
			log.error("保存图片失败: {}", savePath, e);
		}
		return isOk;
	}
	
	/**
	 * 图片二值化
	 * @param imgPath 原图路径
	 * @return 二值化图片路径(若失败返回原图路径)
	 */
	public static String toBinary(String imgPath) {
		String savePath = imgPath;
		try {
			// 读取原图
			File srcFile = new File(imgPath);
			BufferedImage image = ImageIO.read(srcFile);
			final int W = image.getWidth();
			final int H = image.getHeight();

			// 把原图转换为二值化图像
			int whiteCnt = 0; // 白色像素计数器
			int blackCnt = 0; // 黑色像素极速器
			BufferedImage binaryImage = new BufferedImage(W, H,
					BufferedImage.TYPE_BYTE_BINARY); // 可选择模式: 二值化/灰度化
			for (int i = 0; i < W; i++) {
				for (int j = 0; j < H; j++) {
					int RGB = image.getRGB(i, j);
					binaryImage.setRGB(i, j, RGB); // 根据图像模式, RGB会自动转换为黑/白

					RGB = binaryImage.getRGB(i, j);
					if (RGB == RGB_WHITE) {
						whiteCnt++;
					} else {
						blackCnt++;
					}
				}
			}

			// 默认白色为背景色, 黑色为前景色, 当背景色像素小于前景色时， 则对图像取反
			if (whiteCnt < blackCnt) {
				for (int i = 0; i < W; i++) {
					for (int j = 0; j < H; j++) {
						int RGB = binaryImage.getRGB(i, j);
						if (RGB == RGB_WHITE) {
							binaryImage.setRGB(i, j, RGB_BLACK);
						} else {
							binaryImage.setRGB(i, j, RGB_WHITE);
						}
					}
				}
			}
			
			
			toDenoise(binaryImage);	// 降噪： 8邻域降噪(小噪点) + 泛水填充法(连通域大噪点)
			cleanInterferenceLine(binaryImage);	// 去除干扰线

			// 保存二值化图像
			savePath = savePath.replaceFirst("\\.\\w+$", ".".concat(PNG));
			File snkFile = new File(savePath);
			ImageIO.write(binaryImage, PNG, snkFile);	// 为保证无色差, 只能为BMP/PNG格式
			
		} catch (Exception e) {
			log.error("二值化图片失败: {}", imgPath, e);
		}
		return savePath;
	}
	
	/**
	 * 降噪： 8邻域降噪(小噪点) + 泛水填充法(连通域大噪点)
	 * @param binaryImage 二值化图像
	 * @return
	 */
	private static void toDenoise(BufferedImage binaryImage) {
		final int W = binaryImage.getWidth();
		final int H = binaryImage.getHeight();
		
		_8Neighbourhood(binaryImage, W, H);
//		_FloodFill(binaryImage, W, H);
	}
	
	/**
	 * 8邻域降噪 (适用于去除小噪点：离散像素点)
	 * @param binaryImage 二值化图像
	 * @param W
	 * @param H
	 */
	private static void _8Neighbourhood(BufferedImage binaryImage, final int W, final int H) {
		final int LIMIT_PIXEL = 8;	// 所检查的像素点的相邻像素中, 存在无效像素的上限(越限则认为所检查像素是噪点)
		for (int i = 0; i < W; i++) {
			for (int j = 0; j < H; j++) {
				
				// 边沿区域必是噪点
				if(i == 0 || i == W - 1 || j == 0 || j == H - 1) {
					binaryImage.setRGB(i, j, RGB_WHITE);
					continue;
				}
				
				// 无效区域不处理
				if(binaryImage.getRGB(i, j) == RGB_WHITE) {
					continue;
				}
				
				int cnt = 0;
				cnt += (binaryImage.getRGB(i - 1, j - 1) == RGB_WHITE ? 1 : 0);
				cnt += (binaryImage.getRGB(i - 1, j) == RGB_WHITE ? 1 : 0);
				cnt += (binaryImage.getRGB(i - 1, j + 1) == RGB_WHITE ? 1 : 0);
				cnt += (binaryImage.getRGB(i, j - 1) == RGB_WHITE ? 1 : 0);
				cnt += (binaryImage.getRGB(i, j + 1) == RGB_WHITE ? 1 : 0);
				cnt += (binaryImage.getRGB(i + 1, j - 1) == RGB_WHITE ? 1 : 0);
				cnt += (binaryImage.getRGB(i + 1, j) == RGB_WHITE ? 1 : 0);
				cnt += (binaryImage.getRGB(i + 1, j + 1) == RGB_WHITE ? 1 : 0);
				
				// 相邻像素点为无效像素的数量越限, 将当前像素点设为无效点
				if(cnt >= LIMIT_PIXEL) {
					binaryImage.setRGB(i, j, RGB_WHITE);
				}
			}
		}
	}
	
	/**
	 * 泛水填充法 (适用于去除大噪点：连通像素点)
	 * @param binaryImage
	 * @param W
	 * @param H
	 */
	private static void _FloodFill(BufferedImage binaryImage, final int W, final int H) {
		final int LIMIT_AREA = 4;	// 连通区域<=4的区域认为是噪点
		
		int color = 1;	// 泛水填充颜色
		
		for (int i = 0; i < W; i++) {
			for (int j = 0; j < H; j++) {
				int RGB = binaryImage.getRGB(i, j);
				if(RGB == RGB_BLACK) {
					
				}
			}
		}
	}
	
	/**
	 * 去除干扰线
	 * @param binaryImage
	 */
	private static void cleanInterferenceLine(BufferedImage binaryImage) {
		final int W = binaryImage.getWidth();
		final int H = binaryImage.getHeight();
		
	}
	
	/**
	 * 解析节奏风暴验证码图片
	 * {"code":0,"msg":"","message":"","data":{"token":"aa4f1a6dad33c3b16926a70e9e0eadbfb56ba91c","image":"data:image/jpeg;base64,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"}}
	 * @param cookie
	 * @return { 验证码token, 验证码图片的解析值 }
	 */
	private static String[] getStormCaptcha(BiliCookie cookie) {
		Map<String, String> header = GET_HEADER(cookie.toNVCookie(), "");
		Map<String, String> request = _getRequest();
		String response = HttpURLUtils.doGet(STORM_CODE_URL, header, request);
		
		String[] rst = { "", "" };
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				JSONObject data = JsonUtils.getObject(json, BiliCmdAtrbt.data);
				String token = JsonUtils.getStr(data, BiliCmdAtrbt.token);
				String image = JsonUtils.getStr(data, BiliCmdAtrbt.image);
				String savePath = HttpUtils.convertBase64Img(image, "./storm/", "storm");
				
				rst[0] = token;
				rst[1] = VercodeUtils.recognizeStormImage(savePath);
			}
		} catch(Exception e) {
			log.error("获取节奏风暴验证码图片异常: {}", response, e);
		}
		return rst;
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
	
}
