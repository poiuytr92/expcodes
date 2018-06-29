package exp.libs.warp.task.cron;


public class _Hour extends __TimeUnit {

	protected _Hour(Cron cron) {
		super(cron);
	}

	@Override
	public String setSubExpression(String subExpression) {
		this.subExpression = subExpression;
		return this.subExpression;
	}

}
