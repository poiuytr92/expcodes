package exp.libs.warp.fpf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import exp.libs.algorithm.struct.queue.pc.PCQueue;
import exp.libs.utils.StrUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.utils.verify.RegexUtils;

class _SRFileMgr {

	protected final static int DEFAULT_CAPACITY = 1024;
	
	private final static String REGEX = "-S(\\d+)";
	
	private String dir;
	
	private PCQueue<String> sendFiles;
	
	/**
	 * sessionId -> recv filenames
	 */
	private Map<String, PCQueue<String>> recvFiles;
	
	/**
	 * 禁忌表: 存储本侧服务端写到外存的流文件, 避免该文件被本侧客户端重新读入
	 */
	private Set<String> sendTabus;
	
	private Set<String> recvTabus;
	
	/**
	 * 
	 * @param dir 数据流文件的收发目录
	 */
	protected _SRFileMgr(String dir) {
		this.dir = dir;
		this.sendFiles = new PCQueue<String>(DEFAULT_CAPACITY);
		this.recvFiles = new HashMap<String, PCQueue<String>>();
		this.sendTabus = new HashSet<String>();
		this.recvTabus = new HashSet<String>();
	}

	protected String getDir() {
		return dir;
	}
	
	protected void addSendTabu(String filePath) {
		filePath = PathUtils.toLinux(filePath);
		synchronized (sendTabus) {
			sendTabus.add(filePath);
		}
	}
	
	protected void addRecvTabu(String filePath) {
		filePath = PathUtils.toLinux(filePath);
		synchronized (recvTabus) {
			recvTabus.add(filePath);
		}
	}
	
	protected void addSendFile(String filePath) {
		filePath = PathUtils.toLinux(filePath);
		synchronized (sendTabus) {
			if(sendTabus.remove(filePath) == true) {
				return;
			}
		}
		
		sendFiles.add(filePath);
	}
	
	protected String getSendFile() {
		return sendFiles.get();	// 阻塞
	}
	
	protected void addRecvFile(String filePath) {
		filePath = PathUtils.toLinux(filePath);
		synchronized (recvTabus) {
			if(recvTabus.remove(filePath) == true) {
				return;
			}
		}
		
		String sessionId = RegexUtils.findFirst(filePath, REGEX);
		if(StrUtils.isNotEmpty(sessionId)) {
			PCQueue<String> list = recvFiles.get(sessionId);
			if(list == null) {
				list = new PCQueue<String>(DEFAULT_CAPACITY);
				recvFiles.put(sessionId, list);
			}
			list.add(filePath);
		}
	}
	
	protected String getRecvFile(String sessionId) {
		String filePath = null;
		PCQueue<String> list = recvFiles.get(sessionId);
		if(list != null) {
			filePath = list.get();
		}
		return filePath;
	}
	
	protected void clearSendFiles() {
		sendFiles.clear();
	}
	
	protected void clearRecvFiles(String sessionId) {
		PCQueue<String> list = recvFiles.remove(sessionId);
		if(list != null) {
			list.clear();
		}
	}
	
	protected void clear() {
		Iterator<PCQueue<String>> rLists = recvFiles.values().iterator();
		while(rLists.hasNext()) {
			rLists.next().clear();
		}
		recvFiles.clear();
		sendFiles.clear();
		
		recvTabus.clear();
		sendTabus.clear();
	}
	
}
