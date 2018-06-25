package exp.libs.warp.net.sock.io.common;

import java.net.Socket;

import exp.libs.warp.net.sock.bean.SocketBean;

/**
 * <pre>
 * Socket客户端会话接口(阻塞模式)
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public interface ISession {

	/**
	 * 获取客户端标�?
	 * @return 客户端标�?
	 */
	public String ID();
	
	/**
	 * 获取socket配置
	 * @return socket配置
	 */
	public SocketBean getSocketBean();
	
	/**
	 * 获取socket底层会话对象
	 * @return socket会话对象
	 */
	public Socket getSocket();
	
	/**
	 * 连接socket服务
	 * @return true:连接成功; false:连接失败
	 */
	public boolean conn();
	
	/**
	 * socket会话是否有效（多用于需要登录的判定�?
	 * @return true:有效; false:无效
	 */
	public boolean isVaild();
	
	/**
	 * 检查socket连接是否已断开
	 * @return true:已断开; false:未断开
	 */
	public boolean isClosed();
	
	/**
	 * 断开socket连接并释放所有资�?
	 * @return true:断开成功; false:断开异常
	 */
	public boolean close();
	
	/**
	 * Socket读操�?
	 * @return 服务端返回的消息(若返回null，则出现超时等异�?)
	 */
	public String read();
	
	/**
	 * Socket写操�?.
	 * @param msg 需发送到服务端的的消息报�?
	 * @return true:发送成�?; false:发送失�?
	 */
	public boolean write(String msg);
	
	/**
	 * 临时清理本地缓存.
	 * 建议完成一次完整的读写交互后执�?.
	 */
	public void clearIOBuffer();
	
}
