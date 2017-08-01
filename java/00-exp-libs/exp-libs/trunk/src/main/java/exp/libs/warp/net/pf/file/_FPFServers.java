package exp.libs.warp.net.pf.file;

import java.util.LinkedList;
import java.util.List;

import exp.libs.warp.io.listn.FileMonitor;

class _FPFServers {

	private List<_FPFServer> servers;
	
	/** 收发目录文件监听器 */
	private FileMonitor srFileMonitor;
	
	protected _FPFServers(_SRFileMgr srFileMgr, int overtime, 
			List<FPFConfig> serverConfigs) {
		this.servers = new LinkedList<_FPFServer>();
		if(serverConfigs != null) {
			for(FPFConfig config : serverConfigs) {
				_FPFServer server = new _FPFServer(srFileMgr, overtime, config);
				servers.add(server);
			}
			
			// 设置收发文件目录监听器(只监听 recv 文件)
			_SRFileListener fileListener = new _SRFileListener(srFileMgr, 
					_Envm.PREFIX_RECV, _Envm.SUFFIX);
			this.srFileMonitor = new FileMonitor(srFileMgr.getDir(), 
					_Envm.SCAN_FILE_INTERVAL, fileListener);
		}
	}
	
	public boolean _start() {
		boolean isOk = true;
		srFileMonitor._start();
		for(_FPFServer server : servers) {
			isOk &= server._start();
		}
		return isOk;
	}
	
	public void _stop() {
		for(_FPFServer server : servers) {
			server._stop();
		}
		servers.clear();
		srFileMonitor._stop();
	}
	
}
