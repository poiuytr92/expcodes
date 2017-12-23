package exp.bilibli.plugin.utils.test;

import java.io.File;

import exp.bilibli.plugin.utils.OCRUtils;
import exp.bilibli.plugin.utils.VerCodeUtils;

public class TestVerCodeUtils {

	public static void main(String[] args) {
		File dir = new File("./src/test/java/exp/bilibli/plugin/utils/test/img");
		File[] imgs = dir.listFiles();
		for(File img : imgs) {
			if(img.getName().endsWith(".jpg")) {
				String expression = OCRUtils.jpgToTxt(img.getPath());
				int rst = VerCodeUtils.calculate(expression);
				System.out.println(img.getName() + " : " + expression + "=" + rst);
			}
		}
	}
	
}
