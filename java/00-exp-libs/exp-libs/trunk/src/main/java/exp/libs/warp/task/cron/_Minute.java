package exp.libs.warp.task.cron;


public class _Minute extends __TimeUnit {

	protected _Minute(Cron cron) {
		super(cron);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String setSubExpression(String subExpression) {
		this.subExpression = subExpression;
		return this.subExpression;
	}

}
