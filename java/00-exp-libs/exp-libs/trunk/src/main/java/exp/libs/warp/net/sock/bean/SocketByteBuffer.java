package exp.libs.warp.net.sock.bean;

import java.io.UnsupportedEncodingException;

/**
 * <pre>
 * Socket专用的字节缓冲区(仅用于读取通道数据的操作)
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public final class SocketByteBuffer {

	/** 接收消息时使用的字符集编�? */
	private String charset;

	/** 存储字节流数�? */
	private byte[] byteArray;
	
	/** 字节缓冲器容�? */
	private int capacity;

	/** 字节缓冲器当前已存储的字节数 */
	private int length;

	/** 操作�? */
	private byte[] lock;

	/**
	 * 构造函�?
	 * @param capacity 字节缓冲器容�?
	 * @param charset 字节缓冲器存储的字节使用的编�?
	 */
	public SocketByteBuffer(int capacity, String charset) {
		this.length = 0;
		this.capacity = capacity;
		this.byteArray = new byte[capacity];
		this.charset = charset;

		this.lock = new byte[1];
	}

	/**
	 * <pre>
	 * 往字节缓冲器添加整个字节数组�?
	 * 此方法谨慎使用。若所添加的字节数组未满，数组末尾的无效字符也会作为有效字符处理�? 
	 * </pre>
	 * @param bytes 新字节数�?
	 * @return 添加成功与否
	 * @throws ArrayIndexOutOfBoundsException 数组越界异常
	 */
	public boolean append(byte[] bytes) throws ArrayIndexOutOfBoundsException {
		boolean flag = true;
		
		for(byte newByte : bytes) {
			flag = append(newByte);
			
			if(flag == false) {
				break;
			}
		}
		return flag;
	}
	
	/**
	 * <pre>
	 * 往字节缓冲器添加字节数组第 0~len-1个字节�?
	 * 推荐方法�?
	 * </pre>
	 * @param bytes 新字节数�?
	 * @param len 添加的字节长�?
	 * @return 添加成功与否
	 * @throws ArrayIndexOutOfBoundsException 数组越界异常
	 */
	public boolean append(byte[] bytes, int len) throws ArrayIndexOutOfBoundsException {
		boolean flag = true;
		
		for(int i = 0; i < len; i++) {
			flag = append(bytes[i]);
			
			if(flag == false) {
				break;
			}
		}
		return flag;
	}
	
	/**
	 * <pre>
	 * 往字节缓冲器添�?1个字�?
	 * 推荐方法�?
	 * </pre>
	 * @param newByte 新字�?
	 * @return 添加成功与否
	 * @throws ArrayIndexOutOfBoundsException 数组越界异常
	 */
	public boolean append(byte newByte) throws ArrayIndexOutOfBoundsException {
		boolean flag = false;
		
		synchronized (lock) {
			if(length < capacity) {
				byteArray[length++] = newByte;
				flag = true;
				
			} else {
				
				StringBuilder errMsg = new StringBuilder("ReadBuffer OverFlow\r\n");
				try {
					errMsg.append(new String(byteArray, 
							0, (capacity > 10240 ? 10240 : capacity), charset));
				} catch (UnsupportedEncodingException e) {
					errMsg.append("Inner Error : UnsupportedEncodingException");
				}
				
				this.reset();
				throw new ArrayIndexOutOfBoundsException(errMsg.toString());
			}
		}
		return flag;
	}

	/**
	 * <pre>
	 * 在字节缓冲器查找posStr所在的索引位置
	 * 
	 * 如字节缓冲器 = {0x00, 0x01, 0x02, 0x03}
	 * posStr先被转换编码，如得到 {0x01, 0x02}
	 * 则匹配成功后，返�?0x02在字节缓冲器的索�?"2"
	 * 匹配失败则返�?-1
	 * </pre>
	 * 
	 * @param posStr 查找字符�?
	 * @return 查找到的位置索引；找不到返回-1
	 * @throws UnsupportedEncodingException 不支持字符集编码异常
	 */
	public int indexOf(String posStr) throws UnsupportedEncodingException {
		int rtn = -1;
		boolean flag = true;
		byte[] posByte = posStr.getBytes(charset);

		synchronized (lock) {
			for (int i = 0; i < length; i++) {
				if (length - i < posByte.length) {
					break;
				}

				flag = true;
				for (int j = 0; j < posByte.length; j++) {
					if (byteArray[i + j] != posByte[j]) {
						flag = false;
						break;
					}
				}

				if (flag == true) {
					rtn = i + posByte.length - 1;
					break;
				}
			}
		}
		return rtn;
	}

	/**
	 * 截取字节缓冲器的子数组，并将其转换编码返�?
	 * @param end 截取终点索引
	 * @return 截取部分的转码字符串
	 * @throws UnsupportedEncodingException 不支持字符集编码异常
	 */
	public String subString(int end) throws UnsupportedEncodingException {
		return subString(0, end);
	}
	
	/**
	 * 截取字节缓冲器的子数组，并将其转换编码返�?
	 * @param bgn 截取起点索引
	 * @param end 截取终点索引
	 * @return 截取部分的转码字符串
	 * @throws UnsupportedEncodingException 不支持字符集编码异常
	 */
	public String subString(int bgn, int end) throws UnsupportedEncodingException {
		String rtnStr = null;
		
		synchronized (lock) {
			if(end > length - 1) {
				end = length - 1;
			}
			
			if(bgn <= end && bgn >= 0) {
				byte[] tmpByteArray = new byte[end - bgn + 1];
				
				for(int i = 0; i <= end; i++) {
					tmpByteArray[i] = byteArray[i + bgn];
				}
				rtnStr = new String(tmpByteArray, charset);
			}
		}
		return rtnStr;
	}

	/**
	 * 删除字节缓冲器的某部分字节，后面的字节前�?
	 * @param end 删除终点索引
	 * @return 删除成功与否
	 */
	public boolean delete(int end) {
		return delete(0, end);
	}
	
	/**
	 * 删除字节缓冲器的某部分字节，后面的字节前�?
	 * 包含bgn,包含end
	 * @param bgn 删除起始索引
	 * @param end 删除终点索引
	 * @return 删除成功与否
	 */
	public boolean delete(int bgn, int end) {
		boolean flag = false;
		
		synchronized (lock) {
			if(end > length - 1) {
				end = length - 1;
			}
			
			if(bgn <= end && bgn >= 0) {
				int stt = bgn;
				
				for(int i = end + 1; i < length; i++) {
					byteArray[bgn++] = byteArray[i];
				}
				length -= (end - stt + 1);
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 初始化缓冲区(所有缓存数据丢�?)
	 */
	public void reset() {
		length = 0;
	}
	
	/**
	 * 释放字节缓冲器资�?(程序结束时用)
	 */
	public void clear() {
		reset();
	}
	
	/**
	 * 获取字节缓冲器所存储的字节使用的编码
	 * @return
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * 获取字节缓冲器当前已存储的字节数
	 * @return
	 */
	public int length() {
		return length;
	}

	/**
	 * 获取字节缓冲器容�?
	 * @return
	 */
	public int getCapacity() {
		return capacity;
	}
	
}
