package exp.bilibili.plugin.utils;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * 图像二值化(背景色为白色，前景色为黑色)
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
		} catch (Exception e) {
			log.error("二值化图片失败", e);
		}
		return binaryImage;
	}
	
}
