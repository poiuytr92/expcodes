package exp.fpf.services;

import java.util.LinkedList;
import java.util.List;

import exp.fpf.bean.FPFConfig;
import exp.fpf.cache.SRMgr;
import exp.fpf.proxy.Recver;
import exp.libs.utils.other.ListUtils;

/**
 * <pre>
 * [端口转发代理服务-S] 服务集管理器
 * </pre>	
 * <B>PROJECT : </B> file-port-forwarding
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2017-07-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _FPFServers {

	private List<_FPFServer> servers;
	
	protected _FPFServers(SRMgr srMgr, List<FPFConfig> serverConfigs) {
		this.servers = new LinkedList<_FPFServer>();
		if(ListUtils.isNotEmpty(serverConfigs)) {
			for(FPFConfig config : serverConfigs) {
				_FPFServer server = new _FPFServer(srMgr, config);
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
