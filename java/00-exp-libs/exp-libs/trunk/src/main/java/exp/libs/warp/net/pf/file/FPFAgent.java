package exp.libs.warp.net.pf.file;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.PathUtils;

/**
 * <pre>
 * 双机文件流-端口转发代理程序
 * </pre>	
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-07-28
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class FPFAgent {

	private Logger log = LoggerFactory.getLogger(FPFAgent.class);
	
	private final static int DEFAULT_OVERTIME = 10000;
	
	private _SRFileMgr srFileMgr;
	
	private int overtime;
	
	private List<_FPFServer> servers;
	
	private _FPFClient client;
	
	/**
	 * 仅启动 [端口转发代理服务-C] (请求转发器/响应收转器)
	 * 	适用于 [本侧/对侧] 只有其中一方提供服务的情况.
	 * @param srDir
	 */
	public FPFAgent(String srDir, int overtime) {
		this(srDir, overtime, new FPFConfig[0]);
	}
	
	/**
	 * 启动完整的端口转发代理服务, 包括:
	 * 	1. [端口转发代理服务-S] (请求发送器/响应接收器)
	 * 	2. [端口转发代理服务-C] (请求转发器/响应收转器)
	 * 
	 * 适用于 [本侧/对侧] 两方均提供服务的情况.
	 * @param srDir
	 * @param serverConfigs
	 */
	public FPFAgent(String srDir, int overtime, FPFConfig... serverConfigs) {
		this.srFileMgr = new _SRFileMgr(srDir);
		this.overtime = (overtime <= 0 ? DEFAULT_OVERTIME : overtime);
		this.servers = new LinkedList<_FPFServer>();
		
		if(serverConfigs != null) {
			for(FPFConfig config : serverConfigs) {
				_FPFServer server = new _FPFServer(
						srFileMgr, this.overtime, config);
				servers.add(server);
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
		
		if(isOk == true) {
			log.info("初始化端口转发服务成功, 数据缓存目录: [{}]", dir);
			
		} else {
			log.error("初始化端口转发服务失败, 数据缓存目录未被授权读写: [{}]", dir);
		}
		return isOk;
	}
	
	public void _start() {
		if(_init()) {
			boolean isOk = true;
			for(_FPFServer server : servers) {
				isOk &= server._start();
			}
			client._start();
			
			if(isOk == true) {
				log.info("所有端口转发服务启动成功");
			} else {
				log.warn("存在端口转发服务启动失败");
			}
		}
	}
	
	public void _stop() {
		for(_FPFServer server : servers) {
			server._stop();
		}
		servers.clear();
		client._stop();
		srFileMgr.clear();
		
		log.info("所有端口转发服务已停止");
	}
	
}
