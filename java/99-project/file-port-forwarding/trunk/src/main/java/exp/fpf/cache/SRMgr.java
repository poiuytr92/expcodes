package exp.fpf.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.fpf.envm.Param;
import exp.libs.algorithm.struct.queue.pc.PCQueue;
import exp.libs.envm.Charset;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;

/**
 * <pre>
 * 收发数据管理器
 * </pre>	
 * <br/><B>PROJECT : </B> file-port-forwarding
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2017-07-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class SRMgr {

	/** 日志器 */
	private Logger log = LoggerFactory.getLogger(SRMgr.class);
	
	/** 提取会话ID与文件时序的正则 */
	private final static String REGEX = "-S(\\d+)-T(\\d+)";
	
	/** 发送文件目录 */
	private String sendDir;
	
	/** 接收文件目录 */
	private String recvDir;
	
	/** 被第三方程序转发的、从真正客户端发出的请求数据文件集合 */
	private PCQueue<String> sendFiles;
	
	/**
	 * 被内置Socket转发的、从真正服务端返回的响应数据集合.
	 * 
	 * 适用于socket监听模式
	 * sessionId -> recv data
	 */
	private Map<String, PCQueue<String>> recvDatas;
	
	/**
	 * 被第三方程序转发的、从真正服务端返回的响应数据文件集合.
	 * 
	 * 适用于文件扫描模式.
	 * sessionId -> recv filenames
	 */
	private Map<String, PCQueue<String>> recvFiles;
	
	/**
	 * 缓存被第三方程序转发的、从真正服务端返回的文件
	 * (用于调整文件时序, 避免发送时序错乱导致会话异常).
	 * 
	 * 适用于文件扫描模式.
	 * sessionId -> recv file cache
	 */
	private Map<String, RecvCache> recvCaches;
	
	/**
	 * 禁忌表: 存储本侧服务端写到外存的流文件, 避免该文件被本侧客户端重新读入
	 */
	private Set<String> sendTabus;
	
	/**
	 * 禁忌表: 存储本侧客户端写到外存的流文件, 避免该文件被本侧服务端重新读入
	 */
	private Set<String> recvTabus;
	
	/**
	 * 构造函数
	 * @param sendDir
	 * @param recvDir
	 */
	public SRMgr(String sendDir, String recvDir) {
		this.sendDir = (sendDir == null ? "" : sendDir.trim());
		this.recvDir = (recvDir == null ? "" : recvDir.trim());
		this.sendFiles = new PCQueue<String>(Param.PC_CAPACITY);
		this.recvDatas = new HashMap<String, PCQueue<String>>();
		this.recvFiles = new HashMap<String, PCQueue<String>>();
		this.recvCaches = new HashMap<String, RecvCache>();
		this.sendTabus = new HashSet<String>();
		this.recvTabus = new HashSet<String>();
	}

	public String getSendDir() {
		return sendDir;
	}

	public String getRecvDir() {
		return recvDir;
	}
	
	public void addSendTabu(String fileName) {
		synchronized (sendTabus) {
			sendTabus.add(fileName);
		}
	}
	
	public void addRecvTabu(String fileName) {
		synchronized (recvTabus) {
			recvTabus.add(fileName);
		}
	}
	
	/**
	 * 添加被第三方程序转发的、从真正客户端发出的请求数据文件到缓存队列
	 * @param fileName
	 */
	public void addSendFile(String fileName) {
		synchronized (sendTabus) {
			if(sendTabus.remove(fileName) == true) {
				return;
			}
		}
		
		sendFiles.add(fileName);
	}
	
	/**
	 * 获取被第三方程序转发的、从真正客户端发出的请求数据文件
	 * @return
	 */
	public String getSendFile() {
		return sendFiles.get();	// 阻塞
	}
	
	/**
	 * 清理请求数据文件
	 */
	public void clearSendFiles() {
		sendFiles.clear();
	}
	
	/**
	 * 添加被第三方程序转发的、从真正服务端返回的响应数据文件到缓存队列.
	 * (适用于文件扫描模式)
	 * @param fileName
	 */
	public void addRecvFile(String fileName) {
		synchronized (recvTabus) {
			if(recvTabus.remove(fileName) == true) {
				return;
			}
		}
		
		List<String> groups = RegexUtils.findGroups(fileName, REGEX);
		if(groups.size() != 2) {
			return;
		}
		
		String sessionId = groups.get(0);
		int timeSequence = NumUtils.toInt(groups.get(1), -1);
		
		if(Param.MGR_SESS_ID.equals(sessionId)) {
			String filePath = PathUtils.combine(recvDir, fileName);
			String data = FileUtils.read(filePath, Charset.ISO);
			if(Param.MARK_RESET.equals(data)) {
				SessionMgr.getInstn().clear();
				clear();
				log.warn("端口转发代理服务已重启, 所有会话重置");
			}
			FileUtils.delete(filePath);
			
		} else if(StrUtils.isNotEmpty(sessionId) && timeSequence >= 0) {
			PCQueue<String> list = recvFiles.get(sessionId);
			if(list == null) {
				list = new PCQueue<String>(Param.PC_CAPACITY);
				recvFiles.put(sessionId, list);
			}
			
			RecvCache cache = recvCaches.get(sessionId);
			if(cache == null) {
				cache = new RecvCache();
				recvCaches.put(sessionId, cache);
			}
			
			// 在缓存调整接收文件的时序后再放进文件接收队列
			cache.add(timeSequence, fileName);
			List<String> fileNames = cache.getAll();
			for(String fn : fileNames) {
				list.add(fn);
			}
		}
	}
	
	/**
	 * 获取被第三方程序转发的、从真正服务端返回的响应数据文件
	 * @param sessionId
	 * @return
	 */
	public String getRecvFile(String sessionId) {
		String fileName = null;
		PCQueue<String> list = recvFiles.get(sessionId);
		if(list != null) {
			fileName = list.getQuickly();
		}
		return fileName;
	}
	
	/**
	 * 清理某个会话响应数据文件
	 * @param sessionId
	 */
	public void clearRecvFiles(String sessionId) {
		PCQueue<String> list = recvFiles.remove(sessionId);
		if(list != null) {
			list.clear();
		}
	}
	
	/**
	 * 添加被内置Socket转发的、从真正服务端返回的响应数据到缓存队列
	 * @param jsonData
	 */
	public void addRecvData(String jsonData) {
		try {
			JSONObject json = JSONObject.fromObject(jsonData);
			String sessionId = JsonUtils.getStr(json, Param.SID);
			String data = JsonUtils.getStr(json, Param.DATA);
			
			if(Param.MGR_SESS_ID.equals(sessionId)) {
				if(Param.MARK_RESET.equals(data)) {
					SessionMgr.getInstn().clear();
					clear();
					log.warn("端口转发代理服务已重启, 所有会话重置");
				}
				
			} else if(StrUtils.isNotEmpty(sessionId)) {
				PCQueue<String> list = recvDatas.get(sessionId);
				if(list == null) {
					list = new PCQueue<String>(Param.PC_CAPACITY);
					recvDatas.put(sessionId, list);
				}
				list.add(data);
			}
		} catch(Exception e) {
			log.warn("丢弃非JSON格式数据: [{}]", jsonData);
		}
	}
	
	/**
	 * 获取被内置Socket转发的、从真正服务端返回的响应数据
	 * @param sessionId
	 * @return
	 */
	public String getRecvData(String sessionId) {
		String data = null;
		PCQueue<String> list = recvDatas.get(sessionId);
		if(list != null) {
			data = list.getQuickly();
		}
		return data;
	}
	
	/**
	 * 清理某个会话响应数据缓存
	 * @param sessionId
	 */
	public void clearRecvDatas(String sessionId) {
		PCQueue<String> list = recvDatas.remove(sessionId);
		if(list != null) {
			list.clear();
		}
	}
	
	/**
	 * 清理收发管理器缓存
	 */
	public void clear() {
		Iterator<PCQueue<String>> dLists = recvDatas.values().iterator();
		while(dLists.hasNext()) {
			dLists.next().clear();
		}
		recvDatas.clear();
		
		Iterator<PCQueue<String>> fLists = recvFiles.values().iterator();
		while(fLists.hasNext()) {
			fLists.next().clear();
		}
		recvFiles.clear();
		
		Iterator<RecvCache> cLists = recvCaches.values().iterator();
		while(cLists.hasNext()) {
			cLists.next().clear();
		}
		recvCaches.clear();
		
		sendFiles.clear();
		recvTabus.clear();
		sendTabus.clear();
	}
	
}
