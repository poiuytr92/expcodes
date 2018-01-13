package exp.bilibili.plugin.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.bean.pdm.ChatMsg;
import exp.bilibili.plugin.bean.pdm.GuardBuy;
import exp.bilibili.plugin.bean.pdm.SendGift;
import exp.bilibili.plugin.core.back.MsgSender;
import exp.bilibili.plugin.envm.Gift;
import exp.bilibili.plugin.envm.Level;
import exp.bilibili.plugin.utils.UIUtils;
import exp.libs.envm.Charset;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.io.flow.FileFlowReader;

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

	/** 管理员在B站的用户ID */
	private final static String SENDER_UID = CryptoUtils.deDES("349B00EE2F2B0A6B");
	
	/** 触发私信的活跃值单位(即每至少超过1W活跃值时发送一次私信) */
	private final static int COST_UNIT = 10000;
	
	/** 打印活跃值时需要除掉的单位（100） */
	private final static int SHOW_UNIT = 100;
	
	public final static String COST_PATH = "./data/activity/cost.dat";
	
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
		
		read();
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
	
	public void add(ChatMsg gift) {
		if(isRecord() == false) {
			return;
		}
		
		users.put(gift.getUid(), gift.getUsername());
		int cost = countCost("弹幕", 1);
		add(gift.getUid(), cost);
	}

	public void add(SendGift gift) {
		if(isRecord() == false) {
			return;
		}
		
		users.put(gift.getUid(), gift.getUname());
		int cost = countCost(gift.getGiftName(), gift.getNum());
		add(gift.getUid(), cost);
	}
	
	public void add(GuardBuy gift) {
		if(isRecord() == false) {
			return;
		}
		
		users.put(gift.getUid(), gift.getUsername());
		int cost = countCost(gift.getGuardDesc(), 1);
		add(gift.getUid(), cost);
	}
	
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
	
	private static int countCost(String giftName, int num) {
		return Gift.getCost(giftName) * num;
	}
	
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
		
		FileFlowReader ffr = new FileFlowReader(COST_PATH, Charset.ISO);
		while(ffr.hasNextLine()) {
			String line = ffr.readLine();
			line = CryptoUtils.deDES(line.trim());
			
			try {
				String[] datas= line.split("=");
				users.put(datas[0], datas[2]);
				costs.put(datas[0], NumUtils.toInt(datas[1], 0));
				
			} catch(Exception e) {}
		}
		ffr.close();
	}
	
	/**
	 * 保存活跃值
	 */
	public void save() {
		if(Config.LEVEL < Level.ADMIN) {
			return;	// 仅管理员可以操作
		}
		
		StringBuilder data = new StringBuilder();
		Iterator<String> uids = costs.keySet().iterator();
		while(uids.hasNext()) {
			String uid = uids.next();
			int cost = costs.get(uid);
			String username = users.get(uid);
			
			String line = StrUtils.concat(uid, "=", cost, "=", username);
			data.append(CryptoUtils.toDES(line)).append("\r\n");
		}
		
		FileUtils.write(COST_PATH, data.toString(), Charset.ISO, false);
		users.clear();
		costs.clear();
	}

}
