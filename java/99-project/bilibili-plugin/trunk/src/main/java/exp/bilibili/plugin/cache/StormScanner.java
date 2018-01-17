package exp.bilibili.plugin.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibili.plugin.core.back.MsgSender;
import exp.bilibili.plugin.core.back.WebSockClient;
import exp.libs.envm.Charset;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.ListUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.thread.LoopThread;

/**
 * <PRE>
 * 节奏风暴扫描器
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-01-11
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class StormScanner extends LoopThread {

	private final static Logger log = LoggerFactory.getLogger(StormScanner.class);
	
	/** 试探轮询行为的间隔 */
	private final static int SLEEP_LIMIT = 2000;
	
	/** 最大的查询分页(每页最多30个房间): 每页30个房间 */
	private final static int MAX_PAGES = 2;
	
	/** 最少在线人数达标的房间才扫描 */
	private final static int MIN_ONLINE = 3000;
	
	/** 扫描每个房间的间隔(风险行为， 频率需要控制，太快可能被查出来，太慢成功率太低) */
	private final static long SCAN_INTERVAL = 50;
	
	/** 每轮询N次所有房间，则刷新房间列表 */
	private final static int LOOP_LIMIT = 10;
	
	/** 轮询所有房间次数 */
	private int loopCnt;
	
	/** 扫描用的cookie（全平台扫描类似DDOS攻击，尽量不要用大号） */
	private String scanCookie;
	
	/** 总开关：是否扫描房间 */
	private boolean scan;
	
	/**
	 * 是否使用主动扫描模式:
	 * 	主动扫描模式：使用马甲号轮询前N个热门房间是否存在节奏风暴, 消耗本地资源少, 频繁请求服务器, 命中率低
	 *  被动监听模式：使用N个webSocket连接到N个热门房间, 消耗本地资源多, 由服务器推送节奏风暴, 命中率高
	 */
	private boolean activeMode;
	
	/** 人气房间号(真实房号, 即长号) */
	private Set<String> hotRoomIds;
	
	/**
	 * 人气房间的WebSocket连接
	 * 真实房号 -> webSocket连接
	 */
	private Map<String, WebSockClient> hotRoomLinks;
	
	private static volatile StormScanner instance;
	
	protected StormScanner() {
		super("节奏风暴扫描器");
		
		this.loopCnt = LOOP_LIMIT;
		this.scanCookie = FileUtils.read(LoginMgr.MINI_COOKIE_PATH, Charset.ISO);
		scanCookie = (StrUtils.isEmpty(scanCookie) ? Browser.COOKIES() : scanCookie.trim());
		this.scan = false;
		this.activeMode = true;	// FIXME
		this.hotRoomIds = new HashSet<String>();
		this.hotRoomLinks = new HashMap<String, WebSockClient>();
	}

	public static StormScanner getInstn() {
		if(instance == null) {
			synchronized (StormScanner.class) {
				if(instance == null) {
					instance = new StormScanner();
				}
			}
		}
		return instance;
	}
	
	public boolean isScan() {
		return scan;
	}

	public void setScan() {
		scan = !scan;
	}
	
	@Override
	protected void _before() {
		log.info("{} 已启动", getName());
	}

	@Override
	protected void _loopRun() {
		if(isScan() == true) {
			if(loopCnt++ >= LOOP_LIMIT) {
				loopCnt = 0;
				reflashHotLives();
				
				// 被动监听模式: 在刷新直播间列表的同时更新websocket连接
				if(activeMode == false) {
					listnAndJoinStorm();
				}
			}
			
			// 主动扫描模式: 在刷新直播间列表之前尽可能扫描每一个直播间
			if(activeMode == true) {
				sancAndJoinStorm();
			}
		}
		_sleep(SLEEP_LIMIT);
	}

	@Override
	protected void _after() {
		log.info("{} 已停止", getName());
	}
	
	/**
	 * 刷新热门直播间
	 * @return
	 */
	public boolean reflashHotLives() {
		List<String> roomIds = MsgSender.queryTopLiveRoomIds(
				scanCookie, MAX_PAGES, MIN_ONLINE);
		if(ListUtils.isNotEmpty(roomIds)) {
			hotRoomIds.clear();
			hotRoomIds.addAll(roomIds);
			log.info("已更新 [Top {}] 的人气直播间.", hotRoomIds.size());
		}
		return hotRoomIds.isEmpty();
	}
	
	/**
	 * 扫描并加入节奏风暴抽奖
	 */
	public void sancAndJoinStorm() {
		int cnt = MsgSender.scanStorms(scanCookie, hotRoomIds, SCAN_INTERVAL);
		if(cnt > 0) {
			log.info("参与节奏风暴抽奖成功(连击x{})", cnt);
		}
	}
	
	/**
	 * 监听并加入节奏风暴抽奖
	 *  (严格来说只需要维持N个房间的WebSocket连接即可, 抽奖会通过事件自动触发)
	 *  
	 *  FIXME: 异常：抽奖的是当前界面的直播间，而非连接所属的直播间
	 */
	public void listnAndJoinStorm() {
		
		// 移除非热门房间的webSocket连接
		Set<String> invailds = ListUtils.subtraction(hotRoomLinks.keySet(), hotRoomIds);
		for(String roomId : invailds) {
			WebSockClient wsc = hotRoomLinks.remove(roomId);
			if(wsc != null) {
				wsc._stop();
			}
		}
		log.info("已放弃监听 [{}] 个过气房间的节奏风暴", invailds.size());
		invailds.clear();
		
		
		// 更新热门房间的webSocket连接
		for(String roomId : hotRoomIds) {
			int realRoomId = NumUtils.toInt(roomId, -1);
			if(realRoomId < 0) {
				continue;
			}
			
			WebSockClient wsc = hotRoomLinks.get(roomId);
			if(wsc == null) {
				wsc = new WebSockClient(realRoomId, true);
				wsc.reset(realRoomId);
				wsc._start();
				hotRoomLinks.put(roomId, wsc);
				
			} else if(wsc.isClosed()) {
				wsc.relink(realRoomId);
			}
		}
		log.info("已监听 [{}] 个热门房间的节奏风暴", hotRoomLinks.size());
	}
}
