package exp.libs.warp.fpf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.algorithm.struct.queue.pc.PCQueue;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.io.listn.FileMonitor;
import exp.libs.warp.net.socket.bean.SocketBean;
import exp.libs.warp.net.socket.io.client.SocketClient;
import exp.libs.warp.thread.LoopThread;

class _FPFClient extends LoopThread {

	private Logger log = LoggerFactory.getLogger(_FPFClient.class);
	
	private final static String REGEX = "([\\d\\.]+)@(\\d+)-T\\d+-S(\\d+)";
	
	private final static int IDX_IP = 1, IDX_PORT = 2, IDX_SID = 3;
	
	private final static int PC_CAPACITY = _SRFileMgr.DEFAULT_CAPACITY;
	
	/**
	 * sessionId -> SocketClient
	 */
	private Map<String, SocketClient> clients;
	
	/**
	 * sessionId -> 文件名称集
	 */
	private Map<String, PCQueue<String>> sFiles;
	
	private _SRFileMgr srFileMgr;
	
	private int overtime;
	
	private FileMonitor fileMonitor;
	
	protected _FPFClient(_SRFileMgr srFileMgr, int overtime) {
		super("IOPFClient");	// FIXME
		this.clients = new HashMap<String, SocketClient>();
		this.sFiles = new HashMap<String, PCQueue<String>>();
		
		this.srFileMgr = srFileMgr;
		this.overtime = overtime;
		
		// FIXME 间隔时间
		_FileListener fileListener = new _FileListener(srFileMgr, 
				_FileListener.PREFIX_SEND, _FileListener.SUFFIX);
		this.fileMonitor = new FileMonitor(srFileMgr.getDir(), 100, fileListener);
	}

	@Override
	protected void _before() {
		fileMonitor._start();
		log.info("启动端口转发接收器成功");
	}

	@Override
	protected void _loopRun() {
		String sendFilePath = srFileMgr.getSendFile();	// 阻塞
		
		SocketClient client = null;
		String sessionId = null;
		String ip = null;
		int port = 0;
		List<String> datas = RegexUtils.findFirstMatches(sendFilePath, REGEX);
		if(!datas.isEmpty()) {
			sessionId = datas.get(IDX_SID);
			ip = datas.get(IDX_IP);
			port = NumUtils.toInt(datas.get(IDX_PORT), 0);
			
			client = clients.get(sessionId);
			PCQueue<String> list = sFiles.get(sessionId);
			if(client == null) {
				SocketBean sb = new SocketBean(ip, port);
				client = new SocketClient(sb);
				clients.put(sessionId, client);
				
				list = new PCQueue<String>(PC_CAPACITY);
				sFiles.put(sessionId, list);
			}
			
			
			if(client.isClosed()) {
				client.reconn();
				
				// FIXME 参数
				new _TranslateCData(sessionId, _TranslateCData.PREFIX_SEND, overtime, 
						client.getSocket(), ip, port, srFileMgr, list).start();
				new _TranslateCData(sessionId, _TranslateCData.PREFIX_RECV, overtime, 
						client.getSocket(), ip, port, srFileMgr, list).start();
			}
			
			list.add(sendFilePath);
		}
	}
	
	@Override
	protected void _after() {
		fileMonitor._stop();
		Iterator<SocketClient> sockets = clients.values().iterator();
		while(sockets.hasNext()) {
			sockets.next().close();
		}
		clients.clear();
		
		Iterator<PCQueue<String>> list = sFiles.values().iterator();
		while(list.hasNext()) {
			list.next().clear();
		}
		sFiles.clear();
		
		log.info("启动端口转发接收器停止");
	}
	
}
