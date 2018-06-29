package exp.libs.warp.task.cron;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.plaf.ListUI;

import exp.libs.utils.other.ListUtils;
import exp.libs.utils.other.StrUtils;


/*
秒（0~59） 
分钟（0~59） 
小时（0~23） 
天（月）（0~31，但是你需要考虑你月的天数）
月（0~11） 
天（星期）（1~7 1=SUN 或 SUN，MON，TUE，WED，THU，FRI，SAT） 
年份（1970－2099） 
 */

// * * * * * ? *
//	位置	时间域	允许值		特殊值
//	1	秒		0-59	    , - * /
//	2	分钟		0-59	    , - * /
//	3	小时		0-23	    , - * /
//	4	日期		1-31	    , - * ? / L W C
//	5	月份		1-12	    , - * /
//	6	星期		1-7	        , - * ? / L C #
//	7	年份（可选）1970－2099	, - * /
//	星号(*)：可用在所有字段中，表示对应时间域的每一个时刻，例如， 在分钟字段时，表示“每分钟”；
//	
//	问号（?）：该字符只在日期和星期字段中使用，它通常指定为“无意义的值”，相当于点位符；
//	
//	减号(-)：表达一个范围，如在小时字段中使用“10-12”，则表示从10到12点，即10,11,12；
//	
//	逗号(,)：表达一个列表值，如在星期字段中使用“MON,WED,FRI”，则表示星期一，星期三和星期五；
//	
//	斜杠(/)：x/y表达一个等步长序列，x为起始值，y为增量步长值。如在分钟字段中使用0/15，则表示为0,15,30和45秒，而5/15在分钟字段中表示5,20,35,50，你也可以使用*/y，它等同于0/y；
//	
//	L：该字符只在日期和星期字段中使用，代表“Last”的意思，但它在两个字段中意思不同。L在日期字段中，表示这个月份的最后一天，如一月的31号，非闰年二月的28号；如果L用在星期中，则表示星期六，等同于7。但是，如果L出现在星期字段里，而且在前面有一个数值X，则表示“这个月的最后X天”，例如，6L表示该月的最后星期五；
//	
//	W：该字符只能出现在日期字段里，是对前导日期的修饰，表示离该日期最近的工作日。例如15W表示离该月15号最近的工作日，如果该月15号是星期六，则匹配14号星期五；如果15日是星期日，则匹配16号星期一；如果15号是星期二，那结果就是15号星期二。但必须注意关联的匹配日期不能够跨月，如你指定1W，如果1号是星期六，结果匹配的是3号星期一，而非上个月最后的那天。W字符串只能指定单一日期，而不能指定日期范围；
//	
//	LW组合：在日期字段可以组合使用LW，它的意思是当月的最后一个工作日；
//	
//	井号(#)：该字符只能在星期字段中使用，表示当月某个工作日。如6#3表示当月的第三个星期五(6表示星期五，#3表示当前的第三个)，而4#5表示当月的第五个星期三，假设当月没有第五个星期三，忽略不触发；
//	
//	C：该字符只在日期和星期字段中使用，代表“Calendar”的意思。它的意思是计划所关联的日期，如果日期没有被关联，则相当于日历中所有日期。例如5C在日期字段中就相当于日历5日以后的第一天。1C在星期字段中相当于星期日后的第一天。
//	
//	Cron表达式对特殊字符的大小写不敏感，对代表星期的缩写英文大小写也不敏感。


abstract class __TimeUnit {

	protected String subExpression;
	
	protected Cron cron;
	
	protected __TimeUnit(Cron cron) {
		this.cron = cron;
		this.subExpression = "*";
	}
	
	/** 每值: 根据时间单位不同, 可表示 "每秒/每分/每小时/每日/每月/每周/每年" */
	public String withEvery() {
		subExpression = "*";
		return subExpression;
	}
	
	public String withRange(int from, int to) {
		subExpression = StrUtils.concat(from, "-", to);
		return subExpression;
	}
	
	public String withList(int... list) {
		if(list != null) {
			List<Integer> _list = new LinkedList<Integer>();
			for(int e : list) {
				_list.add(e);
			}
			ListUtils.removeDuplicate(_list);	// 去重
			Collections.sort(_list);	// 排序
			
			subExpression = StrUtils.concat(_list, ",");
		}
		return subExpression;
	}
	
	public String withStep(int from, int interval) {
		subExpression = StrUtils.concat(from, "/", interval);
		return subExpression;
	}
	
	// 子类用正则校验
	// 其他方法的取值范围也让子类实现
	public abstract String setSubExpression(String subExpression);
		
	public String toExpression() {
		return subExpression;
	}
	
	@Override
	public String toString() {
		return toExpression();
	}
	
}
