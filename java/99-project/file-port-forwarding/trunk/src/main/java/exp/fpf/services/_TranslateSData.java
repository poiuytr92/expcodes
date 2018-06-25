package exp.fpf.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.fpf.Config;
import exp.fpf.bean.FPFConfig;
import exp.fpf.cache.SRMgr;
import exp.fpf.envm.Param;
import exp.fpf.envm.ResponseMode;
import exp.fpf.utils.BIZUtils;
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
 * <B>PROJECT : </B> file-port-forwarding
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-07-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _TranslateSData extends Thread {

	/** 转发日志�? */
	private Logger log = LoggerFactory.getLogger(_TranslateSData.class);
	
	/** 会话交互日志�? */
	private final static Logger slog = LoggerFactory.getLogger("SESSION");
	
	/** 收发文件管理�? */
	private SRMgr srMgr;
	
	/** socket通道会话ID */
	private String sessionId;
	
	/** 转发数据类型: send/recv */
	private String type;
	
	/** 通道无数据的超时时限 */
	private long overtime;
	
	/** 数据流来�? */
	private Socket src;
	
	/** 数据流目的IP */
	private String snkIP;
	
	/** 数据流目的端�? */
	private int snkPort;
	
	/** 发送文件时�? */
	private int timeSequence;
	
	/**
	 * 
	 * @param srMgr
	 * @param config
	 * @param sessionId
	 * @param type
	 * @param overtime
	 * @param src
	 */
	protected _TranslateSData(SRMgr srMgr, FPFConfig config, 
			String sessionId, String type, long overtime, Socket src) {
		this.srMgr = srMgr;
		this.sessionId = sessionId;
		this.type = type;
		this.overtime = overtime;
		this.src = src;
		this.snkIP = config.getRemoteIP();
		this.snkPort = config.getRemotePort();
		this.timeSequence = 0;
	}
	
	@Override
	public void run() {
		if(Param.PREFIX_SEND.equals(type)) {
			conn();				// 建立连接
			requestToFile();	// 请求发�?
			
		} else if(Param.PREFIX_RECV.equals(type)) {
			
			// 响应接收
			if(Config.getInstn().getRspMode() == ResponseMode.SOCKET) {
				sockToResponse();
			} else {
				fileToResponse();
			}
			
		} else {
			log.error("无效的数据转发操作类型： [{}]", type);
		}
	}
	
	/**
	 * 【数据转发流�?-0: TCP握手�?
	 * 
	 * 通过创建一个内容仅�? #conn# 标识的文�?, 通知对侧与真正的服务端口建立socket连接.
	 * 	(某些Socket连接需要先收到服务端响应才会触发客户端发送请求，
	 * 	因此若不先创建连接获取响应，就会一直阻塞在本侧的read方法)
	 */
	private void conn() {
		slog.debug("会话 [{}] 正在建立连接...", sessionId);
		String emptyFilePath = _getSendFilePath();
		FileUtils.write(emptyFilePath, Param.MARK_CONN, Charset.ISO, false);
	}
	
	/**
	 * 【数据转发流�?-1�?
	 * 
	 * 请求发送器.
	 * 	把[本侧应用程序]的请求数据流转换成文�?, 存储到指定的[收发目录], 
	 * 	由第三方程序把收发目录中的数据流文件送到[对侧], 
	 *  借由[对侧的请求转发器]把请求送到[对侧真正的服务端口].
	 */
	private void requestToFile() {
		slog.debug("会话 [{}] [转发流程1] 已就�?", sessionId);
		try {
			long bgnTime = System.currentTimeMillis();
			InputStream in = src.getInputStream();
			while (!src.isClosed()) {
				byte[] buffer = new byte[Param.IO_BUFF];
				int len = in.read(buffer);
				if (len > 0) {
					String data = BIZUtils.encode(buffer, 0, len);
					String sendFilePath = _getSendFilePath();
					FileUtils.write(sendFilePath, data, Charset.ISO, false);
					bgnTime = System.currentTimeMillis();
					slog.debug("会话 [{}] [转发流程1] 已发�?  [{}] 数据: \r\n{}", 
							sessionId, sendFilePath, data);
					
				} else {
					if(overtime <= 0) {
						break;
					} else {
						ThreadUtils.tSleep(Param.SCAN_DATA_INTERVAL);
						if(System.currentTimeMillis() - bgnTime >= overtime) {
							throw new SocketTimeoutException("超时无数据交�?");
						}
					}
				}
			}
		} catch (SocketTimeoutException e) {
			log.warn("Socket会话 [{}] 的{}转发通道超时 [{}ms] 无数据交�?, 自动断开", 
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
	 * 为[本侧请求发送器]构造数据流文件�?.
	 * 	同时该数据流文件列入禁忌�?, 避免被[本侧请求转发器]误读.
	 * @return 数据流文件路�?
	 */
	private String _getSendFilePath() {
		String sendFileName = BIZUtils.toFileName(
				sessionId, timeSequence++, type, snkIP, snkPort);
		srMgr.addSendTabu(sendFileName);
		
		String sendFilePath = PathUtils.combine(srMgr.getSendDir(), sendFileName);
		return sendFilePath;
	}
	
	/**
	 * 【数据转发流�?-4: socket监听模式�?
	 * 
	 * 响应接收�?:
	 * 	从过[固有socket管道]中读取[对侧真正的服务端口]返回的响应数�?, 
	 * 	把响应数据[回传]到[本侧应用程序]
	 */
	private void sockToResponse() {
		_toResponse(ResponseMode.SOCKET);
	}

	/**
	 * 【数据转发流�?-4: 文件扫描模式�?
	 * 
	 * 响应接收�?:
	 * 	从[收发目录]中获取由第三方程序送来的数据流文件, 
	 * 	从数据流文件中读取[对侧真正的服务端口]返回的响应数�?, 
	 * 	把响应数据[回传]到[本侧应用程序]
	 */
	private void fileToResponse() {
		_toResponse(ResponseMode.FILE);
	}
	
	/**
	 * 【数据转发流�?-4�?
	 * @param mode 转发模式
	 */
	private void _toResponse(int mode) {
		slog.debug("会话 [{}] [转发流程4] 已就�?", sessionId);
		try {
			long curTime = System.currentTimeMillis();
			OutputStream out = src.getOutputStream();
			while(!src.isClosed()) {
				
				String tmp = (ResponseMode.SOCKET == mode ? 
						_getRecvDatas() : 	// 等待管道数据
						_getRecvFilePath()	// 等待文件生成
				);
				if(StrUtils.isEmpty(tmp)) {
					if(overtime <= 0) {
						break;
						
					} else {
						ThreadUtils.tSleep(Param.SCAN_DATA_INTERVAL);
						if(System.currentTimeMillis() - curTime >= overtime) {
							throw new SocketTimeoutException("超时无数据交�?");
						}
						continue;
					}
				}
				
				String data = (ResponseMode.SOCKET == mode ? 
						tmp : 	// 管道数据直接可用, 无需处理
						_readFileDatas(tmp)	// 读取文件数据
				);
				slog.debug("会话 [{}] [转发流程4] 已接�? [{}] 数据 : \r\n{}", sessionId, 
						(ResponseMode.SOCKET == mode ? "SOCKET" : tmp), data);
				
				// 解析数据转送到本地socket通道
				byte[] buffer = BIZUtils.decode(data);
				for(int offset = 0, len = 0; offset < buffer.length; offset += len) {
					len = buffer.length - offset;
					len = (len > Param.IO_BUFF ? Param.IO_BUFF : len);
					out.write(buffer, offset, len);
					out.flush();
				}
				
				// 删除文件
				if(ResponseMode.FILE == mode) {
					FileUtils.delete(tmp);
				}
				curTime = System.currentTimeMillis();
			}
		} catch (SocketTimeoutException e) {
			log.warn("Socket会话 [{}] 的{}转发通道超时 [{}ms] 无数据交�?, 自动断开", 
					sessionId, type, overtime);
			
		} catch (Exception e) {
			log.error("Socket会话 [{}] 的{}转发通道异常, 通道关闭", sessionId, type, e);
			
		} finally {
			_close(src);
			
			if(ResponseMode.SOCKET == mode) {
				srMgr.clearRecvDatas(sessionId);
			} else {
				srMgr.clearRecvFiles(sessionId);
			}
		}
	}
	
	/**
	 * 为[响应接收器]获取响应数据�?.
	 * @return 响应数据
	 */
	private String _getRecvDatas() {
		return srMgr.getRecvData(sessionId);
	}
	
	/**
	 * 为[响应接收器]获取数据流文件路�?.
	 * @return 数据流文件路�?
	 */
	private String _getRecvFilePath() {
		String recvFileName = srMgr.getRecvFile(sessionId);
		String recvFilePath = ""; 
		if(StrUtils.isNotEmpty(recvFileName)) {
			recvFilePath = PathUtils.combine(srMgr.getRecvDir(), recvFileName);
		}
		return recvFilePath;
	}
	
	/**
	 * 从文件中读取数据（至少读�?2次，确保文件内的数据已传输完成）
	 * @param filePath 文件路径
	 * @return 文件内容
	 * @throws SocketTimeoutException 读取超时
	 */
	private String _readFileDatas(String filePath) throws SocketTimeoutException {
		long bgnTime = System.currentTimeMillis();
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
			
			ThreadUtils.tSleep(Param.WAIT_DATA_INTERVAL);
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
