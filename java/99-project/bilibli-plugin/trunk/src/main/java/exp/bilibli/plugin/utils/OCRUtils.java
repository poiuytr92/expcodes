package exp.bilibli.plugin.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.media.OCR;

import exp.bilibli.plugin.Config;


public class OCRUtils {
	
	private final static Logger log = LoggerFactory.getLogger(UIUtils.class);
	
	private final static String OCR_DIR = Config.getInstn().ORC_DIR();
	
	private final static OCR _OCR = new OCR(OCR_DIR);
	
	protected OCRUtils() {}

	public static String jpgToTxt(String jpgPath) {
		String txt = "";
		try {
			txt = _OCR.recognizeText(jpgPath, OCR.IMG_FORMAT_JPG);
		} catch (Exception e) {
			log.error("识别图片文字失败: {}", jpgPath, e);
		}
		return revise(txt.trim());
	}
	
	/**
	 * 修正常见的识别错误的数字/符号, 提高识别率
	 * @param txt
	 * @return
	 */
	private static String revise(String txt) {
		String revise = txt;
		
		revise = revise.replace("[1", "0");
		revise = revise.replace("[|", "0");
		
		revise = revise.replace("'I", "7");
		
		revise = revise.replace("l•", "4");
		revise = revise.replace("l»", "4");
		revise = revise.replace("b", "4");
		revise = revise.replace("h", "4");
		
		revise = revise.replace("i", "1");
		revise = revise.replace("I", "1");
		revise = revise.replace("]", "1");
		revise = revise.replace("|", "1");
		
		revise = revise.replace("E", "6");
		
		revise = revise.replace("B", "8");
		
		revise = revise.replace("H", "9");
		revise = revise.replace("Q", "9");
		
		revise = revise.replace("·", "-");
		return revise;
	}
	
	public static String pngToTxt(String pngPath) {
		String txt = "";
		try {
			txt = _OCR.recognizeText(pngPath, OCR.IMG_FORMAT_PNG);
		} catch (Exception e) {
			log.error("识别图片文字失败: {}", pngPath, e);
		}
		return txt.trim();
	}
	
}
