package exp.bilibili.plugin.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibili.plugin.bean.pdm.SendGift;
import exp.bilibili.plugin.core.back.MsgSender;
import exp.bilibili.plugin.utils.TimeUtils;
import exp.bilibili.plugin.utils.UIUtils;
import exp.libs.utils.num.RandomUtils;
import exp.libs.utils.other.ListUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.thread.LoopThread;

/**
 * <PRE>
 * 在线聊天管理器:
 *  1.自动晚安
 *  2.自动感谢投喂
 *  3.定时公告
 *  4.自动打call
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class ChatMgr extends LoopThread {

	private final static Logger log = LoggerFactory.getLogger(ChatMgr.class);
	
	/** 同屏可以显示的最大发言�? */
	private final static int SCREEN_CHAT_LIMT = 7;
	
	private final static String NOTICE_KEY = "【公告�?";
	
	private final static String NIGHT_KEY = "晚安(´▽`)�?  ";
	
	/** 同一时间可以感谢的最大用户数（避免刷屏） */
	private final static int THX_USER_LIMIT = 2;
	
	/** 发送消息间�? */
	private final static long SEND_TIME = 500;
	
	/** 自动感谢周期 */
	private final static long THX_TIME = 30000;
	
	/** 滚屏公告周期 */
	private final static long NOTICE_TIME = 300000;
	
	/** 自动打call周期 */
	private final static long CALL_TIME = 30000;
	
	/** 检测待发送消息间�? */
	private final static long SLEEP_TIME = 1000;
	
	private final static int THX_LIMIT = (int) (THX_TIME / SLEEP_TIME);
	
	private final static int NOTICE_LIMIT = (int) (NOTICE_TIME / SLEEP_TIME);
	
	private final static int CALL_LIMIT = (int) (CALL_TIME / SLEEP_TIME);
	
	private int thxCnt;
	
	private int noticeCnt;
	
	private int callCnt;
	
	/** 自动答谢 */
	private boolean autoThankYou;
	
	/** 自动公告 */
	private boolean autoNotice;
	
	/** 自动打call */
	private boolean autoCall;
	
	/** 自动晚安 */
	private boolean autoGoodNight;
	
	/** 已经被晚安过的用�? */
	private Set<String> nightedUsers;
	
	/**
	 * 一段时间内，每个用户赠送的礼物清单.
	 *  username -> giftName -> giftName
	 */
	private Map<String, Map<String, Integer>> userGifts;
	
	/**
	 * 发言计数�?(主要针对定时公告和自动打call)
	 * 	当同屏存在自己的发言时，则取消本次自动发言，避免刷�?.
	 */
	private int chatCnt;
	
	private static volatile ChatMgr instance;
	
	private ChatMgr() {
		super("自动发言�?");
		this.thxCnt = 0;
		this.noticeCnt = 0;
		this.callCnt = 0;
		this.chatCnt = SCREEN_CHAT_LIMT;
		this.autoThankYou = false;
		this.autoNotice = false;
		this.autoCall = false;
		this.autoGoodNight = false;
		this.nightedUsers = new HashSet<String>();
		this.userGifts = new LinkedHashMap<String, Map<String, Integer>>();
	}
	
	public static ChatMgr getInstn() {
		if(instance == null) {
			synchronized (ChatMgr.class) {
				if(instance == null) {
					instance = new ChatMgr();
				}
			}
		}
		return instance;
	}
	
	private void clear() {
		nightedUsers.clear();
		userGifts.clear();
	}
	
	@Override
	protected void _before() {
		log.info("{} 已启�?", getName());
	}

	@Override
	protected void _loopRun() {
		
		// 自动感谢礼物投喂
		if(thxCnt++ >= THX_LIMIT) {
			thxCnt = 0;
			toThxGift();
		}
		
		// 定时公告
		if(noticeCnt++ >= NOTICE_LIMIT && allowAutoChat()) {
			noticeCnt = 0;
			toNotice();
		}
		
		// 定时打call（支持主播公告）
		if(callCnt++ >= CALL_LIMIT && allowAutoChat()) {
			callCnt = 0;
			toCall();
		}
		
		_sleep(SLEEP_TIME);
	}

	@Override
	protected void _after() {
		clear();
		log.info("{} 已停�?", getName());
	}
	
	/**
	 * 开播打招呼
	 * @param roomId
	 */
	public void helloLive(int roomId) {
		String card = RandomUtils.randomElement(MsgKwMgr.getCards());
		String msg = "滴~".concat(card);
		
		int hour = TimeUtils.getCurHour(8);	// 中国8小时时差
		if(hour >= 6 && hour < 12) {
			msg = msg.concat("早上�?");
			
		} else if(hour >= 12 && hour < 18) {
			msg = msg.concat("下午�?");
			
		} else if(hour >= 18 && hour < 24) {
			msg = msg.concat("晚上�?");
			
		} else {
			msg = msg.concat("还在浪吗?");
		}
		MsgSender.sendChat(msg, roomId);
	}
	
	/**
	 * 自动晚安
	 * @param username
	 * @param msg
	 */
	public void addNight(String username, String msg) {
		if(!isAutoGoodNight() || 
				msg.startsWith(NIGHT_KEY) ||		// 避免跟机器人对话
				nightedUsers.contains(username)) { 	// 避免重复晚安
			return;
		}
		
		if(MsgKwMgr.containsNight(msg)) {
			String chatMsg = StrUtils.concat(NIGHT_KEY, ", ", username);
			MsgSender.sendChat(chatMsg, UIUtils.getCurChatColor());
			nightedUsers.add(username);
		}
	}
	
	/**
	 * 房间内高能礼物感谢与中奖祝贺
	 * @param msg
	 * @return
	 */
	public boolean sendThxEnergy(String msg) {
		boolean isOk = false;
		if(isAutoThankYou()) {
			isOk = MsgSender.sendChat(StrUtils.concat(NOTICE_KEY, msg), 
					UIUtils.getCurChatColor());
		}
		return isOk;
	}
	
	/**
	 * 感谢上船
	 * @param msg
	 */
	public void sendThxGuard(String msg) {
		if(!isAutoThankYou()) {
			return;
		}
		
		MsgSender.sendChat(StrUtils.concat(NOTICE_KEY, "感谢 ", msg), 
				UIUtils.getCurChatColor());
	}
	
	/**
	 * 添加到投喂感谢列�?
	 * @param msgBean
	 */
	public void addThxGift(SendGift msgBean) {
		if(!isAutoThankYou() || msgBean.getNum() <= 0) {
			return;
		}
		
		String username = msgBean.getUname();
		String giftName = msgBean.getGiftName();
		
		synchronized (userGifts) {
			Map<String, Integer> gifts = userGifts.get(username);
			if(gifts == null) {
				gifts = new HashMap<String, Integer>();
				userGifts.put(username, gifts);
			}
			
			Integer sum = gifts.get(giftName);
			sum = (sum == null ? 0 : sum);
			gifts.put(giftName, (sum + msgBean.getNum()));
		}
	}
	
	/**
	 * 感谢一段时间内所有用户的投喂
	 */
	private void toThxGift() {
		Map<String, Map<String, Integer>> tmp = 
				new LinkedHashMap<String, Map<String,Integer>>();
		synchronized (userGifts) {
			tmp.putAll(userGifts);
			userGifts.clear();
		}
		
		// 若短时间内投喂用户过�?, 则不逐一感谢, 避免刷屏
		int userNum = tmp.keySet().size();
		if(userNum > THX_USER_LIMIT) {
			String msg = StrUtils.concat(NOTICE_KEY, "感谢前面[", userNum, 
					"]个大佬的投喂d(´ω｀*)");
			MsgSender.sendChat(msg, UIUtils.getCurChatColor());
			
		// 分别合并每个用户的投喂礼物再感谢
		} else {
			Iterator<String> userIts = tmp.keySet().iterator();
			while(userIts.hasNext()) {
				String username = userIts.next();
				Map<String, Integer> gifts = tmp.get(username);
				
				toThxGift(username, gifts);
				_sleep(SEND_TIME);
				
				userIts.remove();
			}
			tmp.clear();
		}
	}
	
	/**
	 * 感谢某个用户的投�?
	 * @param username
	 * @param gifts
	 */
	private void toThxGift(String username, Map<String, Integer> gifts) {
		if(gifts.size() <= 0) {
			return;
			
		// 1个礼物多�?
		} else if(gifts.size() == 1) {
			Iterator<String> giftIts = gifts.keySet().iterator();
			if(giftIts.hasNext()) {
				String giftName = giftIts.next();
				Integer num = gifts.get(giftName);
				if(num != null && num > 0) {
					int cost = ActivityMgr.showCost(giftName, num);
					String msg = StrUtils.concat(NOTICE_KEY, "感谢[", username, "]", 
							MsgKwMgr.getAdj(), "投喂", num, "个[", giftName, "],活跃+", cost);
					MsgSender.sendChat(msg, UIUtils.getCurChatColor());
				}
			}
			
		// 多个礼物多份
		} else {
			int cost = 0;
			StringBuilder sb = new StringBuilder();
			Iterator<String> giftIts = gifts.keySet().iterator();
			while(giftIts.hasNext()) {
				String giftName = giftIts.next();
				sb.append(giftName).append(",");
				cost += ActivityMgr.showCost(giftName, gifts.get(giftName));
			}
			sb.setLength(sb.length() - 1);
			
			String msg = StrUtils.concat(NOTICE_KEY, "感谢[", username, "]", 
					MsgKwMgr.getAdj(), "投喂[", sb.toString(), "],活跃+", cost);
			MsgSender.sendChat(msg, UIUtils.getCurChatColor());
		}
		
		gifts.clear();
	}
	
	/**
	 * 定时公告
	 */
	private void toNotice() {
		if(!isAutoNotice() || ListUtils.isEmpty(MsgKwMgr.getNotices())) {
			return;
		}
		
		String msg = NOTICE_KEY.concat(RandomUtils.randomElement(MsgKwMgr.getNotices()));
		MsgSender.sendChat(msg, UIUtils.getCurChatColor());
	}
	
	/**
	 * 定时打call
	 */
	private void toCall() {
		if(!isAutoCall() || ListUtils.isEmpty(MsgKwMgr.getCalls())) {
			return;
		}
		
		String msg = RandomUtils.randomElement(MsgKwMgr.getCalls());
		MsgSender.sendChat(msg, UIUtils.getCurChatColor());
	}
	
	public void setAutoThankYou() {
		autoThankYou = !autoThankYou;
		userGifts.clear();	// 切换状态时, 清空已投喂的礼物列表
	}
	
	public boolean isAutoThankYou() {
		return autoThankYou;
	}
	
	public void setAutoNotice() {
		autoNotice = !autoNotice;
		chatCnt = SCREEN_CHAT_LIMT;
	}
	
	public boolean isAutoNotice() {
		return autoNotice;
	}
	
	public void setAutoCall() {
		autoCall = !autoCall;
		chatCnt = SCREEN_CHAT_LIMT;
	}
	
	public boolean isAutoCall() {
		return autoCall;
	}
	
	public void setAutoGoodNight() {
		autoGoodNight = !autoGoodNight;
		nightedUsers.clear();	// 切换状态时, 清空已晚安的用户列表
	}
	
	public boolean isAutoGoodNight() {
		return autoGoodNight;
	}
	
	/**
	 * 计算登陆用户的发言次数
	 * @param chatUser 当前发言用户
	 */
	public void countChatCnt(String chatUser) {
		
		// 当是登陆用户发言�?, 清空计数�?
		if(LoginMgr.getInstn().getLoginUser().equals(chatUser)) {
			chatCnt = 0;
			
		// 当是其他用户发言�?, 计数�?+1
		} else {
			chatCnt++;
		}
	}
	
	/**
	 * 是否允许自动发言:
	 * 	当距离上一次发言超过同屏显示限制时，则允许自动发言
	 * @return
	 */
	private boolean allowAutoChat() {
		return chatCnt >= SCREEN_CHAT_LIMT;
	}
	
}
