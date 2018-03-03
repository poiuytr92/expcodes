package com.org.tensorflow;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import org.tensorflow.Operation;

import exp.bilibili.plugin.utils.ImageUtils;

/**
 * <PRE>
 * TensorFlow深度学习训练模型调用接口
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-03-04
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class TensorFlow {
	
	private TensorFlowInferenceInterface tfi;
	
	public TensorFlow(String pbPath) {
		this.tfi = new TensorFlowInferenceInterface(pbPath);
	}
	
	/**
	 * 设置训练模型的输入变量矩阵值 （即输入张量）
	 * @param feedName 变量名称（在python训练模型时定义）
	 * @param feedValue 变量矩阵（N维输入矩阵降维到一维矩阵的值）
	 * @param dims 变量矩阵的维度值列表， 如 2x3矩阵，则此处为 {2, 3}
	 */
	public void setInput(final String feedName, float[] feedValue, long... dims) {
		tfi.feed(feedName, feedValue, dims);
	}
	
	/**
	 * 获取输出矩阵（即输出张量）
	 * @param fetchName
	 * @return
	 */
	public float[] getOutput(final String fetchName) {
		tfi.run(new String[] { fetchName });	// 执行模型运算
		
		// 提取输出张量的节点
		Operation op = tfi.graphOperation(fetchName);
		
		// 获取输出张量降维到一维后的矩阵维度 (如输出张量为 2x3 则维度为 6)
		final int dimension = (int) op.output(0).shape().size(1);
		
		// 存储输出张量的矩阵
		float[] output = new float[dimension];
		tfi.fetch(fetchName, output);
		return output;
	}
	
	/**
	 * 加载单通道2D图像，并降维到一维数组
	 * 	(模拟python的PIL组件所读取的单通道图像数据格式, 其中黑色标记值为0， 白色标记值为1)
	 * @param binaryImage 单通道图像
	 * @return
	 */
	public float[] loadImage(BufferedImage binaryImage) {
		float[] pixels = new float[0];
		if(binaryImage != null && 
				binaryImage.getType() == BufferedImage.TYPE_BYTE_BINARY) {
			final int H = binaryImage.getHeight();	// 高（即行数）
			final int W = binaryImage.getWidth();	// 宽（即列数）
			pixels = new float[H * W];
			
			for (int h = 0; h < H; h++) {
				int offset = h * W;
				for (int w = 0; w < W; w++) {
					int RGB = binaryImage.getRGB(w, h);
					float val = (RGB == ImageUtils.RGB_BLACK ? 0.0f : 1.0f);
					pixels[offset + w] = val;
				}
			}
		}
		return pixels;
	}
	
	/**
	 * 仿照python的numpy.argmax功能.
	 * 	求 matrix 第axis维 的最大值的索引矩阵
	 * 
	 * 此方法暂时只针对二维矩阵
	 *  当axis=0时，求每列的的最大值的索引
	 *  当axis=1时，求每行的的最大值的索引
	 * 
	 * @param matrix
	 * @param row
	 * @param col
	 * @param axis 0或1
	 * @return
	 */
	public int[] argmax(float[] matrix, int row, int col, int axis) {
		int[] idxs = new int[0];
		if(matrix.length == row * col) {
			
			final int DIM = (axis == 1 ? row : col);
			idxs = new int[DIM];
			float[] maxs = new float[DIM];
			
			Arrays.fill(idxs, 0);
			Arrays.fill(maxs, -Float.MAX_VALUE);
			
			for(int i = 0; i < matrix.length; i++) {
				int rOffset = i / col;
				int cOffset = i % col;
				int offset = (axis == 1 ? rOffset : cOffset);
				float val = matrix[i];
				
				if(maxs[offset] < val) {
					maxs[offset] = val;
					idxs[offset] = (axis == 1 ? cOffset : rOffset);
				}
			}
		}
		return idxs;
	}
	
}
