package exp.bilibili.plugin.bean.ldm;
import java.awt.image.BufferedImage;

import exp.libs.utils.img.ImageUtils;

/**
 * <PRE>
 * 像素矩阵
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2018-04-26
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Matrix {

	/** 像素矩阵所呈现的图片�? */
	private String value;
	
	/** 像素矩阵(背景�?0, 前景�?1) */
	private int[][] pixels;
	
	/** 前景像素的个�? */
	private int pixelNum;
	
	/**
	 * 构造函�?
	 * @param imagePath 图片路径
	 */
	public Matrix(String value, String imagePath) {
		this.value = value;
		init(imagePath);
	}
	
	/**
	 * 初始化像素矩�?
	 * @param imagePath 图片路径
	 */
	private void init(String imagePath) {
		BufferedImage image = ImageUtils.read(imagePath);
		final int W = image.getWidth();
		final int H = image.getHeight();
		this.pixels = new int[H][W];
		this.pixelNum = 0;
		
		for (int i = 0; i < W; i++) {
			for (int j = 0; j < H; j++) {
				int RGB = image.getRGB(i, j);
				if(RGB != ImageUtils.RGB_WHITE) {
					pixels[j][i] = 1;
					pixelNum++;
				} else {
					pixels[j][i] = 0;
				}
			}
		}
	}
	
	public String VAL() {
		return value;
	}
	
	public int PIXEL_NUM() {
		return pixelNum;
	}
	
	public int[][] PIXELS() {
		return pixels;
	}
	
}
