#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}._demo.services.sock.nio;

import exp.libs.utils.os.ThreadUtils;
import exp.libs.warp.net.sock.bean.SocketBean;
import exp.libs.warp.net.sock.nio.client.NioSocketClient;

/**
 * <PRE>
 * NioSocket客户端 样例
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-08-17
 * @author    EXP: www.exp-blog.com
 * @since     jdk版本：jdk1.6
 */
public class Demo_SocketClient {

	public static void main(String[] args) {
//		SocketBean sb = Config.getInstn().getSocket();
		SocketBean sb = new SocketBean();
		sb.setIp("127.0.0.1");
		sb.setPort(9998);
		
		NioSocketClient client = new NioSocketClient(sb, new NioClientHandler());
		if(client.conn()) {
			client.write("hello server");
		}
		
		ThreadUtils.tSleep(5000);
		client.close();
	}
	
}
