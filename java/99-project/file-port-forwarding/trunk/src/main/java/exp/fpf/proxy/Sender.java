package exp.fpf.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.fpf.Config;
import exp.libs.warp.net.sock.bean.SocketBean;
import exp.libs.warp.net.sock.io.client.SocketClient;

/**
 * <pre>
 * [响应数据传输管道-发送端]
 * 	把[真正的服务端口]返回的响应数据, 通过socket管道送到 [响应数据传输管道-接收端]
 * 
 * 仅用于[接收模式]为socket监听模式的情况下 (文件扫描模式由第三方程序负责传输)
 * </pre>	
 * <B>PROJECT：</B> file-port-forwarding
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-01-16
 * @author    EXP: www.exp-blog.com
 * @since     jdk版本：jdk1.6
 */
public class Sender {

	private Logger log = LoggerFactory.getLogger(Sender.class);
	
	private final static String NAME = "响应数据传输管道";
	
	private SocketClient client;
	
	private static volatile Sender instance;
	
	private Sender() {
		SocketBean sockConf = Config.getInstn().newSocketConf();
		this.client = new SocketClient(sockConf);
		log.info("[{}]-[发送端] 已初始化, [接收端]socket为 [{}]", NAME, sockConf.getSocket());
	}
	
	public static Sender getInstn() {
		if(instance == null) {
			synchronized (Sender.class) {
				if(instance == null) {
					instance = new Sender();
				}
			}
		}
		return instance;
	}
	
	public void send(String data) {
		if(client.isClosed()) {
			if(!client.conn()) {
				log.error("[{}] 连接到 [接收端] 失败", NAME);
			}
		}
		
		if(!client.write(data)) {
			log.error("[{}] 发送数据失败: {}", NAME, data);
		}
	}
	
	public void close() {
		client.close();
		log.info("[{}]-[发送端] 会话已关闭", NAME);
	}
	
}
