package exp.libs.warp.net.pf.file;

import java.util.LinkedList;
import java.util.List;

import exp.libs.utils.io.FileUtils;

public class FPFAgents {

	private _SRFileMgr srFileMgr;
	
	private List<_FPFServer> servers;
	
	private _FPFClient client;
	
	/**
	 * 启动转发代理服务: 只启动客户端
	 * @param srDir
	 */
	public FPFAgents(String srDir) {
		this(srDir, new FPFConfig[0]);
	}
	
	/**
	 * 启动转发代理服务: 启动N服务端和一个客户端
	 * @param srDir
	 * @param serverConfigs
	 */
	public FPFAgents(String srDir, FPFConfig... serverConfigs) {
		this.srFileMgr = new _SRFileMgr(srDir);
		this.servers = new LinkedList<_FPFServer>();
		
		if(serverConfigs != null) {
			for(FPFConfig config : serverConfigs) {
				_FPFServer agent = new _FPFServer(srFileMgr, config);
				servers.add(agent);
			}
		}
		
		this.client = new _FPFClient(srFileMgr, 10000);	//FIXME
	}
	
	private boolean _init() {
		boolean isOk = true;
		
		// 清空所有残留的数据流文件
		isOk &= FileUtils.delete(srFileMgr.getDir());
		isOk &= (FileUtils.createDir(srFileMgr.getDir()) != null);
		return isOk;
	}
	
	public void _start() {
		if(_init()) {
			for(_FPFServer server : servers) {
				server._start();
			}
			client._start();
		} else {
			// TODO 初始化失败
		}
	}
	
	public void _stop() {
		for(_FPFServer server : servers) {
			server._stop();
		}
		servers.clear();
		client._stop();
		srFileMgr.clear();
	}
	
}
