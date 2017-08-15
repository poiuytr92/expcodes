package exp.libs.mrp.services;

import java.util.LinkedList;
import java.util.List;

import org.apache.maven.project.MavenProject;

import exp.libs.mrp.Log;
import exp.libs.mrp.utils.MvnUtils;

/**
 * <PRE>
 * Jar包加载器
 * </PRE>
 * <B>项    目：</B>凯通J2SE开发平台(KTJSDP)
 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2014
 * @version   1.0 2014-10-20
 * @author    廖权斌：liaoquanbin@gdcattsoft.com
 * @since     jdk版本：jdk1.6
 */
public class JarLoader {
	
	private static volatile JarLoader instance;
	
	private JarLoader() {}
	
	public static JarLoader getInstn() {
		if(instance == null) {
			synchronized (JarLoader.class) {
				if(instance == null) {
					instance = new JarLoader();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 加载主项目记录在pom.xml文件的依赖包
	 * @param mvnPrj maven项目对象
	 */
	@SuppressWarnings("unchecked")
	public void loadMainPrjPom(MavenProject mvnPrj) {
		Log.info("正在解析pom依赖...");
		List<org.apache.maven.artifact.Artifact> artifacts = 
				mvnPrj.getCompileArtifacts();
		List<String> jarPathList = new LinkedList<String>();
		
		for(org.apache.maven.artifact.Artifact artifact : artifacts) {
			jarPathList.add(PathUtils.combPath(
					_MvnConfig.getInstn().getMavenLibPath(), 
					MvnUtils.getRelativeJarPath(artifact)));
		}
		JarMgr.getInstn().addMore(
				_MvnConfig.getInstn().getProjectName(), jarPathList);
		Log.info("pom依赖解析完成.");
	}
	
}
