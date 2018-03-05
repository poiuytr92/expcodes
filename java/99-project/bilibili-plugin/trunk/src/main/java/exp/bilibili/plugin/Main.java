package exp.bilibili.plugin;

import java.io.File;

import exp.bilibili.plugin.utils.VercodeUtils;

public class Main {

	public static void main(String[] args) {
		File dir = new File("./img/");
		File[] imgs = dir.listFiles();
		for(File img : imgs) {
			int rst = VercodeUtils.calculateExpressionImage(img.getPath());
			System.out.println(img.getName() + " : " + rst);
		}
	}
	
}
