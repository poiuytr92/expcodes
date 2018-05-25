package exp.github.test;

import exp.github.tools.DirFiller;

public class TestFiller {

	public static void main(String[] args) {
		String prjRootDir = "把此处修改为 [指定项目根目录] 的绝对路径";
		DirFiller.fillEmptyDir(prjRootDir);
	}
	
}
