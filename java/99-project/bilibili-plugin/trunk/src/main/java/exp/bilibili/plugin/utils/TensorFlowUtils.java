package exp.bilibili.plugin.utils;

import java.awt.image.BufferedImage;

import exp.libs.algorithm.dl.tensorflow.TensorFlow;
import exp.libs.utils.img.ImageUtils;
import exp.libs.utils.io.FileUtils;

/**
 * <PRE>
 * TensorFlow深度学习模型接口
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class TensorFlowUtils {

	/** 已训练好的验证码模型 */
	private final static String PB_PATH = "./conf/tensorflow/storm-captch.pb";
	
	/** TensorFlow接口 */
	private final static TensorFlow _TF = new TensorFlow(PB_PATH);
	
	/** 验证码图片宽�? */
	private final static int IMG_WIDTH = 112;
	
	/** 验证码图片高�? */
	private final static int IMG_HEIGHT = 32;
	
	/** 验证码图片中含有的字符个�? */
	private final static int CHAR_NUM = 5;
	
	/**
	 * 验证码图片中的字符的取值范�?: 
	 * 	26(小写英文字符) + 10(数字)
	 */
	private final static int CHAR_RANGE = 36;
	
	/** 私有化构造函�? */
	protected TensorFlowUtils() {}
	
	/**
	 * 把图像识别成文本内容
	 * @param imgPath
	 * @return
	 */
	public static String imgToTxt(String imgPath) {
		if(!FileUtils.exists(PB_PATH)) {
			return "";
		}
		
		// 读取图片数据并转换为二进制格式（黑色�?0，白色为1�?
		BufferedImage image = ImageUtils.read(imgPath);
		BufferedImage binaryImage = ImageUtils.toBinary(image);
		float[] inputImage = _TF.loadImage(binaryImage);
		
		// 输入张量image_input: 待解析的二进制图片数�?
		_TF.setInput("image_input", inputImage, IMG_WIDTH, IMG_HEIGHT);
		
		// 输入张量keep_prob: 神经元被选中的概率，用于防过拟合，取�?0~1.0，值越大训练越快，但准确率越低; 但使用模型时固定�?1即可
		_TF.setInput("keep_prob", new float[] { 1.0f }, 1);		
		
		// 获取此CNN模型的输出张量： 
		// 张量维度�? 180 = 5x36 (26+10), 每行可解析为一个验证码字符
		float[] output = _TF.getOutput("final_output");
		
		// 分析输出张量，得到最终解(取输出张量矩阵每行最大值的索引�?)
		int[] idxs = _TF.argmax(output, CHAR_NUM, CHAR_RANGE, 1);
		
		// 把索引值解析成字符
		StringBuilder captch = new StringBuilder();
		for(int idx : idxs) {
			if(idx < 26) {	// 解析为小写英文字�?
				captch.append((char) (idx + 'a'));
				
			} else {		// 解析为数�?
				captch.append((char) (idx - 26 + '0'));
			}
		}
		return captch.toString();
	}
	
}
