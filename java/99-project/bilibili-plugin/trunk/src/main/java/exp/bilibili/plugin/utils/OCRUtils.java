package exp.bilibili.plugin.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.media.OCR;


/**
 * <PRE>
 * 图像识别工具
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class OCRUtils {
	
	private final static Logger log = LoggerFactory.getLogger(OCRUtils.class);
	
	private final static String OCR_DIR = "./conf/ocr/tesseract";
	
	private final static OCR _OCR = new OCR(OCR_DIR);
	
	protected OCRUtils() {}

	/**
	 * 把图像识别成文本内容
	 * @param imgPath
	 * @return
	 */
	protected static String imgToTxt(String imgPath) {
		String txt = "";
		try {
			txt = _OCR.recognizeText(imgPath);
		} catch (Exception e) {
			log.error("识别图片文字失败: {}", imgPath, e);
		}
		return txt.trim();
	}
	
}
