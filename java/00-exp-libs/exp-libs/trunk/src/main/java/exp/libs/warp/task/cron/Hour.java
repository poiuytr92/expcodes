package exp.libs.warp.task.cron;

public class Hour extends _TimeUnit {

	@Override
	public String setSubExpression(String subExpression) {
		this.subExpression = subExpression;
		return this.subExpression;
	}

}
