package exp.bilibli.plugin.core.gift;

import java.net.URI;

import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibli.plugin.bean.ldm.Frame;
import exp.bilibli.plugin.utils.UIUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.warp.thread.LoopThread;

public class WebSockMonitor extends LoopThread {

	private final static Logger log = LoggerFactory.getLogger(WebSockMonitor.class);
	
	// FIXME 可配
	private final static String WS_URL = 
			"ws://broadcastlv.chat.bilibili.com:2244/sub";
	
	private final static Draft DRAFT = new Draft_6455();
	
	/** B站维持websocket的心跳间隔是30秒 */
	private final static long HB_TIME = 30000;
	
	private final static long SLEEP_TIME = 1000;
	
	private WebSockClient client;
	
	private static volatile WebSockMonitor instance;
	
	private WebSockMonitor() {
		super("websocket连接监控线程");
		reconn();
	}
	
	public static WebSockMonitor getInstn() {
		if(instance == null) {
			synchronized (WebSockMonitor.class) {
				if(instance == null) {
					instance = new WebSockMonitor();
				}
			}
		}
		return instance;
	}
	
	private boolean reconn() {
		if(client != null) {
			if(client.isOpen()) {
				return true;
				
			} else {
				client.send(Frame.C2S_CLOSE());	// 断开连接前通知服务端断开
				client.close();
			}
		}
		
		boolean isOk = false;
		try {
			this.client = new WebSockClient(new URI(WS_URL), DRAFT);
			if(client.conn()) {
				client.send(Frame.C2S_CONN());	// B站的websocket连接成功后需要马上发送连接请求
				isOk = true;
				
				log.info("连接/重连到websocket成功: [{}]", WS_URL);
				UIUtils.log("正在尝试入侵B站后台...");
			}
		} catch (Exception e) {
			log.error("连接到websocket失败: [{}]", WS_URL, e);
		}
		return isOk;
	}

	@Override
	protected void _before() {
		log.info("{} 已启动", getName());
	}

	@Override
	protected void _loopRun() {
		while(!reconn()) {
			ThreadUtils.tSleep(SLEEP_TIME);
		}
		
		// B站的websocket需要每30秒发送一次心跳保活
		client.send(Frame.C2S_HB());
		_sleep(HB_TIME);
	}

	@Override
	protected void _after() {
		log.info("{} 已停止", getName());
	}
	
}
