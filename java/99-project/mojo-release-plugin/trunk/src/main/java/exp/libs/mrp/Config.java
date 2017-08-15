package exp.libs.mrp;

import exp.libs.envm.Charset;
import exp.libs.mrp.api.MvnInstallMojo;
import exp.libs.mrp.envm.CmpPathMode;
import exp.libs.mrp.envm.DependType;
import exp.libs.utils.StrUtils;
import exp.libs.utils.other.BoolUtils;

public class Config {

	private DependType dependType;
	
	private String selfLibDir;
	
	private String mavenRepository;
	
	/** 自动生成, 无需配置 */
	private String prjName;
	
	/** 自动生成, 无需配置 */
	private String prjVer;
	
	private boolean noPrjVer;
	
	private String noVerJarRegex;
	
	/** 自动生成, 无需配置 */
	private String releaseDir;
	
	/** 自动生成, 无需配置 */
	private String releaseName;
	
	private String mainClass;
	
	private String verClass;
	
	private String charset;
	
	private String xms;
	
	private String xmx;
	
	private String jdkParams;
	
	private String threadSuffix;
	
	private CmpPathMode cmpPathMode;
	
	private boolean proguard;
	
	private static volatile Config instance;
	
	private Config() {
		this.dependType = DependType.SELF;
		this.selfLibDir = "./lib";
		this.mavenRepository = "D:\\mavenRepository";
		this.prjName = "";
		this.prjVer = "";
		this.noPrjVer = true;
		this.noVerJarRegex = "";
		this.releaseDir = "";
		this.releaseName = "";
		this.mainClass = "foo.bar.prj.Main";
		this.verClass = "foo.bar.prj.Version";
		this.charset = Charset.UTF8;
		this.xms = "64m";
		this.xmx = "128m";
		this.jdkParams = "";
		this.threadSuffix = "";
		this.cmpPathMode = CmpPathMode.STAND;
		this.proguard = false;
	}
	
	public static Config createInstn(MvnInstallMojo mvn) {
		if(mvn == null) {
			Log.error("初始化 mojo-release-plugin 失败");
			System.exit(1);
		}
		
		if(instance == null) {
			synchronized (Config.class) {
				if(instance == null) {
					instance = new Config();
					instance.init(mvn);
				}
			}
		}
		return instance;
	}
	
	/**
	 * 获取单例
	 * @return 单例
	 */
	public static Config getInstn() {
		if(instance == null) {
			synchronized (Config.class) {
				if(instance == null) {
					Log.error("mojo-release-plugin 尚未初始化");
					System.exit(1);
				}
			}
		}
		return instance;
	}
	
	private void init(MvnInstallMojo mvn) {
		try {
			this.dependType = DependType.toType(mvn.getDependType().trim());
			this.selfLibDir = mvn.getSelfLibDir().trim();
			this.mavenRepository = mvn.getMavenRepository().trim();
			this.prjName = mvn.getProject().getArtifactId();
			this.prjVer = mvn.getProject().getVersion();
			this.noPrjVer = BoolUtils.toBool(mvn.getNoPrjVer().trim(), false);
			this.noVerJarRegex = mvn.getNoVerJarRegex().trim();
			this.releaseDir = StrUtils.concat("./target/", prjName, "-", prjVer);
			this.releaseName = StrUtils.concat(prjName, (noPrjVer ? "" : "-".concat(prjVer)), ".jar");
			this.mainClass = mvn.getMainClass().trim();
			this.verClass = mvn.getVerClass().trim();
			this.charset = mvn.getCharset().trim();
			this.xms = mvn.getXms().trim();
			this.xmx = mvn.getXmx().trim();
			this.jdkParams = mvn.getJdkParams().trim();
			this.threadSuffix = mvn.getThreadSuffix().trim().concat(" ");	// 线程后缀必须至少有一个空格, 便于sh脚本定位线程号
			this.cmpPathMode = CmpPathMode.toMode(mvn.getCmpPathMode().trim());
			this.proguard = BoolUtils.toBool(mvn.getProguard().trim(), false);
			
		} catch(Exception e) {
			Log.error("加载 mojo-release-plugin 配置失败", e);
			System.exit(1);
		}
	}

	public DependType getDependType() {
		return dependType;
	}

	public String getSelfLibDir() {
		return selfLibDir;
	}
	
	public String getMavenRepository() {
		return mavenRepository;
	}

	public String getPrjName() {
		return prjName;
	}

	public String getPrjVer() {
		return prjVer;
	}

	public boolean isNoPrjVer() {
		return noPrjVer;
	}

	public String getNoVerJarRegex() {
		return noVerJarRegex;
	}

	public String getReleaseDir() {
		return releaseDir;
	}

	public String getReleaseName() {
		return releaseName;
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

	public CmpPathMode getCmpPathMode() {
		return cmpPathMode;
	}

	public boolean isProguard() {
		return proguard;
	}

}
