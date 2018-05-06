package exp.libs.envm;

/**
 * <PRE>
 * 枚举类：文件类型(提供文件类型后缀、以及文件头信息)
 * -------------------------------------
 *  同一文件类型, 可能存在多个文件头(如:MPEG)
 *  不同不稳健类型, 可能存在相同的文件头(如：DOC、XLS、PPT)
 *  存在某些文件类型没有文件头(如：TXT)
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-05-07
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public enum FileType {

	UNKNOW("unknow", "", "", 0),
	
	/** TXT类的纯文本文件不存在文件头 */
	TXT("txt", ".txt", "", 0), 
	
	BAT("bat", ".bat", "", 0), 
	
	BIN("bin", ".bin", "", 0), 
	
	INI("ini", ".ini", "", 0), 
	
	TMP("tmp", ".tmp", "", 0), 
	
	JPG("jpg", ".jpg", "FFD8FF", 3), 

	PNG("png", ".png", "89504E47", 4), 

	BMP("bmp", ".bmp", "424D", 2), 
	
	GIF("gif", ".gif", "47494638", 4), 

	TIFF("tif", ".tif", "49492A00", 4), 

	CAD("dwg", ".dwg", "41433130", 4), 

	/** Adobe Photoshop */
	PSD("psd", ".psd", "38425053", 4), 

	/** Rich Text Format */
	RTF("rtf", ".rtf", "7B5C727466", 5), 

	XML("xml", ".xml", "3C3F786D6C", 5), 

	HTML("html", ".html", "68746D6C3E", 5), 

	EMAIL("eml", ".eml", "44656C69766572792D646174653A", 14), 

	/** MS Outlook */
	OUTLOOK("pst", ".pst", "2142444E", 4), 
	
	/** MS Outlook Express */
	OE("dbx", ".dbx", "CFAD12FEC5FD746F", 8), 
	
	/** MS Access */
	ACCESS("mdb", ".mdb", "5374616E64617264204A", 10), 
	
	/** MS Word 2003 (DOC、XLS、PPT 的文件头是相同的) */
	DOC("doc", ".doc", "D0CF11E0", 4), 
	
	/** MS Excel 2003 (DOC、XLS、PPT 的文件头是相同的) */
	XLS("xls", ".xls", "D0CF11E0", 4), 
	
	/** MS Power Point 2003 (DOC、XLS、PPT 的文件头是相同的) */
	PPT("ppt", ".ppt", "D0CF11E0", 4), 

	/** MS Word (DOCX、XLSX、PPTX 的文件头与ZIP是相同的, 实际上就是ZIP文件) */
	DOCX("docx", ".docx", "504B0304", 4), 
	
	/** MS Excel (DOCX、XLSX、PPTX 的文件头与ZIP是相同的, 实际上就是ZIP文件) */
	XLSX("xlsx", ".xlsx", "504B0304", 4), 
	
	/** MS Power Point (DOCX、XLSX、PPTX 的文件头与ZIP是相同的, 实际上就是ZIP文件) */
	PPTX("pptx", ".pptx", "504B0304", 4), 
	
	/** ZIP Archive */
	ZIP("zip", ".zip", "504B0304", 4), 

	/** RAR Archive */
	RAR("rar", ".rar", "52617221", 4), 
	
	TAR("tar", ".tar", "1F9D", 2),
	
	GZ("gz", ".gz", "1F8B", 2),
	
	BZ2("bz2", ".bz2", "425A68", 3),

	/** WordPerfect */
	WPD("wpd", ".wpd", "FF575043", 4), 

	/** Postscript */
	EPS("eps", ".eps", "252150532D41646F6265", 10), 

	/** Postscript */
	PS("ps", ".ps", "252150532D41646F6265", 10), 
	
	/** Adobe Acrobat */
	PDF("pdf", ".pdf", "255044462D312E", 7), 

	/** Quicken */
	QDF("qdf", ".qdf", "AC9EBD8F", 4), 

	/** Windows Password */
	PWL("pwl", ".pwl", "E3828596", 4), 

	WAVE("wav", ".wav", "57415645", 4), 

	AVI("avi", ".avi", "41564920", 4), 

	/** Real Audio */
	RAM("ram", ".ram", "2E7261FD", 4), 

	/** Real Media */
	RM("rm", ".rm", "2E524D46", 4), 

//	/** MPEG (只包含视频数据) */
//	MPEG("mpg", ".mpg", "000001B3", 4), 
	
	/** MPEG (同时包含视频数据和音频数据) */
	MPEG("mpg", ".mpg", "000001BA", 4), 

	/** Quicktime */
	MOV("mov", ".mov", "6D6F6F76", 4), 

	/** Windows Media */
	ASF("asf", ".asf", "3026B2758E66CF11", 8), 

	MIDI("mid", ".mid", "4D546864", 4), 
	
	DLL("dll", ".dll", "4D5A90", 3), 
	
	EXE("exe", ".exe", "4D5A90", 3), 
	
	;
	
	/** 文件类型名称 */
	public String NAME;
	
	/** 文件类型后缀 */
	public String EXT;
	
	/** 16进制文件头(存在相同文件头的不同文件类型) */
	public String HEADER;
	
	/** 文件头的字节数 (1字节=2个16进制字符) */
	public int HEAD_LEN;
	
	/**
	 * 私有化构造函数
	 * @param name 文件类型名称
	 * @param ext 文件类型后缀 
	 * @param header 16进制文件头
	 * @param headLen 文件头的字节数 (1字节=2个16进制字符)
	 */
	private FileType(String name, String ext, String header, int headLen) {
		this.NAME = name;
		this.EXT = ext;
		this.HEADER = header;
		this.HEAD_LEN = headLen;
	}

}
