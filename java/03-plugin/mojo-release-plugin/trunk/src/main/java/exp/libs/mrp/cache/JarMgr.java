package exp.libs.mrp.cache;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.maven.project.MavenProject;

import exp.libs.mrp.Config;
import exp.libs.mrp.Log;
import exp.libs.mrp.envm.CmpPathMode;
import exp.libs.mrp.envm.DependType;
import exp.libs.mrp.utils.MvnUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;

/**
 * <PRE>
 * Jar包管理器
 * </PRE>
 * <B>PROJECT : </B> mojo-release-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-08-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class JarMgr {

	private List<String> srcPaths;
	
	private List<String> jarPaths;
	
	private Map<String, String> jarNames;
	
	private _PathTree jarPathTree;
	
	private List<String> jarPathPrefixs;
	
	private static volatile JarMgr instance;
	
	private JarMgr() {
		this.srcPaths = new LinkedList<String>();
		this.jarPaths = new LinkedList<String>();
		this.jarNames = new HashMap<String, String>();
		
		this.jarPathTree = new _PathTree();
		this.jarPathPrefixs = new LinkedList<String>();
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
	
	public void loadJarPaths(MavenProject mvnPrj) {
		_loadDependJarPaths(mvnPrj);
		_loadPrjJarPath();
	}
	
	private void _loadDependJarPaths(MavenProject mvnPrj) {
		srcPaths.addAll(MvnUtils.getArtifactPaths(mvnPrj));
		for(String srcPath : srcPaths) {
			String jarPath = srcPath;
			
			if(DependType.SELF == Config.getInstn().getDependType()) {
				String jarName = PathUtils.toLinux(srcPath).replaceFirst(".*/", "");
				jarName = _cutVer(jarName);	// 版本号裁�?
				jarNames.put(srcPath, jarName);
				
				String jarDir = Config.getInstn().getJarLibDir();
				jarPath = PathUtils.combine(jarDir, jarName);
			}
			
			jarPaths.add(jarPath);
			jarPathTree.add(jarPath);
		}
	}
	
	private String _cutVer(String jarName) {
		String name = jarName;
		if(RegexUtils.matches(name, Config.getInstn().getNoVerJarRegex())) {
			name = name.replaceAll("[-_\\.]?\\d+\\..*", ".jar");
		}
		return name;
	}
	
	private void _loadPrjJarPath() {
		String srcDir = Config.getInstn().isProguard() ? 
				Config.getInstn().getProguardDir() : Config.TARGET_DIR;
		String srcName = StrUtils.concat(Config.getInstn().getPrjName(), "-", 
				Config.getInstn().getPrjVer(), ".jar");
		String srcPath = PathUtils.combine(srcDir, srcName);
		
		String jarDir = Config.getInstn().getJarLibDir();
		String jarName = !Config.getInstn().isNoPrjVer() ? srcName : 
				StrUtils.concat(Config.getInstn().getPrjName(), ".jar");
		String jarPath = PathUtils.combine(jarDir, jarName);
		
		jarNames.put(srcPath, jarName);
		srcPaths.add(0, srcPath);
		jarPaths.add(0, jarPath);
		jarPathTree.add(jarPath);
	}
	
	public void copyJars() {
		for(String srcPath : srcPaths) {
			String snkPath = PathUtils.combine(
					Config.getInstn().getCopyJarDir(), jarNames.get(srcPath));
			FileUtils.copyFile(srcPath, snkPath);
			
			Log.debug("拷贝: [".concat(srcPath).concat("] => [").
					concat(snkPath).concat("]"));
			
			if(DependType.SELF != Config.getInstn().getDependType()) {
				break;	// 若不使用私有仓库�? 则只复制项目jar�?
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
		if(jarPathPrefixs.isEmpty()) {
			jarPathPrefixs.addAll(
					jarPathTree.getLinuxPrefixs(CmpPathMode.STAND));
		}
		return jarPathPrefixs;
	}
	
	public List<String> getJarPaths() {
		return jarPaths;
	}
	
}
