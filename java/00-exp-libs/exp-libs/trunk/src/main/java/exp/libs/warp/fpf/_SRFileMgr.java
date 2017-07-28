package exp.libs.warp.fpf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import exp.libs.algorithm.struct.queue.pc.PCQueue;
import exp.libs.utils.StrUtils;
import exp.libs.utils.verify.RegexUtils;

// FIXME 改成实例对象， CS各持有一个
class _SRFileMgr {

	private final static int DEFAULT_CAPACITY = 1024;
	
	private final static String REGEX = "-S(\\d+)";
	
	private PCQueue<String> sendFiles;
	
	/**
	 * sessionId -> recv filenames
	 */
	private Map<String, PCQueue<String>> recvFiles;
	
	private static volatile _SRFileMgr instance;
	
	private _SRFileMgr() {
		this.sendFiles = new PCQueue<String>(DEFAULT_CAPACITY);
		this.recvFiles = new HashMap<String, PCQueue<String>>();
	}

	private static _SRFileMgr getInstn() {
		if(instance == null) {
			synchronized (_SRFileMgr.class) {
				if(instance == null) {
					instance = new _SRFileMgr();
				}
			}
		}
		return instance;
	}
	
	private void _addSendFile(String filePath) {
		sendFiles.add(filePath);
	}
	
	private String _getSendFile() {
		return sendFiles.get();	// 阻塞
	}
	
	private void _addRecvFile(String filePath) {
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
	
	private String _getRecvFile(String sessionId) {
		String filePath = null;
		PCQueue<String> list = recvFiles.get(sessionId);
		if(list != null) {
			filePath = list.get();
		}
		return filePath;
	}
	
	private void _clearSendFiles() {
		sendFiles.clear();
	}
	
	private void _clearRecvFiles(String sessionId) {
		PCQueue<String> list = recvFiles.remove(sessionId);
		if(list != null) {
			list.clear();
		}
	}
	
	private void _clear() {
		sendFiles.clear();
		
		Iterator<PCQueue<String>> rLists = recvFiles.values().iterator();
		while(rLists.hasNext()) {
			rLists.next().clear();
		}
		recvFiles.clear();
	}
	
	protected static void addSendFile(String filePath) {
		getInstn()._addSendFile(filePath);
	}
	
	protected static String getSendFile() {
		return getInstn()._getSendFile();
	}
	
	protected static void addRecvFile(String filePath) {
		getInstn()._addRecvFile(filePath);
	}
	
	protected static String getRecvFile(String sessionId) {
		return getInstn()._getRecvFile(sessionId);
	}
	
	protected static void clearSendFiles() {
		getInstn()._clearSendFiles();
	}
	
	protected static void clearRecvFiles(String sessionId) {
		getInstn()._clearRecvFiles(sessionId);
	}
	
	protected static void clear() {
		getInstn()._clear();
	}
	
}
