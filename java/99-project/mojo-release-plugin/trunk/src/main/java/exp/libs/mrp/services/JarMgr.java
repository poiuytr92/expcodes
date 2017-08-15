package exp.libs.mrp.services;

import java.util.LinkedList;
import java.util.List;

import org.apache.maven.project.MavenProject;

import exp.libs.mrp.Config;
import exp.libs.mrp.bean.JarPath;
import exp.libs.mrp.envm.DependType;
import exp.libs.mrp.utils.MvnUtils;
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

	private List<JarPath> jarPaths;
	
	private static volatile JarMgr instance;
	
	private JarMgr() {
		this.jarPaths = new LinkedList<JarPath>();
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
	
	public void loadDependJarPaths(MavenProject mvnPrj) {
		List<String> srcPaths = MvnUtils.getArtifactPaths(mvnPrj);
		for(String srcPath : srcPaths) {
			String snkName = PathUtils.toLinux(srcPath).replaceFirst(".*/", "");
			snkName = cutVer(snkName);	// 版本号裁剪
			String snkPath = PathUtils.combine(Config.getInstn().getSelfLibDir(), snkName);
			jarPaths.add(new JarPath(srcPath, snkPath));
		}
	}
	
	private String cutVer(String jarName) {
		String name = jarName;
		if(RegexUtils.matches(name, Config.getInstn().getNoVerJarRegex())) {
			name = name.replaceAll("[-_\\.]?\\d+\\..*", ".jar");
		}
		return name;
	}
	
	public void copyDependJars() {
		if(Config.getInstn().getDependType() == DependType.SELF) {
			for(JarPath jarPath : jarPaths) {
				FileUtils.copyFile(jarPath.getSrcPath(), jarPath.getSnkPath());
			}
		}
	}
	
}
