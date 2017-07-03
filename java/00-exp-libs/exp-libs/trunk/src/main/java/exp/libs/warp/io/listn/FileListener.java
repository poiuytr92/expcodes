package exp.libs.warp.io.listn;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

/**
 * <PRE>
 * 文件监控器的事件监听器
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class FileListener implements FileAlterationListener {

	@Override
	public void onStart(FileAlterationObserver observer) {
//		System.out.println("onStart");	// 每次扫描开始时均会触发
	}

	@Override
	public void onDirectoryCreate(File directory) {
		System.out.println("onDirectoryCreate:" + directory.getName());
	}

	@Override
	public void onDirectoryChange(File directory) {
		System.out.println("onDirectoryChange:" + directory.getName());
	}

	@Override
	public void onDirectoryDelete(File directory) {
		System.out.println("onDirectoryDelete:" + directory.getName());
	}

	@Override
	public void onFileCreate(File file) {
		System.out.println("onFileCreate:" + file.getName());
	}

	@Override
	public void onFileChange(File file) {
		System.out.println("onFileChange : " + file.getName());
	}

	@Override
	public void onFileDelete(File file) {
		System.out.println("onFileDelete :" + file.getName());
	}

	@Override
	public void onStop(FileAlterationObserver observer) {
//		System.out.println("onStop");	// 每次扫描结束时均会触发
	}

}
