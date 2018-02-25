package exp.bilibili.plugin.utils;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.FileType;

public class OpencvUtils {

	public static void main(String[] args) throws Exception {
		System.loadLibrary("./conf/opencv/x64/".concat(Core.NATIVE_LIBRARY_NAME));
		
		String srcPath = "./log/节奏风暴验证码/1.jpg";
		srcPath = ImageUtils.toBinary(srcPath);	//	二值化
		
		BufferedImage image = readImage(srcPath);
		for(int i = 0; i < image.getWidth(); i++) {
			
			int cnt = 0;
			for(int j = 0; j < image.getHeight(); j++) {
				cnt += (image.getRGB(i, j) == RGB_BLACK ? 1 : 0);
			}
			System.out.print(cnt);
			System.out.print(",");
		}
		
		
		
		
		
		
		
//		String snkPath = "./log/节奏风暴验证码/1.png";
//		Mat src = readMat(srcPath);
//		Mat snk = OpencvUtils.gray(src);
//		boolean isOk = writeMat(snk, snkPath, FileType.PNG);
//		System.out.println(isOk);
	}
	
	private final static Logger log = LoggerFactory.getLogger(OpencvUtils.class);
	
	/** javax.imageio.ImageIO 的黑色RGB(应该是反码, 理论值应为0) */
	private final static int RGB_BLACK = -16777216;
	
	/** javax.imageio.ImageIO 的白色RGB值(应该是反码, 理论值应为16777215) */
	private final static int RGB_WHITE = -1;
	
	/**
	 * 
	 * @param imgPath
	 * @return
	 */
	public static BufferedImage readImage(String imgPath) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(imgPath));
		} catch (Exception e) {
			log.error("读取图片失败: {}", imgPath, e);
		}
		return image;
	}
	
	/**
	 * 
	 * @param imgPath
	 * @return
	 */
	public static Mat readMat(String imgPath) {
		BufferedImage image = readImage(imgPath);
		return toMat(image);
	}
	
	/**
	 * 
	 * @param image
	 * @param savePath
	 * @param imageType
	 * @return
	 */
	public static boolean writeImage(BufferedImage image, String savePath, FileType imageType) {
		boolean isOk = false;
		try {
			isOk = ImageIO.write(image, imageType.NAME, new File(savePath));
		} catch (Exception e) {
			log.error("保存图片失败: {}", savePath, e);
		}
		return isOk;
	}
	
	/**
	 * 
	 * @param mat
	 * @param savePath
	 * @param imageType
	 * @return
	 */
	public static boolean writeMat(Mat mat, String savePath, FileType imageType) {
		BufferedImage image = toBufferedImage(mat, imageType);
		return writeImage(image, savePath, imageType);
	}
	
	/**
	 * 
	 * @param image
	 * @return
	 */
	public static Mat toMat(BufferedImage image) {
		Mat mat = null;
		if(image != null) {
			BufferedImage tmp = new BufferedImage(image.getWidth(), 
					image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
			Graphics2D graphics = tmp.createGraphics();
			graphics.setComposite(AlphaComposite.Src);
			graphics.drawImage(image, 0, 0, null);
			graphics.dispose();
			
			byte[] pixels = ((DataBufferByte) tmp.getRaster().getDataBuffer()).getData();
			mat = Mat.eye(tmp.getHeight(), tmp.getWidth(), CvType.CV_8UC3);
			mat.put(0, 0, pixels);
		}
		return mat;
	}

	/**
	 * 
	 * @param mat
	 * @param imageType
	 * @return
	 */
	public static BufferedImage toBufferedImage(Mat mat, FileType imageType) {
		BufferedImage image = null;
		if(mat != null) {
			MatOfByte mob = new MatOfByte();
			Imgcodecs.imencode(imageType.EXT, mat, mob);
			byte[] bytes = mob.toArray();
			
			try {
				InputStream is = new ByteArrayInputStream(bytes);
				image = ImageIO.read(is);
				is.close();
			} catch (Exception e) {}
		}
		return image;
	}
	
	/**
	 * 反色处理
	 * @param mat
	 * @return
	 */
	public static Mat inverse(Mat mat) {
		int width = mat.cols();
		int height = mat.rows();
		int dims = mat.channels();
		byte[] data = new byte[width * height * dims];
		mat.get(0, 0, data);

		int index = 0;
		int r = 0, g = 0, b = 0;
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width * dims; col += dims) {
				index = row * width * dims + col;
				b = data[index] & 0xff;
				g = data[index + 1] & 0xff;
				r = data[index + 2] & 0xff;

				r = 255 - r;
				g = 255 - g;
				b = 255 - b;

				data[index] = (byte) b;
				data[index + 1] = (byte) g;
				data[index + 2] = (byte) r;
			}
		}

		mat.put(0, 0, data);
		return mat;
	}

	/**
	 * 亮度提升
	 * @param image
	 * @return
	 */
	public static Mat brightness(Mat mat) {
		Mat dst = new Mat();
		Mat black = Mat.zeros(mat.size(), mat.type());
		Core.addWeighted(mat, 1.2, black, 0.5, 0, dst);
		return dst;
	}

	/**
	 * 亮度降低
	 * @param mat
	 * @return
	 */
	public static Mat darkness(Mat mat) {
		Mat dst = new Mat();
		Mat black = Mat.zeros(mat.size(), mat.type());
		Core.addWeighted(mat, 0.5, black, 0.5, 0, dst);
		return dst;
	}

	/**
	 * 灰度
	 * @param image
	 * @return
	 */
	public static Mat gray(Mat mat) {
		Mat gray = new Mat();
		Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);
		return gray;
	}

	/**
	 * 锐化
	 * @param image
	 * @return
	 */
	public static Mat sharpen(Mat mat) {
		Mat dst = new Mat();
		float[] sharper = new float[] { 0, -1, 0, -1, 5, -1, 0, -1, 0 };
		Mat operator = new Mat(3, 3, CvType.CV_32FC1);
		operator.put(0, 0, sharper);
		Imgproc.filter2D(mat, dst, -1, operator);
		return dst;
	}

	/**
	 * 高斯模糊
	 * @param image
	 * @return
	 */
	public static Mat blur(Mat mat) {
		Mat dst = new Mat();
		Imgproc.GaussianBlur(mat, dst, new Size(15, 15), 0);
		return dst;
	}

	/**
	 * 梯度
	 * @param image
	 * @return
	 */
	public static Mat gradient(Mat mat) {
		Mat grad_x = new Mat();
		Mat grad_y = new Mat();
		Mat abs_grad_x = new Mat();
		Mat abs_grad_y = new Mat();

		Imgproc.Sobel(mat, grad_x, CvType.CV_32F, 1, 0);
		Imgproc.Sobel(mat, grad_y, CvType.CV_32F, 0, 1);
		Core.convertScaleAbs(grad_x, abs_grad_x);
		Core.convertScaleAbs(grad_y, abs_grad_y);
		grad_x.release();
		grad_y.release();
		Mat gradxy = new Mat();
		Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 10, gradxy);
		return gradxy;
	}
	
}
