package exp.bilibili.plugin.utils.test;

import java.io.File;

import exp.bilibili.plugin.utils.VercodeUtils;

public class TestVercodeUtils {

	public static void main(String[] args) {
		File dir = new File("./src/test/resources/exp/bilibili/plugin/utils/test/img");
		File[] imgs = dir.listFiles();
		for(File img : imgs) {
			int rst = VercodeUtils.calculateExpressionImage(img.getPath());
			System.out.println(img.getName() + " : " + rst);
		}
	}
	
}
