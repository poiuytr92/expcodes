package exp.libs.mrp;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exp.libs.envm.Charset;
import exp.libs.mrp.api.MvnInstallMojo;
import exp.libs.mrp.envm.DependType;
import exp.libs.mrp.envm.FinalParams;
import exp.libs.mrp.services.PathTree;
import exp.libs.mrp.services.SearchUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.PathUtils;

public class Config {

	/** 自动生成, 无需配置 */
	private String prjName;
	
	/** 自动生成, 无需配置 */
	private String prjVer;
	
	/** 自动生成, 无需配置 */
	private String releasePath;
	
	/** 自动生成, 无需配置 */
	private String releaseName;
	
	private DependType dependType;
	
	private String winMavenRepository;
	
	private String linuxMavenRepository;
	
	private String winLibPath;
	
	private String linuxLibPath;
	
	private String mainClass;
	
	private String verClass;
	
	private String charset;
	
	private String xms;
	
	private String xmx;
	
	private String jdkParams;
	
	private String threadSuffix;
	
	private boolean noPrjVer;
	
	private Set<String> noVerJars;
	
	private boolean allNoVer;
	
	private int cmpPathMode;
	
	private static volatile Config instance;
	
	private Config() {
		this.prjName = "";
		this.prjVer = "";
		this.releasePath = "";
		this.releaseName = "";
		this.dependType = DependType.LIB;
		this.winMavenRepository = "D:\\mavenRepository";
		this.linuxMavenRepository = "/home/cattsoft/mavenRepository";
		this.winLibPath = ".\\lib";
		this.linuxLibPath = "./lib";
		this.mainClass = "foo.bar.prj.Main";
		this.verClass = "foo.bar.prj.Version";
		this.charset = Charset.UTF8;
		this.xms = "64m";
		this.xmx = "128m";
		this.jdkParams = "";
		this.threadSuffix = "";
		this.noPrjVer = true;
		this.noVerJars = new HashSet<String>();
		this.allNoVer = false;
		this.cmpPathMode = PathTree.PRE_MODE_STAND;
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
	
	
	private boolean init(MvnInstallMojo mvn) {
		boolean isOk = true;
		try {
			this.prjName = mvn.getProject().getArtifactId();
			this.prjVer = mvn.getProject().getVersion();
			this.dependType = DependType.toDependType(mvn.getDependType());
			this.winMavenRepository = PathUtils.toWin(mvn.getWinMavenRepository());
			this.linuxMavenRepository = PathUtils.toLinux(mvn.getLinuxMavenRepository());
			this.winLibPath = PathUtils.toWin(mvn.getWinLibPath());
			this.linuxLibPath = PathUtils.toLinux(mvn.getLinuxLibPath());
			this.mainClass = mvn.getMainClass();
			this.verClass = mvn.getVerClass();
			
		} catch(Exception e) {
			isOk = false;
			Log.error("加载 mojo-release-plugin 配置失败", e);
		}
		return isOk;
	}
	
	public String getProjectName() {
		return prjName;
	}

	public String getProjectVersion() {
		return prjVer;
	}

	/**
	 * 获取脚本发布路径(根据调用接口、项目名称、项目版本自动生成)
	 * @return 脚本发布路径
	 */
	public String getReleasePath() {
		if(releasePath == null || "".equals(releasePath)) {
			this.releasePath = (isCallByMaven == false ? "." : 
					("." + File.separator + "target" + File.separator + 
					prjName + "-" + prjVer));
		}
		return releasePath;
	}

	/**
	 * 获取项目发布名称(根据项目名称、项目版本自动生成)
	 * @return 项目发布名称
	 */
	public String getReleaseName() {
		if(releaseName == null || "".equals(releaseName)) {
			StringBuilder sb = new StringBuilder();
			sb.append(prjName);
			if(mainProVersion == true) {
				sb.append('-').append(prjVer);
			}
			sb.append(".jar");
			releaseName = sb.toString();
		}
		return releaseName;
	}
	
	public String getScriptType() {
		return scriptType;
	}

	public String getMainClass() {
		return mainClass;
	}

	public String getVersionClass() {
		return versionClass;
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

	public String getThreadSuffix() {
		return threadSuffix;
	}
	
	public String getJdkParams() {
		return jdkParams;
	}

	public String getLibPath() {
		return (ValidateUtils.isWin() == true ? 
				getWinLibPath() : getLinuxLibPath());
	}
	
	public String getLinuxLibPath() {
		return linuxLibPath;
	}

	public String getWinLibPath() {
		return winLibPath;
	}

	public String getCommonLibPath() {
		return (ValidateUtils.isWin() == true ? 
				getWinCommonLibPath() : getLinuxCommonLibPath());
	}
	
	public String getLinuxCommonLibPath() {
		return linuxCommonLibPath;
	}

	public String getWinCommonLibPath() {
		return winCommonLibPath;
	}

	public String getMavenLibPath() {
		return (ValidateUtils.isWin() == true ? 
				getWinMavenLibPath() : getLinuxMavenLibPath());
	}
	
	public String getLinuxMavenLibPath() {
		return linuxMavenLibPath;
	}

	public String getWinMavenLibPath() {
		return winMavenLibPath;
	}

	public int getPathPrefixMode() {
		return pathPrefixMode;
	}
	
	public boolean isMainProVersion() {
		return mainProVersion;
	}

	public Set<String> getNoVerJars() {
		return noVerJars;
	}

	public boolean isAllNoVer() {
		return allNoVer;
	}

	public boolean isCpsMain() {
		return cpsMain;
	}

	public boolean isCpsAnt() {
		return cpsAnt;
	}

	public boolean isCpsCrypto() {
		return cpsCrypto;
	}

	public boolean isCpsAutodb() {
		return cpsAutodb;
	}

	public boolean isCpsStartcheck() {
		return cpsStartcheck;
	}

	public void setProjectName(String prjName) {
		if(isLockConf == false) {
			this.prjName = prjName;
		}
	}

	public void setProjectVersion(String prjVer) {
		if(isLockConf == false) {
			this.prjVer = prjVer;
		}
	}

	public void setScriptType(String scriptType) {
		if(isLockConf == false) {
			this.scriptType = scriptType;
		}
	}

	public void setMainClass(String mainClass) {
		if(isLockConf == false) {
			this.mainClass = mainClass;
		}
	}

	public void setVersionClass(String versionClass) {
		if(isLockConf == false) {
			this.versionClass = versionClass;
		}
	}

	public void setCharset(String charset) {
		if(isLockConf == false) {
			this.charset = charset;
		}
	}

	public void setXms(String xms) {
		if(isLockConf == false) {
			this.xms = xms;
		}
	}

	public void setXmx(String xmx) {
		if(isLockConf == false) {
			this.xmx = xmx;
		}
	}

	public void setThreadSuffix(String threadSuffix) {
		if(isLockConf == false) {
			
			// 线程后缀必须至少有一个空格, 便于sh脚本定位线程号
			this.threadSuffix = threadSuffix.trim() + " ";
		}
	}

	public void setJdkParams(String jdkParams) {
		if(isLockConf == false) {
			this.jdkParams = jdkParams;
		}
	}

	public void setLinuxLibPath(String linuxLibPath) {
		if(isLockConf == false) {
			this.linuxLibPath = linuxLibPath;
		}
	}

	public void setWinLibPath(String winLibPath) {
		if(isLockConf == false) {
			this.winLibPath = winLibPath;
		}
	}

	public void setLinuxCommonLibPath(String linuxCommonLibPath) {
		if(isLockConf == false) {
			this.linuxCommonLibPath = linuxCommonLibPath;
		}
	}

	public void setWinCommonLibPath(String winCommonLibPath) {
		if(isLockConf == false) {
			this.winCommonLibPath = winCommonLibPath;
		}
	}

	public void setLinuxMavenLibPath(String linuxMavenLibPath) {
		if(isLockConf == false) {
			this.linuxMavenLibPath = linuxMavenLibPath;
		}
	}

	public void setWinMavenLibPath(String winMavenLibPath) {
		if(isLockConf == false) {
			this.winMavenLibPath = winMavenLibPath;
		}
	}

	public void setPathPrefixMode(String pathPrefixMode) {
		if(isLockConf == false) {
			try {
				setPathPrefixMode(Integer.parseInt(pathPrefixMode));
			} catch (NumberFormatException e) {
				//
			}
		}
	}
	
	public void setPathPrefixMode(int pathPrefixMode) {
		if(isLockConf == false) {
			this.pathPrefixMode = pathPrefixMode;
		}
	}

	public void setMainProVersion(String mainProVersion) {
		if(isLockConf == false) {
			if("false".equalsIgnoreCase(mainProVersion)) {
				setMainProVersion(false);
				
			} else if("true".equalsIgnoreCase(mainProVersion)) {
				setMainProVersion(true);
			}
		}
	}
	
	public void setMainProVersion(boolean mainProVersion) {
		if(isLockConf == false) {
			this.mainProVersion = mainProVersion;
		}
	}
	
	public void setNoVerJars(String noVerJarList) {
		if(isLockConf == true && noVerJarList == null) {
			return;
		}
		
		String[] noVerJars = noVerJarList.trim().split(",|;");
		for(String noVerJar: noVerJars) {
			if(noVerJar == null) {
				continue;
			}
			
			noVerJar = noVerJar.trim();
			if(!"".equals(noVerJar)) {
				this.noVerJars.add(noVerJar);
			}
		}
		this.noVerJars.add(FinalParams.CATT_JAR_PREFIX);	// catt的包全部移除版本号
		
		if(this.noVerJars.contains("*")) {
			this.allNoVer = true;
		}
	}
	
	@Override
	public String toString() {
		return toPrintKV();
	}
}
