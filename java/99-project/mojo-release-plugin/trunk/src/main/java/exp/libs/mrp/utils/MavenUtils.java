package exp.libs.mrp.utils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.project.MavenProject;

import exp.libs.mrp.Config;
import exp.libs.mrp.services.PathUtils;
import exp.libs.utils.StrUtils;

/**
 * <PRE>
 * Maven工具类。
 * 主要提供针对pom的操作方法。
 * </PRE>
 * <B>项    目：</B>凯通J2SE开发平台(KTJSDP)
 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2014
 * @version   1.0 2017-08-15
 * @author    廖权斌：liaoquanbin@gdcattsoft.com
 * @since     jdk版本：jdk1.6
 */
public class MavenUtils {

	protected MavenUtils() {}
	
	/**
	 * 获取pom中依赖构件的绝对路径（有序）
	 * @param mvnPrj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> getArtifactPaths(MavenProject mvnPrj) {
		List<String> paths = new LinkedList<String>();
		List<Artifact> artifacts = mvnPrj.getCompileArtifacts();
		for(Artifact artifact : artifacts) {
			paths.add(PathUtils.combPath(
					Config.getInstn().getMavenRepository(), 
					getRelativePath(artifact)));
		}
		return paths;
	}
	
	/**
	 * 根据jar包的pom坐标，获取其在maven仓库中的相对路径。
	 * 
	 * @param artifact jar包的pom坐标
	 * @return jar包相对于maven仓库的相对路径
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
	 * 根据jar包的pom坐标，获取其包名的完整名称
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
