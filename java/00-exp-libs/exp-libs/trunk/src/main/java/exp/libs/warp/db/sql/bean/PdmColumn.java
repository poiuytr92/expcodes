package exp.libs.warp.db.sql.bean;

public class PdmColumn {

	/** 列编号 */
	private String code;
	
	/** 列名 */
	private String name;
	
	/** 列类型 */
	private String type;
	
	/** 列解析 */
	private String comment;

	public PdmColumn() {}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
