package exp.fpf.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.fpf.cache.SRMgr;
import exp.fpf.envm.Param;
import exp.libs.algorithm.struct.queue.pc.PCQueue;
import exp.libs.warp.net.sock.io.client.SocketClient;

/**
 * <pre>
 * [端口转发代理会话线程-C] (请求转发器/响应收转器).
 * 	1. 请求转发器: 把[对侧]的请求[转发]到[本侧真正的服务端口].
 * 	2. 响应收转器: 把[本侧真正的服务端口]返回的响应数据[收转]到[对侧].
 * </pre>	
 * <B>PROJECT：</B> file-port-forwarding
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-07-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _FPFClientSession extends Thread {

	/** 日志器 */
	private Logger log = LoggerFactory.getLogger(_FPFClientSession.class);
	
	/** 收发文件管理器 */
	private SRMgr srMgr;
	
	/** 超时时间 */
	private int overtime;
	
	/** 会话ID */
	private String sessionId;
	
	/** 真实服务IP */
	private String ip;
	
	/** 真实服务端口 */
	private int port;
	
	/** 本侧与真正服务的Socket连接 */
	private SocketClient session;
	
	/** 对侧请求文件队列 */
	private PCQueue<String> sendList;
	
	/** 请求转发器: 把[对侧]的请求[转发]到[本侧真正的服务端口] */
	private _TranslateCData sender;
	
	/** 响应收转器: 把[本侧真正的服务端口]返回的响应数据[收转]到[对侧] */
	private _TranslateCData recver;
	
	/** 是否已初始化 */
	private boolean init;
	
	/**
	 * 构造函数
	 * @param sessionId
	 * @param ip
	 * @param port
	 */
	protected _FPFClientSession(SRMgr srMgr, int overtime, 
			String sessionId, String ip, int port) {
		super("");
		
		this.srMgr = srMgr;
		this.overtime = overtime;
		this.sessionId = sessionId;
		this.ip = ip;
		this.port = port;
		this.session = new SocketClient(ip, port, overtime);
		this.sendList = new PCQueue<String>(Param.PC_CAPACITY);
		this.init = false;
	}

	@Override
	public void run() {
		if(session.conn()) {	// 此方法会阻塞, 为了不影响其他会话, 需要放在线程中运行
			this.sender = new _TranslateCData(srMgr, sessionId, Param.PREFIX_SEND, 
					overtime, sendList, session.getSocket());	// 请求转发
			this.recver = new _TranslateCData(srMgr, sessionId, Param.PREFIX_RECV, 
					overtime, sendList, session.getSocket());	// 响应转发
			sender.start();
			recver.start();
			log.info("新增一个到服务端口 [{}:{}] 的会话 [{}]", ip, port, sessionId);
			
		} else {
			log.warn("会话 [{}] 连接到服务端口 [{}:{}] 失败", sessionId, ip, port);
		}
		init = true;
	}
	
	/**
	 * 添加 [对侧] 请求文件
	 * @param sendFilePath  [对侧] 请求文件
	 */
	protected void add(String sendFilePath) {
		sendList.add(sendFilePath);
	}
	
	/**
	 * 代理会话是否已断开
	 * @return
	 */
	protected boolean isClosed() {
		return init ? session.isClosed() : false;
	}
	
	/**
	 * 清理内存
	 */
	protected void clear() {
		sendList.clear();
	}
	
}
