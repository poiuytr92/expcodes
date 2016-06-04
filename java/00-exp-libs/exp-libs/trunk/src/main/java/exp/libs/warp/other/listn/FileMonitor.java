package exp.libs.warp.other.listn;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <PRE>
 * 文件监控器.
 * 	可监控指定文件夹下的所有文件和子文件夹(包括子文件夹下的文件)
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class FileMonitor {

	/** 日志器 */
	private static Logger log = LoggerFactory.getLogger(FileMonitor.class);
	
	/** 文件监控器 */
	private FileAlterationMonitor monitor;

	/**
	 * 构造间隔
	 * @param interval 监控间隔(ms)
	 */
	public FileMonitor(long interval) {
		monitor = new FileAlterationMonitor(interval);
	}

	/**
	 * 监控指定文件/文件夹.
	 *  若监控的是文件夹，则该文件夹下所有文件和子目录均会被监控.
	 * 
	 * @param path 所监控的文件/文件夹位置 
	 * @param listener 监听器
	 */
	public void monitor(String path, FileAlterationListener listener) {
		FileAlterationObserver observer = new FileAlterationObserver(new File(path));
		observer.addListener(listener);
		
		monitor.addObserver(observer);
	}

	/**
	 * 启动监控器
	 */
	public void _start() {
		try {
			monitor.start();
		} catch (Exception e) {
			log.error("文件监控器启动失败.", e);
		}
	}
	
	/**
	 * 停止监控器
	 */
	public void _stop() {
		try {
			monitor.stop();
		} catch (Exception e) {
			log.error("文件监控器停止失败.", e);
		}
	}

}
