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

	UNKNOW(".unknow", ""),
	
	RAR(".rar", "52617221"),
	
	ZIP(".zip", "504B0304"),
	
	TAR(".tar", "4D746F73"),
	
	GZ(".gz", "1F8B0800"),
	
	BZ2(".bz2", "425A6839"),
	
	XLS(".xls", "D0CF11E0"),
	
	XLSX(".xlsx", "504B0304"),
	
	JPG(".jpg", "FFD8FFE0"),
	
	PNG(".png", "89504E47"),
	
	;
	
	public String EXT;
	
	public String HEX_HEADER;
	
	private FileType(String ext, String hexHeader) {
		this.EXT = (ext != null ? ext.trim().toLowerCase() : "");
		this.HEX_HEADER = (hexHeader != null ? hexHeader.trim().toUpperCase() : "");
	}
	
	public static FileType toFileType(String hexHeader) {
		FileType type = UNKNOW;
		if(RAR.HEX_HEADER.equals(hexHeader)) {
			type = RAR;
			
		} else if(ZIP.HEX_HEADER.equals(hexHeader)) {
			type = ZIP;
			
		} else if(TAR.HEX_HEADER.equals(hexHeader)) {
			type = TAR;
			
		} else if(GZ.HEX_HEADER.equals(hexHeader)) {
			type = GZ;
			
		} else if(BZ2.HEX_HEADER.equals(hexHeader)) {
			type = BZ2;
			
		} else if(XLS.HEX_HEADER.equals(hexHeader)) {
			type = XLS;
			
		} else if(XLSX.HEX_HEADER.equals(hexHeader)) {
			type = XLSX;
			
		} else if(JPG.HEX_HEADER.equals(hexHeader)) {
			type = JPG;
			
		} else if(PNG.HEX_HEADER.equals(hexHeader)) {
			type = PNG;
			
		}
		return type;
	}
	
}
