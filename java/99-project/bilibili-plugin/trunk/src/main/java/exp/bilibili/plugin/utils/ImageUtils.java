package exp.bilibili.plugin.utils;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageUtils {

	private final static Logger log = LoggerFactory.getLogger(ImageUtils.class);
	
	/** javax.imageio.ImageIO 的黑色RGB(应该是反码, 理论值应为0) */
	private final static int RGB_BLACK = -16777216;
	
	/** javax.imageio.ImageIO 的白色RGB值(应该是反码, 理论值应为16777215) */
	private final static int RGB_WHITE = -1;
	
	/** 二值化图片格式 */
	private final static String PNG = "png";
	
	private ImageUtils() {}
	
	public static void main(String[] args) {
		File dir = new File("./log/节奏风暴验证码");
		File[] imgs = dir.listFiles();
		for(File img : imgs) {
			String bmpPath = toBinary(img.getPath());
			System.out.println(bmpPath);
		}
		
//		String token = "aa4f1a6dad33c3b16926a70e9e0eadbfb56ba91c";
//		String dataUrl = "data:image/jpeg;base64,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";
//		HttpUtils.convertBase64Img(dataUrl, "./log/节奏风暴验证码", "img");
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
