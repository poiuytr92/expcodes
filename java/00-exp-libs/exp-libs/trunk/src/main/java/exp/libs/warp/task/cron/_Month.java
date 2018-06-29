package exp.libs.warp.task.cron;


public class _Month extends __TimeUnit {

	protected _Month(Cron cron) {
		super(cron);
	}

	@Override
	public String setSubExpression(String subExpression) {
		this.subExpression = subExpression;
		return this.subExpression;
	}

}
