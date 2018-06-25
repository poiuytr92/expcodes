package exp.bilibili.plugin.cache;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.bean.ldm.HotLiveRange;
import exp.bilibili.plugin.utils.TimeUtils;
import exp.bilibili.plugin.utils.UIUtils;
import exp.bilibili.protocol.XHRSender;
import exp.libs.utils.other.ListUtils;
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
	
	/** 默认扫描每个房间的间�?(风险行为�? 频率需要控制，太快可能被查出来，太慢成功率太低) */
	private final static long MIN_SCAN_INTERVAL = Config.getInstn().STORM_FREQUENCY();
	
	/** 默认试探轮询行为的间�? */
	private final static long MIN_SLEEP_TIME = 2000;
	
	/** 每轮询N次所有房间，则刷新房间列�? */
	private final static int LOOP_LIMIT = 10;
	
	/** 轮询所有房间次�? */
	private int loopCnt;
	
	/** 总开关：是否扫描房间 */
	private boolean scan;
	
	/** 人气房间�?(真实房号, 即长�?) */
	private List<Integer> hotRoomIds;
	
	private static volatile StormScanner instance;
	
	protected StormScanner() {
		super("节奏风暴扫描�?");
		
		this.loopCnt = LOOP_LIMIT;
		this.scan = false;
		this.hotRoomIds = new LinkedList<Integer>();
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
		if(scan == true) {
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
			}
			
			// 在刷新直播间列表之前尽可能扫描每一个直播间
			sancAndJoinStorm();
		}
		_sleep(getSleepTime());
	}
	
	/**
	 * 每扫描一轮房间的休眠时间
	 * @return
	 */
	private long getSleepTime() {
		long sleepTime = MIN_SLEEP_TIME;
		if(TimeUtils.inZeroPointRange()) {
			sleepTime *= 5;	// 零点附近存在大量跨天日常任务, 避免大量请求, 适当增大每轮间隔
			
		} else if(TimeUtils.isDawn()) {
			sleepTime *= 2;	// 凌晨基本没人直播, 无需大量请求, 适当增大每轮间隔
		}
		return sleepTime;
	}

	@Override
	protected void _after() {
		log.info("{} 已停�?", getName());
	}
	
	/**
	 * 刷新热门直播�?
	 * @return
	 */
	public boolean reflashHotLives() {
		HotLiveRange range = UIUtils.getHotLiveRange();
		List<Integer> roomIds = XHRSender.queryTopLiveRoomIds(range);
		if(ListUtils.isNotEmpty(roomIds)) {
			hotRoomIds.clear();
			hotRoomIds.addAll(roomIds);
			log.info("已更�? [Page:{}-{}] �? [{}] 个人气直播间.", 
					range.BGN_PAGE(), range.END_PAGE(), hotRoomIds.size());
		}
		return hotRoomIds.isEmpty();
	}
	
	/**
	 * 扫描并加入其他热门房间的节奏风暴抽奖
	 */
	public void sancAndJoinStorm() {
		long scanInterval = MIN_SCAN_INTERVAL;
		if(TimeUtils.inZeroPointRange()) {
			scanInterval *= 3;	// 零点附近存在大量跨天日常任务, 避免大量请求, 适当增大连续扫描房间扫描间隔
		}
		
		XHRSender.scanAndJoinStorms(hotRoomIds, scanInterval);
	}
	
}
