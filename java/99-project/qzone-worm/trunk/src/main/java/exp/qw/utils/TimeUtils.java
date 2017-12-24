package exp.qw.utils;

import exp.libs.utils.other.StrUtils;

public class TimeUtils extends exp.libs.utils.time.TimeUtils {

	public static String getCurTimePrefix() {
		return StrUtils.concat("[", toStr(System.currentTimeMillis()), "] ");
	}
	
}
