package exp.libs.mrp.utils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.project.MavenProject;

import exp.libs.mrp.Config;
import exp.libs.utils.other.PathUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * Maven工具类。
 * 主要提供针对pom的操作方法。
 * </PRE>
 * <B>PROJECT : </B> mojo-release-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-08-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class MvnUtils {

	protected MvnUtils() {}
	
	/**
	 * 获取pom中依赖构件的在Maven仓库中的绝对路径（有序）
	 * @param mvnPrj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getArtifactPaths(MavenProject mvnPrj) {
		List<String> paths = new LinkedList<String>();
		List<Artifact> artifacts = mvnPrj.getCompileArtifacts();
		for(Artifact artifact : artifacts) {
			paths.add(PathUtils.combine(
					Config.getInstn().getMavenRepository(), 
					getRelativePath(artifact)));
		}
		return paths;
	}
	
	/**
	 * 根据jar包的pom坐标，获取其在maven仓库中的相对路径�?
	 * 
	 * @param artifact jar包的pom坐标
	 * @return jar包相对于maven仓库的相对路�?
	 */
	private static String getRelativePath(Artifact artifact) {
		return StrUtils.concat(getRelativeDir(artifact), 
				File.separatorChar, getJarName(artifact));
	}
	
	private static String getRelativeDir(Artifact artifact) {
		StringBuilder dir = new StringBuilder();
		dir.append(artifact.getGroupId().replace('.', File.separatorChar));
		dir.append(File.separatorChar);
		dir.append(artifact.getArtifactId());
		dir.append(File.separatorChar);
		dir.append(getVersion(artifact));
		return dir.toString();
	}
	
	/**
	 * 根据jar包的pom坐标，获取其包名的完整名�?
	 *
	 * @param artifact jar包的pom坐标
	 * @return jar包的完整名称
	 */
	private static String getJarName(Artifact artifact) {
		StringBuilder path = new StringBuilder();

		// 包名和版本号
		path.append(artifact.getArtifactId()).append('-');
		path.append(getVersion(artifact));

		// 快照版或发布版的标识
		if (artifact.hasClassifier()) {
			path.append('-').append(artifact.getClassifier());
		}
		
		// 后缀
		ArtifactHandler artifactHandler = artifact.getArtifactHandler();
		if (artifactHandler.getExtension() != null
				&& artifactHandler.getExtension().length() > 0) {
			path.append('.').append(artifactHandler.getExtension());
		}
		return path.toString();
	}
	
	private static String getVersion(Artifact artifact) {
		String version = artifact.getVersion();
		if(artifact.isSnapshot()) {	// 处理快照中的时间部分
			version = version.replaceFirst("\\d{8}\\.\\d{6}.*$", "SNAPSHOT");
		}
		return version;
	}
	
}
