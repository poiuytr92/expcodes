package exp.libs.warp.other.serial;

import java.io.File;
import java.io.Serializable;

import exp.libs.utils.pub.StrUtils;

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
	
	private FlowObjectOutputStream foos;
	
	public SerialFlowWriter(String filePath) {
		init(StrUtils.isEmpty(filePath) ? 
				new File(DEFAULT_FILEPATH) : new File(filePath));
	}
	
	public SerialFlowWriter(File file) {
		init(file);
	}
	
	private void init(File file) {
		file = (file == null ? new File(DEFAULT_FILEPATH) : file);
		this.foos = new FlowObjectOutputStream(file, true);
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
	
}
