package exp.bilibli.plugin.core.back;

import java.net.URI;

import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibli.plugin.bean.ldm.Frame;
import exp.bilibli.plugin.utils.UIUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.warp.thread.LoopThread;

/**
 * <PRE>
 * websocket客户端
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class WebSockClient extends LoopThread {

	private final static Logger log = LoggerFactory.getLogger(WebSockClient.class);
	
	private final static int DEFAULT_ROOM_ID = 390480;
	
	// FIXME 可配
	private final static String WS_URL = 
			"ws://broadcastlv.chat.bilibili.com:2244/sub";
	
	private final static Draft DRAFT = new Draft_6455();
	
	/** B站维持websocket的心跳间隔是30秒 */
	private final static long HB_TIME = 30000;
	
	private final static long SLEEP_TIME = 1000;
	
	private final static int LOOP_CNT = (int) (HB_TIME / SLEEP_TIME);
	
	private int loopCnt;
	
	private WebSockSession session;
	
	private int roomId;
	
	public WebSockClient() {
		super("websocket连接监控线程");
		this.roomId = DEFAULT_ROOM_ID;
		this.loopCnt = LOOP_CNT;
	}
	
	@Override
	protected void _before() {
		log.info("{} 已启动", getName());
	}

	@Override
	protected void _loopRun() {
		while(!conn()) {
			ThreadUtils.tSleep(SLEEP_TIME);
		}
		
		// B站的websocket需要每30秒发送一次心跳保活
		if(loopCnt >= LOOP_CNT) {
			loopCnt = 0;
			session.send(Frame.C2S_HB());
		}
		
		_sleep(SLEEP_TIME);
		loopCnt++;
	}

	@Override
	protected void _after() {
		log.info("{} 已停止", getName());
	}
	
	private boolean conn() {
		if(session != null) {
			if(session.isOpen()) {
				return true;
			} else {
				close();
			}
		}
		
		boolean isOk = false;
		try {
			this.session = new WebSockSession(new URI(WS_URL), DRAFT);
			if(session.conn()) {
				session.send(Frame.C2S_CONN(roomId));	// B站的websocket连接成功后需要马上发送连接请求
				isOk = true;
				
				log.info("连接/重连到websocket成功: [{}]", WS_URL);
				UIUtils.log("正在尝试入侵直播间 [", roomId, "] 后台...");
			}
		} catch (Exception e) {
			log.error("连接到websocket失败: [{}]", WS_URL, e);
		}
		return isOk;
	}
	
	private void close() {
		if(session != null) {
			session.send(Frame.C2S_CLOSE());	// 断开连接前通知服务端断开
			session.close();
		}
	}
	
	public void relink(int roomId) {
		reset(roomId);
		close();
	}
	
	public void reset(int roomId) {
		if(roomId <= 0 || roomId == this.roomId) {
			return;
		}
		this.roomId = roomId;
	}
	
}
