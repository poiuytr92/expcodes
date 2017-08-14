package exp.libs.mrp.api;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * <PRE>
 * 项目发布插件:Maven调用入口 - install功能部分。
 * 
 * 说明：根据脚本类型，自动生成J2SE项目的启动、停止、启动检查等脚本。
 * 
 * 		该插件在install生命周期后运行execute phase
 * 		运行插件前需要进行jar依赖分析requiresDependencyResolution
 * 
 * </PRE>
 * 
 * <B>项 目：</B>凯通J2SE开发平台(KTJSDP) 
 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2014
 * 
 * @version 1.0 2017-08-14
 * @author EXP：272629724@qq.com
 * @since jdk版本：jdk1.6
 * 
 * @goal install
 * @requiresDependencyResolution runtime
 * @execute phase= "install"
 */
public class MvnInstallMojo extends org.apache.maven.plugin.AbstractMojo {

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
	 * 依赖类型.
	 * 	maven: 所发布的项目脚本，直接依赖本地maven仓库的jar包
	 * 	lib: 所发布的项目的依赖jar包会先复制到lib目录，再在项目脚本中依赖lib下的jar包
	 * </PRE>
	 * 
	 * @parameter default-value="maven"
	 * @required
	 */
	private String dependType;
	
	/**
	 * win默认maven仓库路径
	 * 
	 * @parameter default-value="${settings.localRepository}"
	 * @required
	 */
	private String winMavenRepository;
	
	/**
	 * linux默认maven仓库路径
	 * 
	 * @parameter default-value="/home/cattsoft/mavenRepository"
	 * @required
	 */
	private String linuxMavenRepository;
	
	/**
	 * win默认lib目录路径
	 * 
	 * @parameter default-value=".\\lib"
	 * @required
	 */
	private String winLibPath;
	
	/**
	 * linux默认lib目录路径
	 * 
	 * @parameter default-value="./lib"
	 * @required
	 */
	private String linuxLibPath;
	
	/**
	 * 项目启动类路径
	 * 
	 * @parameter default-value="foo.bar.prj.Main"
	 * @required
	 */
	private String mainClass;
	
	/**
	 * 项目版本类路径
	 * 
	 * @parameter default-value="foo.bar.prj.Version"
	 * @required
	 */
	private String verClass;
	
	/**
	 * 项目编码
	 * 
	 * @parameter default-value="UTF-8"
	 * @required
	 */
	private String charset;

	/**
	 * 默认分配JVM堆空间
	 * 
	 * @parameter default-value="64m"
	 * @required
	 */
	private String xms;
	
	/**
	 * 最大分配JVM堆空间
	 * 
	 * @parameter default-value="128m"
	 * @required
	 */
	private String xmx;

	/**
	 * jdk参数，若非空则会【附加】到所有脚本的JDK参数表中
	 * 
	 * @parameter default-value=" "
	 * @required
	 */
	private String jdkParams;
	
	/**
	 * 项目运行时的线程名称默认为 [项目名称].
	 * 	而 [线程后缀] 则附加在启动脚本/停止脚本的 [项目名称] 后面
	 * 
	 * @parameter default-value=" "
	 * @required
	 */
	private String threadSuffix;
	
	/**
	 * 所发布项目jar包是否带版本号（只影响启动脚本-cp列表中所发布项目的jar名称）。
	 * 对于maven项目, 主项目jar包是由第三方ant插件编译生成的，必定会带版本号。
	 * 
	 * 因此若此处值为true, 需要在ant插件配置中，拷贝所发布项目jar包时去掉版本号。
	 * 反之若此处值为false, 则需要在ant插件配置中，拷贝所发布项目jar包时加上版本号。
	 * 
	 * @parameter default-value="true"
	 * @required
	 */
	private String noPrjVer;
	
	/**
	 * 打包时需要去掉版本号的jars列表（前缀匹配，逗号分隔）
	 * 
	 * @parameter default-value=" "
	 * @required
	 */
	private String noVerJars;
	
	/**
	 * 路径压缩模式：
	 * 1：提取尽可能少的路径前缀：各路径中相同的节点至少出现2次以上才会被提取前缀，子前缀压缩。
	 * 2：提取标准数量的路径前缀：路径中同层同名的节点至少出现2次以上才会被提取前缀，相同前缀压缩。
	 * 3：提取尽可能多的路径前缀：所有路径都会被提取前缀，相同前缀压缩。
	 * 
	 * @parameter default-value="2"
	 * @required
	 */
	private String cmpPathMode;
	
	/**
	 * 构造函数
	 */
	public MvnInstallMojo() {}
	
	/**
	 * Maven插件调用入口
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		// TODO Auto-generated method stub
		
	}

	public MavenProject getProject() {
		return project;
	}

	public String getDependType() {
		return dependType;
	}

	public String getWinMavenRepository() {
		return winMavenRepository;
	}

	public String getLinuxMavenRepository() {
		return linuxMavenRepository;
	}

	public String getWinLibPath() {
		return winLibPath;
	}

	public String getLinuxLibPath() {
		return linuxLibPath;
	}

	public String getMainClass() {
		return mainClass;
	}

	public String getVerClass() {
		return verClass;
	}

	public String getCharset() {
		return charset;
	}

	public String getXms() {
		return xms;
	}

	public String getXmx() {
		return xmx;
	}

	public String getJdkParams() {
		return jdkParams;
	}

	public String getThreadSuffix() {
		return threadSuffix;
	}

	public String getNoPrjVer() {
		return noPrjVer;
	}

	public String getNoVerJars() {
		return noVerJars;
	}

	public String getCmpPathMode() {
		return cmpPathMode;
	}

}
