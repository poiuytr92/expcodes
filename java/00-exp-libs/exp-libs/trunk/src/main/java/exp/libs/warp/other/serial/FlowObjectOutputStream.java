package exp.libs.warp.other.serial;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <PRE>
 * 多对象序列化输出流接口.
 * 
 * 	主要覆写 ObjectOutputStream 的 writeStreamHeader 方法，
 * 	以解决无法在同一个文件中连续序列化多个对象的问题。
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2016-07-01
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
final class FlowObjectOutputStream {

	/** 日志器 */
	private static Logger log = LoggerFactory.getLogger(FlowObjectOutputStream.class);
	
	private File file;
	
	private boolean append;
	
	private _ObjectOutputStream oos;
	
	private boolean closed;
	
	protected FlowObjectOutputStream(File file, boolean append) {
		this.file = file;
		this.append = append;
		try {
			this.oos = new _ObjectOutputStream(new FileOutputStream(file, append));
			this.closed = false;
			
		} catch (Exception e) {
			log.error("初始化 FlowObjectOutputStream 对象失败.", e);
			this.closed = true;
		}
	}
	
	protected boolean writeObject(Object obj) {
		boolean isOk = false;
		if(oos != null && closed == false) {
			try {
				oos.writeObject(obj);
				isOk = true;
				
			} catch (Exception e) {
				log.error("序列化对象 [{}] 到文件 [{}] 失败.", 
						obj, file.getAbsoluteFile(), e);
			}
		}
		return isOk;
	}
	
	protected boolean flush() {
		boolean isOk = false;
		if(oos != null && closed == false) {
			try {
				oos.flush();
				isOk = true;
				
			} catch (Exception e) {
				log.error("刷新序列化对象缓存到文件 [{}] 失败.", file.getAbsoluteFile(), e);
			}
		}
		return isOk;
	}
	
	protected boolean isClosed() {
		return closed;
	}
	
	protected boolean close() {
		if(oos != null && closed == false) {
			try {
				oos.close();
				closed = true;
				
			} catch (Exception e) {
				log.error("关闭 FlowObjectOutputStream 对象失败.", e);
			}
		}
		return closed;
	}
	
	/**
	 * <PRE>
	 * 默认情况下，ObjectOutputStream 在往文件写入序列化对象时，默认都会带文件 Header，
	 * 导致若以【追加】方式往同一个文件写入多个对象时，会无法读取。
	 * 
	 * 通过重写writeStreamHeader方法，使得只在第一次写入序列化对象时带 Header，
	 * 后续【追加】的对象均不再写入 Header。
	 * </PRE>
	 * <B>PROJECT：</B> exp-libs
	 * <B>SUPPORT：</B> EXP
	 * @version   1.0 2016-07-01
	 * @author    EXP: 272629724@qq.com
	 * @since     jdk版本：jdk1.6
	 */
	private class _ObjectOutputStream extends ObjectOutputStream {

		private _ObjectOutputStream(OutputStream out) throws IOException {
			super(out);
		}
		
		@Override
		protected void writeStreamHeader() throws IOException {
			if (file == null || !file.exists() || 
					file.length() == 0 || append == false) {
				super.writeStreamHeader();
				
			} else {
				super.reset();
			}
		}
	}
	
}
