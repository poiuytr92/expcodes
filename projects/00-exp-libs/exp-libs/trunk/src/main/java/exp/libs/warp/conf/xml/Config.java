package exp.libs.warp.conf.xml;

import java.io.File;
import java.util.Iterator;

import exp.libs.utils.os.ThreadUtils;

public class Config extends ConfBox implements Runnable {

	private boolean isDestroy;
	
	protected Config(String boxName) {
		super(boxName);
		this.isDestroy = false;
	}

	public void autoReflash() {
		new Thread(this).start();
	}
	
	public void stopReflash() {
		isDestroy = true;
	}
	
	@Override
	public void run() {
		while(!isDestroy) {
			ThreadUtils.tSleep(60000);
			
			for(Iterator<File> files = confFiles.iterator(); files.hasNext();) {
				File file = files.next();
				if(!file.exists()) {
					files.remove();
				}
				
//				if(file.getModifyTime) ...
				
				loadConfFile(file.getPath());	// FIXME: 应用配置可能会覆盖工程配置。。。
//				loadConfFileInJar(confFilePath);	// jar内配置文件需要分开记录
			}
		}
	}

}
