package exp.libs.warp.net.pf.file;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

// FIXME 测试10秒监听， 依序放入 1~10个文件， 是否按照时序取出
class _SRFileListener implements FileAlterationListener {

	private _SRFileMgr srFileMgr;
	
	private String prefix;
	
	private String suffix;
	
	protected _SRFileListener(_SRFileMgr srFileMgr, 
			String prefix, String suffix) {
		this.srFileMgr = srFileMgr;
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
			if(_Envm.PREFIX_SEND.equals(prefix)) {
				srFileMgr.addSendFile(file.getAbsolutePath());
				
			} else if(_Envm.PREFIX_RECV.equals(prefix)) {
				srFileMgr.addRecvFile(file.getAbsolutePath());
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
