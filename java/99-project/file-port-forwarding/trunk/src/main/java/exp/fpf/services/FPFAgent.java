package exp.fpf.services;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.fpf.bean.FPFConfig;
import exp.fpf.cache.SRMgr;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <pre>
 * 双机文件流-端口转发代理程序
 * </pre>	
 * <B>PROJECT : </B> file-port-forwarding
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-07-28
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class FPFAgent {

	private Logger log = LoggerFactory.getLogger(FPFAgent.class);
	
	private SRMgr srMgr;
	
	private _FPFServers servers;
	
	private _FPFClient client;
	
	/**
	 * 仅启�? [端口转发代理服务-C] (请求转发�?/响应收转�?)
	 * 	适用�? [本侧/对侧] 只有其中一方提供服务的情况.
	 * @param sendDir 数据流发送目�?
	 * @param recvDir 数据流接收目�?
	 * @param overtime 超时无交互断开转发通道(单位ms),  需根据实际传输的数据量以正比调�?
	 */
	public FPFAgent(String sendDir, String recvDir, int overtime) {
		this(sendDir, recvDir, overtime, new FPFConfig[0]);
	}
	
	/**
	 * 启动完整的端口转发代理服�?, 包括:
	 * 	1. [端口转发代理服务-S] (请求发送器/响应接收�?)
	 * 	2. [端口转发代理服务-C] (请求转发�?/响应收转�?)
	 * 
	 * 适用�? [本侧/对侧] 两方均提供服务的情况.
	 * @param sendDir 数据流发送目�?
	 * @param recvDir 数据流接收目�?
	 * @param overtime 超时无交互断开转发通道(单位ms),  需根据实际传输的数据量以正比调�?
	 * @param serverConfigs 服务配置列表
	 */
	public FPFAgent(String sendDir, String recvDir, 
			int overtime, FPFConfig... serverConfigs) {
		this(sendDir, recvDir, overtime, Arrays.asList(
				serverConfigs == null ? new FPFConfig[0] : serverConfigs));
	}
	
	/**
	 * 启动完整的端口转发代理服�?, 包括:
	 * 	1. [端口转发代理服务-S] (请求发送器/响应接收�?)
	 * 	2. [端口转发代理服务-C] (请求转发�?/响应收转�?)
	 * 
	 * 适用�? [本侧/对侧] 两方均提供服务的情况.
	 * @param sendDir 数据流发送目�?
	 * @param recvDir 数据流接收目�?
	 * @param overtime 超时无交互断开转发通道(单位ms),  需根据实际传输的数据量以正比调�?
	 * @param serverConfigs 服务配置列表
	 */
	public FPFAgent(String sendDir, String recvDir, 
			int overtime, List<FPFConfig> serverConfigs) {
		this.srMgr = new SRMgr(sendDir, recvDir);
		this.servers = new _FPFServers(srMgr, serverConfigs);
		this.client = new _FPFClient(srMgr, overtime);
	}
	
	private boolean _init() {
		boolean isOk = true;
		if(!_init(srMgr.getSendDir())) {
			isOk |= false;
			log.warn("初始化发送数据缓存目录失�?(未配置或无读写权�?): [{}]", srMgr.getSendDir());
		}
		
		if(!_init(srMgr.getRecvDir())) {
			isOk |= false;
			log.warn("初始化接收数据缓存目录失�?(未配置或无读写权�?): [{}]", srMgr.getRecvDir());
		}
		
		// 对于socket监听模式, 不需要两个目录同时配�?
		if(isOk == true) {
			log.info("初始化数据缓存目录成�?");
			log.info("发送目�?: [{}]", srMgr.getSendDir());
			log.info("接收目录: [{}]", srMgr.getRecvDir());
		}
		return isOk;
	}
	
	private boolean _init(String srDir) {
		boolean isOk = true;
		
		// 禁止使用系统根目�?(会清空该目录下所有文件和文件�?)
		if(StrUtils.isEmpty(srDir) || 
				"/".equals(srDir) || PathUtils.toLinux("C:/").equals(srDir)) {
			isOk = false;
			
		// 清空所有残留的数据流文�?
		} else {
			isOk &= FileUtils.delete(srDir);
			isOk &= (FileUtils.createDir(srDir) != null);
		}
		return isOk;
	}
	
	public void _start() {
		if(_init()) {
			servers._start();
			client._start();
		}
	}
	
	public void _stop() {
		servers._stop();
		client._stop();
		srMgr.clear();
	}
	
}
