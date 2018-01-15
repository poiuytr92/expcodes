package exp.bilibili.plugin.cache;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.bean.pdm.ChatMsg;
import exp.bilibili.plugin.bean.pdm.GuardBuy;
import exp.bilibili.plugin.bean.pdm.SendGift;
import exp.bilibili.plugin.bean.pdm.TActivity;
import exp.bilibili.plugin.core.back.MsgSender;
import exp.bilibili.plugin.envm.Gift;
import exp.bilibili.plugin.envm.Level;
import exp.bilibili.plugin.utils.UIUtils;
import exp.libs.envm.Charset;
import exp.libs.envm.DBType;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.io.JarUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.db.sql.SqliteUtils;
import exp.libs.warp.db.sql.bean.DataSourceBean;

/**
 * <PRE>
 * 用户活跃度管理器.
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class ActivityMgr {

	private final static Logger log = LoggerFactory.getLogger(ActivityMgr.class);
	
	private final static String ENV_DB_SCRIPT = "/exp/bilibili/plugin/bean/pdm/BP-DB.sql";
	
	private final static String ENV_DB_DIR = "./conf/";
	
	private final static String ENV_DB_NAME = ".BP";
	
	private final static String ENV_DB_PATH = ENV_DB_DIR.concat(ENV_DB_NAME);
	
	public final static DataSourceBean DS = new DataSourceBean();
	static {
		DS.setDriver(DBType.SQLITE.DRIVER);
		DS.setName(ENV_DB_PATH);
	}
	
	/** 管理员在B站的用户ID */
	private final static String SENDER_UID = CryptoUtils.deDES("349B00EE2F2B0A6B");
	
	/** 触发私信的活跃值单位(即每至少超过1W活跃值时发送一次私信) */
	private final static int COST_UNIT = 10000;
	
	/** 打印活跃值时需要除掉的单位（100） */
	private final static int SHOW_UNIT = 100;
	
	/** 只针对此直播间计算活跃度, 在其他直播间的行为不计算活跃度. */
	private final static int ROOM_ID = RoomMgr.getInstn().getRealRoomId(
			Config.getInstn().ACTIVITY_ROOM_ID());
	
	/**
	 * 用户集
	 *  UID -> username
	 */
	private Map<String, String> users;
	
	/**
	 * 用户活跃度:
	 *  UID -> 累计活跃度
	 */
	private Map<String, Integer> costs;
	
	private static volatile ActivityMgr instance;
	
	private ActivityMgr() {
		this.users = new HashMap<String, String>();
		this.costs = new HashMap<String, Integer>();
		
		if(initEnv()) {
			read();
		}
	}
	
	public static ActivityMgr getInstn() {
		if(instance == null) {
			synchronized (ActivityMgr.class) {
				if(instance == null) {
					instance = new ActivityMgr();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 初始化活跃值数据库环境
	 * @return
	 */
	private static boolean initEnv() {
		boolean isOk = true;
		if(Config.LEVEL < Level.ADMIN) {
			return isOk;	// 仅管理员可以操作
		}
		
		File dbFile = new File(ENV_DB_PATH);
		if(!dbFile.exists()) {
			FileUtils.createDir(ENV_DB_DIR);
			Connection conn = SqliteUtils.getConn(DS);
			String script = JarUtils.read(ENV_DB_SCRIPT, Charset.ISO);
			String[] sqls = script.split(";");
			for(String sql : sqls) {
				if(StrUtils.isNotTrimEmpty(sql)) {
					isOk &= SqliteUtils.execute(conn, sql);
				}
			}
			SqliteUtils.close(conn);
			
			FileUtils.hide(dbFile);
		}
		log.info("初始化活跃值数据库{}", (isOk ? "成功" : "失败"));
		return isOk;
	}
	
	/**
	 * 是否记录活跃值
	 *  当且仅当是管理员身份, 且在监听特定直播间时才记录
	 * @return
	 */
	private boolean isRecord() {
		boolean isRecord = false;
		if(Config.LEVEL >= Level.ADMIN) {
			int curRoomId = RoomMgr.getInstn().getRealRoomId(UIUtils.getCurRoomId());
			if(ROOM_ID > 0 && ROOM_ID == curRoomId) {
				isRecord = true;
			}
		}
		return isRecord;
	}
	
	/**
	 * 增加的活跃值（弹幕）
	 * @param gift 弹幕信息
	 */
	public void add(ChatMsg gift) {
		if(isRecord() == false) {
			return;
		}
		
		users.put(gift.getUid(), gift.getUsername());
		int cost = countCost("弹幕", 1);
		add(gift.getUid(), cost);
	}

	/**
	 * 增加的活跃值（投喂）
	 * @param gift 投喂信息
	 */
	public void add(SendGift gift) {
		if(isRecord() == false) {
			return;
		}
		
		users.put(gift.getUid(), gift.getUname());
		int cost = countCost(gift.getGiftName(), gift.getNum());
		add(gift.getUid(), cost);
	}
	
	/**
	 * 增加的活跃值（船员）
	 * @param gift 船员信息
	 */
	public void add(GuardBuy gift) {
		if(isRecord() == false) {
			return;
		}
		
		users.put(gift.getUid(), gift.getUsername());
		int cost = countCost(gift.getGuardDesc(), 1);
		add(gift.getUid(), cost);
	}
	
	/**
	 * 增加活跃值(达到一定活跃值则发送私信)
	 * @param uid 用户ID
	 * @param cost 活跃值
	 */
	private void add(String uid, int cost) {
		if(cost <= 0) {
			return;
		}
		
		Integer before = costs.get(uid);
		before = (before == null ? 0 : before);
		int after = before + cost;
		costs.put(uid, after);
		
		if(UIUtils.isLogined() && // 登陆后才能发送私信
				(before % COST_UNIT + cost) >= COST_UNIT) {
			String msg = StrUtils.concat("恭喜您在 [", ROOM_ID, "] 直播间的活跃度达到 [", 
					(after / SHOW_UNIT), "]");
			MsgSender.sendPrivateMsg(SENDER_UID, uid, msg);
		}
	}
	
	/**
	 * 计算活跃值
	 * @param giftName
	 * @param num
	 * @return
	 */
	private static int countCost(String giftName, int num) {
		return Gift.getCost(giftName) * num;
	}
	
	/**
	 * 在版聊区显示活跃值（为实际值/100）
	 * @param giftName
	 * @param num
	 * @return
	 */
	public static int showCost(String giftName, int num) {
		return (countCost(giftName, num) / SHOW_UNIT);
	}
	
	/**
	 * 读取历史活跃值
	 */
	private void read() {
		if(Config.LEVEL < Level.ADMIN) {
			return;	// 仅管理员可以操作
		}
		
		List<TActivity> activitys = _queryAll();
		for(TActivity activity : activitys) {
			users.put(activity.getUid(), activity.getUsername());
			costs.put(activity.getUid(), activity.getCost());
		}
		log.info("已读取直播间 [{}] 的历史活跃值", ROOM_ID);
	}
	
	private static List<TActivity> _queryAll() {
		String where = StrUtils.concat(TActivity.getRoomid$CN(), " = ", ROOM_ID);
		Connection conn = SqliteUtils.getConnByJDBC(DS);
		List<TActivity> activitys = TActivity.querySome(conn, where);
		SqliteUtils.close(conn);
		return activitys;
	}
	
	/**
	 * 更新保存活跃值
	 */
	public void save() {
		if(users.size() <= 0 || costs.size() <= 0) {
			return;
		}
		
		List<TActivity> activitys = new LinkedList<TActivity>();
		Iterator<String> uids = costs.keySet().iterator();
		while(uids.hasNext()) {
			String uid = uids.next();
			String username = users.get(uid);
			int cost = costs.get(uid);
			
			TActivity activity = new TActivity();
			activity.setUid(uid);
			activity.setUsername(username);
			activity.setCost(cost);
			activity.setRoomid(ROOM_ID);
			activitys.add(activity);
		}
		
		boolean isOk = _truncate();
		isOk &= _saveAll(activitys);
		log.info("更新直播间 [{}] 的活跃值{}", ROOM_ID, (isOk ? "成功" : "失败"));
		
		users.clear();
		costs.clear();
	}
	
	private static boolean _truncate() {
		String where = StrUtils.concat(TActivity.getRoomid$CN(), " = ", ROOM_ID);
		Connection conn = SqliteUtils.getConnByJDBC(DS);
		boolean isOk = TActivity.delete(conn, where);
		SqliteUtils.close(conn);
		return isOk;
	}
	
	private static boolean _saveAll(List<TActivity> activitys) {
		boolean isOk = true;
		Connection conn = SqliteUtils.getConnByJDBC(DS);
		SqliteUtils.setAutoCommit(conn, false);
		try {
			for(TActivity activity : activitys) {
				isOk &= TActivity.insert(conn, activity);
			}
			conn.commit();
			
		} catch(Exception e) {
			log.error("更新直播间 [{}] 的活跃值异常", ROOM_ID, e);
			isOk = false;
		}
		
		SqliteUtils.setAutoCommit(conn, true);
		SqliteUtils.releaseDisk(conn);
		SqliteUtils.close(conn);
		return isOk;
	}
	
}
