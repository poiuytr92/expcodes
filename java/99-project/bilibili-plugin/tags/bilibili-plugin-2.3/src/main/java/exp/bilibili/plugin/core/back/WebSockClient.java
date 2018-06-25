package exp.bilibili.plugin.core.back;

import java.net.URI;

import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.bean.ldm.Frame;
import exp.bilibili.plugin.utils.UIUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.warp.thread.LoopThread;

/**
 * <PRE>
 * websocket客户端
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class WebSockClient extends LoopThread {

	private final static Logger log = LoggerFactory.getLogger(WebSockClient.class);
	
	private final static String WS_URL = Config.getInstn().WS_URL();
	
	private final static Draft DRAFT = new Draft_6455();
	
	/** B站维持websocket的心跳间隔是30�? */
	private final static long HB_TIME = 30000;
	
	private final static long SLEEP_TIME = 1000;
	
	private final static int LOOP_CNT = (int) (HB_TIME / SLEEP_TIME);
	
	private int loopCnt;
	
	private WebSockSession session;
	
	private int roomId;
	
	private boolean onlyStorm;
	
	public WebSockClient() {
		this(Config.DEFAULT_ROOM_ID, false);
	}
	
	public WebSockClient(int roomId, boolean onlyStorm) {
		super("websocket连接监控线程");
		this.roomId = roomId;
		this.loopCnt = LOOP_CNT;
		this.onlyStorm = onlyStorm;
	}
	
	@Override
	protected void _before() {
		log.info("{} 已启�?", getName());
	}

	@Override
	protected void _loopRun() {
		while(!conn()) {
			ThreadUtils.tSleep(SLEEP_TIME);
		}
		
		// B站的websocket需要每30秒发送一次心跳保�?
		if(loopCnt >= LOOP_CNT) {
			loopCnt = 0;
			session.send(Frame.C2S_HB());
		}
		
		_sleep(SLEEP_TIME);
		loopCnt++;
	}

	@Override
	protected void _after() {
		close();
		log.info("{} 已停�?", getName());
	}
	
	private boolean conn() {
		if(session != null) {
			if(session.isConn()) {
				return true;
			} else {
				close();
			}
		}
		
		boolean isOk = false;
		try {
			this.session = new WebSockSession(new URI(WS_URL), DRAFT, roomId, onlyStorm);
			if(session.conn()) {
				session.send(Frame.C2S_CONN(roomId));	// B站的websocket连接成功后需要马上发送连接请�?
				isOk = true;
				
				log.info("连接/重连到直播间 [{}] 的websocket成功: [{}]", roomId, WS_URL);
				if(onlyStorm == false) {
					UIUtils.log("正在尝试入侵直播�? [", roomId, "] 后台...");
				}
			}
		} catch (Exception e) {
			log.error("连接到直播间 [{}] 的websocket失败: [{}]", roomId, WS_URL, e);
		}
		return isOk;
	}
	
	public boolean isClosed() {
		boolean isClosed = true;
		if(session != null) {
			isClosed = !session.isConn();
		}
		return isClosed;
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
