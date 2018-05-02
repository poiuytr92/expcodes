package exp.fpf.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;
import exp.fpf.envm.Param;
import exp.libs.algorithm.struct.queue.pc.PCQueue;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;

/**
 * <pre>
 * 收发数据管理器
 * </pre>	
 * <B>PROJECT：</B> file-port-forwarding
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-01-16
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class SRMgr {

	private final static String REGEX = "-S(\\d+)-T(\\d+)";
	
	private String sendDir;
	
	private String recvDir;
	
	/**
	 * 从真正客户端发出的请求数据文件集合
	 */
	private PCQueue<String> sendFiles;
	
	/**
	 * 从真正服务端返回的响应数据集合.
	 * 
	 * 适用于socket监听模式
	 * sessionId -> recv data
	 */
	private Map<String, PCQueue<String>> recvDatas;
	
	/**
	 * 从真正服务端返回的响应数据文件集合.
	 * 
	 * 适用于文件扫描模式.
	 * sessionId -> recv filenames
	 */
	private Map<String, PCQueue<String>> recvFiles;
	
	/**
	 * 缓存接收到的文件(用于调整文件时序, 避免发送时序错乱导致会话异常).
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
	 * 
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
	
	public void addSendFile(String fileName) {
		synchronized (sendTabus) {
			if(sendTabus.remove(fileName) == true) {
				return;
			}
		}
		
		sendFiles.add(fileName);
	}
	
	public String getSendFile() {
		return sendFiles.get();	// 阻塞
	}
	
	public void clearSendFiles() {
		sendFiles.clear();
	}
	
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
		if(StrUtils.isNotEmpty(sessionId) && timeSequence >= 0) {
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
			while(true) {
				fileName = cache.get();
				if(StrUtils.isNotEmpty(fileName)) {
					list.add(fileName);
				} else {
					break;
				}
			}
		}
	}
	
	public String getRecvFile(String sessionId) {
		String fileName = null;
		PCQueue<String> list = recvFiles.get(sessionId);
		if(list != null) {
			fileName = list.getQuickly();
		}
		return fileName;
	}
	
	public void clearRecvFiles(String sessionId) {
		PCQueue<String> list = recvFiles.remove(sessionId);
		if(list != null) {
			list.clear();
		}
	}
	
	public void addRecvData(String jsonData) {
		try {
			JSONObject json = JSONObject.fromObject(jsonData);
			String sessionId = JsonUtils.getStr(json, Param.SID);
			if(StrUtils.isNotEmpty(sessionId)) {
				PCQueue<String> list = recvDatas.get(sessionId);
				if(list == null) {
					list = new PCQueue<String>(Param.PC_CAPACITY);
					recvDatas.put(sessionId, list);
				}
				
				String data = JsonUtils.getStr(json, Param.DATA);
				list.add(data);
			}
		} catch(Exception e) {
			// Undo 非json数据则丢弃
		}
	}
	
	public String getRecvData(String sessionId) {
		String data = null;
		PCQueue<String> list = recvDatas.get(sessionId);
		if(list != null) {
			data = list.getQuickly();
		}
		return data;
	}
	
	public void clearRecvDatas(String sessionId) {
		PCQueue<String> list = recvDatas.remove(sessionId);
		if(list != null) {
			list.clear();
		}
	}
	
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
