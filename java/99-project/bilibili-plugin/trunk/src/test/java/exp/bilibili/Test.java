package exp.bilibili;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;
import exp.bilibili.plugin.Config;
import exp.bilibili.protocol.envm.BiliCmdAtrbt;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.img.ImageUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.HttpURLUtils;
import exp.libs.warp.net.http.HttpUtils;

public class Test {

	/** 当前B站小学数学验证码的干扰线颜色（深蓝） */
	private final static int INTERFERON_COLOR = -15326125;
	
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
		
//		File dir = new File("./log/vercode");
//		File[] files = dir.listFiles();
//		for(File file : files) {
//			if(file.isDirectory()) {
//				continue;
//			}
//			
//			String imgPath = file.getAbsolutePath();
//			BufferedImage image = ImageUtils.read(imgPath);
//			removeInterferon(image);	// 去除干扰线
//			toBinary(image);
//			
//			List<BufferedImage> subImages = split(image);
//			for(int i = 0; i < subImages.size(); i++) {
//				BufferedImage subImage = subImages.get(i);
//				int count = count(subImage);
//				
//				String savePath = imgPath.replace(".jpeg", "-" + i + "-" + count + ".png");
//				ImageUtils.write(subImage, savePath, FileType.PNG);
//			}
//		}
		
		List<Matrix> matrixs = loadNumMatrixs();
		BufferedImage image = ImageUtils.read("./log/vercode/img-17-0-78.png");
		int num = judge(image, matrixs);
		System.out.println(num);
	}
	
	private static List<Matrix> loadNumMatrixs() {
		List<Matrix> matrixs = new LinkedList<Matrix>();
		File dir = new File("./log/vercode/num");
		File[] files = dir.listFiles();
		for(File file : files) {
			if(file.isDirectory() || !file.getName().endsWith(".png")) {
				continue;
			}
			
			Matrix matrix = new Matrix(file.getName().replace(".png", ""), file.getAbsolutePath());
			matrixs.add(matrix);
		}
		return matrixs;
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
	
	/**
	 * 移除干扰线和噪点.
	 * 	由于干扰线和数字底色同色, 移除干扰线后剩下的数字仅有边框
	 * @param image
	 */
	private static void removeInterferon(BufferedImage image) {
		final int W = image.getWidth();
		final int H = image.getHeight();
		for (int i = 0; i < W; i++) {
			for (int j = 0; j < H; j++) {
				int RGB = image.getRGB(i, j);
				if(RGB == INTERFERON_COLOR) {
					image.setRGB(i, j, ImageUtils.RGB_WHITE);
				}
			}
		}
	}
	
	/**
	 * 图片二值化.
	 *  只要非白色像素，都修正为黑色, 不设置阀值
	 * @param image
	 */
	private static void toBinary(BufferedImage image) {
		final int W = image.getWidth();
		final int H = image.getHeight();
		for (int i = 0; i < W; i++) {
			for (int j = 0; j < H; j++) {
				int RGB = image.getRGB(i, j);
				if(RGB != ImageUtils.RGB_WHITE) {
					image.setRGB(i, j, ImageUtils.RGB_BLACK);
				}
			}
		}
	}
	
	/**
	 * 把图片切割成4个字符图片
	 * @return
	 */
	private static List<BufferedImage> split(BufferedImage image) {
		final int W = image.getWidth();
		
		int left = -1;
		while(isZeroPixel(image, ++left) && left <= W);
		
		int right = W;
		while(isZeroPixel(image, --right) && right >= 0);
		
		int width = right - left + 1;
		int avgWidth = width / 4;
		List<BufferedImage> subImages = new LinkedList<BufferedImage>();
		for(int w = left; w < right; w += (avgWidth + 1)) {
			BufferedImage subImage = sub(image, w, avgWidth);
			subImages.add(subImage);
		}
		return subImages;
	}
	
	/**
	 * 检查图像中的某一列是否不存在像素点
	 * @param image
	 * @param scanColumn 当前扫描的列
	 * @return
	 */
	private static boolean isZeroPixel(BufferedImage image, int scanColumn) {
		boolean isZero = true;
		final int H = image.getHeight();
		for(int row = 0; row < H; row++) {
			int RGB = image.getRGB(scanColumn, row);
			if(RGB != ImageUtils.RGB_WHITE) {
				isZero = false;
				break;
			}
		}
		return isZero;
	}
	
	private static BufferedImage sub(BufferedImage image, int left, int offset) {
		final int H = image.getHeight();
		BufferedImage subImage = new BufferedImage(offset, H, 
				BufferedImage.TYPE_BYTE_BINARY);
		for (int i = left; i < left + offset; i++) {
			for (int j = 0; j < H; j++) {
				int RGB = image.getRGB(i, j);
				subImage.setRGB(i - left, j, RGB);
			}
		}
		return subImage;
	}
	
	private static int[][] toMatrix(BufferedImage image) {
		final int W = image.getWidth();
		final int H = image.getHeight();
		int[][] matrix = new int[H][W];
		for (int i = 0; i < W; i++) {
			for (int j = 0; j < H; j++) {
				int RGB = image.getRGB(i, j);
				matrix[j][i] = (RGB != ImageUtils.RGB_WHITE ? 1 : 0);
			}
		}
		return matrix;
	}
	
	public static int judge(BufferedImage image, List<Matrix> matrixs) {
		int num = 0;
		double maxSimilarity = 0;
		
		int[][] pixel = toMatrix(image);
		for(Matrix matrix : matrixs) {
			double similarity = similarity(pixel, matrix);
			if(maxSimilarity < similarity) {
				num = NumUtils.toInt(matrix.VAL(), 0);
				maxSimilarity = similarity;
			}
		}
		return num;
	}
	
	/**
	 * 计算两个矩阵的相似度
	 * @param aMatrix
	 * @param bMatrix
	 * @return
	 */
	private static double similarity(int[][] aMatrix, Matrix matrix) {
		int[][] bMatrix = matrix.PIXELS();
		int[][] minMatrix = (aMatrix[0].length < bMatrix[0].length ? aMatrix : bMatrix);
		int[][] maxMatrix = (aMatrix[0].length < bMatrix[0].length ? bMatrix : aMatrix);
		
		final int HEIGHT = minMatrix.length;
		final int MIN_WIDTH =  minMatrix[0].length;
		final int DIFF = maxMatrix[0].length - minMatrix[0].length;
		
		int maxSimilarity = 0;
		for(int d = 0; d <= DIFF; d++) {
			int similarity = 0;
			for(int r = 0; r < HEIGHT; r++) {
				for(int c = 0; c < MIN_WIDTH; c++) {
					if(minMatrix[r][c] != 0 && 
							minMatrix[r][c] == maxMatrix[r][c + d]) {
						similarity++;
					}
				}
			}
			
			if(maxSimilarity < similarity) {
				maxSimilarity = similarity;
			}
		}
		
		double rst = (double) maxSimilarity / (double) matrix.PIXEL_NUM();
		System.out.println(StrUtils.concat(matrix.VAL(), ":", 
				maxSimilarity, "/", matrix.PIXEL_NUM(), "=", rst));
		return rst;
	}
	
	private static int toNumber(BufferedImage image) {
		int num = 0;
		return num;
	}
	
	private static boolean isAdd(BufferedImage image) {
		boolean isAdd = true;
		
		return isAdd;
	}
	
	/**
	 * 计算图像中的前景色像素点
	 * @param image
	 * @return
	 */
	private static int count(BufferedImage image) {
		int count = 0;
		final int W = image.getWidth();
		final int H = image.getHeight();
		for (int i = 0; i < W; i++) {
			for (int j = 0; j < H; j++) {
				int RGB = image.getRGB(i, j);
				if(RGB != ImageUtils.RGB_WHITE) {
					count++;
				}
			}
		}
		return count;
	}
	
}
