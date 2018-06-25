package exp.libs.envm;


/**
 * <PRE>
 * 枚举类：文件类型(提供文件类型后缀、以及文件头信息)
 * -------------------------------------
 *  同一文件类型, 可能存在多个文件头(如:MPEG)
 *  不同不稳健类型, 可能存在相同的文件头(如：DOC、XLS、PPT)
 *  存在某些文件类型没有文件头(如：TXT)
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2018-05-07
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public enum FileType {

	/** 未知文件类型 */
	UNKNOW("UNKNOW", "", "", 0),
	
	/** TXT类的纯文本文件不存在确定的文件头(其文件头受存储的内容影响) */
	TXT("TXT", ".txt", "", 0), 
	
	/** 批处理脚�? */
	BAT("BAT", ".bat", "", 0), 
	
	/** 二进制文�? */
	BIN("BIN", ".bin", "", 0), 
	
	/** INI配置文件 */
	INI("INI", ".ini", "", 0), 
	
	/** 临时文件 */
	TMP("TMP", ".tmp", "", 0), 
	
	/** MP3音频文件(不存在固定文件头) */
	MP3("MP3", ".mp3", "", 0), 
	
	/** Wave格式音频文件 */
	WAVE("Wave", ".wav", "57415645", 4), 
	
	/** Musical Instrument Digital Interface (最小的音符文件) */
	MIDI("mid", ".mid", "4D546864", 4), 
	
	/** 矢量�? */
	JPG("JPG", ".jpg", "FFD8FF", 3), 

	/** 无损压缩位图 */
	PNG("PNG", ".png", "89504E47", 4), 

	/** 24位位�? */
	BMP("BMP", ".bmp", "424D", 2), 
	
	/** 动态图�? */
	GIF("GIF", ".gif", "47494638", 4), 

	/** 标签图像 */
	TIFF("TIFF", ".tif", "49492A00", 4), 

	/** Computer Aided Design */
	CAD("CAD", ".dwg", "41433130", 4), 

	/** Adobe Photoshop */
	PSD("Adobe Photoshop", ".psd", "38425053", 4), 

	/** Rich Text Format */
	RTF("Rich Text Format", ".rtf", "7B5C727466", 5), 

	/** XML配置文件 */
	XML("XML", ".xml", "3C3F786D6C", 5), 

	/** HTML网页文件 */
	HTML("HTML", ".html", "68746D6C3E", 5), 

	/** EMAIL */
	EMAIL("Email", ".eml", "44656C69766572792D646174653A", 14), 

	/** MS Outlook */
	OUTLOOK("MS Outlook", ".pst", "2142444E", 4), 
	
	/** MS Outlook Express */
	OE("MS Outlook Express", ".dbx", "CFAD12FEC5FD746F", 8), 
	
	/** MS Access */
	ACCESS("MS Access", ".mdb", "5374616E64617264204A", 10), 
	
	/** MS Word 2003 (DOC、XLS、PPT 的文件头是相同的) */
	DOC("MS Word 2003", ".doc", "D0CF11E0", 4), 
	
	/** MS Excel 2003 (DOC、XLS、PPT 的文件头是相同的) */
	XLS("MS Excel 2003", ".xls", "D0CF11E0", 4), 
	
	/** MS Power Point 2003 (DOC、XLS、PPT 的文件头是相同的) */
	PPT("MS Power Point 2003", ".ppt", "D0CF11E0", 4), 

	/** MS Word (DOCX、XLSX、PPTX 的文件头与ZIP是相同的, 实际上就是ZIP文件) */
	DOCX("MS Word", ".docx", "504B0304", 4), 
	
	/** MS Excel (DOCX、XLSX、PPTX 的文件头与ZIP是相同的, 实际上就是ZIP文件) */
	XLSX("MS Excel", ".xlsx", "504B0304", 4), 
	
	/** MS Power Point (DOCX、XLSX、PPTX 的文件头与ZIP是相同的, 实际上就是ZIP文件) */
	PPTX("MS Power Point", ".pptx", "504B0304", 4), 
	
	/** ZIP Archive */
	ZIP("ZIP", ".zip", "504B0304", 4), 

	/** RAR Archive */
	RAR("RAR", ".rar", "52617221", 4), 
	
	/** TAR Archive */
	TAR("TAR", ".tar", "1F9D", 2),
	
	/** GZ Archive */
	GZ("GZ", ".gz", "1F8B", 2),
	
	/** BZ2 Archive */
	BZ2("BZ2", ".bz2", "425A68", 3),

	/** WordPerfect */
	WPD("WordPerfect", ".wpd", "FF575043", 4), 

	/** Postscript */
	PS("Postscript", ".ps", "252150532D41646F6265", 10), 
	
	/** Postscript */
	EPS("Postscript", ".eps", "252150532D41646F6265", 10), 

	/** Adobe Acrobat */
	PDF("PDF", ".pdf", "255044462D312E", 7), 

	/** Quicken */
	QDF("Quicken", ".qdf", "AC9EBD8F", 4), 

	/** Windows Password */
	PWL("Windows Password", ".pwl", "E3828596", 4), 

	/** AVI格式视音频文�?(Audio Video Interleaved) */
	AVI("AVI", ".avi", "41564920", 4), 

	/** Real Audio */
	RAM("Real Audio", ".ram", "2E7261FD", 4), 

	/** Real Media */
	RM("Real Media", ".rm", "2E524D46", 4), 

	/** MPEG (只包含视频数�?) */
	MPEG_VIDEO("MPEG (Only Video)", ".mpg", "000001B3", 4), 
	
	/** MPEG (同时包含视频数据和音频数�?) */
	MPEG("MPEG", ".mpg", "000001BA", 4), 

	/** Quicktime */
	MOV("Quicktime", ".mov", "6D6F6F76", 4), 

	/** Windows Media */
	ASF("Windows Media", ".asf", "3026B2758E66CF11", 8), 

	/** Windows 动态库文件 */
	DLL("DLL", ".dll", "4D5A90", 3), 
	
	/** Windows 可执行文�? */
	EXE("EXE", ".exe", "4D5A90", 3), 
	
	;
	
	/** 文件类型名称 */
	public String NAME;
	
	/** 文件类型后缀 */
	public String EXT;
	
	/** 16进制文件�?(存在相同文件头的不同文件类型) */
	public String HEADER;
	
	/** 文件头的字节�? (1字节=2�?16进制字符) */
	public int HEAD_LEN;
	
	/**
	 * 私有化构造函�?
	 * @param name 文件类型名称
	 * @param ext 文件类型后缀 
	 * @param header 16进制文件�?
	 * @param headLen 文件头的字节�? (1字节=2�?16进制字符)
	 */
	private FileType(String name, String ext, String header, int headLen) {
		this.NAME = name;
		this.EXT = ext;
		this.HEADER = header;
		this.HEAD_LEN = headLen;
	}
	
	@Override
	public String toString() {
		return EXT;
	}
	
}
