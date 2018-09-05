package exp.libs.warp.db.redis.bean;

import java.io.Serializable;

import exp.libs.utils.other.ObjUtils;

public class TG implements Serializable {

	private String key;
	
	private String value;
	
	public TG() {
		this.key = "111";
		this.value = "222";
	}
	
	@Override
	public String toString() {
		return ObjUtils.toBeanInfo(this);
	}
	
}
