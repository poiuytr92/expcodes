package exp.libs.warp.task;

import java.text.ParseException;

import org.quartz.CronExpression;

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

	// FIXME 规则为步长的时候还需指定  触发器的起止时间 ？
	
	private String expression;
	
	public Cron() {
		
	}
	
	public Cron(String expression) {
		try {
			CronExpression ce = new CronExpression(expression);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		this.expression = (expression == null ? "" : expression);
	}
	
	public String toExpression() {
		return expression;
	}
	
}
