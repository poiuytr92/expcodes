package exp.bilibili.plugin.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibili.plugin.core.back.MsgSender;
import exp.bilibili.plugin.core.back.WebSockClient;
import exp.libs.envm.Charset;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.ListUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.thread.LoopThread;

/**
 * <PRE>
 * 节奏风暴扫描器
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2018-01-11
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class StormScanner extends LoopThread {

	private final static Logger log = LoggerFactory.getLogger(StormScanner.class);
	
	/** 试探轮询行为的间�? */
	private final static long SLEEP_TIME = 2000;
	
	/** 最大的查询分页(每页最�?30个房�?): 每页30个房�? */
	private final static int MAX_PAGES = 2;
	
	/** 最少在线人数达标的房间才扫�? */
	private final static int MIN_ONLINE = 3000;
	
	/** 使用websocket直接监听的房�?(相对耗资�?, 暂时针对TOP-10) */
	private final static int TOP = 10;
	
	/** 扫描每个房间的间�?(风险行为�? 频率需要控制，太快可能被查出来，太慢成功率太低) */
	private final static long SCAN_INTERVAL = 50;
	
	/** 每轮询N次所有房间，则刷新房间列�? */
	private final static int LOOP_LIMIT = 10;
	
	/** 轮询所有房间次�? */
	private int loopCnt;
	
	/** 扫描用的cookie（全平台扫描类似DDOS攻击，尽量不要用大号�? */
	private String scanCookie;
	
	/** 总开关：是否扫描房间 */
	private boolean scan;
	
	/** 人气房间�?(真实房号, 即长�?) */
	private List<Integer> hotRoomIds;
	
	/**
	 * TOP人气房间的WebSocket连接
	 * 真实房号 -> webSocket连接
	 */
	private Map<Integer, WebSockClient> hotRoomLinks;
	
	private static volatile StormScanner instance;
	
	protected StormScanner() {
		super("节奏风暴扫描�?");
		
		this.loopCnt = LOOP_LIMIT;
		this.scanCookie = FileUtils.read(LoginMgr.MINI_COOKIE_PATH, Charset.ISO);
		scanCookie = (StrUtils.isEmpty(scanCookie) ? Browser.COOKIES() : scanCookie.trim());
		this.scan = false;
		this.hotRoomIds = new LinkedList<Integer>();
		this.hotRoomLinks = new HashMap<Integer, WebSockClient>(TOP);
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
		
		if(scan == false) {
			clearTopRoomLinks();
		} else {
			loopCnt = LOOP_LIMIT;	// 触发重新扫描房间�?
		}
	}
	
	@Override
	protected void _before() {
		log.info("{} 已启�?", getName());
	}

	@Override
	protected void _loopRun() {
		if(isScan() == true) {
			if(loopCnt++ >= LOOP_LIMIT) {
				loopCnt = 0;
				reflashHotLives();
				
				// 被动监听模式: 在刷新直播间列表的同时更新websocket连接(针对TOP10房间)
				listnAndJoinStorm();
			}
			
			// 主动扫描模式: 在刷新直播间列表之前尽可能扫描每一个直播间(针对其他房间)
			sancAndJoinStorm();
		}
		_sleep(SLEEP_TIME);
	}

	@Override
	protected void _after() {
		clearTopRoomLinks();
		log.info("{} 已停�?", getName());
	}
	
	/**
	 * 刷新热门直播�?
	 * @return
	 */
	public boolean reflashHotLives() {
		List<Integer> roomIds = MsgSender.queryTopLiveRoomIds(
				scanCookie, MAX_PAGES, MIN_ONLINE);
		if(ListUtils.isNotEmpty(roomIds)) {
			hotRoomIds.clear();
			hotRoomIds.addAll(roomIds);
			log.info("已更�? [Top {}] 的人气直播间.", hotRoomIds.size());
		}
		return hotRoomIds.isEmpty();
	}
	
	/**
	 * 监听并加入TOP房间的节奏风暴抽�?
	 *  (严格来说只需要维持N个房间的WebSocket连接即可, 抽奖会通过事件自动触发)
	 */
	public void listnAndJoinStorm() {
		
		// 提取TOP房间
		Set<Integer> tops = new HashSet<Integer>();
		int size = (hotRoomIds.size() >= TOP ? TOP : hotRoomIds.size());
		for(int i = 0; i < size; i++) {
			tops.add(hotRoomIds.remove(0));
		}
		
		// 移除已经不是TOP房间的webSocket连接
		Set<Integer> invailds = ListUtils.subtraction(hotRoomLinks.keySet(), tops);
		for(Integer roomId : invailds) {
			WebSockClient wsc = hotRoomLinks.remove(roomId);
			if(wsc != null) {
				wsc._stop();
			}
		}
		invailds.clear();
		
		// 更新热门房间的webSocket连接
		for(Integer roomId : tops) {
			if(roomId < 0) {
				continue;
			}
			
			WebSockClient wsc = hotRoomLinks.get(roomId);
			if(wsc == null) {
				wsc = new WebSockClient(roomId, true);
				wsc.reset(roomId);
				wsc._start();
				hotRoomLinks.put(roomId, wsc);
				
			} else if(wsc.isClosed()) {
				wsc.relink(roomId);
			}
		}
		
		log.info("已重点监�? [Top {}] 直播间的节奏风暴.", TOP);
	}
	
	private void clearTopRoomLinks() {
		Iterator<Integer> roomIds = hotRoomLinks.keySet().iterator();
		while(roomIds.hasNext()) {
			Integer roomId = roomIds.next();
			WebSockClient wsc = hotRoomLinks.get(roomId);
			wsc._stop();
		}
		hotRoomLinks.clear();
	}
	
	/**
	 * 扫描并加入其他热门房间的节奏风暴抽奖
	 */
	public void sancAndJoinStorm() {
		int cnt = MsgSender.scanStorms(scanCookie, hotRoomIds, SCAN_INTERVAL);
		if(cnt > 0) {
			log.info("参与节奏风暴抽奖成功(连击x{})", cnt);
		}
	}
	
}
