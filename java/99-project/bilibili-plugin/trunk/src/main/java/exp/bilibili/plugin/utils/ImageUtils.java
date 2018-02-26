package exp.bilibili.plugin.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.FileType;

public class ImageUtils {

	private final static Logger log = LoggerFactory.getLogger(ImageUtils.class);
	
	/** javax.imageio.ImageIO 的黑色RGB(应该是反码, 理论值应为0) */
	private final static int RGB_BLACK = -16777216;
	
	/** javax.imageio.ImageIO 的白色RGB值(应该是反码, 理论值应为16777215) */
	private final static int RGB_WHITE = -1;
	
	/** 二值化图片格式 */
	private final static String PNG = "png";
	
	/** 私有化构造函数 */
	private ImageUtils() {}
	
	public static void main(String[] args) {
		String imgPath = "./tmp/9.jpg";
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
		for(int i = 0; i < W && fres.get(i) == 0; i++) {
			splitIdx[0] = i;
		}
		
		// 最后一条分割线位置
		for(int i = W - 1; i >= 0 && fres.get(i) == 0; i--) {
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
			write(subImage, "./tmp/9-" + i + ".jpg", FileType.PNG);
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
	
}
