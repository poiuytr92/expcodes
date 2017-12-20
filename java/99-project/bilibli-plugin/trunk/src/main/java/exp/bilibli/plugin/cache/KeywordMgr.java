package exp.bilibli.plugin.cache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import exp.bilibli.plugin.Config;
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

	private List<String> adjs;
	
	private Set<String> nights;
	
	private static volatile KeywordMgr instance;
	
	private KeywordMgr() {
		this.adjs = new ArrayList<String>();
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
		String[] adjs = Config.getInstn().ADJS().split(",");
		for(String adj : adjs) {
			if(StrUtils.isNotEmpty(adj)) {
				this.adjs.add(adj);
			}
		}
		
		String[] nights = Config.getInstn().NIGHTS().split(",");
		for(String night : nights) {
			if(StrUtils.isNotEmpty(night)) {
				this.nights.add(night);
			}
		}
	}
	
	public static String getAdj() {
		return getInstn()._getAdj();
	}
	
	private String _getAdj() {
		int idx = RandomUtils.randomInt(adjs.size());
		return adjs.get(idx);
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
