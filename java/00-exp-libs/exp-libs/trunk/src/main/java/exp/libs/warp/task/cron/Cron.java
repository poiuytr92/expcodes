package exp.libs.warp.task.cron;

import java.text.ParseException;

import org.quartz.CronExpression;

import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 适用于任务调度管理器的cron对象（用于生成cron表达式规则字符串）
 * </PRE>
 * <br/><B>PROJECT : </B> exp-libs
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-06-28
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Cron {

	private _Second second;
	
	private _Minute minute;
	
	private _Hour hour;
	
	private _Day day;
	
	private _Month month;
	
	private _Week week;
	
	private _Year year;
	
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
		try {
			CronExpression ce = new CronExpression(expression);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public _Second Second() {
		return second;
	}
	
	public _Minute Minute() {
		return minute;
	}
	
	public _Hour Hour() {
		return hour;
	}
	
	public _Day Day() {
		return day;
	}
	
	public _Month Month() {
		return month;
	}
	
	public _Week Week() {
		return week;
	}
	
	public _Year Year() {
		return year;
	}
	
	public Cron setExpression(String expression) {
		this.expression = (expression == null ? "" : expression);
		return this;
	}
	
	// FIXME 冲突约束
	public String toExpression() {
		this.expression = StrUtils.concat(
				second.toExpression(), " ", 
				minute.toExpression(), " ", 
				hour.toExpression(), " ", 
				day.toExpression(), " ", 
				month.toExpression(), " ", 
				week.toExpression(), " ", 
				year.toExpression()
		);
		return expression;
	}
	
	@Override
	public String toString() {
		return toExpression();
	}
	
}
