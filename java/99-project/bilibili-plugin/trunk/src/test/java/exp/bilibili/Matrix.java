package exp.bilibili;
import java.awt.image.BufferedImage;

import exp.libs.utils.img.ImageUtils;


public class Matrix {

	/** 像素矩阵所呈现的图片值 */
	private String value;
	
	/** 像素矩阵(背景0, 前景1) */
	private int[][] pixels;
	
	/** 前景像素的个数 */
	private int pixelNum;
	
	/**
	 * 
	 * @param imagePath 
	 */
	public Matrix(String value, String imagePath) {
		this.value = value;
		init(imagePath);
	}
	
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
