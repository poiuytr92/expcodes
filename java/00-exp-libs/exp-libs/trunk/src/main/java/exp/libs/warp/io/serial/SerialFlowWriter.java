package exp.libs.warp.io.serial;

import java.io.File;
import java.io.Serializable;

import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 批量序列化写入器(需配合读取器使用).
 * 	用于把多个内存对象写到外存的同一个序列化文件.
 * 
 * 使用示例:
 * 	SerialFlowWriter sfw = new SerialFlowWriter(FILE_PATH, true);
 * 	while(true) {
 * 		sfw.write(obj);
 * 		// .....
 * 	}
 * 	sfw.flush();
 * 
 * 	sfw.write(obj2);
 * 	sfw.write(obj3);
 * 	sfw.close();
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2016-07-01
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class SerialFlowWriter extends _SerialFlow {

	/** 序列化文�? */
	private File file;
	
	/** 序列化输出流 */
	private _FlowObjectOutputStream foos;
	
	/**
	 * <PRE>
	 * 构造函�?.
	 * 序列化文件使用默认位�?: ./serializable.dat
	 * 存储模式为：以覆写方式保存最后一个序列化对象
	 * </PRE>
	 */
	public SerialFlowWriter() {
		init(null, false);
	}
	
	/**
	 * <PRE>
	 * 构造函�?.
	 * 序列化文件使用默认位�?: ./serializable.dat
	 * </PRE>
	 * @param append 存储模式, true:以附加方式保存序列化对象; false:以覆写方式保存最后一个序列化对象
	 */
	public SerialFlowWriter(boolean append) {
		init(null, append);
	}
	
	/**
	 * <PRE>
	 * 构造函�?.
	 * 存储模式为：以覆写方式保存最后一个序列化对象
	 * </PRE>
	 * @param filePath 序列化文件存储位�?
	 */
	public SerialFlowWriter(String filePath) {
		init(StrUtils.isEmpty(filePath) ? 
				new File(DEFAULT_FILEPATH) : new File(filePath), false);
	}
	
	/**
	 * 构造函�?
	 * @param filePath 序列化文件存储位�?
	 * @param append 存储模式, true:以附加方式保存序列化对象; false:以覆写方式保存最后一个序列化对象
	 */
	public SerialFlowWriter(String filePath, boolean append) {
		init(StrUtils.isEmpty(filePath) ? 
				new File(DEFAULT_FILEPATH) : new File(filePath), append);
	}
	
	/**
	 * <PRE>
	 * 构造函�?.
	 * 存储模式为：以覆写方式保存最后一个序列化对象
	 * </PRE>
	 * @param file 序列化文�?
	 */
	public SerialFlowWriter(File file) {
		init(file, false);
	}
	
	/**
	 * 构造函�?
	 * @param file 序列化文�?
	 * @param append 存储模式, true:以附加方式保存序列化对象; false:以覆写方式保存最后一个序列化对象
	 */
	public SerialFlowWriter(File file, boolean append) {
		init(file, append);
	}
	
	/**
	 * 初始�?
	 * @param file 序列化文�?
	 * @param append 存储模式, true:以附加方式保存序列化对象; false:以覆写方式保存最后一个序列化对象
	 */
	private void init(File file, boolean append) {
		this.file = (file == null ? new File(DEFAULT_FILEPATH) : file);
		this.file.getParentFile().mkdirs();
		this.foos = new _FlowObjectOutputStream(this.file, append);
	}
	
	/**
	 * 把序列化对象写到序列化文�?
	 * @param o 序列化对�?
	 * @return true:写入成功; false:写入失败
	 */
	public boolean write(Serializable o) {
		boolean isOk = false;
		if(foos.isClosed() == false) {
			isOk = foos.writeObject(o);
		}
		return isOk;
	}
	
	/**
	 * 刷新序列化输出流（即把内存序列化数据强制写到外存�?
	 * @return true:成功; false:写入
	 */
	public boolean flush() {
		boolean isOk = false;
		if(foos.isClosed() == false) {
			isOk = foos.flush();
		}
		return isOk;
	}
	
	/**
	 * 测试序列化输出流是否关闭
	 * @return true:关闭; false:未关�?
	 */
	public boolean isClosed() {
		return foos.isClosed();
	}
	
	/**
	 * 关闭序列化输出流, 并强制把内存序列化数据写到外�?
	 * @return true:成功; false:写入
	 */
	public boolean close() {
		boolean isOk = true;
		if(foos.isClosed() == false) {
			isOk &= foos.flush();
			isOk &= foos.close();
		}
		return isOk;
	}

	/**
	 * 获取内存序列化文件对�?
	 * @return 内存序列化文件对�?
	 */
	public File getFile() {
		return file;
	}
	
}
