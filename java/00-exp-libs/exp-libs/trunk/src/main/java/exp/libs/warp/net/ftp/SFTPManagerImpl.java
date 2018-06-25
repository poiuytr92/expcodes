package exp.libs.warp.net.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import exp.libs.utils.verify.RegexUtils;

/**
 * 
 * <PRE>
 * SFTP实现
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2016-02-14
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class SFTPManagerImpl implements FTPConnection {

	/**
	 * ftp通道对象
	 */
	public ChannelSftp sftp = null;

	/**
	 * 会话
	 */
	public Session sshSession = null;

	/**
	 * 构造方�?
	 * 
	 * @param ftpIp ip地址
	 * @param ftpPort 端口�?
	 * @param ftpUsername 账号
	 * @param ftpPassword 密码
	 * @param timeOut 超时
	 * @throws JSchException 异常
	 */
	public SFTPManagerImpl(String ftpIp, int ftpPort, String ftpUsername,
			String ftpPassword, int timeOut) throws JSchException {
		JSch jsch = new JSch();
		if (ftpPort == -1) {
			sshSession = jsch.getSession(ftpUsername, ftpIp, 22);
		} else {
			sshSession = jsch.getSession(ftpUsername, ftpIp, ftpPort);
		}
		sshSession.setPassword(ftpPassword);
		Hashtable<String, String> sshConfig = new Hashtable<String, String>();
		sshConfig.put("StrictHostKeyChecking", "no");
		sshSession.setConfig(sshConfig);
		sshSession.connect();
		sshSession.setTimeout(timeOut);
		ChannelSftp channel = (ChannelSftp) sshSession.openChannel("sftp");
		channel.connect();
		sftp = channel;

	}

	@Override
	public void download(String localDirectory, String remoteFile)
			throws Exception {
		// Add By WuZhongtian 2014-11-2上午10:13:21
		File file = new File(remoteFile);
		OutputStream is = new FileOutputStream(localDirectory + File.separator
				+ file.getName());
		sftp.get(remoteFile, is);
	}

	@Override
	public void upload(String localFile, String remoteDirectory)
			throws Exception {
		// Add By WuZhongtian 2014-11-2上午10:13:21
		File file = new File(localFile);
		InputStream is = new FileInputStream(file);
		sftp.cd(remoteDirectory);
		sftp.put(is, file.getName());
	}

	@Override
	public void close() {
		// Add By WuZhongtian 2014-11-2上午10:23:47
		try {
			if (sftp != null) {
				sftp.disconnect();
			}
			if (sshSession != null) {
				sshSession.disconnect();
			}
		} catch (Exception e) {
		} finally {
			// 释放掉下载数
			// relFtpIpConn(this, null);
			sshSession = null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> listFiles(String remoteDirectory) throws Exception {
		// Add By WuZhongtian 2014-12-12上午10:43:23
		Vector<LsEntry> v = (Vector<LsEntry>) sftp.ls(remoteDirectory);
		List<String> filelistTemp = new ArrayList<String>();
		for (Iterator<LsEntry> iterator = v.iterator(); iterator.hasNext();) {
			LsEntry lsEntry = (LsEntry) iterator.next();
			String fileName = remoteDirectory + "/" + lsEntry.getFilename();
			SftpATTRS file = sftp.lstat(fileName);
			if (file.isDir() == false) {
				filelistTemp.add(fileName);
			}
		}
		return filelistTemp;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> listDirs(String remoteDirectory) throws IOException {
		Vector<LsEntry> v = null;
		List<String> filelistTemp = new ArrayList<String>();
		try {
			v = (Vector<LsEntry>) sftp.ls(remoteDirectory);
			for (Iterator<LsEntry> iterator = v.iterator(); iterator.hasNext();) {
				LsEntry lsEntry = (LsEntry) iterator.next();
				String fileName = remoteDirectory + "/" + lsEntry.getFilename();
				SftpATTRS file = sftp.lstat(fileName);
				if (file.isDir() == false) {
					filelistTemp.add(fileName);
				}
			}
		} catch (SftpException e) {
			throw new IOException(e);
		}
		return filelistTemp;
	}

	@Override
	public long getFileLength(String remoteFileName) throws IOException {
		long size = -1;
		try {
			SftpATTRS file = sftp.lstat(remoteFileName);
			size = file.getSize();
		} catch (SftpException e) {
			throw new IOException(e);
		}
		return size;
	}

	@Override
	public long getLastModified(String remoteFileName) throws IOException {
		long time = -1;
		try {
			SftpATTRS file = sftp.lstat(remoteFileName);
			if (file != null) {
//			-rw-r--r-- 0 0 872093 Fri Mar 21 15:34:30 CST 2014
				String strDate = file.toString();
				strDate = RegexUtils.findFirst(strDate, ".*\\s\\d+\\s\\d+\\s\\d+\\s(.*)");
				SimpleDateFormat sdf = new SimpleDateFormat(
						"EEE MMM DD HH:mm:ss z yyyy", Locale.ENGLISH);
				time = sdf.parse(strDate).getTime();
			}
			
		} catch (SftpException e) {
			throw new IOException(e);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return time;
	}

	
}
