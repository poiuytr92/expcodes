package exp.libs.mrp.cache;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.maven.project.MavenProject;

import exp.libs.mrp.Config;
import exp.libs.mrp.envm.CmpPathMode;
import exp.libs.mrp.envm.DependType;
import exp.libs.mrp.utils.MvnUtils;
import exp.libs.utils.StrUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.utils.verify.RegexUtils;

/**
 * <PRE>
 * Jar包管理器
 * </PRE>
 * <B>项    目：</B>凯通J2SE开发平台(KTJSDP)
 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2014
 * @version   1.0 2014-9-19
 * @author    廖权斌：liaoquanbin@gdcattsoft.com
 * @since     jdk版本：jdk1.6
 */
public class JarMgr {

	private List<String> srcPaths;
	
	private List<String> snkPaths;
	
	private Map<String, String> snkNames;
	
	private _PathTree snkPathTree;
	
	private List<String> snkPathPrefixs;
	
	private static volatile JarMgr instance;
	
	private JarMgr() {
		this.srcPaths = new LinkedList<String>();
		this.snkPaths = new LinkedList<String>();
		this.snkNames = new HashMap<String, String>();
		
		this.snkPathTree = new _PathTree();
		this.snkPathPrefixs = new LinkedList<String>();
	}
	
	public static JarMgr getInstn() {
		if(instance == null) {
			synchronized (JarMgr.class) {
				if(instance == null) {
					instance = new JarMgr();
				}
			}
		}
		return instance;
	}
	
	public void loadPrjJarPath() {
		String srcDir = Config.getInstn().isProguard() ? 
				Config.getInstn().getProguardDir() : Config.TARGET_DIR;
		String srcName = StrUtils.concat(Config.getInstn().getPrjName(), "-", 
				Config.getInstn().getPrjVer(), ".jar");
		String srcPath = PathUtils.combine(srcDir, srcName);
		
		String snkDir = Config.getInstn().getSelfLibDir();
		String snkName = !Config.getInstn().isNoPrjVer() ? srcName : 
				StrUtils.concat(Config.getInstn().getPrjName(), ".jar");
		String snkPath = PathUtils.combine(snkDir, snkName);
		
		srcPaths.add(0, srcPath);
		snkNames.put(srcPath, snkName);
		snkPaths.add(0, snkPath);
		snkPathTree.add(snkPath);
	}

	public void loadDependJarPaths(MavenProject mvnPrj) {
		srcPaths.addAll(MvnUtils.getArtifactPaths(mvnPrj));
		for(String srcPath : srcPaths) {
			String snkPath = srcPath;
			
			if(DependType.SELF == Config.getInstn().getDependType()) {
				String snkName = PathUtils.toLinux(srcPath).replaceFirst(".*/", "");
				snkName = cutVer(snkName);	// 版本号裁剪
				snkNames.put(srcPath, snkName);
				
				String snkDir = Config.getInstn().getSelfLibDir();
				snkPath = PathUtils.combine(snkDir, snkName);
			}
			
			snkPaths.add(snkPath);
			snkPathTree.add(snkPath);
		}
	}
	
	private String cutVer(String jarName) {
		String name = jarName;
		if(RegexUtils.matches(name, Config.getInstn().getNoVerJarRegex())) {
			name = name.replaceAll("[-_\\.]?\\d+\\..*", ".jar");
		}
		return name;
	}
	
	public void copyJars() {
		for(String srcPath : srcPaths) {
			String snkPath = PathUtils.combine(
					Config.getInstn().getCopyJarDir(), snkNames.get(srcPath));
			FileUtils.copyFile(srcPath, snkPath);
			
			if(DependType.SELF != Config.getInstn().getDependType()) {
				break;	// 若不使用私有仓库， 则只复制项目jar包
			}
		}
	}
	
	public String getJarSrcPathsInfo() {
		StringBuilder sb = new StringBuilder();
		for(String srcPath : srcPaths) {
			sb.append("  ").append(srcPath).append("\r\n");
		}
		return sb.toString();
	}
	
	public List<String> getJarPathPrefixs() {
		if(snkPathPrefixs.isEmpty()) {
			snkPathPrefixs.addAll(
					snkPathTree.getLinuxPathPrefixSet(CmpPathMode.STAND));
		}
		return snkPathPrefixs;
	}
	
	public List<String> getJarPaths() {
		return snkPaths;
	}
	
}
