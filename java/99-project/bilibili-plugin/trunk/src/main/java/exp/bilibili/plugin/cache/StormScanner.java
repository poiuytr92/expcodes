package exp.bilibili.plugin.cache;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibili.plugin.core.back.MsgSender;
import exp.libs.utils.other.ListUtils;
import exp.libs.warp.thread.LoopThread;
import exp.libs.warp.ui.SwingUtils;

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

	// 每10分钟更新当前top50房间, 只扫描那些
	private final static Logger log = LoggerFactory.getLogger(StormScanner.class);
	
	/** 最大的查询分页(每页最多30个房间): 每页30个房间 */
	private final static int MAX_PAGES = 2;
	
	/** 最少在线人数达标的房间才扫描 */
	private final static int MIN_ONLINE = 3000;
	
	/** 扫描每个房间的间隔(FIXME 频率需要控制，太快可能被查出来，太慢成功率太低) */
	private final static long SCAN_INTERVAL = 50;
	
	/** 扫描用的cookie（全平台扫描类似DDOS攻击，尽量不要用大号） */
	private String scanCookie;
	
	/** 是否扫描 */
	private boolean scan;
	
	private List<String> hotRoomId;
	
	private static volatile StormScanner instance;
	
	protected StormScanner() {
		super("节奏风暴扫描器");
		
//		this.scanCookie = Browser.COOKIES();
		this.scanCookie = "fts=1515566327; sid=i1ullm4k; DedeUserID__ckMd5=d1a0e260e1010310; SESSDATA=e764cf2a%2C1518158344%2Cbd05661e; DedeUserID=247056833; buvid3=A82B9293-C27E-4B59-8C14-A6B87C4EDC9531028infoc; bili_jct=17753d82b12a4306f50befd1de3de6a0; LIVE_BUVID=AUTO2815155663477917; finger=540129ae; _dfcaptcha=118ac0b938e70b640764ccde26b8e63b";
		this.scan = false;
		this.hotRoomId = new LinkedList<String>();
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
	
	@Override
	protected void _before() {
		log.info("{} 已启动", getName());
		
		if(SwingUtils.confirm("是否使用 [马甲号] 扫描 ? (收益归主号所有)")) {
			
//			_LoginUI loginUI = new _LoginUI();
//			loginUI._view();
			// FIXME: 马甲号的cookies也保留
		}
	}

	@Override
	protected void _loopRun() {
		
		if(isScan() == true) {
			reflashHotLives();
			sancAndJoinStorm();
		}
		_sleep(1000);
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
			hotRoomId.clear();
			hotRoomId.addAll(roomIds);
			log.info("已更新 [Top {}] 的人气直播间.", hotRoomId.size());
		}
		return hotRoomId.isEmpty();
	}
	
	/**
	 * 扫描并加入节奏风暴抽奖
	 */
	public void sancAndJoinStorm() {
		int cnt = MsgSender.scanStorms(scanCookie, hotRoomId, SCAN_INTERVAL);
		if(cnt > 0) {
			log.info("参与节奏风暴抽奖成功(连击x{})", cnt);
		}
	}
	
	public boolean isScan() {
		return scan;
	}

	public void setScan() {
		scan = !scan;
	}
	
}
