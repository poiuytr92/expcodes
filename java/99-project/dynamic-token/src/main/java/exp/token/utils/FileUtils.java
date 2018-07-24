package exp.token.utils;

import java.io.File;
import java.io.IOException;

/**
 * <PRE>
 * 文件工具类
 * </PRE>
 * <br/><B>PROJECT : </B> dynamic-token
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   1.0 # 2015-07-08
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class FileUtils {

	/**
	 * 私有化构造函数，避免误用.
	 */
	private FileUtils() {}
	
	/**
	 * <pre>
	 * 在指定位置下创建文件/文件夹，并自动创建该文件的父目录。
	 * 若文件已存在，则不创建，此时返回new File(filePath)。
	 * </pre>
	 * 
	 * @param filePath  文件位置，相对或绝对路径均可。若传入目录路径，则最后的一个文件夹会作为文件创建。
	 * @param createFile true:创建文件; false:创建文件夹
	 * @return 返回创建成功的文件对象，若创建失败则返回null。
	 */
	public static File createFileOrDir(String filePath, boolean createFile) {
		File file = new File(filePath);
		file.setWritable(true, false); // 考虑linux的权限问题

		boolean isCreated = true;
		try {
			if (file.exists() == false) {
				if (false == file.getParentFile().exists()) {
					isCreated = file.getParentFile().mkdirs();
				}
				isCreated = (createFile ? file.createNewFile() : file.mkdir());
			}
			
		} catch (IOException e) {
			isCreated = false;
		}
		
		file = (isCreated ? file : null);
		return file;
	}
	
	/**
	 * 删除指定文件.
	 * 若是目录，则删除该目录（包括）下所有文件和文件夹.
	 * @param filePath 指定位置
	 * @return 全部删除成功:true; 至少一个删除失败:false
	 */
	public static boolean delete(String filePath) {
		return delete(new File(filePath));
	}
	
	/**
	 * 删除指定文件.
	 * 若是目录，则删除该目录（包括）下所有文件和文件夹.
	 * @param file 指定文件
	 * @return 全部删除成功:true; 至少一个删除失败:false
	 */
	public static boolean delete(File file) {
		boolean isOk = true;
		if(file.exists()) {
			if(file.isFile()) {
				isOk &= file.delete();
				
			} else if(file.isDirectory()) {
				File[] files = file.listFiles();
				for(File f : files) {
					isOk &= delete(f);
				}
				isOk &= file.delete();
			}
		}
		return isOk;
	}
	
}
