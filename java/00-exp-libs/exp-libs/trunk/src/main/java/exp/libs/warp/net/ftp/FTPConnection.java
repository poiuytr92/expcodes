package exp.libs.warp.net.ftp;

import java.io.IOException;
import java.util.List;

/**
 * <PRE>
 * FTP连接操作接口
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2016-02-14
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public interface FTPConnection {

	/**
	 * 上传功能 <br>
	 * Describe By WuZhongtian 2014-12-12上午10:43:07
	 * 
	 * @param localFile
	 *            本地目录
	 * @param remoteDirectory
	 *            远程目录
	 * @throws Exception
	 *             异常信息
	 */
	public void upload(String localFile, String remoteDirectory)
			throws Exception;

	/**
	 * 下载功能 <br>
	 * Describe By WuZhongtian 2014-12-12上午10:42:59
	 * 
	 * @param localDirectory
	 *            本地目录
	 * @param remoteFile
	 *            远程目录
	 * @throws Exception
	 *             异常信息
	 */
	public void download(String localDirectory, String remoteFile)
			throws Exception;

	/**
	 * 查询功能 <br>
	 * Describe By WuZhongtian 2014-12-12上午10:42:40
	 * 
	 * @param remoteDirectory
	 *            远程目录
	 * @return 返回文件绝对路径名称的列�?
	 * @throws Exception
	 *             异常信息
	 */
	public List<String> listFiles(String remoteDirectory) throws Exception;

	
	/**
	 * 查询功能 <br>
	 * 
	 * @param remoteDirectory
	 *            远程目录
	 * @return 返回文件绝对路径名称的列�?
	 * @throws IOException 
	 */
	public List<String> listDirs(String remoteDirectory) throws IOException;
	
	/**
	 * 关闭连接 <br>
	 * Describe By WuZhongtian 2014-12-12上午11:12:18
	 */
	public void close();
	
	/**
	 * 获取文件大小
	 *
	 * @param remoteFileName 远程文件�?
	 * @return
	 * @throws IOException 
	 */
	public long getFileLength(String remoteFileName) throws IOException;
	
	/**
	 * 获取文件最后修改时�?
	 *
	 * @param remoteFileName 远程文件�?
	 * @return
	 * @throws IOException 
	 */
	public long getLastModified(String remoteFileName) throws IOException;

}
