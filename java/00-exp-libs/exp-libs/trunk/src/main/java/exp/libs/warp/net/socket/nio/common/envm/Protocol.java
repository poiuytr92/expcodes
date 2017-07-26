package exp.libs.warp.net.socket.nio.common.envm;

/**
 * <pre>
 * NioSocket 客户端和服务端内部默认的通信协议报文
 * </pre>	
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Protocol {

	/** 连接受限 */
	public final static String CONN_LIMIT = "Connected limited number";
	
	/** 周期心跳 */
	public final static String HEARTBEAT = "Regular Heartbeat";
	
	/** 断开命令 */
	public final static String EXIT_CMD = "exit";
	
}
