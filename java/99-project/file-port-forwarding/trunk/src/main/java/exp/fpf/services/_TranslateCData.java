package exp.fpf.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.fpf.Config;
import exp.fpf.cache.SRMgr;
import exp.fpf.envm.Param;
import exp.fpf.envm.ResponseMode;
import exp.fpf.proxy.Sender;
import exp.fpf.utils.BIZUtils;
import exp.libs.algorithm.struct.queue.pc.PCQueue;
import exp.libs.envm.Charset;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <pre>
 * [端口转发代理服务-C] 数据转发器
 * 	1. 请求转发器: 把[对侧]的请求[转发]到[本侧真正的服务端口].
 * 	2. 响应收转器: 把[本侧真正的服务端口]返回的响应数据[收转]到[对侧].
 * </pre>	
 * <br/><B>PROJECT : </B> file-port-forwarding
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2017-07-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _TranslateCData extends Thread {

	/** 转发日志器 */
	private final static Logger log = LoggerFactory.getLogger(_TranslateCData.class);
	
	/** 会话交互日志器 */
	private final static Logger slog = LoggerFactory.getLogger("SESSION");
	
	/** 收发文件管理器 */
	private SRMgr srMgr;
	
	/** socket通道会话ID */
	private String sessionId;
	
	/** 转发数据类型: send/recv */
	private String type;
	
	/** 通道无数据的超时时限 */
	private long overtime;
	
	/** 数据流来源的文件队列 */
	private PCQueue<String> srcList;
	
	/** 数据流目的(真实的socket服务会话) */
	private Socket snk;
	
	/** 数据流目的IP */
	private String snkIP;
	
	/** 数据流目的端口 */
	private int snkPort;
	
	/** 发送文件时序 */
	private int timeSequence;
	
	/**
	 * 
	 * @param srMgr
	 * @param sessionId
	 * @param type
	 * @param overtime
	 * @param srcList
	 * @param snk
	 */
	protected _TranslateCData(SRMgr srMgr, 
			String sessionId, String type, long overtime, 
			PCQueue<String> srcList, Socket snk) {
		this.srMgr = srMgr;
		this.sessionId = sessionId;
		this.type = type;
		this.overtime = overtime;
		this.srcList = srcList;
		this.snk = snk;
		this.snkIP = snk.getInetAddress().getHostAddress();
		this.snkPort = snk.getPort();
		this.timeSequence = 0;
	}
	
	@Override
	public void run() {
		
		// 请求转发
		if(Param.PREFIX_SEND.equals(type)) {
			fileToRequest();
			
		// 响应接收
		} else if(Param.PREFIX_RECV.equals(type)) {
			if(Config.getInstn().getRspMode() == ResponseMode.SOCKET) {
				responseToSock();
			} else {
				responseToFile();
			}
		} else {
			log.error("无效的数据转发操作类型： [{}]", type);
		}
	}
	
	/**
	 * 【数据转发流程-2】
	 * 
	 * 请求转发器: 
	 * 	从[收发目录]中获取由第三方程序送来的数据流文件, 
	 * 	从数据流文件中读取[对侧应用程序]发送的请求数据, 
	 * 	把请求数据[转发]到[本侧真正的服务端口]
	 */
	private void fileToRequest() {
		slog.debug("会话 [{}] [转发流程2] 已就绪", sessionId);
		try {
			long lastTime = System.currentTimeMillis();
			OutputStream out = snk.getOutputStream();
			while(!snk.isClosed()) {
				
				// 等待文件
				String sendFilePath = srcList.getQuickly();
				if(StrUtils.isEmpty(sendFilePath)) {
					if(overtime <= 0) {
						break;
						
					} else {
						ThreadUtils.tSleep(Param.SCAN_DATA_INTERVAL);
						if(System.currentTimeMillis() - lastTime >= overtime) {
							throw new SocketTimeoutException("超时无数据交互");
						}
						continue;
					}
				}
				
				// 读取文件数据（文件已生成、但数据可能未写入到文件，需要确认数据已传输完毕）
				String data = _readFileDatas(sendFilePath);
				slog.debug("会话 [{}] [转发流程2] 已接收  [{}] 数据: \r\n{}", 
						sessionId, sendFilePath, data);
				
				// 解析文件数据转送到socket通道
				byte[] buffer = BIZUtils.decode(data);
				for(int offset = 0, len = 0; offset < buffer.length; offset += len) {
					len = buffer.length - offset;
					len = (len > Param.IO_BUFF ? Param.IO_BUFF : len);
					out.write(buffer, offset, len);
					out.flush();
				}
				
				// 删除文件
				FileUtils.delete(sendFilePath);
				lastTime = System.currentTimeMillis();
			}
		} catch (SocketTimeoutException e) {
			log.warn("Socket会话 [{}] 的{}转发通道超时 [{}ms] 无数据交互, 自动断开", 
					sessionId, type, overtime);
			
		} catch (Exception e) {
			log.error("Socket会话 [{}] 的{}转发通道异常, 通道关闭", sessionId, type, e);
			
		} finally {
			close(snk);
		}
	}
	
	/**
	 * 从文件中读取数据（至少读取2次，确保文件内的数据已传输完成）
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
		
		// 若文件内容是连接标识, 则认为文件内容为空（仅socket会话创建连接）
		if(Param.MARK_CONN.equals(data)) {
			data = "";
		}
		return data;
	}
	
	/**
	 * 【数据转发流程-3: socket监听模式】
	 * 
	 * 响应收转器:
	 * 	把[本侧真正的服务端口]返回的响应数据流通过[固有socket管道]送到[对侧], 
	 * 	借由[对侧的响应接收器]把响应送到[对侧应用程序].
	 */
	private void responseToSock() {
		_responseTo(ResponseMode.SOCKET);
	}

	/**
	 * 【数据转发流程-3: 文件扫描模式】
	 * 
	 * 响应收转器:
	 * 	把[本侧真正的服务端口]返回的响应数据流转换成文件, 存储到指定的[收发目录], 
	 * 	由第三方程序把收发目录中的数据流文件送到[对侧], 
	 * 	借由[对侧的响应接收器]把响应送到[对侧应用程序].
	 */
	private void responseToFile() {
		_responseTo(ResponseMode.FILE);
	}
	
	/**
	 * 【数据转发流程-3】
	 * @param mode 转发模式
	 */
	private void _responseTo(int mode) {
		slog.debug("会话 [{}] [转发流程3] 已就绪", sessionId);
		try {
			InputStream in = snk.getInputStream();
			while (!snk.isClosed()) {
				byte[] buffer = new byte[Param.IO_BUFF];
				int len = in.read(buffer);	// 阻塞
				if (len >= 0) {
					String data = BIZUtils.encode(buffer, 0, len);
					
					if(ResponseMode.SOCKET == mode) {
						String json = _getRecvJsonData(data);
						Sender.getInstn().send(json);
						slog.debug("会话 [{}] [转发流程3] 已发送 [SOCKET] 数据 : \r\n{}", 
								sessionId, json);
						
					} else {
						String recvFilePath = _getRecvFilePath();
						FileUtils.write(recvFilePath, data, Charset.ISO, false);
						slog.debug("会话 [{}] [转发流程3] 已发送 [{}] 数据 : \r\n{}", 
								sessionId, recvFilePath, data);
					}
					
				} else {
					break;
				}
			}
		} catch (SocketTimeoutException e) {
			log.warn("Socket会话 [{}] 的{}转发通道超时 [{}ms] 无数据交互, 自动断开", 
					sessionId, type, overtime);
			
		} catch (IOException e) {
			log.debug("Socket会话 [{}] 的{}转发通道已断开", sessionId, type);
			
		} catch (Exception e) {
			log.error("Socket会话 [{}] 的{}转发通道异常, 通道关闭", sessionId, type, e);
			
		} finally {
			close(snk);
		}
	}
	
	/**
	 * 为[本侧响应收转器]构造Json格式数据流.
	 * @param data 需要封装的原数据
	 * @return
	 */
	private String _getRecvJsonData(String data) {
		return _getRecvJsonData(sessionId, data);
	}
	
	/**
	 * 为[本侧响应收转器]构造Json格式数据流.
	 * @param sessionId 会话ID
	 * @param data 需要封装的原数据
	 * @return
	 */
	protected static String _getRecvJsonData(String sessionId, String data) {
		JSONObject json = new JSONObject();
		json.put(Param.SID, sessionId);
		json.put(Param.DATA, data);
		return json.toString();
	}
	
	/**
	 * 
	 * 	同时该数据流文件列入禁忌表, 避免被[本侧响应接收器]误读.
	 * @return 数据流文件路径
	 */
	private String _getRecvFilePath() {
		return _getRecvFilePath(srMgr, sessionId, timeSequence++, type, snkIP, snkPort);
	}
	
	/**
	 * 为[本侧响应收转器]构造数据流文件路径.
	 * @param srMgr
	 * @param sessionId
	 * @param timeSequence
	 * @param type
	 * @param snkIP
	 * @param snkPort
	 * @return
	 */
	protected static String _getRecvFilePath(SRMgr srMgr, String sessionId, 
			int timeSequence, String type, String snkIP, int snkPort) {
		String recvFileName = BIZUtils.toFileName(
				sessionId, timeSequence++, type, snkIP, snkPort);
		srMgr.addRecvTabu(recvFileName);
		
		String recvFilePath = PathUtils.combine(srMgr.getSendDir(), recvFileName);
		return recvFilePath;
	}
	
	/**
	 * 关闭socket会话
	 * @param socket
	 */
	private void close(Socket socket) {
		try {
			socket.close();
		} catch (Exception e) {
			log.error("关闭Socket会话 [{}] 的{}转发通道失败", sessionId, type, e);
		}
	}
	
}
