package exp.libs.warp.net.websock.interfaze;

import exp.libs.warp.net.websock.bean.Frame;

/**
 * <pre>
 * WebSocket客户端会话接口
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2017-08-21
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public interface ISession {

	/**
	 * 设置本地连接保活超时�?0不生效，默认60，即60秒后自动断开�?
	 * @param keepTimeout 保活超时时间, 单位:�?
	 */
	public void setKeepTimeout(int keepTimeout);
	
	/**
	 * 切换调试模式
	 * @param debug
	 */
	public void debug(boolean debug);
	
	/**
	 * 检查WebSocket连接是否连接�?
	 * @return
	 */
	public boolean isConnecting();
	
	/**
	 * 向服务器发送数据帧
	 * @param frame
	 */
	public void send(Frame frame);
	
}
