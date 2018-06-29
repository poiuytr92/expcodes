package exp.libs.warp.task.cron;


public class _Year extends __TimeUnit {

	protected _Year(Cron cron) {
		super(cron);
	}

	@Override
	public String setSubExpression(String subExpression) {
		this.subExpression = subExpression;
		return this.subExpression;
	}

}
