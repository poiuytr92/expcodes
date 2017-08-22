package exp.libs.envm;

/**
 * <PRE>
 * 枚举类：文件类型
 * 	(提供文件类型后缀、以及文件头信息)
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-08-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public enum FileType {

	RAR(".rar", "52617221"),
	
	ZIP(".zip", "504B0304"),
	
	TAR(".tar", "4D746F73"),
	
	GZ(".gz", "1F8B0800"),
	
	BZ2(".bz2", "425A6839"),
	
	XLS(".xls", "D0CF11E0"),
	
	XLSX(".xlsx", "504B0304"),
	
	;
	
	public String EXT;
	
	public String HEAD_MSG;
	
	private FileType(String ext, String headMsg) {
		this.EXT = (ext != null ? ext.trim().toLowerCase() : "");
		this.HEAD_MSG = (headMsg != null ? ext.trim().toUpperCase() : "");
	}
	
}
