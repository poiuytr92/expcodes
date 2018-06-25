package exp.libs.warp.net.ftp;

import java.io.IOException;
import java.util.List;

import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.net.ftp.bean.FtpBean;

/**
 * 
 * <PRE>
 * FTP工具类
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2016-02-14
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class FTPUtils {

	/**
	 * 私有构造方�?
	 */
	private FTPUtils() {
	}
	
	/**
	 * ftp://username:password@10.8.63.197:21/dir
	 *	
	 * @param ftpUrl
	 * @return FtpConfBean
	 * @throws Exception 
	 */
	public static FtpBean ftpUrl2FtpBean(String ftpUrl) throws Exception {
		FtpBean bean = new FtpBean();
		String rule = "(.*)://(.*):(.*)@(.*):(\\d+)/(.*)";
		
		List<List<String>> list = null;
		List<String> datas = null;
		try {
			list = RegexUtils.findAll(ftpUrl, rule);
			datas = list.get(0);
		} catch (Exception e) {
			list = RegexUtils.findAll(ftpUrl + "/", rule);
			if (list == null) {
				throw new Exception("Format is not supported, " + ftpUrl);
			}
			datas = list.get(0);
		}
		if (datas.get(0).equalsIgnoreCase("ftp")) {
			bean.setFtpType(FtpBean.FTP);
		} else if (datas.get(0).equalsIgnoreCase("sftp")) {
			bean.setFtpType(FtpBean.SFTP);
		}
		
		bean.setUsername(datas.get(1));
		bean.setPassword(datas.get(2));
		bean.setIp(datas.get(3));
		bean.setPort(Integer.parseInt(datas.get(4)));
		String dir = datas.get(5);
		if (dir == null || "".endsWith(dir)) {
			dir = "./";
		}
		bean.setDir(dir);
		
		return bean;
	}

	/**
	 * 上传
	 * 
	 * @param ftpConn
	 *            连接对象
	 * @param localFile
	 *            本地文件
	 * @param remoteDirectory
	 *            远程目录
	 * @throws Exception
	 *             异常信息
	 */
	public static void upload(FTPConnection ftpConn, String localFile,
			String remoteDirectory) throws Exception {
		// Add By WuZhongtian 2014-11-3上午10:42:27
		ftpConn.upload(localFile, remoteDirectory);
	}

	/**
	 * 下载
	 * 
	 * @param ftpConn
	 *            连接对象
	 * @param localDirectory
	 *            本地目录
	 * @param remoteFile
	 *            远程文件
	 * @throws Exception
	 *             异常信息
	 */
	public static void download(FTPConnection ftpConn, String localDirectory,
			String remoteFile) throws Exception {
		// Add By WuZhongtian 2014-11-3上午10:42:33
		ftpConn.download(localDirectory, remoteFile);
	}

	/**
	 * 查询文件 <br>
	 * Describe By WuZhongtian 2014-12-12上午10:59:11
	 * 
	 * @param ftpConn
	 *            连接对象
	 * @param remoteDirectory
	 *            远程目录
	 * @return 返回文件绝对路径名称的列�?
	 * @throws Exception
	 *             异常信息
	 */
	public static List<String> listFiles(FTPConnection ftpConn,
			String remoteDirectory) throws Exception {
		// Add By WuZhongtian 2014-11-3上午10:42:33
		return ftpConn.listFiles(remoteDirectory);
	}
	
	
	public static long getFileLength(FTPConnection ftpConn,
			String remoteFileName) throws IOException {
		return ftpConn.getFileLength(remoteFileName);
	}

	public static long getLastModified(FTPConnection conn, String remoteFileName) 
			throws IOException {
		return conn.getLastModified(remoteFileName);
	}
}
