package exp.bilibli.plugin.cache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import exp.bilibli.plugin.Config;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.num.RandomUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 关键字管理器
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class KeywordMgr {

	private List<String> advs;
	
	private Set<String> nights;
	
	private static volatile KeywordMgr instance;
	
	private KeywordMgr() {
		this.advs = new ArrayList<String>();
		this.nights = new HashSet<String>();
		
		init();
	}
	
	public static KeywordMgr getInstn() {
		if(instance == null) {
			synchronized (KeywordMgr.class) {
				if(instance == null) {
					instance = new KeywordMgr();
				}
			}
		}
		return instance;
	}
	
	private void init() {
		List<String> advLines = FileUtils.readLines(
				Config.getInstn().ADV_PATH(), Config.DEFAULT_CHARSET);
		for(String line : advLines) {
			line = line.trim();
			if(StrUtils.isNotEmpty(line)) {
				advs.add(line);
			}
		}
		
		List<String> nightLines = FileUtils.readLines(
				Config.getInstn().NIGHT_PATH(), Config.DEFAULT_CHARSET);
		for(String line : nightLines) {
			line = line.trim();
			if(StrUtils.isNotEmpty(line)) {
				nights.add(line);
			}
		}
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
	
}
