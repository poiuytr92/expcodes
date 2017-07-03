package exp.libs.warp.io.serial;

import java.io.File;
import java.io.Serializable;

import exp.libs.utils.StrUtils;

/**
 * <PRE>
 * 批量序列化写入器.
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2016-07-01
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class SerialFlowWriter {

	private final static String DEFAULT_FILEPATH = "./serializable.dat";
	
	private File file;
	
	private FlowObjectOutputStream foos;
	
	public SerialFlowWriter() {
		init(null, false);
	}
	
	public SerialFlowWriter(boolean append) {
		init(null, append);
	}
	
	public SerialFlowWriter(String filePath) {
		init(StrUtils.isEmpty(filePath) ? 
				new File(DEFAULT_FILEPATH) : new File(filePath), false);
	}
	
	public SerialFlowWriter(String filePath, boolean append) {
		init(StrUtils.isEmpty(filePath) ? 
				new File(DEFAULT_FILEPATH) : new File(filePath), append);
	}
	
	public SerialFlowWriter(File file) {
		init(file, false);
	}
	
	public SerialFlowWriter(File file, boolean append) {
		init(file, append);
	}
	
	private void init(File file, boolean append) {
		this.file = (file == null ? new File(DEFAULT_FILEPATH) : file);
		this.file.getParentFile().mkdirs();
		this.foos = new FlowObjectOutputStream(this.file, append);
	}
	
	public boolean write(Serializable o) {
		boolean isOk = false;
		if(foos.isClosed() == false) {
			isOk = foos.writeObject(o);
		}
		return isOk;
	}
	
	public boolean flush() {
		boolean isOk = false;
		if(foos.isClosed() == false) {
			isOk = foos.flush();
		}
		return isOk;
	}
	
	public boolean isClosed() {
		return foos.isClosed();
	}
	
	public boolean close() {
		boolean isOk = true;
		if(foos.isClosed() == false) {
			isOk &= foos.flush();
			isOk &= foos.close();
		}
		return isOk;
	}

	public File getFile() {
		return file;
	}
	
}
