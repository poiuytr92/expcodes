package exp.gfed;

/**
 * <PRE>
 * 使用 .empty 文件填充指定文件夹下的所有空的子文件夹.
 * 主要是用于上传项目到GitHub时用, GitHub不允许上传空文件夹.
 * </PRE>
 * <B>PROJECT：</B> github-fill-empty-dir
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-04-28
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Main {

	public static void main(String[] args) {
		String prjRootDir = "把此处修改为 [指定项目根目录] 的绝对路径";
		Filler.fillEmptyDir(prjRootDir);
	}
	
	
}
