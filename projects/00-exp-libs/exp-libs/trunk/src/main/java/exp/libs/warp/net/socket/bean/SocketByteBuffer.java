package exp.libs.warp.net.socket.bean;

import java.io.UnsupportedEncodingException;

/**
 * <pre>
 * Socket专用的字节缓冲区，仅用于读通道数据的操作时
 * </pre>	
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public final class SocketByteBuffer {

	/**
	 * <pre>
	 * 接收消息时使用的字符集编码
	 * </pre>
	 */
	private String recvCharset;

	/**
	 * 存储接收到的字节流
	 */
	private byte[] recvByteArray;
	
	/**
	 * remoteByteArray容量
	 */
	private int size;

	/**
	 * remoteByteArray当前已存储的字节数
	 */
	private int length;

	/**
	 * 操作锁
	 */
	private byte[] lock;

	/**
	 * 构造函数
	 * @param size 字节数组容量
	 * @param recvCharset 远端机器的编码字符集
	 */
	public SocketByteBuffer(int size, String recvCharset) {
		this.length = 0;
		this.setSize(size);
		this.setRecvCharset(recvCharset);

		this.lock = new byte[1];
	}

	/**
	 * <pre>
	 * 往remoteByteArray添加整个字节数组。
	 * 此方法谨慎使用。若所添加的字节数组未满，数组末尾的无效字符也会作为有效字符处理。 
	 * </pre>
	 * @param bytes 新字节数组
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
	 * 往remoteByteArray添加字节数组第 0~len-1个字节。
	 * 推荐方法。
	 * </pre>
	 * @param bytes 新字节数组
	 * @param len 添加的字节长度
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
	 * 往remoteByteArray添加1个字节
	 * 推荐方法。
	 * </pre>
	 * @param newByte 新字节
	 * @return 添加成功与否
	 * @throws ArrayIndexOutOfBoundsException 数组越界异常
	 */
	public boolean append(byte newByte) throws ArrayIndexOutOfBoundsException {
		boolean flag = false;
		
		synchronized (lock) {
			if(length < size) {
				recvByteArray[length++] = newByte;
				flag = true;
				
			} else {
				
				StringBuilder errMsg = new StringBuilder("ReadBuffer OverFlow\r\n");
				try {
					errMsg.append(new String(recvByteArray, 
							0, (size > 10240 ? 10240 : size), recvCharset));
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
	 * 在remoteByteArray查找posStr所在的索引位置
	 * 
	 * 如remoteByteArray = {0x00, 0x01, 0x02, 0x03}
	 * posStr先被转换编码，如得到 {0x01, 0x02}
	 * 则匹配成功后，返货0x02在remoteByteArray的索引"2"
	 * 匹配失败则返回-1
	 * </pre>
	 * 
	 * @param posStr 查找字符串
	 * @return 查找到的位置索引；找不到返回-1
	 * @throws UnsupportedEncodingException 不支持字符集编码异常
	 */
	public int indexOf(String posStr) throws UnsupportedEncodingException {
		int rtn = -1;
		boolean flag = true;
		byte[] posByte = posStr.getBytes(recvCharset);

		synchronized (lock) {
			for (int i = 0; i < length; i++) {
				if (length - i < posByte.length) {
					break;
				}

				flag = true;
				for (int j = 0; j < posByte.length; j++) {
					if (recvByteArray[i + j] != posByte[j]) {
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
	 * 截取remoteByteArray的子数组，并将其转换编码返回
	 * @param end 截取终点索引
	 * @return 截取部分的转码字符串
	 * @throws UnsupportedEncodingException 不支持字符集编码异常
	 */
	public String subString(int end) throws UnsupportedEncodingException {
		return subString(0, end);
	}
	
	/**
	 * 截取remoteByteArray的子数组，并将其转换编码返回
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
					tmpByteArray[i] = recvByteArray[i + bgn];
				}
				rtnStr = new String(tmpByteArray, recvCharset);
			}
		}
		return rtnStr;
	}

	/**
	 * 删除remoteByteArray的某部分字节，后面的字节前移
	 * @param end 删除终点索引
	 * @return 删除成功与否
	 */
	public boolean delete(int end) {
		return delete(0, end);
	}
	
	/**
	 * 删除remoteByteArray的某部分字节，后面的字节前移
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
					recvByteArray[bgn++] = recvByteArray[i];
				}
				length -= (end - stt + 1);
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 初始化缓冲区，所有数据丢失
	 */
	public void reset() {
		length = 0;
	}
	
	/**
	 * 释放remoteByteArray资源，程序结束时用
	 */
	public void clear() {
		length = 0;
		size = 0;
		recvByteArray = null;
	}
	
	public String getRecvCharset() {
		return recvCharset;
	}

	public void setRecvCharset(String recvCharset) {
		this.recvCharset = recvCharset;
	}

	public int getLength() {
		return length;
	}

	public int getSize() {
		return size;
	}

	/**
	 * 设置remoteByteArray的容量，并初始化remoteByteArray
	 * @param size 容量
	 */
	public void setSize(int size) {
		this.size = size;
		this.recvByteArray = new byte[size];
	}

}
