package exp.libs.warp.net.socket.io.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.pub.StrUtils;
import exp.libs.warp.net.socket.bean.SocketBean;
import exp.libs.warp.net.socket.bean.SocketByteBuffer;

public class SocketClient {

	/** 日志器 */
	private Logger log = LoggerFactory.getLogger(SocketClient.class);
	
	/** Socket重连间隔(ms) */
	private final static long RECONN_INTERVAL = 10000;
	
	/** Socket连续重连次数上限 */
	private final static int RECONN_LIMIT = 30;
	
	/** Socket配置信息 */
	private SocketBean socketBean;
	
	/** Socket客户端 */
	private Socket socketClient;
	
	/** Socket本地缓存 */
	private SocketByteBuffer localBuffer;
	
	/**
	 * 构造函数
	 * @param socketBean socket配置信息
	 */
	public SocketClient(SocketBean socketBean) {
		this.socketBean = socketBean;
	}
	
	public SocketBean getSocketBean() {
		return socketBean;
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
			socketClient = new Socket(socketBean.getIp(), socketBean.getPort());
			socketClient.setSoTimeout(socketBean.getOvertime());
			socketClient.setReceiveBufferSize(socketBean.getReadBufferSize());
			localBuffer = new SocketByteBuffer(	//本地缓存要比Socket缓存稍大
					socketBean.getReadBufferSize() * 2, socketBean.getReadCharset());
			isOk = true;
			
		} catch (Exception e) {
			log.error("Socket {} 创建会话失败.", socketBean.getSocket(), e);
		}
		return isOk;
	}
	
	/**
	 * 重连 socket
	 */
	public void reconn() {
		if(socketBean == null) {
			return;
		}
		
		int cnt = 0;
		do {
			if(conn() == true) {
				break;
				
			} else {
				close();
				log.warn("Socket {} 连接异常, {}ms后重连, 已重试{}次.", 
						socketBean.getSocket(), RECONN_INTERVAL, cnt);
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
						(socketBean == null ? "null" : socketBean.getId()), e);
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
		String msg = null;
		if(isClosed()) {
			log.error("Socket [{}] 连接已断开, 无法读取返回消息.", socketBean.getId());
			return msg;
		}
		
		try {
			msg = read(socketClient.getInputStream(), localBuffer, 
					socketBean.getReadDelimiter(), socketBean.getOvertime());
			
		} catch (ArrayIndexOutOfBoundsException e) {
			log.error("Socket [{}] 本地缓冲区溢出(单条报文过长), 当前缓冲区大小: {}KB.", 
					socketBean.getId(), (socketBean.getReadBufferSize() * 2), e);
						
		} catch (UnsupportedEncodingException e) {
			log.error("Socket [{}] 编码非法, 当前编码: {}.", 
					socketBean.getId(), socketBean.getReadCharset(), e);
					
		} catch (IOException e) {
			log.error("Socket [{}] 读操作超时, 当前超时上限: {}ms.", 
					socketBean.getId(), socketBean.getOvertime(), e);
			close();
			
		} catch (Exception e) {
			log.error("Socket [{}] 读操作异常.", socketBean.getId(), e);
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
					
					if(System.currentTimeMillis() - bgnTime > timeout) {
						throw new IOException("Socket服务端超时未返回消息终止符, 自动重连会话.");
						
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
					StrUtils.concat(msg, socketBean.getWriteDelimiter()), 
					socketBean.getWriteCharset());
			
		} catch (UnsupportedEncodingException e) {
			log.error("Socket [{}] 编码非法, 当前编码: {}.", 
					socketBean.getId(), socketBean.getWriteCharset(), e);
					
		} catch (Exception e) {
			log.error("Socket [{}] 写操作异常.", socketBean.getId(), e);
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
	
}
