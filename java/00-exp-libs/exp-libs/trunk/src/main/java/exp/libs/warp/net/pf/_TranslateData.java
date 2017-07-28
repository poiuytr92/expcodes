package exp.libs.warp.net.pf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.os.ThreadUtils;

class _TranslateData extends Thread {

	protected final static String TYPE_REQUEST = "REQUEST";
	
	protected final static String TYPE_RESPONE = "RESPONE";
	
	private Logger log = LoggerFactory.getLogger(_TranslateData.class);
	
	private String sessionId;
	
	private String type;
	
	private long overtime;
	
	private Socket src;
	
	private Socket snk;
	
	protected _TranslateData(String sessionId, String type, long overtime, 
			Socket src, Socket snk) {
		this.sessionId = sessionId;
		this.type = type;
		this.overtime = overtime;
		this.src = src;
		this.snk = snk;
	}
	
	@Override
	public void run() {
		try {
			long bgnTime = System.currentTimeMillis();
			InputStream in = src.getInputStream();
			OutputStream out = snk.getOutputStream();
			while (true) {
				byte[] buffer = new byte[10240];	//每次最多取出10K的数据
				int len = in.read(buffer);
				if (len > 0) {
					out.write(buffer, 0, len);
					out.flush();
					bgnTime = System.currentTimeMillis();
					
				} else {
					if(overtime <= 0) {
						break;
						
					} else {
						ThreadUtils.tSleep(100);
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
			close(src);
			close(snk);
		}
	}
	
	private void close(Socket socket) {
		try {
			socket.close();
		} catch (Exception e) {
			log.error("关闭Socket会话 [{}] 的{}转发通道失败", sessionId, type, e);
		}
	}

}
