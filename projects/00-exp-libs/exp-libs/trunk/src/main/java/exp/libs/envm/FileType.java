package exp.libs.envm;

public enum FileType {

	RAR(".rar", "52617221"),
	
	ZIP(".zip", "504B0304"),
	
	TAR(".tar", "4D746F73"),
	
	GZ(".gz", "1F8B0800"),
	
	BZ2(".bz2", "425A6839"),
	
	;
	
	public String EXT;
	
	public String HEAD_MSG;
	
	private FileType(String ext, String headMsg) {
		this.EXT = (ext != null ? ext.trim().toLowerCase() : "");
		this.HEAD_MSG = (headMsg != null ? ext.trim().toUpperCase() : "");
	}
	
}
