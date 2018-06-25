package exp.libs.warp.net.sock.nio.common.interfaze;

/**
 * <pre>
 * NioSocket配置接口
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public interface IConfig {

	/**
	 * 获取服务IP
	 * @return 服务IP
	 */
	public String getIp();

	/**
	 * 获取服务端口
	 * @return 服务端口
	 */
	public int getPort();

	/**
	 * <pre>
	 * 获取接收消息时使用的字符集编码�?
	 * 接收到消息字节流后，会使用这个编码读取字节�?
	 * </pre>
	 * @return 远端机字符集
	 */
	public String getReadCharset();
	
	/**
	 * <pre>
	 * 获取发送消息时使用的字符集编码
	 * 发送消息前，会把消息转换为这个编码的字节流�?
	 * </pre>
	 * @return 本地机字符集
	 */
	public String getWriteCharset();

	/**
	 * 获取读缓冲区大小
	 * @return 读缓冲区大小
	 */
	public int getReadBufferSize();
	
	/**
	 * 获取写缓冲区大小
	 * @return 写缓冲区大小
	 */
	public int getWriteBufferSize();
	
	/**
	 * 获取接收消息分隔�?
	 * @return 接收消息分隔�?
	 */
	public String getReadDelimiter();
	
	/**
	 * 获取发送消息分隔符
	 * @return 接收消息分隔�?
	 */
	public String getWriteDelimiter();
	
	/**
	 * 获取断开连接命令
	 * @return 断开连接命令
	 */
	public String getExitCmd();
	
}
