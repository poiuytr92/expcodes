package exp.libs.warp.net.sock.nio.common.interfaze;

import java.nio.channels.SocketChannel;
import java.util.Map;

/**
 * <pre>
 * NioSocket会话接口
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public interface ISession {

	/**
	 * 获取会话名称
	 * @return 会话名称
	 */
	public String getName();
	
	/**
	 * 封装底层会话对象
	 * 
	 * @param layerSession 底层会话对象
	 */
	public void pack(SocketChannel layerSession);

	/**
	 * 获取底层会话对象
	 * 
	 * @return 底层会话对象
	 */
	public SocketChannel getLayerSession();
	
	/**
	 * 获取远端机IP
	 * 
	 * @return 远端机IP地址
	 * @throws Exception 异常
	 */
	public String getIp() throws Exception;

	/**
	 * 获取远端机端�?
	 * 
	 * @return 远端机端�?
	 * @throws Exception 异常
	 */
	public int getPort() throws Exception;

	/**
	 * <pre>
	 * 获取会话状态�?
	 * 
	 * 一般是用户自定义的会话状态，也可用于获取States枚举类的会话状�?
	 * </pre>
	 * 
	 * @return 会话状�?
	 */
	public Object getState();
	
	/**
	 * <pre>
	 * 检查会话是否已经执行过验证操作�?
	 * 
	 * 会话�?3种状态：未验证，验证成功、验证失�?
	 * </pre>
	 * 
	 * @return 验证成功、验证失�?:true; 未验�?:false
	 */
	public boolean isVerfied();
	
	/**
	 * <pre>
	 * 设置会话验证状�?
	 * </pre>
	 * 
	 * @param isVerfy true:验证成功; false:验证失败
	 */
	public void setVerfyState(boolean isVerfy);
	
	/**
	 * 获NioSocket服务�?/客户端的配置对象
	 * 
	 * @return 配置对象
	 */
	public IConfig getConfig();
	
	/**
	 * 获取会话属性集�?
	 * 
	 * @return 会话属性集�?
	 */
	public Map<String, Object> getProperties();
	
	/**
	 * <pre>
	 * 向远端机发送消�?
	 * </pre>
	 * 
	 * @param msg 消息
	 * @throws Exception 异常
	 */
	public void write(Object msg) throws Exception;

	/**
	 * <pre>
	 * 最后一次向远端机发送消息�?
	 * 
	 * 此方法会做两件事�?
	 * 发送消�? + 调用closeNotify方法
	 * </pre>
	 * 
	 * @param msg 消息
	 * @throws Exception 异常
	 */
	public void writeClose(Object msg) throws Exception;
	
	/**
	 * <pre>
	 * 通知断开连接，被动的断开连接方式（建议）�?
	 * 
	 * 此方法会发送一条断开连接命令到远端机，通知远端机：本地已经准备好断开连接�?
	 * 此时会话进入等待关闭状态，禁止再发送任何消息�?
	 * 远端机收到断开连接命令后，会调用close方法断开连接（此机制已内嵌到过滤链链头）�?
	 * 在本地检测到连接被断开后，会关闭会话并清空资源�?
	 * 
	 * 建议的关闭会话方法�?
	 * 若双方没有定义断开连接命令，则在超时后会断开连接，期间只能接收消息，无法发送消息�?
	 * </pre>
	 */
	public void closeNotify() throws Exception;
	
	/**
	 * <pre>
	 * 马上断开连接，主动的断开连接方式（不建议）�?
	 * 关闭会话并清空资源，可能会导致未处理消息全部丢失�?
	 * 
	 * 此方法在连接发生异常时（如断开）会被动调用。但不建议的主动调用�?
	 * 建议仅在收到远端机的断开命令时、或远端机超时时才主动调用�?
	 * </pre>
	 * 
	 * @throws Exception 异常
	 */
	public void close() throws Exception;

	/**
	 * 检查会话是否处于等待关闭状�?
	 * 
	 * @return true:等待关闭; false:非等待关�?
	 */
	public boolean isWaitingToClose();
	
	/**
	 * <pre>
	 * 检查会话是否已关闭
	 * </pre>
	 * 
	 * @return true:关闭成功; false:关闭失败
	 */
	public boolean isClosed();
	
}
