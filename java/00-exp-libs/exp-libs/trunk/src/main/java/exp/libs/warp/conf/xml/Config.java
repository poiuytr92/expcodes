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

	public void reflash() {
		reflash(DEFAULT_REFLASH_TIME);
	}
	
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
		isReflash = true;
		ThreadUtils.tNotify(lock);	// 退出无限阻塞， 进入限时阻塞状态
	}
	
	public void pause() {
		isReflash = false;
		ThreadUtils.tNotify(lock);	// 退出限时阻塞， 进入无限阻塞状态
	}
	
	public void destroy() {
		isRun = false;
		ThreadUtils.tNotify(lock);	// 退出阻塞态, 通过掉落陷阱终止线程
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
		if(confFiles == null || confFiles.isEmpty()) {
			return;
		}
		
		for(Iterator<String[]> fileInfos = confFiles.iterator(); 
				fileInfos.hasNext();) {
			String[] fileInfo = fileInfos.next();
			String filePath = fileInfo[0];
			String fileType = fileInfo[1];
			
			File file = new File(filePath);
			if(!file.exists()) {
				fileInfos.remove();
			}
			
			if(DISK_FILE.equals(fileType)) {
				loadConfFile(filePath);
				
			} else if(JAR_FILE.equals(fileType)) {
				loadConfFileInJar(filePath);
				
			} else {
				fileInfos.remove();
			}
		}
	}

}
