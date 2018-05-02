package exp.fpf.services;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.fpf.Config;
import exp.fpf.cache.SRMgr;
import exp.fpf.envm.Param;
import exp.fpf.proxy._SRFileListener;
import exp.libs.algorithm.struct.queue.pc.PCQueue;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.io.listn.FileMonitor;
import exp.libs.warp.net.sock.io.client.SocketClient;
import exp.libs.warp.thread.LoopThread;

/**
 * <pre>
 * [端口转发代理服务-C] (请求转发器/响应收转器).
 * 	1. 请求转发器: 把[对侧]的请求[转发]到[本侧真正的服务端口].
 * 	2. 响应收转器: 把[本侧真正的服务端口]返回的响应数据[收转]到[对侧].
 * </pre>	
 * <B>PROJECT：</B> file-port-forwarding
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-07-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _FPFClient extends LoopThread {

	private Logger log = LoggerFactory.getLogger(_FPFClient.class);
	
	/** [对侧]请求文件名称正则 */
	private final static String REGEX = "([\\d\\.]+)@(\\d+)-S(\\d+)";
	
	/**
	 * [对侧]请求文件名称正则组ID
	 * 	1: [本侧]真正的服务IP
	 * 	2: [本侧]真正的服务端口
	 * 	3: [对侧]的会话ID
	 */
	private final static int IDX_IP = 1, IDX_PORT = 2, IDX_SID = 3;
	
	private SRMgr srMgr;
	
	private int overtime;
	
	/** 对侧会话ID -> 对侧请求文件队列 */
	private Map<String, PCQueue<String>> sendFiles;
	
	/** 对侧会话ID -> 本侧与真正服务的Socket连接 */
	private Map<String, SocketClient> sessions;
	
	/** 收发目录文件监听器 */
	private FileMonitor srFileMonitor;
	
	protected _FPFClient(SRMgr srMgr, int overtime) {
		super(_FPFClient.class.getName());
		
		this.srMgr = srMgr;
		this.overtime = overtime;
		this.sendFiles = new HashMap<String, PCQueue<String>>();
		this.sessions = new HashMap<String, SocketClient>();
		
		// 设置收发文件目录监听器(只监听 send 文件)
		_SRFileListener fileListener = new _SRFileListener(srMgr, 
				Param.PREFIX_SEND, Param.SUFFIX);
		this.srFileMonitor = new FileMonitor(srMgr.getRecvDir(), 
				Param.SCAN_DATA_INTERVAL, fileListener);
	}

	@Override
	protected void _before() {
		srFileMonitor._start();
		log.info("端口转发数据接收器启动成功");
	}

	@Override
	protected void _loopRun() {
		String sendFileName = srMgr.getSendFile();	// 阻塞
		if(StrUtils.isEmpty(sendFileName)) {
			ThreadUtils.tSleep(Param.SCAN_DATA_INTERVAL);
			return;
		}
		
		String sendFilePath = PathUtils.combine(srMgr.getRecvDir(), sendFileName);
		List<String> features = RegexUtils.findGroups(sendFilePath, REGEX);
		if(features.isEmpty()) {
			FileUtils.delete(sendFilePath);
			log.warn("无效的数据流文件 [{}], 直接删除", sendFilePath);
			
		} else {
			String sessionId = features.get(IDX_SID);
			String ip = features.get(IDX_IP);
			int port = NumUtils.toInt(features.get(IDX_PORT), 0);
			
			PCQueue<String> sendList = sendFiles.get(sessionId);
			SocketClient session = sessions.get(sessionId);
			if(session == null) {
				sendList = new PCQueue<String>(Param.PC_CAPACITY);
				session = new SocketClient(ip, port);
				session.getSocketBean().setOvertime(Config.getInstn().getOvertime());
				
				if(session.conn()) {
					new _TranslateCData(srMgr, sessionId, Param.PREFIX_SEND, 
							overtime, sendList, session.getSocket()).start();	// 请求转发
					new _TranslateCData(srMgr, sessionId, Param.PREFIX_RECV, 
							overtime, sendList, session.getSocket()).start();	// 响应转发
					log.info("新增一个到服务端口 [{}:{}] 的会话 [{}]", ip, port, sessionId);
					
				} else {
					log.warn("会话 [{}] 连接到服务端口 [{}:{}] 失败", sessionId, ip, port);
				}
				
				sessions.put(sessionId, session);
				sendFiles.put(sessionId, sendList);
			}
			
			if(session.isClosed()) {
				sendList.clear();
				sendFiles.remove(sessionId);
				sessions.remove(sessionId);
				FileUtils.delete(sendFilePath);
				
			} else {
				sendList.add(sendFilePath);
			}
		}
	}
	
	@Override
	protected void _after() {
		srFileMonitor._stop();
		
		Iterator<PCQueue<String>> list = sendFiles.values().iterator();
		while(list.hasNext()) {
			list.next().clear();
		}
		sendFiles.clear();
		
		Iterator<SocketClient> socks = sessions.values().iterator();
		while(socks.hasNext()) {
			socks.next().close();
		}
		sessions.clear();
		
		log.info("端口转发数据接收器已停止");
	}
	
}
