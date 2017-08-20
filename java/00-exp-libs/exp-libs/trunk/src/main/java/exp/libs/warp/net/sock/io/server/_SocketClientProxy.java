package exp.libs.warp.net.sock.io.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.num.IDUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.sock.bean.SocketBean;
import exp.libs.warp.net.sock.bean.SocketByteBuffer;
import exp.libs.warp.net.sock.io.common.ISession;

class _SocketClientProxy implements ISession, Runnable {

	/** 日志器 */
	private Logger log = LoggerFactory.getLogger(_SocketClientProxy.class);
	
	private String id;
	
	private SocketBean sockConf;
	
	/** Socket本地读缓存 */
	private SocketByteBuffer localBuffer;
	
	private Socket socket;
	
	/** 业务处理器 */
	private IHandler handler;
	
	protected _SocketClientProxy(SocketBean sockConf, 
			Socket socket, IHandler handler) {
		this.id = String.valueOf(IDUtils.getTimeID());
		this.sockConf = sockConf;
		this.localBuffer = new SocketByteBuffer(	//本地缓存要比Socket缓存稍大
				sockConf.getReadBufferSize() * 2, sockConf.getReadCharset());
		
		this.socket = socket;
		this.handler = handler;
	}
	
	@Override
	public void run() {
		try {
			handler._handle(this);
		} catch(Throwable e) {
			log.error("Socket会话 [{}] 异常: 未捕获的业务逻辑错误", ID(), e);
		}
	}
	
	@Override
	public String ID() {
		return id;
	}
	
	@Override
	public SocketBean getSocketBean() {
		return sockConf;
	}
	
	@Override
	public Socket getSocket() {
		return socket;
	}
	
	@Deprecated
	@Override
	public boolean conn() {
		// Undo 客户端代理会话已处于连接状态, 无需再连接
		return false;
	}
	
	@Override
	public boolean isClosed() {
		boolean isClosed = true;
		if(socket != null) {
			isClosed = socket.isClosed();
		}
		return isClosed;
	}
	
	@Override
	public boolean close() {
		boolean isClose = true;
		if(socket != null) {
			try {
				socket.close();
			} catch (Exception e) {
				isClose = false;
				log.error("关闭Socket会话失败.", e);
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
			log.error("Socket [{}] 连接已断开, 无法读取返回消息.", sockConf.getId());
			return msg;
		}
		
		try {
			msg = read(socket.getInputStream(), localBuffer, 
					sockConf.getReadDelimiter(), sockConf.getOvertime());
			
		} catch (ArrayIndexOutOfBoundsException e) {
			log.error("Socket [{}] 本地缓冲区溢出(单条报文过长), 当前缓冲区大小: {}KB.", 
					sockConf.getId(), (sockConf.getReadBufferSize() * 2), e);
						
		} catch (UnsupportedEncodingException e) {
			log.error("Socket [{}] 编码非法, 当前编码: {}.", 
					sockConf.getId(), sockConf.getReadCharset(), e);
					
		} catch (SocketTimeoutException e) {
			log.error("Socket [{}] 读操作超时. 当前超时上限: {}ms.", 
					sockConf.getId(), sockConf.getOvertime(), e);
			
		} catch (Exception e) {
			log.error("Socket [{}] 读操作异常.", sockConf.getId(), e);
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
						throw new SocketTimeoutException("Socket客户端超时未返回消息终止符.");
						
					} else {
						ThreadUtils.tSleep(1);
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
		try {
			write(socket.getOutputStream(), 
					StrUtils.concat(msg, sockConf.getWriteDelimiter()), 
					sockConf.getWriteCharset());
			
		} catch (UnsupportedEncodingException e) {
			log.error("Socket [{}] 编码非法, 当前编码: {}.", 
					sockConf.getId(), sockConf.getWriteCharset(), e);
					
		} catch (Exception e) {
			log.error("Socket [{}] 写操作异常.", sockConf.getId(), e);
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
	@Override
	public void clearIOBuffer() {
		if(localBuffer != null) {
			localBuffer.reset();
		}
	}

}
