package exp.libs.warp.io.listn;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <PRE>
 * 文件监听器.
 * 	可监听指定文件夹下的所有文件和子文件夹(包括子文件夹下的文件)的增删改事件.
 * 
 * 
 * 使用示例:
 * 	FileListener listn = new FileListener(); // 实现 FileAlterationListener 接口
 * 	FileMonitor fm = new FileMonitor(DIR_PATH, listn);
 * 	fm._start();
 * 	// do something ...
 * 	fm._stop();
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class FileMonitor {

	/** 日志�? */
	private final static Logger log = LoggerFactory.getLogger(FileMonitor.class);
	
	/** 默认扫描间隔 */
	public final static long DEFAULR_SCAN_INTERVAL = 100;
	
	/** 文件监控�? */
	private FileAlterationMonitor monitor;

	/**
	 * 构造函�?:监控指定文件/文件夹的增删改事�?(默认扫描间隔�?100ms)
	 * 
	 * @param path 所监听的文�?/文件夹位�? (若监控的是文件夹，则该文件夹下所有文件和子目录均会被监控)
	 * @param listener 监听�?
	 */
	public FileMonitor(String path, FileAlterationListener listener) {
		this(path, DEFAULR_SCAN_INTERVAL, listener);
	}
	
	/**
	 * 构造函�?:监控指定文件/文件夹的增删改事�?.
	 * 
	 * @param path 所监听的文�?/文件夹位�? (若监控的是文件夹，则该文件夹下所有文件和子目录均会被监控)
	 * @param scanInterval 文件/文件夹扫描间�?(ms)
	 * @param listener 监听�?
	 */
	public FileMonitor(String path, long scanInterval, 
			FileAlterationListener listener) {
		scanInterval = (scanInterval <= 0 ? DEFAULR_SCAN_INTERVAL : scanInterval);
		listener = (listener == null ? new _DefaultFileListener() : listener);
		FileAlterationObserver observer = new FileAlterationObserver(new File(path));
		observer.addListener(listener);
		
		this.monitor = new FileAlterationMonitor(scanInterval);
		monitor.addObserver(observer);
	}

	/**
	 * 启动监听�?
	 */
	public void _start() {
		try {
			monitor.start();
		} catch (Exception e) {
			log.error("启动文件监听器失�?.", e);
		}
	}
	
	/**
	 * 停止监听�?
	 */
	public void _stop() {
		try {
			monitor.stop();
		} catch (Exception e) {
			log.error("停止文件监听器失�?.", e);
		}
	}

}
