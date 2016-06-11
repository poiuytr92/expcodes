package exp.gfed;
import java.io.File;
import java.io.IOException;

/**
 * 使用 .empty 文件填充指定文件夹下的所有空的子文件夹.
 * 主要是用于上传项目到GitHub时用, GitHub不允许上传空文件夹.
 */
public class Main {

	public static void main(String[] args) {
		String projectDir = ".";
		create(new File(projectDir));
	}
	
	public static void fillEmptyDir(File projectDir) {
		create(projectDir);
	}
	
	private static void create(File file) {
		if(file.exists()) {
			if(file.isFile()) {
				
			} else if(file.isDirectory()) {
				File[] files = file.listFiles();
				if(files.length <= 0) {
					String path = file.getAbsolutePath();
					create(path + "/" + ".empty");
					System.out.println("已填充空文件夹: " + path);
					
				} else {
					for(File f : files) {
						create(f);
					}
				}
			}
		}
	}
	
	private static void create(String fileName) {
		try {
			File file = new File(fileName);
			file.setWritable(true, false); // 处理linux的权限问题
			file.createNewFile();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
