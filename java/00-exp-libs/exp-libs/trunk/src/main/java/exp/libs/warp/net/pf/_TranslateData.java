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
	
	private Logger log = LoggerFactory.getLogger(_PFHandler.class);
	
	private String sessionId;
	
	private Socket src;
	
	private Socket snk;
	
	private long overtime;
	
	private String type;
	
	protected _TranslateData(String sessionId, Socket src, Socket snk, 
			long overtime, String type) {
		this.sessionId = sessionId;
		this.src = src;
		this.snk = snk;
		this.overtime = overtime;
		this.type = type;
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
					ThreadUtils.tSleep(100);
					if(overtime > 0 && 
							System.currentTimeMillis() - bgnTime >= overtime) {
						throw new SocketTimeoutException("超时无数据交互");
					}
				}
			}
		} catch (SocketTimeoutException e) {
			log.warn("Socket会话 [{}] 的{}转发通道超过 [{}ms] 无数据交互, 通道自动关闭", 
					sessionId, type, overtime);
			
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
		} catch (IOException e) {
			log.error("关闭Socket会话 [{}] 的{}转发通道失败", sessionId, type, e);
		}
	}

}
