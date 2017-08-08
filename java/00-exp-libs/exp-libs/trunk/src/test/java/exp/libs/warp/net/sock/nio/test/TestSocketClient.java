package exp.libs.warp.net.sock.nio.test;

import exp.libs.utils.os.ThreadUtils;
import exp.libs.warp.net.sock.bean.SocketBean;
import exp.libs.warp.net.sock.nio.client.NioClientConfig;
import exp.libs.warp.net.sock.nio.client.NioSocketClient;

public class TestSocketClient {

	public static void main(String[] args) {
		SocketBean sb = new SocketBean();
		sb.setIp("172.168.10.26");
		sb.setPort(9998);
		
		NioClientConfig sc = new NioClientConfig(sb, new NioClientHandler());
		NioSocketClient client = new NioSocketClient(sc);
		if(client.conn()) {
			client.write("hello server");
		}
		
		ThreadUtils.tSleep(5000);
		client.close();
	}
	
}
