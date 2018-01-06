package exp.bilibli.plugin.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import exp.bilibli.plugin.Config;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.num.RandomUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 消息关键字管理器
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class MsgKwMgr {

	private List<String> advs;
	
	private Set<String> nights;
	
	/** 开播上车的卡片类型 */
	private List<String> cards;
	
	/** 滚屏公告的候选列表 */
	private List<String> notices;
	
	/** 自动打call的候选列表 */
	private List<String> calls;
	
	private static volatile MsgKwMgr instance;
	
	private MsgKwMgr() {
		this.advs = new ArrayList<String>();
		this.nights = new HashSet<String>();
		this.cards = new ArrayList<String>();
		this.notices = new ArrayList<String>();
		this.calls = new ArrayList<String>();
		
		init();
	}
	
	public static MsgKwMgr getInstn() {
		if(instance == null) {
			synchronized (MsgKwMgr.class) {
				if(instance == null) {
					instance = new MsgKwMgr();
				}
			}
		}
		return instance;
	}
	
	private void init() {
		read(Config.getInstn().ADV_PATH(), advs);
		read(Config.getInstn().NIGHT_PATH(), nights);
		read(Config.getInstn().CARD_PATH(), cards);
		
		read(Config.getInstn().NOTICE_PATH(), notices);
		read(Config.getInstn().CALL_PATH(), calls);
	}
	
	private void read(String path, Collection<String> list) {
		List<String> lines = FileUtils.readLines(path, Config.DEFAULT_CHARSET);
		for(String line : lines) {
			line = line.trim();
			if(StrUtils.isNotEmpty(line)) {
				list.add(line);
			}
		}
	}
	
	public void clear() {
		advs.clear();
		nights.clear();
		cards.clear();
		
		notices.clear();
		calls.clear();
	}
	
	public void reload() {
		notices.clear();
		read(Config.getInstn().NOTICE_PATH(), notices);
		
		calls.clear();
		read(Config.getInstn().CALL_PATH(), calls);
	}

	public static String getAdj() {
		return getInstn()._getAdj();
	}
	
	private String _getAdj() {
		if(advs.size() <= 0) {
			return "";
		}
		
		int idx = RandomUtils.randomInt(advs.size());
		return advs.get(idx);
	}
	
	public static boolean containsNight(String msg) {
		return getInstn()._containsNight(msg);
	}
	
	private boolean _containsNight(String msg) {
		boolean isContains = false;
		for(String night : nights) {
			if(msg.contains(night)) {
				isContains = true;
				break;
			}
		}
		return isContains;
	}

	public static List<String> getNotices() {
		return getInstn()._getNotices();
	}
	
	private List<String> _getNotices() {
		return notices;
	}

	public static List<String> getCalls() {
		return getInstn()._getCalls();
	}
	
	public List<String> _getCalls() {
		return calls;
	}

	public static List<String> getCards() {
		return getInstn()._getCards();
	}
	
	public List<String> _getCards() {
		return cards;
	}
	
}