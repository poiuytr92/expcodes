package exp.libs.warp.task.cron;


public class _Day extends __TimeUnit {

	protected _Day(Cron cron) {
		super(cron);
	}

	@Override
	public String setSubExpression(String subExpression) {
		this.subExpression = subExpression;
		return this.subExpression;
	}
	
	/** 任意值: 即此位置的时间值对最终的cron规则无约束 */
	public String withNone() {
		subExpression = "?";
		return subExpression;
	}

	private void trigger(Cron cron) {
		// 只要当表达式发生变化时均会触发到此方法的执行, 主要处理子表达式的冲突问题，例如 Day和week
	}
	
}
