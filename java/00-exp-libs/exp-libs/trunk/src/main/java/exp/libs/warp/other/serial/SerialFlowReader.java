package exp.libs.warp.other.serial;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.StrUtils;

/**
 * <PRE>
 * 批量序列化读取器.
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2016-07-01
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class SerialFlowReader {

	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(SerialFlowReader.class);
	
	private final static String DEFAULT_FILEPATH = "./serializable.dat";
	
	private File file;
	
	private ObjectInputStream ois;
	
	private Object obj;
	
	private boolean closed;
	
	public SerialFlowReader() {
		init(null);
	}
	
	public SerialFlowReader(String filePath) {
		init(StrUtils.isEmpty(filePath) ? 
				new File(DEFAULT_FILEPATH) : new File(filePath));
	}
	
	public SerialFlowReader(File file) {
		init(file);
	}
	
	private void init(File file) {
		this.file = (file == null ? new File(DEFAULT_FILEPATH) : file);
		try {
			this.ois = new ObjectInputStream(new FileInputStream(this.file));
			this.closed = false;
			
		} catch (Exception e) {
			this.closed = true;
		}
		this.obj = null;
	}
	
	public boolean hasNext() {
		obj = get();
		return (obj != null);
	}
	
	public Object next() {
		return obj;
	}
	
	private Object get() {
		Object o = null;
		if(ois != null && closed == false) {
			try {
				o = ois.readObject();
				
			} catch (Exception e) {
				// Undo 通过捕获异常判定已到结尾
			}
		}
		return o;
	}
	
	public boolean isClosed() {
		return closed;
	}
	
	public boolean close() {
		if(ois != null && closed == false) {
			try {
				ois.close();
				closed = true;
				
			} catch (Exception e) {
				log.error("关闭 SerialFlowReader 对象失败.", e);
			}
		}
		return closed;
	}

	public File getFile() {
		return file;
	}
	
}
