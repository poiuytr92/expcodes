package exp.libs.warp.task;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.DateFormat;
import exp.libs.utils.time.TimeUtils;

/**
 * <PRE>
 * 定时任务管理
 * 依赖包：
 *  quartz-all-1.8.3.jar
 * 	slf4j-api-1.5.8.jar
 * 	slf4j-log4j12-1.5.8.jar
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class TaskMgr {
	
	/** 日志�? */
	private final static Logger log = LoggerFactory.getLogger(TaskMgr.class);
	
	/** 任务调度管理�? */
	private static SchedulerFactory sf = new StdSchedulerFactory();
	
	/** 工作分组 */
	public final static String JOB_GROUP_NAME = "_default_group1";

	/** 触发器分�? */
	public final static String TRIGGER_GROUP_NAME = "_default_trigger1";

	/** 任务信息队列，taskName是任务标识，value是Scheduler对象 */
//	public static Map<String, Scheduler> taskMap = new HashMap<String, Scheduler>();
	
	/** cron构造类型，按分�?  */
	public static final int TYPE_MINUTE = 1;

	/** cron构造类�? ，按小时  */
	public static final int TYPE_HOUR = 2;

	/**
	 * 私有 构造方�?
	 */
	private TaskMgr() {
	}
	
	/**
	 * 获取所有任务名�?
	 *
	 * @return
	 * @throws SchedulerException
	 */
	public static String[] getTaskNames() throws SchedulerException {
		String[] trigs = null;
		Scheduler sd = sf.getScheduler();
		trigs = sd.getTriggerNames(TRIGGER_GROUP_NAME);
//		print(Arrays.toString(trigs));
		return trigs;
	}
	
	/**
	 * <PRE>
	 * 获取任务状�?
		Trigger.Trigger.STATE_NONE: -1	任务不存�?
		STATE_NORMAL: 0	正常
		Trigger.STATE_PAUSED: 1	暂停
		Trigger.STATE_COMPLETE: 2	完成
		Trigger.STATE_ERROR: 3	异常
		Trigger.STATE_BLOCKED: 4	阻塞
		</PRE>
	 *
	 * @param taskName	任务标识
	 * @return
	 * @throws SchedulerException
	 */
	public static int getTaskState(String taskName) throws SchedulerException {
		return sf.getScheduler().getTriggerState(taskName, TRIGGER_GROUP_NAME);
	}
	
	/**
	 * 获取所有任务状�?
	 *
	 * @return
	 * @throws SchedulerException
	 */
	public static Map<String, Integer> getTaskStates() throws SchedulerException {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (String name : getTaskNames()) {
			map.put(name, getTaskState(name));
		}
		return map;
	}
	
	/**
	 * 获取任务计划
	 *
	 * @param taskName	任务标识
	 * @return
	 * @throws SchedulerException
	 */
	public static Trigger getTrigger(String taskName) throws SchedulerException {
		Trigger trig = null;
		trig = sf.getScheduler().getTrigger(taskName, TRIGGER_GROUP_NAME);
		return trig;
	}
	
	/**
	 * 获取所有任务计�?
	 *
	 * @return
	 * @throws SchedulerException
	 */
	public static List<Trigger> getTriggers() throws SchedulerException {
		List<Trigger> trigs = new ArrayList<Trigger>();
		Trigger trig = null;
		for (String name : getTaskNames()) {
			trig = getTrigger(name);
			trigs.add(trig);
		}
		return trigs;
	}
	
	/**
	 * 添加任务
	 * 
	 * @param taskName
	 *            任务标识
	 * @param job
	 *            工作处理�?
	 * @param intervalTime
	 *            时间间隔，单位秒
	 * @param params
	 *            任务处理参数，可以为�?
	 * @throws ParseException
	 * @throws SchedulerException
	 */
	public static void add(String taskName, Job job, int intervalTime,
			Map<String, Object> params) throws ParseException,
			SchedulerException {
		Scheduler sched = null;
		sched = sf.getScheduler();
		JobDetail jobDetail = new JobDetail(taskName, JOB_GROUP_NAME,
				job.getClass());

		if (params != null) {
			jobDetail.getJobDataMap().putAll(params);
		}

		SimpleTrigger trigger = new SimpleTrigger(taskName, TRIGGER_GROUP_NAME);
		trigger.setStartTime(new Date());

		// 如果设置时间间隔�?0，则任务只执行一次，不放入任务队�?
		if (intervalTime == 0) {
			trigger.setRepeatCount(0);
		} else {
			trigger.setRepeatCount(-1);
			trigger.setRepeatInterval(intervalTime * 1000);
		}
		sched.scheduleJob(jobDetail, trigger);
		// 启动
		if (!sched.isShutdown()) {
			sched.start();
		}
	}

	/**
	 * 添加任务,并启�?
	 * 
	 * @param taskName
	 *            任务标识
	 * @param job
	 *            工作处理�?
	 * @param cron
	 *            定时规则Cron
	 * @param params
	 *            任务处理参数，可以为�?
	 * @throws ParseException
	 * @throws SchedulerException
	 */
	public static void add(String taskName, Job job, String cron,
			Map<String, Object> params) throws ParseException,
			SchedulerException {
		add(taskName, job, cron, params, null, null);
	}

	/**
	 * 添加任务,并启�?
	 * 
	 * @param taskName
	 *            任务标识
	 * @param job
	 *            工作处理�?
	 * @param cron
	 *            定时规则Cron
	 * @param params
	 *            任务处理参数，可以为�?
	 * @param startTime
	 * @param endTime
	 * @throws ParseException
	 * @throws SchedulerException
	 */
	public static void add(String taskName, Job job, String cron,
			Map<String, Object> params, Date startTime, Date endTime)
			throws ParseException, SchedulerException {

		Scheduler sched = null;
			sched = sf.getScheduler();
			JobDetail jobDetail = new JobDetail(taskName, JOB_GROUP_NAME,
					job.getClass());

			if (params != null) {
				jobDetail.getJobDataMap().putAll(params);
			}

			if (isEmpty(cron)) {
				throw new SchedulerException("Cron is not null�?");
			} else {
				CronTrigger trigger = new CronTrigger(taskName, TRIGGER_GROUP_NAME);
				trigger.setCronExpression(cron);// 触发器时间设�?
				if (startTime != null) {
					trigger.setStartTime(startTime);
				}
				if (endTime != null) {
					trigger.setEndTime(endTime);
				}
				sched.scheduleJob(jobDetail, trigger);
			}
		// 启动
		if (!sched.isShutdown()) {
			sched.start();
		}
	}

	/**
	 * 获取下个触发时间
	 * 
	 * @param date	计算开始时�?
	 * @param cron	cron表达�?
	 * @return
	 * @throws ParseException
	 */
	public static String getNextValidTimeAfter(Date date, String cron)
			throws ParseException {
//		CronExpression cronExpression = new CronExpression(cron);
//		Date next = cronExpression.getNextValidTimeAfter(date);
		return TimeUtils.toStr(getNextValidTimeAfterDate(date, cron), 
				DateFormat.YMDHMS);
	}
	
	/**
	 * 获取下个触发时间
	 * 
	 * @param date	计算开始时�?
	 * @param cron	cron表达�?
	 * @return
	 * @throws ParseException
	 */
	public static Date getNextValidTimeAfterDate(Date date, String cron)
			throws ParseException {
		CronExpression cronExpression = new CronExpression(cron);
		return cronExpression.getNextValidTimeAfter(date);
	}

	/**
	 * 判断字符串是否为�?
	 *
	 * @param cs
	 *            字符串序�?
	 * @return boolean
	 */
	private static boolean isEmpty(CharSequence cs) {
		return cs == null || cs.length() == 0;
	}

	/**
	 * 关闭任务管理�?
	 * 
	 * @throws SchedulerException
	 */
	public static void kill() throws SchedulerException {

		// 删除所有任�?
		removeAll();

		Scheduler sched = sf.getScheduler();
		// 关闭
		sched.shutdown();

		sched = null;

		sf = null;
	}

	/**
	 * 删除任务
	 * 
	 * @param taskName
	 *            任务标识
	 * @return true成功 false失败
	 * @throws SchedulerException
	 */
	public static boolean remove(String taskName) throws SchedulerException {
		Scheduler sched = sf.getScheduler();
		// 停止触发�?
		sched.pauseTrigger(taskName, TRIGGER_GROUP_NAME);
		// 移除触发�?
		boolean flag = sched.unscheduleJob(taskName, TRIGGER_GROUP_NAME);
		if (flag) {
			// 删除任务
			flag = sched.deleteJob(taskName, JOB_GROUP_NAME);
		}
		return flag;
	}

	/**
	 * 删除任务所有任�?
	 * 
	 * @return true成功 false失败
	 * @throws SchedulerException
	 */
	public static boolean removeAll() throws SchedulerException {
		boolean flag = true;
		String[] tasknames = getTaskNames();
		for (String taskName : tasknames) {
			flag = remove(taskName);
		}
		tasknames = null;
		return flag;
	}

	/**
	 * 更新任务
	 * 
	 * @param taskName
	 *            任务标识
	 * @param job
	 *            工作处理�?
	 * @param intervalTime
	 *            时间间隔，单位秒
	 * @param params
	 *            任务处理参数，可以为�?
	 * @throws ParseException
	 * @throws SchedulerException
	 */
	public static void update(String taskName, Job job, int intervalTime,
			Map<String, Object> params) throws ParseException,
			SchedulerException {

		Scheduler sched = sf.getScheduler();
		JobDetail jobDetail = new JobDetail(taskName, JOB_GROUP_NAME, job.getClass());
		if (params != null) {
			jobDetail.getJobDataMap().putAll(params);
		}

		SimpleTrigger trigger = (SimpleTrigger) sched.getTrigger(taskName,
				TRIGGER_GROUP_NAME);
		trigger.setStartTime(new Date());

		// 如果设置时间间隔�?0，则任务只执行一次，不放入任务队�?
		if (intervalTime == 0) {
			trigger.setRepeatCount(0);
		} else {
			trigger.setRepeatCount(-1);
			trigger.setRepeatInterval(intervalTime * 1000);
		}
		sched.addJob(jobDetail, true);
		sched.rescheduleJob(taskName, TRIGGER_GROUP_NAME, trigger);

	}

	/**
	 * 更新任务
	 * 
	 * @param taskName
	 *            任务标识
	 * @param job
	 *            工作处理�?
	 * @param cron
	 *            定时规则Cron
	 * @param params
	 *            任务处理参数，可以为�?
	 * @throws ParseException
	 * @throws SchedulerException
	 */
	public static void update(String taskName, Job job, String cron,
			Map<String, Object> params) throws ParseException,
			SchedulerException {
		update(taskName, job, cron, params, null, null);
	}

	/**
	 * 更新任务
	 * 
	 * @param taskName
	 *            任务标识
	 * @param job
	 *            工作处理�?
	 * @param cron
	 *            定时规则Cron
	 * @param params
	 *            任务处理参数，可以为�?
	 * @param startTime
	 *            开始时�?
	 * @param endTime
	 *            结束时间
	 * @throws ParseException
	 * @throws SchedulerException
	 */
	public static void update(String taskName, Job job, String cron,
			Map<String, Object> params, Date startTime, Date endTime)
			throws ParseException, SchedulerException {
		
		//FIXME 使用先删除再添加的方法进行任务更新，否则会出现更新时重复多次执行任务的情�?
		remove(taskName);
		add(taskName, job, cron, params , startTime, endTime);
/*		
 		Scheduler sched = sf.getScheduler();
		JobDetail jobDetail = new JobDetail(taskName, JOB_GROUP_NAME,
				job.getClass());
		if (params != null)
			jobDetail.getJobDataMap().putAll(params);
		if (isEmpty(cron))
			throw new SchedulerException("Cron is not null\uFF01");
		CronTrigger trigger = (CronTrigger) sched.getTrigger(taskName,
				TRIGGER_GROUP_NAME);
		trigger.setCronExpression(cron);
		if (startTime != null)
			trigger.setStartTime(startTime);
		if (endTime != null)
			trigger.setEndTime(endTime);
		sched.addJob(jobDetail, true);
		sched.rescheduleJob(taskName, TRIGGER_GROUP_NAME, trigger);
		*/

	}

}
