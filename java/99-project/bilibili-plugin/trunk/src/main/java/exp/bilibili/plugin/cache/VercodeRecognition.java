package exp.bilibili.plugin.cache;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibili.plugin.bean.ldm.Matrix;
import exp.libs.envm.FileType;
import exp.libs.utils.img.ImageUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.num.RandomUtils;

/**
 * <PRE>
 * B站小学数学验证码识别器
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2018-04-26
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class VercodeRecognition {
	
	/** 日志�? */
	private final static Logger log = LoggerFactory.getLogger(VercodeRecognition.class);
	
	/** 当前B站小学数学验证码的干扰线颜色（深蓝） */
	private final static int INTERFERON_COLOR = -15326125;
	
	/** 当前B站小学数学验证码的字符个�? */
	private final static int CHAR_NUM = 4;
	
	/**
	 * 置信识别�?.
	 * 	当一个字符的识别率高�?25%时， 认为是这次识别是准确�?.
	 */
	private final static double CREDIBLE_RADIO = 0.25;
	
	/** 0-9数字的的参照图像目录 */
	private final static String REFER_NUM_DIR = "./conf/vercode-refer/number";
	
	/** 运算符的的参照图像目�? */
	private final static String REFER_OP_DIR = "./conf/vercode-refer/operator";
	
	/** 0-9数字的的参照像素矩阵 */
	private List<Matrix> REFER_NUM_MATRIX;
	
	/** 运算的的参照像素矩阵 */
	private List<Matrix> REFER_OP_MATRIXS;

	/** 单例 */
	private static volatile VercodeRecognition instance;
	
	/** 私有化构造函�? */
	private VercodeRecognition() {
		this.REFER_NUM_MATRIX = new LinkedList<Matrix>();
		this.REFER_OP_MATRIXS = new LinkedList<Matrix>();
		
		_loadReferMatrixs(REFER_NUM_DIR, REFER_NUM_MATRIX);	// 加载0-9数字的的参照像素矩阵
		_loadReferMatrixs(REFER_OP_DIR, REFER_OP_MATRIXS);	// 加载+-运算符的的参照像素矩�?
	}
	
	/**
	 * 获取单例
	 * @return 单例
	 */
	public static VercodeRecognition getInstn() {
		if(instance == null) {
			synchronized (VercodeRecognition.class) {
				if(instance == null) {
					instance = new VercodeRecognition();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 加载参照图像并生成像素矩�?
	 * @param referDir 图像目录
	 * @param referMatrixs 存储像素矩阵的队�?
	 */
	private void _loadReferMatrixs(String referDir, List<Matrix> referMatrixs) {
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
	 * 从小学数学验证码的图片中析取表达�?.
	 * ------------------------------------
	 *   验证码表达式的特�?:
	 *    1. 仅有 a+b �? a-b 两种形式的验证码 (其中a�?2位数, b�?1位数)
	 *    2. a的取值范围是 [10, 99]
	 *    3. b的取值范围是 [1, 9]
	 *    4. 验证码结果的取值范围是 [1, 108]
	 * 
	 * @param imgPath 小学数学验证码图片路�?
	 * @return 数学表达�?
	 */
	public String analyse(String imgPath) {
		String expression = "";
		try {
			expression = _analyse(imgPath);
			
		} catch(Exception e) {
			log.error("解析小学数学验证码图片失�?: {}", imgPath, e);
		}
		return expression;
	}
	
	/**
	 * 从小学数学验证码的图片中析取表达�?.
	 * ------------------------------------
	 *   验证码表达式的特�?:
	 *    1. 仅有 a+b �? a-b 两种形式的验证码 (其中a�?2位数, b�?1位数)
	 *    2. a的取值范围是 [10, 99]
	 *    3. b的取值范围是 [1, 9]
	 *    4. 验证码结果的取值范围是 [1, 108]
	 * 
	 * @param imgPath 小学数学验证码图片路�?
	 * @return 数学表达�?
	 */
	private String _analyse(String imgPath) {
		BufferedImage image = ImageUtils.read(imgPath);
		removeInterferon(image);	// 去除干扰�?
		BufferedImage binImage = ImageUtils.toBinary(image, true);
		
		StringBuilder expression = new StringBuilder();
		List<BufferedImage> subImages = split(binImage, CHAR_NUM);
		
		// 当前小学数学验证码数字，由于字体原因不是等宽�?, 除了数字1之外, 其他数字的标准宽度是15
		// 而在当前的识别算法下, 切割验证码后�?4个子图宽度必定是相同�?
		// 但是由于数字 1 的存�?, 且图片存在旋转干�?, 使得子图的宽度不是准确的15, 而是平均宽度�? [10, 15] 之间
		// 可以确定的是�? 若平均宽�? <= 11�? 则至少有一�?1;  若平均宽�? <= 10�? 则至少有两个1
		// 但是无法确定1的位�?, 且数�?1可以还可能作�? 0�?4�?6�?7(旋转�?)�?8�?9 的一部分导致识别率严重降�?
		// 因此对于存在数字1的验证码, 干脆直接不识�?, 而判断是否存在数�?1的依据，就是子图宽度 <= 11
		final int AVG_WIDTH = subImages.get(0).getWidth();
		if(AVG_WIDTH <= 11) {	// 若存在数�?1, 则不识别
			subImages.clear();
		}
		
		for(int i = 0; i < subImages.size(); i++) {
			BufferedImage subImage = subImages.get(i);
			
			// 验证码的第三位为符号�?
			if(i == 2) {
				expression.append(recognizeOperator(subImage));
				
			// 验证码的第二位为数字, 取值范�? [0, 9]
			} else if(i == 1){
				expression.append(recognizeNumber(subImage, "1"));
				
			// 验证码的第一、四位为数字, 取值范�? [1, 9]
			} else {
				expression.append(recognizeNumber(subImage, "0", "1"));
			}
		}
		return expression.toString();
	}
	
	/**
	 * 移除干扰线和噪点.
	 * 	由于干扰线和数字底色同色, 移除干扰线后剩下的数字仅有边�?
	 * @param image
	 */
	private void removeInterferon(BufferedImage image) {
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
	 * @param partNum 等分�?
	 * @return
	 */
	private List<BufferedImage> split(BufferedImage image, int partNum) {
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
	 * 检查图像中的某一列是否不存在像素�?
	 * @param image
	 * @param scanColumn 当前扫描的列
	 * @return
	 */
	private boolean _isZeroPixel(BufferedImage image, int scanColumn) {
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
	 * 识别图像中的运算�?
	 * @param image
	 * @return + �? -
	 */
	private String recognizeOperator(BufferedImage image) {
		return _compare(image, REFER_OP_MATRIXS, false, null);
	}
	
	/**
	 * 识别图像中的数字
	 * @param image 数字图像
	 * @param exclueNumbers 排除数�?
	 * @return
	 */
	private int recognizeNumber(BufferedImage image, String... exclueNumbers) {
		Set<String> exclusions = new HashSet<String>();
		if(exclueNumbers != null) {
			for(String exclueNumber : exclueNumbers) {
				exclusions.add(exclueNumber);
			}
		}
		
		String value = _compare(image, REFER_NUM_MATRIX, true, exclusions);
		return NumUtils.toInt(value, 0);
	}
	
	/**
	 * 把图像与参照像素矩阵一一比对，找出相似度最高的一�?
	 * @param image 待识别图�?
	 * @param referMatrixs 参照像素矩阵
	 * @param ratio 使用重叠率作为相似度（反之使用重叠像素的个数作为相似度）
	 * @param exclusions 排除�?
	 * @return 相似度最高的参照�?
	 */
	private String _compare(BufferedImage image, List<Matrix> referMatrixs, 
			boolean ratio, Set<String> exclusions) {
		double _1stSimilarity = 0;	// 最大相似度
		double _2ndSimilarity = 0;	// 次大相似�?
		String _1stValue = "";		// 最大相似度对应的参照矩阵的�?(首选�?)
		String _2ndValue = "";		// 次大相似度对应的参照矩阵的�?(被选�?)
		int[][] imgMatrix = ImageUtils.toBinaryMatrix(image);
		
		for(Matrix referMatrix : referMatrixs) {
			if(exclusions != null && exclusions.contains(referMatrix.VAL())) {
				continue;
			}
			
			double similarity = _compare(imgMatrix, referMatrix, ratio);
			if(_1stSimilarity < similarity) {
				_2ndSimilarity = _1stSimilarity;
				_2ndValue = _1stValue;
				
				_1stSimilarity = similarity;
				_1stValue = referMatrix.VAL();
				
			} else if(_2ndSimilarity < similarity) {
				_2ndSimilarity = similarity;
				_2ndValue = referMatrix.VAL();
			}
		}
		
		// 当一个字符的识别率高�?25%时， 认为是这次识别是准确�?.
		// 否则从识别率最高和次高的两个参照值中随机选择一�?
		String value = _1stValue;
		if(ratio && _1stSimilarity < CREDIBLE_RADIO) {
			value = RandomUtils.genBoolean() ? _1stValue : _2ndValue;
		}
		return value;
	}
	
	/**
	 * 比较两个矩阵的相似度�? 计算其相似度
	 * @param imgMatrix 图像矩阵
	 * @param referMatrix 参照矩阵
	 * @param ratio 使用重叠率作为相似度（反之使用重叠像素的个数作为相似度）
	 * @return 相似�?
	 */
	private double _compare(int[][] imgMatrix, Matrix referMatrix, boolean ratio) {
		int maxOverlap = _countOverlapPixel(imgMatrix, referMatrix.PIXELS());
		double similarity = (double) maxOverlap / (double) referMatrix.PIXEL_NUM();
		return (ratio ? similarity : maxOverlap);
	}
	
	/**
	 * 枚举AB两个像素矩阵的所有重叠区�?, 
	 * 	找到重叠前景色像素点最多的一个区�?, 返回该区域的重叠像素个数
	 * @param a 像素矩阵a
	 * @param b 像素矩阵b
	 * @return 最大的重叠前景色像素个�?
	 */
	private int _countOverlapPixel(int[][] a, int[][] b) {
		final int AH = a.length;	// 像素矩阵a的高度（行数�?
		final int AW = a[0].length;	// 像素矩阵a的宽度（列数�?
		final int BH = b.length;	// 像素矩阵b的高度（行数�?
		final int BW = b[0].length;	// 像素矩阵b的宽度（列数�?
		
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
	 * 枚举AB两个像素矩阵的所有重叠区�?, 
	 * 	找到重叠前景色像素点最多的一个区�?, 返回该区域的重叠像素个数
	 * ----------------------------------------------
	 *  其中: a嵌套在b内部.
	 *      �? a.H<=b.H �? a.W<=b.W  (H表示高度即行�?, W表示宽度即列�?)
	 * @param a 像素矩阵a
	 * @param b 像素矩阵b
	 * @return 最大的重叠前景色像素个�?
	 */
	private int _countNestOverlapPixel(int[][] a, int[][] b) {
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
	 * 枚举AB两个像素矩阵的所有重叠区�?, 
	 * 	找到重叠前景色像素点最多的一个区�?, 返回该区域的重叠像素个数
	 * ----------------------------------------------
	 *  其中: a与b相互交错.
	 *      �? a.H>=b.H �? a.W<=b.W  (H表示高度即行�?, W表示宽度即列�?)
	 * @param a 像素矩阵a
	 * @param b 像素矩阵b
	 * @return 最大的重叠前景色像素个�?
	 */
	private int _countCrossOverlapPixel(int[][] a, int[][] b) {
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
