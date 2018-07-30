package exp.fpf.services;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.fpf.cache.SRMgr;
import exp.fpf.envm.Param;
import exp.fpf.proxy._SRFileListener;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.os.ThreadUtils;
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
		delInvaildSession();	// 耗时太多 FIXME 改成惰性删除/随机删除/按最后一次使用时间排序删除
		
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
				session.start();	// 通过异步方式建立会话连接, 避免阻塞其他会话
				isNewSession = true;
			}
			
			// 分配请求
			int timeSequence = session.add(sendFilePath);
			
			// FIXME: 临时手段，现场长时间运行会出现一个异常： 同一个会话的多次请求
			// 会在前面 sessions.get拆分成多个会话，间接产生多对收发线程，导致转发不通
			if(isNewSession == true && timeSequence > 0) {
				log.error("会话 [{}] 发生并发错位, 程序终止.", sessionId);
				System.exit(1);
			}
		}
	}
	
	@Override
	protected void _after() {
		srFileMonitor._stop();
		
		delAllSession();
		log.info("端口转发数据接收器已停止");
	}
	
	/**
	 * 清理无效会话
	 */
	private void delInvaildSession() {
		Iterator<_FPFClientSession> sessIts = sessions.values().iterator();
		while(sessIts.hasNext()) {
			_FPFClientSession session = sessIts.next();
			if(session.isClosed()) {
				session.clear();
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
			session.clear();
			sessIts.remove();
		}
		sessions.clear();
	}
	
}
