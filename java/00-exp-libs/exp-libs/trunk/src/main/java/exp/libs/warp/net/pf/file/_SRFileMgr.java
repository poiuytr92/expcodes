package exp.libs.warp.net.pf.file;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import exp.libs.algorithm.struct.queue.pc.PCQueue;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.num.BODHUtils;
import exp.libs.utils.num.IDUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;

class _SRFileMgr {

	private final static String REGEX = "-S(\\d+)";
	
	private String sendDir;
	
	private String recvDir;
	
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
	protected _SRFileMgr(String sendDir, String recvDir) {
		this.sendDir = (sendDir == null ? "" : sendDir.trim());
		this.recvDir = (recvDir == null ? "" : recvDir.trim());
		this.sendFiles = new PCQueue<String>(_Envm.PC_CAPACITY);
		this.recvFiles = new HashMap<String, PCQueue<String>>();
		this.sendTabus = new HashSet<String>();
		this.recvTabus = new HashSet<String>();
	}

	protected String getSendDir() {
		return sendDir;
	}

	protected String getRecvDir() {
		return recvDir;
	}
	
	protected void addSendTabu(String fileName) {
		synchronized (sendTabus) {
			sendTabus.add(fileName);
		}
	}
	
	protected void addRecvTabu(String fileName) {
		synchronized (recvTabus) {
			recvTabus.add(fileName);
		}
	}
	
	protected void addSendFile(String fileName) {
		synchronized (sendTabus) {
			if(sendTabus.remove(fileName) == true) {
				return;
			}
		}
		
		sendFiles.add(fileName);
	}
	
	protected String getSendFile() {
		return sendFiles.get();	// 阻塞
	}
	
	protected void addRecvFile(String fileName) {
		synchronized (recvTabus) {
			if(recvTabus.remove(fileName) == true) {
				return;
			}
		}
		
		String sessionId = RegexUtils.findFirst(fileName, REGEX);
		if(StrUtils.isNotEmpty(sessionId)) {
			PCQueue<String> list = recvFiles.get(sessionId);
			if(list == null) {
				list = new PCQueue<String>(_Envm.PC_CAPACITY);
				recvFiles.put(sessionId, list);
			}
			list.add(fileName);
		}
	}
	
	protected String getRecvFile(String sessionId) {
		String fileName = null;
		PCQueue<String> list = recvFiles.get(sessionId);
		if(list != null) {
			fileName = list.getQuickly();
		}
		return fileName;
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
	
	protected static String encode(byte[] data, int offset, int len) {
		String hex = BODHUtils.toHex(data, offset, len);
		return CryptoUtils.toDES(hex);
	}
	
	protected static byte[] decode(String data) {
		String hex = CryptoUtils.deDES(data);
		return BODHUtils.toBytes(hex);
	}
	
	protected static String toFileName(String sessionId, 
			String fileType, String snkIP, int snkPort) {
		String fileName = StrUtils.concat(fileType, "#", snkIP, "@", snkPort, 
				"-S", sessionId, "-T", IDUtils.getTimeID(), _Envm.SUFFIX);
		return fileName;
	}
	
}
