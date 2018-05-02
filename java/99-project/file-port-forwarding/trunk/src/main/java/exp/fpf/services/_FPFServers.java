package exp.fpf.services;

import java.util.LinkedList;
import java.util.List;

import exp.fpf.bean.FPFConfig;
import exp.fpf.cache.SRMgr;
import exp.fpf.proxy.Recver;
import exp.libs.utils.other.ListUtils;

class _FPFServers {

	private List<_FPFServer> servers;
	
	protected _FPFServers(SRMgr srMgr, int overtime, 
			List<FPFConfig> serverConfigs) {
		this.servers = new LinkedList<_FPFServer>();
		if(ListUtils.isNotEmpty(serverConfigs)) {
			for(FPFConfig config : serverConfigs) {
				_FPFServer server = new _FPFServer(srMgr, overtime, config);
				servers.add(server);
			}
			
			Recver.getInstn()._init(srMgr);
		}
	}
	
	public boolean _start() {
		boolean isOk = true;
		Recver.getInstn()._start();
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
		Recver.getInstn()._stop();
	}
	
}
