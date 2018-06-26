package exp.fpf.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.fpf.Config;
import exp.fpf.cache.SRMgr;
import exp.fpf.envm.Param;
import exp.fpf.envm.ResponseMode;
import exp.libs.warp.io.listn.FileMonitor;
import exp.libs.warp.net.sock.bean.SocketBean;
import exp.libs.warp.net.sock.io.common.IHandler;
import exp.libs.warp.net.sock.io.server.SocketServer;

/**
 * <pre>
 * [响应数据传输管道-接收端]
 * 
 * 1. socket监听模式:
 *  通过socket管道从 [响应数据传输管道-发送端] 提取[真正的服务端口]返回的响应数据
 * 
 * 2. 文件扫描模式:
 * 	从指定目录扫描由第三方程序转发的、存储了[真正的服务端口]返回的响应数据的文件
 * 
 * </pre>	
 * <B>PROJECT : </B> file-port-forwarding
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-01-16
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Recver {

	private Logger log = LoggerFactory.getLogger(Recver.class);
	
	private final static String NAME = "响应数据传输管道";
	
	/** sock监听器 */
	private SocketServer sockListener;
	
	/** 文件监听器 */
	private FileMonitor fileListener;
	
	private boolean isInit;
	
	private static volatile Recver instance;
	
	private Recver() {
		this.isInit = false;
	}
	
	public static Recver getInstn() {
		if(instance == null) {
			synchronized (Recver.class) {
				if(instance == null) {
					instance = new Recver();
				}
			}
		}
		return instance;
	}
	
	public void _init(SRMgr srMgr) {
		isInit = true;
		
		// 设置socket监听器
		if(Config.getInstn().getRspMode() == ResponseMode.SOCKET) {
			SocketBean sockConf = Config.getInstn().newSocketConf();
			IHandler handler = new _SRDataListener(srMgr);
			this.sockListener = new SocketServer(sockConf, handler);
			log.info("[{}]-[接收端] 已初始化, 服务socket为 [{}]", NAME, sockConf.getSocket());
			
		// 设置收发文件目录监听器(只监听 recv 文件)
		} else {
			_SRFileListener fileListener = new _SRFileListener(srMgr, 
					Param.PREFIX_RECV, Param.SUFFIX);
			this.fileListener = new FileMonitor(srMgr.getRecvDir(), 
					Param.SCAN_DATA_INTERVAL, fileListener);
		}
	}
	
	public void _start() {
		if(isInit == false) {
			return;
		}
		
		if(Config.getInstn().getRspMode() == ResponseMode.SOCKET) {
			sockListener._start();
			log.info("[{}]-[接收端] 服务已启动", NAME);
			
		} else {
			fileListener._start();
		}
	}
	
	public void _stop() {
		if(isInit == false) {
			return;
		}
		
		if(Config.getInstn().getRspMode() == ResponseMode.SOCKET) {
			sockListener._stop();
			log.info("[{}]-[接收端] 服务已停止", NAME);
			
		} else {
			fileListener._stop();
		}
	}
	
}
