package exp.bilibili.plugin.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.bean.ldm.BiliCookie;
import exp.bilibili.plugin.bean.ldm.ScanLine;
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
			toDo(imgDir, imgName, imgType);
		}
		
//		CookiesMgr.getInstn().load(CookieType.VEST);
////		getStormCaptcha(CookiesMgr.VEST());
//		
//		final String imgDir = "./storm/";
//		toDo(imgDir, "storm", "jpeg");
	}
	
	public static void toDo(String imgDir, String imgName, String imgType) {
		String imgPath = imgDir + imgName + "." + imgType;
		imgPath = toBinary(imgPath);
		BufferedImage image = read(imgPath);
		List<ScanLine> vScanLines = getVScanLines(image);
		
		// 图像分割
		List<BufferedImage> subImages = split(image, vScanLines);
		for(int i = 0; i < subImages.size(); i++) {
			BufferedImage subImage = subImages.get(i);
			String subPath = imgDir + imgName + "-" + i + ".png";
			write(subImage, subPath, FileType.PNG);
		}
	}
	
	/**
	 * 获取验证码图像中每个字符的垂直扫描线（左右边界）
	 * @param image
	 * @return
	 */
	private static List<ScanLine> getVScanLines(BufferedImage image) {
		int[] vPixels = scanVerticalPixels(image);	// 每一垂直扫描线上的前景元素个数
		final int LR_MIN_PIXEL = 3;	// 最左/最右边界上最少的前景元素个数(小于这个数量认为是无效区)
		final int CHAR_NUM = 5;		// 有效区域内的字符个数
		
		// 有效图像区域的最左边界
		int left = 0;
		while(left < vPixels.length && vPixels[left] < LR_MIN_PIXEL) {
			left++;
		}
		
		// 有效图像区域的最右边界
		int right = vPixels.length - 1;
		while(right > left && vPixels[right] < LR_MIN_PIXEL) {
			right--;
		}
		
		// 单个字符的平均宽度
		final int CHAT_AVG_WIDTH = (right - left) / CHAR_NUM;
		
		// 通过有效图像区域内没有黏连位置，初步切割图像为多个子区域（每个区域可能含有1个以上的字符）
		List<ScanLine> vScanLines = new LinkedList<ScanLine>();
		for(int i = left; i < right; i++) {
			if(vPixels[i] == 0) {	// 无
				vScanLines.add(new ScanLine(left, i - 1));
				
				while(vPixels[++i] == 0);	// 跳过无黏连的空白区域
				left = i--;		// 修正左边界
			}
		}
		vScanLines.add(new ScanLine(left, right));
		
		// 切割每个子区域的黏连字符 FIXME 个数不好控制, 最终必须为5个
		while (vScanLines.size() < CHAR_NUM) {
			int maxDist = 0;
			int maxIdx = 0;
			for(int i = 0; i < vScanLines.size(); i++) {
				ScanLine vScanLine = vScanLines.get(i);
				if(maxDist < vScanLine.getDist()) {
					maxDist = vScanLine.getDist();
					maxIdx = i;
				}
			}
			
			ScanLine vScanLine = vScanLines.get(maxIdx);
			int cnt = vScanLine.getDist() / CHAT_AVG_WIDTH + 1;
			cnt = (cnt <= 1 ? 1 : cnt);
			
			final int BGN = vScanLine.getBgn();
			final int END = vScanLine.getEnd();
			final int AVG = vScanLine.getDist() / cnt;
			vScanLine.setEnd(BGN + AVG);
			
			for(int i = 1; i < cnt; i++) {
				ScanLine scanLine = new ScanLine(BGN + AVG * i, END);
				if(i < cnt - 1) {
					scanLine.setEnd(scanLine.getBgn() + AVG);
				}
				vScanLines.add(maxIdx + i, scanLine);
			}
		}
		
		System.out.println(vScanLines.size());
		for(ScanLine vScanLine : vScanLines) {
			System.out.println(vScanLine.getBgn() + ", " + vScanLine.getEnd());
		}
		return vScanLines;
	}
	
	/**
	 * 扫描垂直方向的像素点
	 * @param image
	 * @return 每一垂直扫描线上的前景色像素个数
	 */
	private static int[] scanVerticalPixels(BufferedImage image) {
		int[] vPixel = new int[image.getWidth()];
		for(int i = 0; i < image.getWidth(); i++) {
			int cnt = 0;
			for(int j = 0; j < image.getHeight(); j++) {
				cnt += (image.getRGB(i, j) == RGB_BLACK ? 1 : 0);
			}
			vPixel[i] = cnt;
			System.out.print(cnt + " ");
		}
		System.out.println();
		return vPixel;
	}
	
	/**
	 * 根据扫描线切割图像
	 * @param image
	 * @param vScanLines 垂直扫描线
	 * @return
	 */
	private static List<BufferedImage> split(BufferedImage image, List<ScanLine> vScanLines) {
		final int HEIGHT = image.getHeight();
		final int SUB_EDGE = 32;
		List<BufferedImage> subImages = new LinkedList<BufferedImage>();
		
		for(ScanLine vScanLine : vScanLines) {
			int width = vScanLine.getEnd() - vScanLine.getBgn();
			BufferedImage area = new BufferedImage(width, HEIGHT,
					BufferedImage.TYPE_BYTE_BINARY);
			
			// 根据原图的垂直扫描线的左右边界截图子图，并确定水平扫描线的上下边界, 得到有效图像区域
			ScanLine hScanLine = new ScanLine(0, HEIGHT);
			for(int h = 0; h < HEIGHT; h++) {
				int cnt = 0;	// 当前行像素个数
				
				for(int w = 0; w < width; w++) {
					int wOffset = w + vScanLine.getBgn();
					int RGB = image.getRGB(wOffset, h);
					area.setRGB(w, h, RGB);
					cnt += (RGB == RGB_BLACK ? 1 : 0); 
				}
				
				// 设置水平扫描线边界
				if(cnt > 0) {
					if(hScanLine.getBgn() <= 0) {
						hScanLine.setBgn(h);
					} else {
						hScanLine.setEnd(h);
					}
				}
			}
			
			// 把有效图像区域放入32x32的子图正中心
			int height = hScanLine.getDist();
			int wOffset = (SUB_EDGE - width) / 2;
			int hOffset = (SUB_EDGE - height) / 2;
			BufferedImage subImage = new BufferedImage(SUB_EDGE, SUB_EDGE,
					BufferedImage.TYPE_BYTE_BINARY);
			for(int w = 0; w < SUB_EDGE; w++) {
				for(int h = 0; h < SUB_EDGE; h++) {
					subImage.setRGB(w, h, RGB_WHITE);
				}
			}
			for(int w = 0; w < width; w++) {
				for(int h = hScanLine.getBgn(); h < hScanLine.getEnd(); h++) {
					int RGB = area.getRGB(w, h);
					subImage.setRGB((w + wOffset), (h - hScanLine.getBgn() + hOffset), RGB);
				}
			}
			subImages.add(subImage);
		}
		return subImages;
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
