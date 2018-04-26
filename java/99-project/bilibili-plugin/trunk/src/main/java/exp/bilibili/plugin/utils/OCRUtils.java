package exp.bilibili.plugin.utils;

import exp.libs.warp.ocr.OCR;


/**
 * <PRE>
 * OCR图像识别工具
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class OCRUtils {
	
	/** OCR组件目录 */
	public final static String OCR_DIR = "./conf/ocr/tesseract";
	
	/** OCR处理对象 */
	private final static OCR OCR = new OCR(OCR_DIR);
	
	/** 私有化构造函数 */
	protected OCRUtils() {}

	/**
	 * 把图像识别成文本内容
	 * @param imgPath
	 * @return
	 */
	public static String imgToTxt(String imgPath) {
		return OCR.recognizeText(imgPath);
	}
	
}
