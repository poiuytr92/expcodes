package exp.bilibli.plugin.bean.ldm;

public class Action {

	private String uId;
	
	/** 用户行为 */
	private String action;
	
	public Action(String uId, String action) {
		this.uId = uId;
		this.action = action;
	}

	public String getUId() {
		return uId;
	}

	public String getAction() {
		return action;
	}

}
