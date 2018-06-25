package exp.libs.envm;

/**
 * <PRE>
 * 枚举类：计算机存储单位
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2017-08-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public enum StorageUnit {

	BIT("bit", "cell: 1bit = 1b", "�?"), 
	
	BYTE("byte", "byte: 1byte = 1B = 8bit", "字节"), 
	
	KB("KB", "kilobyte: 1KB = 1024byte", "千字�?"), 
	
	MB("MB", "megabyte: 1MB = 1024KB", "兆字�?"), 
	
	GB("GB", "gigabyte: 1GB = 1024MB", "吉字�?"), 
	
	TB("TB", "trillionbyte: 1TB = 1024GB", "太字�?"), 
	
	PB("PB", "petabyte: 1PG = 1024TB", "拍字�?"), 
	
	EB("EB", "exabyte: 1EB = 1024PB", "艾字�?"), 
	
	ZB("ZB", "zettabyte: 1ZB = 1024EB", "泽字�?"), 
	
	YB("YB", "yottabyte: 1YB = 1024ZB", "尧字�?"), 
	
	BB("BB", "brontobyte: 1BB = 1024YB", "亿字�?"), 
	
	;
	
	public String VAL;
	
	public String DES_EN;
	
	public String DES_CH;
	
	private StorageUnit(String val, String desEn, String desCh) {
		this.VAL = val;
		this.DES_EN = desEn;
		this.DES_CH = desCh;
	}
	
}
