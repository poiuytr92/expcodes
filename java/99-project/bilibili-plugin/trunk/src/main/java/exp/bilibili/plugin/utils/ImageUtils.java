package exp.bilibili.plugin.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibili.plugin.bean.ldm.ScanLine;
import exp.libs.envm.FileType;

/**
 * <PRE>
 * 图像处理工具
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class ImageUtils {

	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(ImageUtils.class);
	
	/** javax.imageio.ImageIO 的黑色RGB(应该是反码, 理论值应为0) */
	public final static int RGB_BLACK = -16777216;
	
	/** javax.imageio.ImageIO 的白色RGB值(应该是反码, 理论值应为16777215) */
	public final static int RGB_WHITE = -1;
	
	/** 私有化构造函数 */
	private ImageUtils() {}
	
	/**
	 * 读取图像
	 * @param imgPath
	 * @return
	 */
	public static BufferedImage read(String imgPath) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(imgPath));
		} catch (Exception e) {
			log.error("读取图片失败: {}", imgPath, e);
		}
		return image;
	}
	
	/**
	 * 保存图像
	 * @param image 
	 * @param savePath
	 * @param imageType
	 * @return
	 */
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
	 * 图像二值化
	 * @param image 原图
	 * @return 二值化图像
	 */
	public static BufferedImage toBinary(BufferedImage image) {
		image = (image == null ? 
				new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_BINARY) : image);
		final int W = image.getWidth();
		final int H = image.getHeight();
		
		BufferedImage binaryImage = image;
		try {

			// 把原图转换为二值化图像
			int whiteCnt = 0; // 白色像素计数器
			int blackCnt = 0; // 黑色像素极速器
			binaryImage = new BufferedImage(W, H,
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
			
//			toDenoise(binaryImage);	// 降噪： 8邻域降噪(小噪点) + 泛水填充法(连通域大噪点)
//			cleanInterferenceLine(binaryImage);	// 去除干扰线
			
		} catch (Exception e) {
			log.error("二值化图片失败", e);
		}
		return binaryImage;
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
		// TODO 暂未有好的算法
	}
	
	/**
	 * 图像切割
	 * @param image
	 * @param CHAR_NUM 图像中含有的字符数
	 * @return
	 */
	public static List<BufferedImage> split(BufferedImage image, final int CHAR_NUM) {
		List<BufferedImage> subImages = null;
		try {
			image = toBinary(image);	// 图像二值化（含消除噪点、干扰线操作）
			List<ScanLine> vScanLines = calculateVScanLines(image, CHAR_NUM);	// 计算切割边界
			if(vScanLines.size() == CHAR_NUM) {
				subImages = split(image, vScanLines);	// 图像切割
				
			} else {
				log.warn("图像切割错位: 期望区域 [{}] 实际区域 [{}]", CHAR_NUM, vScanLines.size());
			}
		} catch(Exception e) {
			log.error("图像切割失败", e);
		}
		return (subImages == null ? new LinkedList<BufferedImage>() : subImages);
	}
	
	/**
	 * 计算验证码图像中每个字符的垂直扫描线（左右边界）
	 * @param image
	 * @param CHAR_NUM 图像中含有的字符个数
	 * @return
	 */
	private static List<ScanLine> calculateVScanLines(BufferedImage image, final int CHAR_NUM) {
		int[] vPixels = _scanVerticalPixels(image);	// 每一垂直扫描线上的前景元素个数
		final int LR_MIN_PIXEL = 3;	// 最左/最右边界上最少的前景元素个数(小于这个数量认为是无效区)
		
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
				vScanLines.add(new ScanLine(left, i));
				
				while(vPixels[++i] == 0);	// 跳过无黏连的空白区域
				left = i--;		// 修正左边界
			}
		}
		if(left < right) {
			vScanLines.add(new ScanLine(left, right));
		}
		
		// 切割每个子区域的黏连字符
		while (vScanLines.size() < CHAR_NUM) {
			
			// 每次挑选宽度最大的区域进行切割
			int maxDist = 0;
			int maxIdx = 0;
			for(int i = 0; i < vScanLines.size(); i++) {
				ScanLine vScanLine = vScanLines.get(i);
				if(maxDist < vScanLine.getDist()) {
					maxDist = vScanLine.getDist();
					maxIdx = i;
				}
			}
			
			// 以平均字符宽度作为参考, 估计所选区域可切割的次数
			ScanLine vScanLine = vScanLines.get(maxIdx);
			int num = _divide(vScanLine.getDist(), CHAT_AVG_WIDTH);
			num = (num <= 1 ? 2 : num);	// 保证最大的区域被分隔为2部分
			
			int BGN = vScanLine.getBgn();
			int END = vScanLine.getEnd();
			int AVG = vScanLine.getDist() / num;
			int MID = BGN + AVG;
			vScanLine.setEnd(MID);
			
			for(int i = 1; i < num; i++) {
				BGN = MID + 1;
				MID = BGN + AVG;
				MID = (MID > END ? END : MID);
				ScanLine scanLine = new ScanLine(BGN, MID);
				vScanLines.add(maxIdx + i, scanLine);
			}
		}
		return vScanLines;
	}
	
	/**
	 * 扫描垂直方向的像素点
	 * @param image
	 * @return 每一垂直扫描线上的前景色像素个数
	 */
	private static int[] _scanVerticalPixels(BufferedImage image) {
		int[] vPixel = new int[image.getWidth()];
		for(int i = 0; i < image.getWidth(); i++) {
			int cnt = 0;
			for(int j = 0; j < image.getHeight(); j++) {
				cnt += (image.getRGB(i, j) == RGB_BLACK ? 1 : 0);
			}
			vPixel[i] = cnt;
		}
		return vPixel;
	}
	
	/**
	 * 四舍五入除法
	 * @param a
	 * @param b
	 * @return
	 */
	private static int _divide(int a, int b) {
		double rst = (double) a / (double) b;
		return (int) Math.round(rst);	// 四舍五入
	}
	
	/**
	 * 根据扫描线切割图像
	 * @param image
	 * @param vScanLines 垂直扫描线
	 * @return
	 */
	private static List<BufferedImage> split(BufferedImage image, List<ScanLine> vScanLines) {
		final int HEIGHT = image.getHeight();
		final int SUB_EDGE = 32;	// 存储切割子图的容器的宽高(32x32)
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
					BufferedImage.TYPE_3BYTE_BGR);
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
	
}
