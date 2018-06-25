package exp.libs.utils.img;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

import exp.libs.envm.Charset;
import exp.libs.envm.FileType;
import exp.libs.utils.io.FileUtils;

/**
 * <PRE>
 * 二维码生成/解析工具 
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2018-01-29
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class QRCodeUtils {
	
	/** 日志�? */
	private final static Logger log = LoggerFactory.getLogger(QRCodeUtils.class);
	
	/** 默认数据编码 */
	private final static String DATA_CHARSET = Charset.UTF8;
	
	/** 二维码前景色(�?) */
	private final static int BLACK = 0xFF000000;
	
	/** 二维码背景色(�?) */
	private final static int WHITE = 0xFFFFFFFF;
	
	/** 所生成二维码的图像格式 */
	public final static String TYPE_PNG = FileType.PNG.NAME;

	/** 所生成二维码的图像格式 */
	public final static String TYPE_JPG = FileType.JPG.NAME;
	
	/** 私有化构造函�? */
	protected QRCodeUtils() {}
	
	/**
	 * 生成PNG格式的二维码图像
	 * @param data 写入二维码的数据
	 * @param width 图像宽度
	 * @param height 图像高度
	 * @param savePath 保存图像路径
	 * @return 是否生成成功
	 */
	public static boolean toQRCode(String data, 
			int width, int height, String savePath) {
		return toQRCode(data, width, height, savePath, TYPE_PNG);
	}
	
	/**
	 * 生成二维码图�?
	 * @param data 写入二维码的数据
	 * @param width 图像宽度
	 * @param height 图像高度
	 * @param savePath 保存图像路径
	 * @param imageType 图像格式类型:png/jpg
	 * @return 是否生成成功
	 */
	public static boolean toQRCode(String data, int width, int height,
			String savePath, String imageType) {
		boolean isOk = true;
		Map<EncodeHintType, String> hints = new HashMap<EncodeHintType, String>();
		hints.put(EncodeHintType.CHARACTER_SET, DATA_CHARSET);
		try {
			
			// 生成二维码矩�?
			BitMatrix bitData = new MultiFormatWriter().encode(data,
					BarcodeFormat.QR_CODE, width, height, hints);

			// 把二维码矩阵数据逐像素转换成Image数据
			final int W = bitData.getWidth();
			final int H = bitData.getHeight();
			BufferedImage image = new BufferedImage(
					W, H, BufferedImage.TYPE_INT_RGB);
			for (int i = 0; i < W; i++) {
				for (int j = 0; j < H; j++) {
					image.setRGB(i, j, bitData.get(i, j) ? BLACK : WHITE);
				}
			}
			
			// 保存Image数据到文�?
			File qrFile = FileUtils.createFile(savePath);
			isOk = ImageIO.write(image, imageType, qrFile);
			
		} catch (Exception e) {
			isOk = false;
			log.error("生成二维码图片失�?: {}", savePath, e);
		}
		return isOk;
	}
	
	/**
	 * 解析二维码图�?
	 * @param qrcodePath 二维码图片路�?
	 * @return 二维码图片中的数�?
	 */
	public static String toData(String qrcodePath) {
		String data = "";
		try {
			File qrFile = new File(qrcodePath);
			BufferedImage image = ImageIO.read(qrFile);
			LuminanceSource source = new BufferedImageLuminanceSource(image);
			Binarizer binarizer = new HybridBinarizer(source);
			BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
			
			Map<DecodeHintType, String> hints = new HashMap<DecodeHintType, String>();
			hints.put(DecodeHintType.CHARACTER_SET, DATA_CHARSET);
			
			MultiFormatReader formatReader = new MultiFormatReader();
			data = formatReader.decode(binaryBitmap, hints).getText();
			
		} catch (Exception e) {
			log.error("解析二维码图片失�?: {}", qrcodePath, e);
		}
		return data;
	}

}
