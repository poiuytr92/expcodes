package exp.bilibili.plugin.utils;

import java.awt.image.BufferedImage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.org.tensorflow.TensorFlow;

/**
 * <PRE>
 * TensorFlow深度学习模型接口
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class TensorFlowUtils {

	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(TensorFlowUtils.class);
	
	private final static String PB_PATH = "./conf/tensorflow/storm-captch.pb";
	
	private final static TensorFlow _TF = new TensorFlow(PB_PATH);
	
	protected TensorFlowUtils() {}
	
	/**
	 * 把图像识别成文本内容
	 * @param imgPath
	 * @return
	 */
	public static String imgToTxt(String imgPath) {
		BufferedImage image = ImageUtils.read(imgPath);
		BufferedImage binaryImage = ImageUtils.toBinary(image);
		float[] inputImage = _TF.loadImage(binaryImage);
		
		_TF.setInput("image_input", inputImage, 112, 32);		// image_input
		_TF.setInput("keep_prob", new float[] { 1.0f }, 1);		// keep_prob (神经元被选中的概率，用于防过拟合，取值0~1.0，值越大训练越快，但准确率越低)
		
		// 解集： 5x36 (26+10)
		float[] output = _TF.getOutput("final_output");
		int[] idxs = _TF.argmax(output, 5, 36, 1);
		StringBuilder captch = new StringBuilder();
		for(int idx : idxs) {
			if(idx < 26) {
				captch.append((char) (idx + 'a'));
			} else {
				captch.append((char) (idx - 26 + '0'));
			}
		}
		return captch.toString();
	}
	
}
