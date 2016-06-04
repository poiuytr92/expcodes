package exp.libs.warp.net.socket.io.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.warp.net.socket.bean.SocketByteBuffer;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.pub.StrUtils;

/**
 * <PRE>
 * Socket接口会话.
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class SocketSession {

	/** 日志器 */
	private Logger log = LoggerFactory.getLogger(SocketSession.class);
	
	/** Socket重连间隔(ms) */
	private final static long RECONN_INTERVAL = 60000;
	
	/** Socket连续重连次数上限 */
	private final static int RECONN_LIMIT = 10;
	
	/** Socket配置信息 */
	private SocketInfo socketInfo;
	
	/** Socket客户端 */
	private Socket socketClient;
	
	/** Socket本地缓存 */
	private SocketByteBuffer localBuffer;
	
	/**
	 * 构造函数
	 * @param socketInfo socket配置信息
	 */
	public SocketSession(SocketInfo socketInfo) {
		this.socketInfo = socketInfo;
	}
	
	public SocketInfo getSocketInfo() {
		return socketInfo;
	}
	
	/**
	 * 连接socket
	 * @return 是否连接成功
	 */
	public boolean conn() {
		if(isClosed() == false) {
			return true;
		}
		
		// 创建会话
		boolean isOk = false;
		try {
			socketClient = new Socket(socketInfo.getIp(), socketInfo.getPort());
			socketClient.setSoTimeout(socketInfo.getOvertime());
			socketClient.setReceiveBufferSize(socketInfo.getBufferSize());
			localBuffer = new SocketByteBuffer(	//本地缓存要比Socket缓存稍大
					socketInfo.getBufferSize() * 2, socketInfo.getCharset());
			isOk = true;
			
		} catch (Exception e) {
			log.error("Socket {} 创建会话失败.", socketInfo.toNaviInfo(), e);
		}
		
		// 登陆
		if(isOk == true) {
			isOk = login();
			
			if(isOk == false) {
				log.error("Socket {} 登录验证失败.", socketInfo.toNaviInfo());
			}
		}
		return isOk;
	}
	
	/**
	 * 重连 socket
	 */
	public void reconn() {
		if(socketInfo == null) {
			return;
		}
		
		int cnt = 0;
		do {
			if(conn() == true) {
				break;
				
			} else {
				close();
				log.warn("Socket {} 连接异常, {}ms后重连, 已重试{}次.", 
						socketInfo.toNaviInfo(), RECONN_INTERVAL, cnt);
			}
			
			cnt++;
			
			ThreadUtils.tSleep(RECONN_INTERVAL);
		} while(RECONN_LIMIT < 0 || cnt < RECONN_LIMIT);
	}
	
	/**
	 * 检查socket连接是否已断开
	 * @return
	 */
	public boolean isClosed() {
		boolean isClosed = true;
		if(socketClient != null) {
			isClosed = socketClient.isClosed();
		}
		return isClosed;
	}
	
	/**
	 * 释放所有资源
	 */
	public boolean close() {
		boolean isClose = true;
		if(socketClient != null) {
			try {
				socketClient.close();
			} catch (Exception e) {
				isClose = false;
				log.error("Socket [{}] 关闭会话失败.", 
						(socketInfo == null ? "null" : socketInfo.getId()), e);
			}
		}
		
		if(localBuffer != null) {
			localBuffer.clear();
		}
		return isClose;
	}
	
	/**
	 * Socket读操作
	 * @return 读取的报文. 若返回null，则出现异常。
	 */
	public String read() {
		reconn();
		
		String msg = null;
		try {
			msg = read(socketClient.getInputStream(), localBuffer, 
					socketInfo.getDelimiter(), socketInfo.getOvertime());
			
		} catch (ArrayIndexOutOfBoundsException e) {
			log.error("Socket [{}] 本地缓冲区溢出(单条报文过长), 当前缓冲区大小: {}KB.", 
					socketInfo.getId(), (socketInfo.getBufferSize() * 2), e);
						
		} catch (UnsupportedEncodingException e) {
			log.error("Socket [{}] 编码非法, 当前编码: {}.", 
					socketInfo.getId(), socketInfo.getCharset(), e);
					
		} catch (IOException e) {
			log.error("Socket [{}] 读操作超时, 当前超时上限: {}ms.", 
					socketInfo.getId(), socketInfo.getOvertime(), e);
			close();
			
		} catch (Exception e) {
			log.error("Socket [{}] 读操作异常.", socketInfo.getId(), e);
			close();
		}
		return msg;
	}
	
	/**
	 * <pre>
	 * Socket读操作.
	 * 
	 * 此方法会阻塞调用，直到从input读到的字节流中包含delimiter才会返回，
	 * 返回的String编码就是在sockBuff初始化时定义的编码，
	 * 返回后String后，sockBuff会保留剩下的字节。
	 * 
	 * 必须保证一次读取的消息不能大于sockBuff的size，
	 * 否则会抛出溢出异常ArrayIndexOutOfBoundsException，
	 * 并且sockBuff被重置，所有已读取的字节丢失。
	 * </pre>
	 * @param input 消息流入管道
	 * @param localBuff 本地缓冲区
	 * @param delimiter 结束符
	 * @param timeout 读操作超时(ms)
	 * @return 以delimiter结束的单条消息(绝不返回null)
	 * @throws IOException 读操作异常
	 */
	private String read(InputStream input, SocketByteBuffer localBuff, 
			final String delimiter, final long timeout) throws IOException {
		long bgnTime = System.currentTimeMillis();
		int endIndex = localBuff.indexOf(delimiter);	
		int readLen = 0;
		
		if(endIndex != -1) {
			// None: 本地缓冲区 localBuff 中仍有完整的数据未取出
			
		} else {
			while(true) {
				byte[] buffer = new byte[10240];	//每次最多取出10K的数据
				readLen = input.read(buffer);
				localBuff.append(buffer, readLen);
				endIndex = localBuff.indexOf(delimiter);
				if(endIndex != -1) {	// 当存在结束符时，退出循环
					break;
				}
				
				if(timeout > 0) {
					
					// FIXME：主要处理服务端无故不断返回\n的现象，
					// 但若大量数据返回时间超过2分钟，则此处逻辑会导致正常数据接收失败。
					// 但由于现在大量数据都是分批取的，暂时不会触发此问题，但若超时时间<2min可能会触发
					if(System.currentTimeMillis() - bgnTime > timeout) {
						throw new IOException("Socket服务端超时未返回消息终止符.");
						
					} else {
						ThreadUtils.tSleep(100);
					}
				}
			}
		}
		
		String msg = localBuff.subString(endIndex);
		if(msg != null) {
			msg = msg.trim();
			localBuff.delete(endIndex);
		} else {
			msg = "";
		}
		return msg;
	}
	
	/**
	 * Socket写操作.
	 * @param msg 需发送的消息报文
	 */
	public void write(final String msg) {
		reconn();
		
		try {
			write(socketClient.getOutputStream(), 
					StrUtils.concat(msg, socketInfo.getDelimiter()), 
					socketInfo.getCharset());
			
		} catch (UnsupportedEncodingException e) {
			log.error("Socket [{}] 编码非法, 当前编码: {}.", 
					socketInfo.getId(), socketInfo.getCharset(), e);
					
		} catch (Exception e) {
			log.error("Socket [{}] 写操作异常.", socketInfo.getId(), e);
			close();
		}
	}
	
	/**
	 * Socket写操作.
	 * @param output 消息流出管道
	 * @param msg 需要发送的消息
	 * @param charset 消息编码
	 * @throws IOException 写操作异常
	 */
	private void write(OutputStream output, final String msg, final String charset) 
			throws IOException {
		output.write(msg.getBytes(charset));
		output.flush();
	}
	
	/**
	 * 临时清理本地缓存.
	 * 建议完成一次完整的读写交互后执行.
	 */
	public void cleanBuffer() {
		if(localBuffer != null) {
			localBuffer.reset();
		}
	}
	
	/**
	 * 登陆验证
	 * @return true:登陆成功; false:登陆失败
	 */
	private boolean login() {
		// TODO
		return true;
	}
	
}
