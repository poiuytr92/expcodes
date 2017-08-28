package exp.libs.warp.net.pf.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.Charset;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <pre>
 * [端口转发代理服务-S] 数据转发器
 * 	1. 请求发送器: 把[本侧应用程序]的请求[发送]到[对侧真正的服务端口].
 * 	2. 响应接收器: 把[对侧真正的服务端口]返回的响应数据[回传]到[本侧应用程序].
 * </pre>	
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-07-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _TranslateSData extends Thread {

	private Logger log = LoggerFactory.getLogger(_TranslateSData.class);
	
	/** 收发文件管理器 */
	private _SRFileMgr srFileMgr;
	
	/** socket通道会话ID */
	private String sessionId;
	
	/** 转发数据类型: send/recv */
	private String type;
	
	/** 通道无数据的超时时限 */
	private long overtime;
	
	/** 数据流来源 */
	private Socket src;
	
	/** 数据流目的IP */
	private String snkIP;
	
	/** 数据流目的端口 */
	private int snkPort;
	
	/**
	 * 
	 * @param srFileMgr
	 * @param config
	 * @param sessionId
	 * @param type
	 * @param overtime
	 * @param src
	 */
	protected _TranslateSData(_SRFileMgr srFileMgr, FPFConfig config, 
			String sessionId, String type, long overtime, Socket src) {
		this.srFileMgr = srFileMgr;
		this.sessionId = sessionId;
		this.type = type;
		this.overtime = overtime;
		this.src = src;
		this.snkIP = config.getRemoteIP();
		this.snkPort = config.getRemotePort();
	}
	
	@Override
	public void run() {
		if(_Envm.PREFIX_SEND.equals(type)) {
			conn();	// 建立连接
			send();	// 请求发送
			
		} else if(_Envm.PREFIX_RECV.equals(type)) {
			recv();	// 响应接收
			
		} else {
			log.error("无效的数据转发操作类型： [{}]", type);
		}
	}
	
	/**
	 * 通过创建一个内容仅有 #conn# 标识的文件, 通知对侧与真正的服务端口建立socket连接.
	 * 	(某些Socket连接需要先收到服务端响应才会触发客户端发送请求，
	 * 	因此若不先创建连接获取响应，就会一直阻塞在本侧的read方法)
	 */
	private void conn() {
		String emptyFilePath = _getSendFilePath();
		FileUtils.write(emptyFilePath, _Envm.MARK_CONN, Charset.ISO, false);
	}
	
	/**
	 * 请求发送器.
	 * 	把[本侧应用程序]的请求数据流转换成文件, 存储到指定的[收发目录], 
	 * 	由第三方程序把收发目录中的数据流文件送到[对侧], 
	 *  借由[对侧的请求转发器]把请求送到[对侧真正的服务端口].
	 */
	private void send() {
		try {
			long bgnTime = System.currentTimeMillis();
			InputStream in = src.getInputStream();
			while (true) {
				byte[] buffer = new byte[_Envm.IO_BUFF];
				int len = in.read(buffer);
				if (len > 0) {
					String data = _SRFileMgr.encode(buffer, 0, len);
					String sendFilePath = _getSendFilePath();
					FileUtils.write(sendFilePath, data, Charset.ISO, false);
					bgnTime = System.currentTimeMillis();
					
				} else {
					if(overtime <= 0) {
						break;
					} else {
						ThreadUtils.tSleep(_Envm.SCAN_FILE_INTERVAL);
						if(System.currentTimeMillis() - bgnTime >= overtime) {
							throw new SocketTimeoutException("超时无数据交互");
						}
					}
				}
			}
		} catch (SocketTimeoutException e) {
			log.warn("Socket会话 [{}] 的{}转发通道超过 [{}ms] 无数据交互, 通道自动关闭", 
					sessionId, type, overtime);
			
		} catch (IOException e) {
			log.debug("Socket会话 [{}] 的{}转发通道已断开", sessionId, type);
			
		} catch (Exception e) {
			log.error("Socket会话 [{}] 的{}转发通道异常, 通道关闭", sessionId, type, e);
			
		} finally {
			_close(src);
		}
	}
	
	/**
	 * 为[本侧请求发送器]构造数据流文件名.
	 * 	同时该数据流文件列入禁忌表, 避免被[本侧请求转发器]误读.
	 * @return 数据流文件路径
	 */
	private String _getSendFilePath() {
		String sendFileName = _SRFileMgr.toFileName(sessionId, type, snkIP, snkPort);
		srFileMgr.addSendTabu(sendFileName);
		
		String sendFilePath = PathUtils.combine(srFileMgr.getSendDir(), sendFileName);
		return sendFilePath;
	}
	
	/**
	 * 响应接收器.
	 * 	从[收发目录]中获取由第三方程序送来的数据流文件, 
	 * 	从数据流文件中读取[对侧真正的服务端口]返回的响应数据, 
	 * 	把响应数据[回传]到[本侧应用程序]
	 */
	private void recv() {
		try {
			long curTime = System.currentTimeMillis();
			OutputStream out = src.getOutputStream();
			while(!src.isClosed()) {
				
				// 等待文件
				String recvFilePath = _getRecvFilePath();
				if(StrUtils.isEmpty(recvFilePath)) {
					if(overtime <= 0) {
						break;
						
					} else {
						ThreadUtils.tSleep(_Envm.SCAN_FILE_INTERVAL);
						if(System.currentTimeMillis() - curTime >= overtime) {
							throw new SocketTimeoutException("超时无数据交互");
						}
						continue;
					}
				}
				
				// 读取文件数据（文件已生成、但数据可能未写入到文件，需要确认数据已传输完毕）
				curTime = System.currentTimeMillis();
				String data = _readDatas(recvFilePath, curTime);
				
				// 解析文件数据转送到socket通道
				byte[] buffer = _SRFileMgr.decode(data);
				for(int offset = 0, len = 0; offset < buffer.length; offset += len) {
					len = buffer.length - offset;
					len = (len > _Envm.IO_BUFF ? _Envm.IO_BUFF : len);
					out.write(buffer, offset, len);
					out.flush();
				}
				
				// 删除文件
				FileUtils.delete(recvFilePath);
				curTime = System.currentTimeMillis();
			}
		} catch (SocketTimeoutException e) {
			log.warn("Socket会话 [{}] 的{}转发通道超过 [{}ms] 无数据交互, 通道自动关闭", 
					sessionId, type, overtime);
			
		} catch (Exception e) {
			log.error("Socket会话 [{}] 的{}转发通道异常, 通道关闭", sessionId, type, e);
			
		} finally {
			_close(src);
			srFileMgr.clearRecvFiles(sessionId);
		}
		
	}
	
	/**
	 * 为[响应接收器]获取数据流文件路径.
	 * @return 数据流文件路径
	 */
	private String _getRecvFilePath() {
		String recvFileName = srFileMgr.getRecvFile(sessionId);
		String recvFilePath = ""; 
		if(StrUtils.isNotEmpty(recvFileName)) {
			recvFilePath = PathUtils.combine(srFileMgr.getRecvDir(), recvFileName);
		}
		return recvFilePath;
	}
	
	/**
	 * 从文件中读取数据（至少读取2次，确保文件内的数据已传输完成）
	 * @param filePath 文件路径
	 * @param bgnTime 开始读取时间
	 * @return 文件内容
	 * @throws SocketTimeoutException 读取超时
	 */
	private String _readDatas(String filePath, long bgnTime) 
			throws SocketTimeoutException {
		File in = new File(filePath);
		String data = "";
		int dataSize = 0;
		while(true) {
			data = FileUtils.read(in, Charset.ISO);
			int curSize = data.length();
			if(curSize > 0 && dataSize == curSize) {
				break;
			}
			dataSize = curSize;
			
			ThreadUtils.tSleep(_Envm.WAIT_DATA_INTERVAL);
			if(System.currentTimeMillis() - bgnTime >= overtime) {
				throw new SocketTimeoutException("等待文件数据完成传输超时");
			}
		}
		return data;
	}
	
	/**
	 * 关闭socket通道
	 * @param socket
	 */
	private void _close(Socket socket) {
		try {
			socket.close();
		} catch (Exception e) {
			log.error("关闭Socket会话 [{}] 的{}转发通道失败", sessionId, type, e);
		}
	}
	
}
