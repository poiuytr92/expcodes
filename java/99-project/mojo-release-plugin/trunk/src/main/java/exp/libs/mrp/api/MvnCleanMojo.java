package exp.libs.mrp.api;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import exp.libs.mrp.Config;
import exp.libs.utils.io.FileUtils;

/**
 * <PRE>
 * 项目发布插件:Maven调用入口 - clean功能部分。
 * </PRE>
 * 
 * <B>项 目：</B>凯通J2SE开发平台(KTJSDP) 
 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2014
 * 
 * @version 1.0 2017-08-14
 * @author EXP：272629724@qq.com
 * @since jdk版本：jdk1.6
 * 
 * @goal clean
 * @requiresDependencyResolution runtime
 * @execute phase= "clean"
 */
public class MvnCleanMojo extends org.apache.maven.plugin.AbstractMojo {

	/**
	 * Maven所发布的项目对象
	 * 
	 * @parameter default-value="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;
	
	/**
	 * <PRE>
	 * 私有lib仓库的目录
	 * dependType = self 时有效
	 * </PRE>
	 * 
	 * @parameter default-value="./lib"
	 * @required
	 */
	private String selfLibDir;
	
	public MvnCleanMojo() {}
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		String prjName = project.getArtifactId();
		String prjVer = project.getVersion();
		
		String releaseDir = Config.toReleaseDir(prjName, prjVer);
		String copyLibDir = Config.toCopyJarDir(releaseDir, selfLibDir.trim());
		
		if(!isSysDir(copyLibDir)) {
			FileUtils.delete(copyLibDir);
		}
		
		if(!isSysDir(releaseDir)) {
			FileUtils.delete(releaseDir);
		}
	}
	
	private boolean isSysDir(String dir) {
		return "/".equals(dir) || "C:\\\\".equalsIgnoreCase(dir);
	}

}
