package exp.qw.utils;

import exp.libs.utils.pub.StrUtils;

public class TimeUtils extends exp.libs.utils.pub.TimeUtils {

	public static String getCurTimePrefix() {
		return StrUtils.concat("[", toStr(System.currentTimeMillis()), "] ");
	}
	
}
