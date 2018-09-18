package exp.github.tools;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.Charset;
import exp.libs.envm.Delimiter;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.LogUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 项目空文件夹填充器.
 * ------------------------------------
 * 使用 .empty 文件填充指定文件夹下的所有空的子文件夹.
 * 主要是在上传项目到GitHub时用, GitHub不允许上传空文件夹.
 * </PRE>
 * <br/><B>PROJECT : </B> github-fill-empty-dir
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-04-28
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class DirFiller {

	/** 项目根目录路径 */
	private final static String PRJ_ROOT_DIR = "把此处修改为 [指定项目根目录] 的绝对路径"; 
	
	/**
	 * [项目空文件夹填充器] 工具入口
	 * @param args
	 */
	public static void main(String[] args) {
		LogUtils.loadLogBackConfig();
		fillEmptyDir(PRJ_ROOT_DIR);
	}
	
	
///////////////////////////////////////////////////////////////////////////////
	
	
	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(DirFiller.class);
	
	/** 用于填充空目录的文件名 */
	private final static String EMPTY_FILE_NAME = "/.empty";
	
	/** 私有化构造函数 */
	protected DirFiller() {}
	
	/**
	 * 填充空目录
	 * @param dir
	 */
	public static void fillEmptyDir(String dirPath) {
		fillEmptyDir(new File(dirPath));
	}
	
	/**
	 * 填充空目录
	 * @param dir
	 */
	public static void fillEmptyDir(File dir) {
		create(dir);
	}
	
	/**
	 * 递归在每个空目录创建文件
	 * @param file 根目录
	 */
	private static void create(File root) {
		if(root.exists()) {
			if(root.isFile()) {
				// Undo
				
			} else if(root.isDirectory()) {
				File[] files = root.listFiles();
				if(files.length <= 0) {
					String path = root.getAbsolutePath();
					File empty = FileUtils.createFile(path.concat(EMPTY_FILE_NAME));
					String content = StrUtils.concat(
							"# Copyright (C)", Delimiter.CRLF, 
							"# Author: EXP", Delimiter.CRLF, 
							"# Site  : http://exp-blog.com", Delimiter.CRLF, 
							"# Mail  : 272629724@qq.com", Delimiter.CRLF);
					FileUtils.write(empty, content, Charset.ISO, false);
					log.info("已填充空文件夹: {}", path);
					
				} else {
					for(File file : files) {
						create(file);
					}
				}
			}
		}
	}
	
}
