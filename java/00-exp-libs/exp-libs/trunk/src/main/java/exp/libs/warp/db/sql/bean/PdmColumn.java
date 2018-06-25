package exp.libs.warp.db.sql.bean;

/**
 * <PRE>
 * PDM物理模型 - 列.
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class PdmColumn {

	/** 列编�? */
	private String code;
	
	/** 列名 */
	private String name;
	
	/** 列类�? */
	private String type;
	
	/** 列解�? */
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
