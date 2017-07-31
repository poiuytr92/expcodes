package exp.libs.warp.net.pf.file;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.PathUtils;

public class FPFAgent {

	private Logger log = LoggerFactory.getLogger(FPFAgent.class);
	
	private final static int DEFAULT_OVERTIME = 10000;
	
	private _SRFileMgr srFileMgr;
	
	private int overtime;
	
	private List<_FPFServer> servers;
	
	private _FPFClient client;
	
	/**
	 * 启动转发代理服务: 只启动客户端
	 * @param srDir
	 */
	public FPFAgent(String srDir, int overtime) {
		this(srDir, overtime, new FPFConfig[0]);
	}
	
	/**
	 * 启动转发代理服务: 启动N服务端和一个客户端
	 * @param srDir
	 * @param serverConfigs
	 */
	public FPFAgent(String srDir, int overtime, FPFConfig... serverConfigs) {
		this.srFileMgr = new _SRFileMgr(srDir);
		this.overtime = (overtime <= 0 ? DEFAULT_OVERTIME : overtime);
		this.servers = new LinkedList<_FPFServer>();
		
		if(serverConfigs != null) {
			for(FPFConfig config : serverConfigs) {
				_FPFServer agent = new _FPFServer(
						srFileMgr, this.overtime, config);
				servers.add(agent);
			}
		}
		
		this.client = new _FPFClient(srFileMgr, this.overtime);
	}
	
	private boolean _init() {
		boolean isOk = true;
		String dir = srFileMgr.getDir();
		
		// 禁止使用系统根目录(会清空该目录下所有文件和文件夹)
		if("/".equals(dir) || PathUtils.toLinux("C:/").equals(dir)) {
			isOk = false;
			
		// 清空所有残留的数据流文件
		} else {
			isOk &= FileUtils.delete(dir);
			isOk &= (FileUtils.createDir(dir) != null);
		}
		
		if(isOk == false) {
			log.error("初始化端口转发服务失败, 数据缓存目录未被授权读写: [{}]", dir);
		}
		return isOk;
	}
	
	public void _start() {
		if(_init()) {
			for(_FPFServer server : servers) {
				server._start();
			}
			client._start();
			
		} else {
			log.error("启动所有端口转发服务成功, 数据缓存目录: [{}]", srFileMgr.getDir());
		}
	}
	
	public void _stop() {
		for(_FPFServer server : servers) {
			server._stop();
		}
		servers.clear();
		client._stop();
		srFileMgr.clear();
		
		log.error("所有端口转发服务已停止");
	}
	
}
