package exp.libs.warp.task.cron;


public class _Week extends __TimeUnit {

	protected _Week(Cron cron) {
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
	
}
