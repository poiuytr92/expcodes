package exp.libs.mrp.services;

import java.io.File;

/**
 * <PRE>
 * 文件处理工具.
 * 
 * </PRE>
 * <B>项    目：</B>凯通J2SE开发平台(KTJSDP)
 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2014
 * @version   1.0 2014-09-12
 * @author    廖权斌：liaoquanbin@gdcattsoft.com
 * @since     jdk版本：jdk1.6
 */
public class FileUtils {

	/**
	 * 删除文件/文件夹。 
	 * 若文件夹非空，会递归删除所有子文件/文件夹。
	 * 
	 * @param fileOrFolderPath 文件/文件夹路径
	 * @return true:全部删除成功; false:存在删除失败
	 */
	public static boolean delete(String fileOrFolderPath) {
		boolean isDel = true;
		File file = new File(fileOrFolderPath);
		if (file.exists()) {
			if (file.isFile()) {
				isDel = deleteFile(fileOrFolderPath);
			} else {
				isDel = deleteFolder(fileOrFolderPath);
			}
		}
		return isDel;
	}

	/**
	 * 删除单个文件。
	 * 
	 * @param filePath 文件路径
	 * @return true:删除成功; false:删除失败
	 */
	public static boolean deleteFile(String filePath) {
		boolean isDel = true;
		File file = new File(filePath);
		if (file.exists() && file.isFile()) {
			isDel = file.delete();
		}
		return isDel;
	}

	/**
	 * 删除文件夹。 
	 * 若文件夹非空，会递归删除所有子文件/文件夹。
	 * 
	 * @param folderPath 文件夹路径
	 * @return true:全部删除成功; false:存在删除失败
	 */
	public static boolean deleteFolder(String folderPath) {
		boolean isDel = true;
		File floder = new File(folderPath);
		
		if (floder.exists()) {
			if(floder.isFile()) {
				isDel = false;
				
			} else {
				File[] files = floder.listFiles();
				for (File file : files) {
					if (file.isFile()) {
						isDel = deleteFile(file.getAbsolutePath());
						
					} else {
						isDel = deleteFolder(file.getAbsolutePath());
					}
					
					if(isDel == false) {
						break;
					}
				}
			}
			
			// 当且仅当子文件/文件夹全部删除后才删除当前目录
			if(isDel == true) {
				isDel = floder.delete();
			}
		}
		return isDel;
	}

}
