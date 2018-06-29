package exp.libs.warp.task.cron;



public class _Second extends __TimeUnit {

	protected _Second(Cron cron) {
		super(cron);
	}

	@Override
	public String setSubExpression(String subExpression) {
		this.subExpression = subExpression;
		return this.subExpression;
	}

}
