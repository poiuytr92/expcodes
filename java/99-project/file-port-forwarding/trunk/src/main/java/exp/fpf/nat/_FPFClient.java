package exp.fpf.nat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.fpf.Config;
import exp.fpf.cache.SRMgr;
import exp.fpf.envm.Param;
import exp.fpf.envm.ResponseMode;
import exp.fpf.tunnel.Sender;
import exp.fpf.tunnel._SRFileListener;
import exp.fpf.utils.BIZUtils;
import exp.libs.envm.Charset;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.BoolUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.io.listn.FileMonitor;
import exp.libs.warp.thread.LoopThread;

/**
 * <pre>
 * [端口转发代理服务-C]
 *  把[对侧]的 请求 分配到对应的代理会话线程
 *  
 * </pre>	
 * <br/><B>PROJECT : </B> file-port-forwarding
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2017-07-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _FPFClient extends LoopThread {

	/** 日志器 */
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
	
	/** 收发管理器 */
	private SRMgr srMgr;
	
	/** 超时时间 */
	private int overtime;
	
	/** 对侧会话ID -> 本侧与真正服务的会话 */
	private Map<String, _FPFClientSession> sessions;
	
	/** 收发目录文件监听器 */
	private FileMonitor srFileMonitor;
	
	/**
	 * 构造函数
	 * @param srMgr
	 * @param overtime
	 */
	protected _FPFClient(SRMgr srMgr, int overtime) {
		super(_FPFClient.class.getName());
		
		this.srMgr = srMgr;
		this.overtime = overtime;
		this.sessions = new HashMap<String, _FPFClientSession>();
		
		// 设置收发文件目录监听器(只监听 send 文件)
		_SRFileListener fileListener = new _SRFileListener(srMgr, 
				Param.PREFIX_SEND, Param.SUFFIX);
		this.srFileMonitor = new FileMonitor(srMgr.getRecvDir(), 
				Param.SCAN_DATA_INTERVAL, fileListener);
	}

	@Override
	protected void _before() {
		notifyReset();
		srFileMonitor._start();
		log.info("端口转发代理服务启动成功");
	}

	@Override
	protected void _loopRun() {
		String sendFileName = srMgr.getSendFile();	// 阻塞
		if(StrUtils.isEmpty(sendFileName)) {
			ThreadUtils.tSleep(Param.SCAN_DATA_INTERVAL);
			return;
		}
		
		// 每次会话交互有10%的几率清除无效会话（目的是惰性删除，避免过份耗时）
		if(BoolUtils.hit(10)) {
			delInvaildSession();
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
			
			// 分配代理会话
			boolean isNewSession = false;
			_FPFClientSession session = sessions.get(sessionId);
			if(session == null) {
				session = new _FPFClientSession(srMgr, overtime, sessionId, ip, port);
				sessions.put(sessionId, session);
				isNewSession = true;
			}
			
			// 分配请求
			int timeSequence = session.add(sendFilePath);
			if(isNewSession == true) {
				if(timeSequence == 0) {
					session.start();	// 通过异步方式建立会话连接, 避免阻塞其他会话
					
				} else {
					// 若本侧判定是新会话，但时序不为0，说明本侧因为某些原因已经丢失了此会话
					// 但是对端无法感知到到 这个情况，依然使用此会话进行通信，从而导致时序不为0
					// 此时此会话已不能使用，需要通知对端重新申请一个会话
					log.debug("会话 [{}] 已失效(因本侧单方面超时/重启导致).", sessionId);
					session.notifyExit();
				}
			}
		}
	}
	
	@Override
	protected void _after() {
		srFileMonitor._stop();
		
		delAllSession();
		log.info("端口转发代理服务已停止");
	}
	
	/**
	 * 通知对侧进行会话重置（清除本侧重启前残留的所有无效会话）
	 */
	protected void notifyReset() {
		String data = Param.MARK_RESET;
		if(ResponseMode.SOCKET == Config.getInstn().getRspMode()) {
			String json = BIZUtils.genJsonData(Param.MGR_SESS_ID, data);
			Sender.getInstn().send(json);
			
		} else {
			String recvFilePath = BIZUtils.genFileDataPath(
					srMgr, Param.MGR_SESS_ID, 0, Param.PREFIX_RECV, 
					Param.MGR_SESS_IP, Param.MGR_SESS_PORT);
			FileUtils.write(recvFilePath, data, Charset.ISO, false);
		}
		log.debug("已通知 [端口转发代理服务] 重置所有会话");
	}
	
	/**
	 * 清理无效会话
	 */
	private void delInvaildSession() {
		Iterator<_FPFClientSession> sessIts = sessions.values().iterator();
		while(sessIts.hasNext()) {
			_FPFClientSession session = sessIts.next();
			if(session.isClosed()) {
				session.notifyExit();
				sessIts.remove();
			}
		}
	}
	
	/**
	 * 清理所有会话
	 */
	private void delAllSession() {
		Iterator<_FPFClientSession> sessIts = sessions.values().iterator();
		while(sessIts.hasNext()) {
			_FPFClientSession session = sessIts.next();
			session.notifyExit();
			sessIts.remove();
		}
		sessions.clear();
	}
	
}
