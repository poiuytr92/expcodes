package exp.fpf.nat;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.fpf.Config;
import exp.fpf.cache.RecvCache;
import exp.fpf.cache.SRMgr;
import exp.fpf.envm.Param;
import exp.fpf.envm.ResponseMode;
import exp.fpf.tunnel.Sender;
import exp.fpf.utils.BIZUtils;
import exp.libs.algorithm.struct.queue.pc.PCQueue;
import exp.libs.envm.Charset;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.net.sock.bean.SocketBean;
import exp.libs.warp.net.sock.io.client.SocketClient;

/**
 * <pre>
 * [端口转发代理会话线程-C] (请求转发器/响应收转器).
 * 	1. 请求转发器: 把[对侧]的请求[转发]到[本侧真正的服务端口].
 * 	2. 响应收转器: 把[本侧真正的服务端口]返回的响应数据[收转]到[对侧].
 * </pre>	
 * <br/><B>PROJECT : </B> file-port-forwarding
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2017-07-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _FPFClientSession extends Thread {

	/** 日志器 */
	private Logger log = LoggerFactory.getLogger(_FPFClientSession.class);
	
	/** 提取文件时序的正则 */
	private final static String REGEX = "-T(\\d+)";
	
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
	private PCQueue<String> fileList;
	
	/** 缓存接收到的对侧请求文件(用于调整文件时序, 避免发送时序错乱导致会话异常) */
	private RecvCache fileCache;
	
	/** 是否已初始化过 */
	private boolean isInit;
	
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
		SocketBean sockConf = Config.getInstn().newSocketConf(ip, port);
		this.session = new SocketClient(sockConf);
		this.fileList = new PCQueue<String>(Param.PC_CAPACITY);
		this.fileCache = new RecvCache();
		this.isInit = false;
	}

	@Override
	public void run() {
		try {
			if(session.conn()) {	// 此方法会阻塞, 为了不影响其他会话, 需要放在线程中运行
				_TranslateCData sender = new _TranslateCData(
						srMgr, sessionId, Param.PREFIX_SEND, 
						overtime, fileList, session.getSocket());	// 请求转发
				_TranslateCData recver = new _TranslateCData(
						srMgr, sessionId, Param.PREFIX_RECV, 
						overtime, fileList, session.getSocket());	// 响应转发
				
				sender.start();
				recver.start();
				log.info("新增一个到服务端口 [{}:{}] 的会话 [{}]", ip, port, sessionId);
				
			} else {
				log.warn("会话 [{}] 连接到服务端口 [{}:{}] 失败", sessionId, ip, port);
				notifyExit();
			}
		} catch(Throwable e) {
			log.error("内存不足, 无法再分配代理会话", e);
		}
		isInit = true;
	}
	
	/**
	 * 添加 [对侧] 请求文件
	 * @param filePath  [对侧] 请求文件
	 * @return 该文件的时序
	 */
	protected int add(String filePath) {
		int timeSequence = NumUtils.toInt(RegexUtils.findFirst(filePath, REGEX), -1);
		if(timeSequence >= 0) {
			fileCache.add(timeSequence, filePath);
			List<String> filePaths = fileCache.getAll();
			for(String fp : filePaths) {
				fileList.add(fp);
			}
		}
		return timeSequence;
	}
	
	/**
	 * 会话连接到真正的服务端口失败, [转发流程2] 和 [转发流程3] 均不执行.
	 * 直接反馈断开连接通知.
	 */
	protected void notifyExit() {
		String data = Param.MARK_EXIT;
		if(ResponseMode.SOCKET == Config.getInstn().getRspMode()) {
			String json = BIZUtils.genJsonData(sessionId, data);
			Sender.getInstn().send(json);
			log.debug("会话 [{}] 失效, 已通过 [SOCKET] 反馈到对端请求终止 : \r\n{}", 
					sessionId, json);
			
		} else {
			String recvFilePath = BIZUtils.genFileDataPath(
					srMgr, sessionId, 0, Param.PREFIX_RECV, ip, port);
			FileUtils.write(recvFilePath, data, Charset.ISO, false);
			log.debug("会话 [{}] 失效, 已通过 [{}] 反馈到对端请求终止 : \r\n{}", 
					sessionId, recvFilePath, data);
		}
		clear();
	}
	
	/**
	 * 代理会话是否已断开
	 * @return
	 */
	protected boolean isClosed() {
		return isInit ? session.isClosed() : false;
	}
	
	/**
	 * 清理内存
	 */
	protected void clear() {
		while(!fileList.isEmpty()) {
			FileUtils.delete(fileList.getQuickly());
		}
	}
	
}
