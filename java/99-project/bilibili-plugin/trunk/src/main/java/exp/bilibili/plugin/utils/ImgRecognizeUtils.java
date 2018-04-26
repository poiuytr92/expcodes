package exp.bilibili.plugin.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import exp.bilibili.plugin.bean.ldm.Matrix;
import exp.libs.envm.FileType;
import exp.libs.utils.img.ImageUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.num.NumUtils;

/**
 * <PRE>
 * 图像识别工具(针对最新版的小学数学验证码)
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-04-26
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class ImgRecognizeUtils {
	
	/** 当前B站小学数学验证码的干扰线颜色（深蓝） */
	private final static int INTERFERON_COLOR = -15326125;
	
	/** 当前B站小学数学验证码的字符个数 */
	private final static int CHAR_NUM = 4;
	
	/** 前B站小学数学验证码的运算符位置索引(从0开始) */
	private final static int OP_IDX = 2;
	
	/** 0-9数字的的参照图像目录 */
	private final static String REFER_NUM_DIR = "./conf/vercode-refer/number";
	
	/** 0-9数字的的参照像素矩阵 */
	private final static List<Matrix> REFER_NUM_MATRIXS = new LinkedList<Matrix>();
	
	/** 运算符的的参照图像目录 */
	private final static String REFER_OP_DIR = "./conf/vercode-refer/operator";
	
	/** 运算的的参照像素矩阵 */
	private final static List<Matrix> REFER_OP_MATRIXS = new LinkedList<Matrix>();
	
	/** 私有化构造函数 */
	protected ImgRecognizeUtils() {}
	
	/** 预加载参照像素矩阵 */
	static {
		_loadReferMatrixs(REFER_NUM_DIR, REFER_NUM_MATRIXS);// 加载0-9数字的的参照像素矩阵
		_loadReferMatrixs(REFER_OP_DIR, REFER_OP_MATRIXS);	// 加载+-运算符的的参照像素矩阵
	}
	
	/**
	 * 加载参照图像并生成像素矩阵
	 * @param referDir 图像目录
	 * @param referMatrixs 存储像素矩阵的队列
	 */
	private static void _loadReferMatrixs(String referDir, List<Matrix> referMatrixs) {
		File dir = new File(referDir);
		File[] files = dir.listFiles();
		for(File file : files) {
			if(file.isDirectory() || 
					FileUtils.getFileType(file) != FileType.PNG) {
				continue;
			}
			
			String value = file.getName().replace(FileType.PNG.EXT, "");
			Matrix matrix = new Matrix(value, file.getAbsolutePath());
			referMatrixs.add(matrix);
		}
	}
	
	/**
	 * 从小学数学验证码的图片中析取表达式
	 * @param imgPath 小学数学验证码图片路径, 目前仅有 a+b 与 a-b 两种形式的验证码 (其中a为2位数, b为1位数)
	 * @return 数学表达式
	 */
	public static String analyseExpression(String imgPath) {
		BufferedImage image = ImageUtils.read(imgPath);
		removeInterferon(image);	// 去除干扰线
		BufferedImage binImage = ImageUtils.toBinary(image, true);
		
		StringBuilder expression = new StringBuilder();
		List<BufferedImage> subImages = split(binImage, CHAR_NUM);
		for(int i = 0; i < subImages.size(); i++) {
			BufferedImage subImage = subImages.get(i);
			if(OP_IDX == i) {
				expression.append(recognizeOperator(subImage));
			} else {
				expression.append(recognizeNumber(subImage));
			}
		}
		return expression.toString();
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
	 * 把图像切割为N等分
	 * @param image
	 * @param partNum 等分数
	 * @return
	 */
	private static List<BufferedImage> split(BufferedImage image, int partNum) {
		final int W = image.getWidth();
		
		int left = -1;
		while(_isZeroPixel(image, ++left) && left <= W);
		
		int right = W;
		while(_isZeroPixel(image, --right) && right >= 0);
		
		int width = right - left + 1;
		int avgWidth = width / partNum;
		List<BufferedImage> subImages = new LinkedList<BufferedImage>();
		for(int w = left; w < right; w += (avgWidth + 1)) {
			BufferedImage subImage = ImageUtils.cutVertical(image, w, avgWidth);
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
	private static boolean _isZeroPixel(BufferedImage image, int scanColumn) {
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
	
	/**
	 * 识别图像中的运算符
	 * @param image
	 * @return + 或 -
	 */
	private static String recognizeOperator(BufferedImage image) {
		return _compare(image, REFER_OP_MATRIXS, false);
	}
	
	/**
	 * 识别图像中的数字
	 * @param image 数字图像
	 * @return
	 */
	private static int recognizeNumber(BufferedImage image) {
		String value = _compare(image, REFER_NUM_MATRIXS, true);
		return NumUtils.toInt(value, 0);
	}
	
	/**
	 * 把图像与参照像素矩阵一一比对，找出相似度最高的一个
	 * @param image 待识别图像
	 * @param referMatrixs 参照像素矩阵
	 * @param ratio 使用重叠率作为相似度（反之使用重叠像素的个数作为相似度）
	 * @return 相似度最高的参照值
	 */
	private static String _compare(BufferedImage image, List<Matrix> referMatrixs, boolean ratio) {
		double maxSimilarity = 0;		// 最大相似度
		String similarityValue = "";	// 最大相似度对应的参照矩阵的值
		int[][] imgMatrix = ImageUtils.toBinaryMatrix(image);
		
		for(Matrix referMatrix : referMatrixs) {
			double similarity = _compare(imgMatrix, referMatrix, ratio);
			if(maxSimilarity < similarity) {
				maxSimilarity = similarity;
				similarityValue = referMatrix.VAL();
			}
		}
		return similarityValue;
	}
	
	/**
	 * 比较两个矩阵的相似度， 计算其相似度
	 * @param imgMatrix 图像矩阵
	 * @param referMatrix 参照矩阵
	 * @param ratio 使用重叠率作为相似度（反之使用重叠像素的个数作为相似度）
	 * @return 相似度
	 */
	private static double _compare(int[][] imgMatrix, Matrix referMatrix, boolean ratio) {
		int maxOverlap = _countOverlapPixel(imgMatrix, referMatrix.PIXELS());
		double similarity = (double) maxOverlap / (double) referMatrix.PIXEL_NUM();
		return (ratio ? similarity : maxOverlap);
	}
	
	/**
	 * 枚举AB两个像素矩阵的所有重叠区域, 
	 * 	找到重叠前景色像素点最多的一个区域, 返回该区域的重叠像素个数
	 * @param a 像素矩阵a
	 * @param b 像素矩阵b
	 * @return 最大的重叠前景色像素个数
	 */
	private static int _countOverlapPixel(int[][] a, int[][] b) {
		final int AH = a.length;	// 像素矩阵a的高度（行数）
		final int AW = a[0].length;	// 像素矩阵a的宽度（列数）
		final int BH = b.length;	// 像素矩阵b的高度（行数）
		final int BW = b[0].length;	// 像素矩阵b的宽度（列数）
		
		int maxOverlap = 0;
		if(AH <= BH && AW <= BW) {
			maxOverlap = _countNestOverlapPixel(a, b);
			
		} else if(AH >= BH && AW >= BW) {
			maxOverlap = _countNestOverlapPixel(b, a);
			
		} else if(AH >= BH && AW <= BW) {
			maxOverlap = _countCrossOverlapPixel(a, b);
			
		} else if(AH <= BH && AW >= BW) {
			maxOverlap = _countCrossOverlapPixel(b, a);
		}
		return maxOverlap;
	}
	
	/**
	 * 枚举AB两个像素矩阵的所有重叠区域, 
	 * 	找到重叠前景色像素点最多的一个区域, 返回该区域的重叠像素个数
	 * ----------------------------------------------
	 *  其中: a嵌套在b内部.
	 *      即 a.H<=b.H 且 a.W<=b.W  (H表示高度即行数, W表示宽度即列数)
	 * @param a 像素矩阵a
	 * @param b 像素矩阵b
	 * @return 最大的重叠前景色像素个数
	 */
	private static int _countNestOverlapPixel(int[][] a, int[][] b) {
		final int H = a.length;
		final int W = a[0].length;
		final int H_DIFF = b.length - a.length;
		final int W_DIFF = b[0].length - a[0].length;
		
		int maxOverlap = 0;
		for(int hDiff = 0; hDiff <= H_DIFF; hDiff++) {
			for(int wDiff = 0; wDiff <= W_DIFF; wDiff++) {
				
				int overlap = 0;
				for(int h = 0; h < H; h++) {
					for(int w = 0; w < W; w++) {
						if(a[h][w] != 0 && 
								a[h][w] == b[h + hDiff][w + wDiff]) {
							overlap++;
						}
					}
				}
				maxOverlap = (maxOverlap < overlap ? overlap : maxOverlap);
			}
		}
		return maxOverlap;
	}
	
	/**
	 * 枚举AB两个像素矩阵的所有重叠区域, 
	 * 	找到重叠前景色像素点最多的一个区域, 返回该区域的重叠像素个数
	 * ----------------------------------------------
	 *  其中: a与b相互交错.
	 *      即 a.H>=b.H 且 a.W<=b.W  (H表示高度即行数, W表示宽度即列数)
	 * @param a 像素矩阵a
	 * @param b 像素矩阵b
	 * @return 最大的重叠前景色像素个数
	 */
	private static int _countCrossOverlapPixel(int[][] a, int[][] b) {
		final int H = b.length;
		final int W = a[0].length;
		final int H_DIFF = a.length - b.length;
		final int W_DIFF = b[0].length - a[0].length;
		
		int maxOverlap = 0;
		for(int hDiff = 0; hDiff <= H_DIFF; hDiff++) {
			for(int wDiff = 0; wDiff <= W_DIFF; wDiff++) {
				
				int overlap = 0;
				for(int h = 0; h < H; h++) {
					for(int w = 0; w < W; w++) {
						if(a[h][w] != 0 && 
								a[h + hDiff][w] == b[h][w + wDiff]) {
							overlap++;
						}
					}
				}
				maxOverlap = (maxOverlap < overlap ? overlap : maxOverlap);
			}
		}
		return maxOverlap;
	}
	
}
