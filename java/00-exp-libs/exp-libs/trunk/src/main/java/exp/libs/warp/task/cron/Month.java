package exp.libs.warp.task.cron;

public class Month extends _TimeUnit {

	@Override
	public String setSubExpression(String subExpression) {
		this.subExpression = subExpression;
		return this.subExpression;
	}

}
