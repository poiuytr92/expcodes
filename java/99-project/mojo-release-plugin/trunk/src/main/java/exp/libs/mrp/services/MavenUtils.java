package exp.libs.mrp.services;

import java.io.File;

/**
 * <PRE>
 * Maven工具类。
 * 主要提供针对pom的操作方法。
 * </PRE>
 * <B>项    目：</B>凯通J2SE开发平台(KTJSDP)
 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2014
 * @version   1.0 2014-3-14
 * @author    廖权斌：liaoquanbin@gdcattsoft.com
 * @since     jdk版本：jdk1.6
 */
public class MavenUtils {

	/**
	 * 根据jar包的pom坐标，获取其在maven仓库中的相对路径。
	 * 
	 * @param artifact jar包的pom坐标
	 * @return jar包相对于maven仓库的相对路径
	 */
	public static String getRelativeJarPath(
			org.apache.maven.artifact.Artifact artifact) {
		StringBuilder path = new StringBuilder();
		path.append(artifact.getGroupId().replace('.', File.separatorChar));
		path.append(File.separatorChar);
		path.append(artifact.getArtifactId());
		path.append(File.separatorChar);
		if(artifact.isSnapshot()) {
			path.append(artifact.getVersion().replaceAll("\\d{8}\\.\\d{6}.*$", "SNAPSHOT"));
		} else {
			path.append(artifact.getVersion());
		}
		path.append(File.separatorChar);
		path.append(MavenUtils.getJarFullName(artifact));
		return path.toString();
	}
	
	/**
	 * 根据jar包的pom坐标，获取其包名的完整名称
	 *
	 * @param artifact jar包的pom坐标
	 * @return jar包的完整名称
	 */
	public static String getJarFullName(
			org.apache.maven.artifact.Artifact artifact) {
		org.apache.maven.artifact.handler.ArtifactHandler artifactHandler = 
				artifact.getArtifactHandler();
		StringBuilder path = new StringBuilder();

		//包名和版本号
		path.append(artifact.getArtifactId()).append('-');
		path.append(artifact.getVersion());

		//快照版或发布版的标识
		if (artifact.hasClassifier()) {
			path.append('-').append(artifact.getClassifier());
		}
		
		//后缀
		if (artifactHandler.getExtension() != null
				&& artifactHandler.getExtension().length() > 0) {
			path.append('.').append(artifactHandler.getExtension());
		}
		return path.toString();
	}
	
	/**
	 * 根据jar包的pom坐标，获取其包名的简单名称（不带版本号、快照版、发布版标识）
	 *
	 * @param artifact jar包的pom坐标
	 * @return jar包的简单名称
	 */
	public static String getJarSimpName(
			org.apache.maven.artifact.Artifact artifact) {
		org.apache.maven.artifact.handler.ArtifactHandler artifactHandler = 
				artifact.getArtifactHandler();
		StringBuffer path = new StringBuffer();

		//包名
		path.append(artifact.getArtifactId());
		
		//后缀
		if (artifactHandler.getExtension() != null
				&& artifactHandler.getExtension().length() > 0) {
			path.append('.').append(artifactHandler.getExtension());
		}
		return path.toString();
	}
	
}
