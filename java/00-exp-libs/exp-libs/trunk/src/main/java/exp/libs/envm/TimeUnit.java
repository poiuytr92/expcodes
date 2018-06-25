package exp.libs.envm;

/**
 * <PRE>
 * 枚举类：时间单位
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2017-08-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public enum TimeUnit {

	NS("ns", "cell: nanosecond", "纳秒"), 
	
	MS("ms", "microsecond: 1ms = 1000ns", "微秒"), 
	
	SECOND("s", "second: 1s = 1000ms", "�?"), 
	
	MINUTE("min", "minute: 1min = 60s", "�?"), 
	
	HOUR("H", "hour: 1H = 60min", "�?"), 
	
	DAY("d", "day: 1d = 24H", "�?"), 
	
	MONTH("M", "month: 1M = 28/29/30/31d", "�?"), 
	
	YEAR("y", "year: 1y = 12M = 365/366d", "�?"), 
	
	;
	
	public String VAL;
	
	public String DES_EN;
	
	public String DES_CH;
	
	private TimeUnit(String val, String desEn, String desCh) {
		this.VAL = val;
		this.DES_EN = desEn;
		this.DES_CH = desCh;
	}
	
}
