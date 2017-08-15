package exp.libs.mrp.bean;

public class JarPath {

	private String srcPath;
	
	private String snkPath;
	
	public JarPath() {
		this("", "");
	}
	
	public JarPath(String srcPath) {
		this(srcPath, "");
	}
	
	public JarPath(String srcPath, String snkPath) {
		this.srcPath = (srcPath == null ? "" : srcPath.trim());
		this.snkPath = (snkPath == null ? "" : snkPath.trim());
	}

	public String getSrcPath() {
		return srcPath;
	}

	public void setSrcPath(String srcPath) {
		this.srcPath = srcPath;
	}

	public String getSnkPath() {
		return snkPath;
	}

	public void setSnkPath(String snkPath) {
		this.snkPath = snkPath;
	}
	
}
