package exp.libs.warp.fpf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.Charset;
import exp.libs.utils.StrUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.num.BODHUtils;
import exp.libs.utils.num.IDUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.PathUtils;

class _TranslateSData extends Thread {

	private Logger log = LoggerFactory.getLogger(_TranslateSData.class);
	
	protected final static String PREFIX_SEND = _FileListener.PREFIX_SEND;
	
	protected final static String PREFIX_RECV = _FileListener.PREFIX_RECV;
	
	protected final static String SUFFIX = _FileListener.SUFFIX;
	
	private final static int IO_BUFF = 10240;	// 每次最多读写10K数据
	
	private String sessionId;
	
	private String type;
	
	private long overtime;
	
	private Socket src;
	
	private String snkDir;
	
	private String snkIP;
	
	private int snkPort;
	
	private _SRFileMgr srFileMgr;
	
	protected _TranslateSData(String sessionId, String type, 
			Socket src, FPFConfig config, _SRFileMgr srFileMgr) {
		this.sessionId = sessionId;
		this.type = type;
		this.overtime = config.getOvertime();
		this.src = src;
		this.snkDir = srFileMgr.getDir();
		this.snkIP = config.getRemoteIP();
		this.snkPort = config.getRemotePort();
		this.srFileMgr = srFileMgr;
	}
	
	@Override
	public void run() {
		if(PREFIX_SEND.equals(type)) {
			send();
		} else {
			recv();
		}
	}
	
	private void send() {
		// 创建空文件, 使得另一侧先创建连接
		// (某些Socket连接需要先收到服务端响应才会触发客户端发送请求，因此若不先创建连接获取响应，就会一直阻塞在本侧的read方法)
		String sendFilePath = getSendFilePath();
		FileUtils.createFile(sendFilePath);
		
		try {
			long bgnTime = System.currentTimeMillis();
			InputStream in = src.getInputStream();
			while (true) {
				
				byte[] buffer = new byte[IO_BUFF];
				int len = in.read(buffer);
				if (len > 0) {
					String hex = BODHUtils.toHex(buffer, 0, len);
					sendFilePath = getSendFilePath();
					FileUtils.write(sendFilePath, hex, Charset.ISO, false);
					
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
		}
	}
	
	private String getSendFilePath() {
		String name = StrUtils.concat(type, "#", snkIP, "@", snkPort, 
				"-T", IDUtils.getTimeID(), "-S", sessionId, SUFFIX);
		String path = PathUtils.combine(snkDir, name);
		srFileMgr.addSendTabu(path);
		return path;
	}
	
	private void recv() {
		try {
			long bgnTime = System.currentTimeMillis();
			OutputStream out = src.getOutputStream();
			
			while(!src.isClosed()) {
				String recvFilePath = srFileMgr.getRecvFile(sessionId);
				if(StrUtils.isEmpty(recvFilePath)) {
					if(overtime <= 0) {
						break;
						
					} else {
						ThreadUtils.tSleep(100);
						if(System.currentTimeMillis() - bgnTime >= overtime) {
							throw new SocketTimeoutException("超时无数据交互");
						}
						continue;
					}
				}
				
				File in = new File(recvFilePath);
				String hex = FileUtils.read(in, Charset.ISO);
				byte[] buffer = BODHUtils.toBytes(hex);
				
				for(int offset = 0, len = 0; offset < buffer.length; offset += len) {
					len = buffer.length - offset;
					len = (len > IO_BUFF ? IO_BUFF : len);
					out.write(buffer, offset, len);
					out.flush();
				}
				
				FileUtils.delete(recvFilePath);	// 删除读取成功的流式文件
				bgnTime = System.currentTimeMillis();
			}
		} catch (SocketTimeoutException e) {
			log.warn("Socket会话 [{}] 的{}转发通道超过 [{}ms] 无数据交互, 通道自动关闭", 
					sessionId, type, overtime);
			
		} catch (Exception e) {
			log.error("Socket会话 [{}] 的{}转发通道异常, 通道关闭", sessionId, type, e);
			
		} finally {
			close(src);
			srFileMgr.clearRecvFiles(sessionId);
		}
		
	}
	
//	private List<String> getRecvFilePaths() {
//		List<String> recvFilePaths = new LinkedList<String>();
//		
//		File recvDir = new File(snkDir);
//		String[] recvFileNames = recvDir.list(fileFilter);
//		Arrays.sort(recvFileNames, fileSorter);
//		
//		for(String recvFileName : recvFileNames) {
//			String path = PathUtils.combine(snkDir, recvFileName);
//			recvFilePaths.add(path);
//		}
//		return recvFilePaths;
//	}
	
	private void close(Socket socket) {
		try {
			socket.close();
		} catch (Exception e) {
			log.error("关闭Socket会话 [{}] 的{}转发通道失败", sessionId, type, e);
		}
	}
	
//	private void close(Closeable io) {
//		try {
//			io.close();
//		} catch (NullPointerException e) {
//			// Undo
//			
//		} catch (Exception e) {
//			log.error("关闭FTP会话 [{}] 的{}转发通道失败", sessionId, type, e);
//		}
//	}

}
