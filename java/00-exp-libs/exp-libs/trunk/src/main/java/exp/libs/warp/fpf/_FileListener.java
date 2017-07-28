package exp.libs.warp.fpf;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

// FIXME 测试10秒监听， 依序放入 1~10个文件， 是否按照时序取出
class _FileListener implements FileAlterationListener {

	protected final static String PREFIX_SEND = "send";
	
	protected final static String PREFIX_RECV = "recv";
	
	protected final static String SUFFIX = ".txt";
	
	private String prefix;
	
	private String suffix;
	
	protected _FileListener(String prefix, String suffix) {
		this.prefix = prefix;
		this.suffix = suffix;
	}
	
	@Override
	public void onStart(FileAlterationObserver observer) {
		// Undo
	}

	@Override
	public void onDirectoryCreate(File directory) {
		// Undo
	}

	@Override
	public void onDirectoryChange(File directory) {
		// Undo
	}

	@Override
	public void onDirectoryDelete(File directory) {
		// Undo
	}

	@Override
	public void onFileCreate(File file) {
		String name = file.getName();
		if(name.endsWith(suffix) && name.startsWith(prefix)) {
			if(PREFIX_SEND.equals(prefix)) {
				_SRFileMgr.addSendFile(file.getAbsolutePath());
				
			} else {
				_SRFileMgr.addRecvFile(file.getAbsolutePath());
			}
		}
	}

	@Override
	public void onFileChange(File file) {
		// Undo
	}

	@Override
	public void onFileDelete(File file) {
		// Undo
	}

	@Override
	public void onStop(FileAlterationObserver observer) {
		// Undo
	}

}
