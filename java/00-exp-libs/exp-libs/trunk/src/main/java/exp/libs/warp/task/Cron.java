package exp.libs.warp.task;

import java.text.ParseException;

import org.quartz.CronExpression;

import exp.libs.utils.other.StrUtils;
import exp.libs.warp.task.cron.Day;
import exp.libs.warp.task.cron.Hour;
import exp.libs.warp.task.cron.Minute;
import exp.libs.warp.task.cron.Month;
import exp.libs.warp.task.cron.Second;
import exp.libs.warp.task.cron.Week;
import exp.libs.warp.task.cron.Year;

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

	private Second second;
	
	private Minute minute;
	
	private Hour hour;
	
	private Day day;
	
	private Month month;
	
	private Week week;
	
	private Year year;
	
	private String expression;
	
	public Cron() {
		this.second = new Second();
		this.minute = new Minute();
		this.hour = new Hour();
		this.day = new Day();
		this.month = new Month();
		this.week = new Week();
		this.year = new Year();
	}
	
	public Cron(String expression) {
		try {
			CronExpression ce = new CronExpression(expression);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public Second Second() {
		return second;
	}
	
	public Minute Minute() {
		return minute;
	}
	
	public Hour Hour() {
		return hour;
	}
	
	public Day Day() {
		return day;
	}
	
	public Month Month() {
		return month;
	}
	
	public Week Week() {
		return week;
	}
	
	public Year Year() {
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
