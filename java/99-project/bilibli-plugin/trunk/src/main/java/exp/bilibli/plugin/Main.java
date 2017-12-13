package exp.bilibli.plugin;

import java.net.URI;
import java.net.URISyntaxException;

import exp.bilibli.plugin.bean.ldm.Frame;
import exp.bilibli.plugin.bean.ldm.WebSockClient;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.LogUtils;


/**
 * <PRE>
 * 程序入口
 * </PRE>
 * <B>PROJECT：</B> xxxxxx
 * <B>SUPPORT：</B> xxxxxx
 * @version   xxxxxx
 * @author    xxxxxx
 * @since     jdk版本：jdk1.6
 */
public class Main {
	
	private final static String BILIBILI_WS = "ws://broadcastlv.chat.bilibili.com:2244/sub";
	
	public static void main(String[] args) throws URISyntaxException {
		LogUtils.loadLogBackConfig();
		
		WebSockClient client = new WebSockClient(new URI(BILIBILI_WS));
		client.debug(true);
		
		if(client.conn()) {
			Frame connFrame = new Frame(true);
			Frame hbFrame = new Frame(false);
			
			client.sendFrame(connFrame);
			while(client.isOpen()) {
				ThreadUtils.tSleep(20000);	// B站ws每30秒一次心跳
				client.sendFrame(hbFrame);
				System.err.println("heartbeat");
			}
		}

		System.err.println("end");
	}
	
	
}
