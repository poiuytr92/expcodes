package exp.libs.warp.conf.xml;

import java.io.File;
import java.util.Iterator;

import exp.libs.utils.os.ThreadUtils;

// FIXME : 刷新操作非多线程安全
public class Config extends _Config implements Runnable {

	private final static long MIN_REFLASH_TIME = 10000L;
	
	private final static long DEFAULT_REFLASH_TIME = 60000L;
	
	private boolean isInit;
	
	private boolean isRun;
	
	private boolean isReflash;
	
	private long reflashTime;
	
	private byte[] lock;
	
	protected Config(String boxName) {
		super(boxName);
		
		this.isInit = false;
		this.isRun = false;
		this.isReflash = false;
		this.reflashTime = DEFAULT_REFLASH_TIME;
		this.lock = new byte[1];
	}

	/**
	 * 刷新操作会对所加载过的配置文件依次重新加载.
	 * 此行为非多线程安全, 若在重载文件过程中有读取配置的行为，注意加锁.
	 */
	public void reflash() {
		reflash(DEFAULT_REFLASH_TIME);
	}
	
	/**
	 * 刷新操作会对所加载过的配置文件依次重新加载.
	 * 此行为非多线程安全, 若在重载文件过程中有读取配置的行为，注意加锁.
	 * @param timeMillis 刷新间隔
	 */
	public void reflash(long timeMillis) {
		reflashTime = (timeMillis < MIN_REFLASH_TIME ? 
				MIN_REFLASH_TIME : timeMillis);
		
		if(!isInit) {
			synchronized (lock) {
				if(!isInit) {
					isInit = true;
					isRun = true;
					new Thread(this).start();
					ThreadUtils.tSleep(1000);	// 初次启动, 用时间差保证先让线程陷入第一次无限阻塞状态
				}
			}
		}
		
		if(!isReflash) {
			isReflash = true;
			ThreadUtils.tNotify(lock);	// 退出无限阻塞， 进入限时阻塞状态
			log.info("配置 [{}] 自动刷新被激活, 刷新间隔为 [{} ms].", configName, reflashTime);
			
		} else {
			log.info("配置 [{}] 刷新间隔变更为 [{} ms], 下个刷新周期生效.", configName, reflashTime);
		}
	}
	
	public void pause() {
		isReflash = false;
		ThreadUtils.tNotify(lock);	// 退出限时阻塞， 进入无限阻塞状态
		log.info("配置 [{}] 自动刷新被暂停.", configName);
	}
	
	public void destroy() {
		isRun = false;
		ThreadUtils.tNotify(lock);	// 退出阻塞态, 通过掉落陷阱终止线程
		log.info("配置 [{}] 自动刷新被终止.", configName);
	}
	
	@Override
	public void run() {
		while(isRun) {
			ThreadUtils.tWait(lock, 0);
			if(!isRun) { break; }
			
			while(isReflash) {
				ThreadUtils.tWait(lock, reflashTime);
				if(!isRun || !isReflash) { break; }
				reload();
			}
		}
	}
	
	private void reload() {
		log.info("配置 [{}] 开始重载文件...", configName);
		if(confFiles == null || confFiles.isEmpty()) {
			log.info("配置 [{}] 并未加载过任何文件(或文件已被删除), 取消重载操作.", configName);
			return;
		}
		
		for(Iterator<String[]> fileInfos = confFiles.iterator(); 
				fileInfos.hasNext();) {
			String[] fileInfo = fileInfos.next();
			String filePath = fileInfo[0];
			String fileType = fileInfo[1];
			
			File file = new File(filePath);
			if(!file.exists()) {
				log.info("配置文件 [{}] 已不存在, 不重载.", filePath);
				fileInfos.remove();
			}
			
			if(DISK_FILE.equals(fileType)) {
				boolean isOk = loadConfFile(filePath);
				log.info("配置 [{}] 重载文件 [{}] {}.", configName, filePath, (isOk ? "成功" : "失败"));
				
			} else if(JAR_FILE.equals(fileType)) {
				boolean isOk = loadConfFileInJar(filePath);
				log.info("配置 [{}] 重载文件 [{}] {}.", configName, filePath, (isOk ? "成功" : "失败"));
				
			} else {
				log.info("配置文件 [{}] 类型异常, 不重载.", filePath);
				fileInfos.remove();
			}
		}
		log.info("配置 [{}] 重载所有文件完成.", configName);
	}

}
