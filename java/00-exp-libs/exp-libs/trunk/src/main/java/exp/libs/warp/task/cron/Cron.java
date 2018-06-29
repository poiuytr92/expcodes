package exp.libs.warp.task.cron;

import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 适用于任务调度管理器的cron对象（用于生成cron表达式的规则字符串）
 * ----------------------------
 * cron表达式共有7个时间字段, 依次为:
 * 	秒, 分, 时, 日, 月, 周, 年
 * ----------------------------
 * 每个时间字段的取值范围如下:<br/>
+--------------------+---------------------------+------------------+
| [时间字段]         | [取值范围]                | [允许的特殊字符] |
+--------------------+---------------------------+------------------+
| 秒(Second)         | 0-59                      | , - * /          |
| 分(Minute)         | 0-59                      | , - * /          |
| 小时(Hour)         | 0-23                      | , - * /          |
| 日期(DayOfMonth)   | 1-31                      | , - * / ? L W C  |
| 月份(Month)        | 1-12(或JAN-DEC)           | , - * /          |
| 星期(Week)         | 1-7(或SUN-SAT, 其中1=SUN) | , - * / ? L C #  |
| 年(Year)[可选字段] | 空值或1970-2099           | , - * /          |
+--------------------+---------------------------+------------------+
 * <br/>
 * </PRE>
 * <br/><B>PROJECT : </B> exp-libs
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-06-28
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Cron {

	private final _Second second;
	
	private final _Minute minute;
	
	private final _Hour hour;
	
	private final _Day day;
	
	private final _Month month;
	
	private final _Week week;
	
	private final _Year year;
	
	private String expression;
	
	public Cron() {
		this.second = new _Second(this);
		this.minute = new _Minute(this);
		this.hour = new _Hour(this);
		this.day = new _Day(this);
		this.month = new _Month(this);
		this.week = new _Week(this);
		this.year = new _Year(this);
	}
	
	public Cron(String expression) {
		this();
	}
	
	public void reset() {
		second.withEvery();
		minute.withEvery();
		hour.withEvery();
		day.withEvery();
		month.withEvery();
		week.withNone();
		year.withEvery();
	}
	
	final public _Second Second() {
		return second;
	}
	
	final public _Minute Minute() {
		return minute;
	}
	
	final public _Hour Hour() {
		return hour;
	}
	
	final public _Day Day() {
		return day;
	}
	
	final public _Month Month() {
		return month;
	}
	
	final public _Week Week() {
		return week;
	}
	
	final public _Year Year() {
		return year;
	}
	
	public Cron setExpression(String expression) {
		this.expression = (expression == null ? "" : expression);
		// FIXME 同时修改每个字段对象
		return this;
	}
	
	// FIXME 冲突约束
	public String toExpression() {
		this.expression = StrUtils.concat(
				second.getSubExpression(), " ", 
				minute.getSubExpression(), " ", 
				hour.getSubExpression(), " ", 
				day.getSubExpression(), " ", 
				month.getSubExpression(), " ", 
				week.getSubExpression(), " ", 
				year.getSubExpression()
		);
		return expression;
	}
	
	@Override
	public String toString() {
		return toExpression();
	}
	
}