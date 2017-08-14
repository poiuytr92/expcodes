package exp.libs.mrp.api;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exp.libs.envm.Charset;
import exp.libs.utils.io.FileUtils;

public class Config {

	/** 自动生成, 无需配置 */
	private String projectName = "";
	
	/** 自动生成, 无需配置 */
	private String projectVersion = "";
	
	/** 自动生成, 无需配置 */
	private String releasePath = "";
	
	/** 自动生成, 无需配置 */
	private String releaseName = "";
	
	private String dependType = "lib";
	
	private String winLibPath = ".\\lib";
	
	private String linuxLibPath = "./lib";
	
	private String winMavenLibPath = "D:\\mavenRepository";
	
	private String linuxMavenLibPath = "/home/cattsoft/mavenRepository";
	
	private String mainClass = "foo.bar.prj.Main";
	
	private String verClass = "foo.bar.prj.Version";
	
	private String charset = Charset.UTF8;
	
	private String xms = "64m";
	
	private String xmx = "128m";
	
	private String jdkParams = "";
	
	private String threadSuffix = "";
	
	private boolean noPrjVer = true;
	
	private Set<String> noVerJars = null;
	
	private int cmpPathMode = PathTree.PRE_MODE_STAND;
	
	private boolean allNoVer = false;
	
	/**
	 * 单例
	 */
	private static volatile Config instance;
	
	/**
	 * 私有化构造函数
	 */
	private Config() {
		this.noVerJars = new HashSet<String>();
		this.isCallByMaven = true;
		this.isLockConf = false;
	}
	
	/**
	 * 获取单例
	 * @return 单例
	 */
	public static Config getInstn() {
		if(instance == null) {
			synchronized (Config.class) {
				if(instance == null) {
					instance = new Config();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 使用ISO-8859-1默认编码加载配置
	 * @param confPath 配置文件路径
	 */
	public void loadConf(String confPath) {
		loadConf(confPath, FinalParams.DEFAULT_CHARSET);
	}
	
	/**
	 * 加载配置
	 * @param confPath 配置文件路径
	 * @param charset 配置文件编码
	 */
	public void loadConf(String confPath, String charset) {
		this.isCallByMaven = false;
				
		try {
			"Test Charset".getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			charset = FinalParams.DEFAULT_CHARSET;	//把异常编码替换为默认编码
			LogMgr.warn("[" + charset + "]编码异常,自动替换为默认编码[" + 
					FinalParams.DEFAULT_CHARSET + "]加载配置文件[" + 
					confPath + "].");
		}
		
		String content = "";
		try {
			content = FileUtils.readFileToString(new File(confPath), charset);
			
		} catch (Exception e) {
			LogMgr.error("加载配置文件[" + confPath + "]失败,程序退出.", e);
			System.exit(1);
		}
		
		//加载配置文件配置
		Pattern ptn = Pattern.compile(KV_CONF_REGEX);
		Matcher mth = ptn.matcher(content);
		while(mth.find()) {
			String key = mth.group(1).trim();
			String val = mth.group(2).trim();
			setKV(key, val);
		}
		
		//根据配置文件配置的versionClass,检索Version类,获取项目发布参数
		loadVersion();
	}
	
	/**
	 * 设置KV对
	 * @param key 键
	 * @param value 值
	 */
	private void setKV(String key, String value) {
		if("scriptType".equalsIgnoreCase(key)) {
			setScriptType(value);
			
		} else if("mainClass".equalsIgnoreCase(key)) {
			setMainClass(value);
			
		} else if("versionClass".equalsIgnoreCase(key)) {
			setVersionClass(value);
			
		} else if("charset".equalsIgnoreCase(key)) {
			setCharset(value);
			
		} else if("xms".equalsIgnoreCase(key)) {
			setXms(value);
			
		} else if("xmx".equalsIgnoreCase(key)) {
			setXmx(value);
			
		} else if("threadSuffix".equalsIgnoreCase(key)) {
			setThreadSuffix(value);
			
		} else if("jdkParams".equalsIgnoreCase(key)) {
			setJdkParams(value);
			
		} else if("linuxLibPath".equalsIgnoreCase(key)) {
			setLinuxLibPath(value);
			
		} else if("winLibPath".equalsIgnoreCase(key)) {
			setWinLibPath(value);
			
		} else if("linuxCommonLibPath".equalsIgnoreCase(key)) {
			setLinuxCommonLibPath(value);
			
		} else if("winCommonLibPath".equalsIgnoreCase(key)) {
			setWinCommonLibPath(value);
			
		} else if("linuxMavenLibPath".equalsIgnoreCase(key)) {
			setLinuxMavenLibPath(value);
			
		} else if("winMavenLibPath".equalsIgnoreCase(key)) {
			setWinMavenLibPath(value);
			
		} else if("pathPrefixMode".equalsIgnoreCase(key)) {
			setPathPrefixMode(value);
			
		} else if("cpsMain".equalsIgnoreCase(key)) {
			setCpsMain(value);
			
		} else if("mainProVersion".equalsIgnoreCase(key)) {
			setMainProVersion(value);
			
		} else if("noVerJars".equalsIgnoreCase(key)) {
			setNoVerJars(value);
			
		} else if("cpsAnt".equalsIgnoreCase(key)) {
			setCpsAnt(value);
			
		} else if("cpsCrypto".equalsIgnoreCase(key)) {
			setCpsCrypto(value);
			
		} else if("cpsAutodb".equalsIgnoreCase(key)) {
			setCpsAutodb(value);
			
		} else if("cpsStartcheck".equalsIgnoreCase(key)) {
			setCpsStartcheck(value);
		}
	}
	
	/**
	 * 根据versionClass,检索Version类,获取项目发布参数：项目名称、项目版本
	 */
	private void loadVersion() {
		List<String> verPaths = SearchUtils.search("Version.java", "src");
		String prjVerPath = "";
		
		for(String verPath : verPaths) {
			if(verPath.replaceAll("[\\\\|/]", ".").
					endsWith(getVersionClass() + ".java")) {
				prjVerPath = verPath;
				break;
			}
		}
		if("".equals(prjVerPath)) {
			LogMgr.error("找不到版本类[versionClass = " + getVersionClass() + 
					"],无法执行后续创建操作,程序退出.");
			System.exit(1);
		}
		
		List<String> verContent = new LinkedList<String>();
		try {
			verContent = FileUtils.readLines(new File(prjVerPath), 
					getCharset());
			
		} catch (Exception e) {
			LogMgr.error("加载版本类文件[" + prjVerPath + 
					"]失败,无法执行后续创建操作,程序退出.", e);
			System.exit(1);
		}
		
		// 检索项目名称
		Pattern ptn = Pattern.compile("^.*procName = \"([^\"]+).*$");
		Matcher mth = null;
		for(String line : verContent) {
			mth = ptn.matcher(line);
			if(mth.find()) {
				setProjectName(mth.group(1).trim());
				break;
			}
		}
		
		// 检索版本号(取最后一个,即最新版)
		ptn = Pattern.compile("^.*setVer\\(\"([^\"]+).*$");
		for(String line : verContent) {
			mth = ptn.matcher(line);
			if(mth.find()) {
				setProjectVersion(mth.group(1).trim());
			}
		}
		
		if(getProjectName() == null || "".equals(getProjectName()) && 
				getProjectVersion() == null || "".equals(getProjectVersion())) {
			LogMgr.error("从版本类文件[" + prjVerPath + 
					"]中析取项目名称和版本号失败,无法执行后续创建操作,程序退出.");
			System.exit(1);
		}
	}
	
	/**
	 * 打印参数表
	 * @return 参数表
	 */
	public String toPrintKV() {
		StringBuilder sb = new StringBuilder();
		sb.append("\r\n++++++++++++++++ Conf Params ++++++++++++++++\r\n");
		sb.append("isCallByMaven : ").append(isCallByMaven()).append("\r\n");
		sb.append("projectName : ").append(getProjectName()).append("\r\n");
		sb.append("projectVersion : ").append(getProjectVersion()).append("\r\n");
		sb.append("releasePath : ").append(getReleasePath()).append("\r\n");
		sb.append("releaseName : ").append(getReleaseName()).append("\r\n");
		sb.append("scriptType : ").append(getScriptType()).append("\r\n");
		sb.append("mainClass : ").append(getMainClass()).append("\r\n");
		sb.append("versionClass : ").append(getVersionClass()).append("\r\n");
		sb.append("charset : ").append(getCharset()).append("\r\n");
		sb.append("xms : ").append(getXms()).append("\r\n");
		sb.append("xmx : ").append(getXmx()).append("\r\n");
		sb.append("threadSuffix : ").append(getThreadSuffix()).append("\r\n");
		sb.append("jdkParams : ").append(getJdkParams()).append("\r\n");
		sb.append("linuxLibPath : ").append(getLinuxLibPath()).append("\r\n");
		sb.append("winLibPath : ").append(getWinLibPath()).append("\r\n");
		sb.append("linuxCommonLibPath : ").append(getLinuxCommonLibPath()).append("\r\n");
		sb.append("winCommonLibPath : ").append(getWinCommonLibPath()).append("\r\n");
		sb.append("linuxMavenLibPath : ").append(getLinuxMavenLibPath()).append("\r\n");
		sb.append("winMavenLibPath : ").append(getWinMavenLibPath()).append("\r\n");
		sb.append("pathPrefixMode : ").append(getPathPrefixMode()).append("\r\n");
		sb.append("mainProVersion : ").append(isMainProVersion()).append("\r\n");
		sb.append("noVerJars : ").append(getNoVerJars()).append("\r\n");
		
		sb.append("cpsMain : ").append(isCpsMain()).append("\r\n");
		sb.append("cpsAnt : ").append(isCpsAnt()).append("\r\n");
		sb.append("cpsAutodb : ").append(isCpsAutodb()).append("\r\n");
		sb.append("cpsCrypto : ").append(isCpsCrypto()).append("\r\n");
		sb.append("cpsStartcheck : ").append(isCpsStartcheck()).append("\r\n");
		sb.append("---------------------------------------------\r\n");
		return sb.toString();
	}
	
	/**
	 * 当前是否为Maven接口调用，受影响项：
	 * （1）只有在Maven接口调用时才解析Pom文件。
	 * （2）影响项目发布的目录名称是target还是release。
	 * 
	 * @return true:Maven接口调用; false: main方法调用
	 */
	public boolean isCallByMaven() {
		return isCallByMaven;
	}

	/**
	 * 锁定配置项。
	 * 此方法调用一次即可，锁定后无法解锁。
	 * 
	 * 一旦锁定后，所有setter方法均无效(创建脚本开关除外)。
	 */
	public void lockConf() {
		this.isLockConf = true;
	}
	
	public String getProjectName() {
		return projectName;
	}

	public String getProjectVersion() {
		return projectVersion;
	}

	/**
	 * 获取脚本发布路径(根据调用接口、项目名称、项目版本自动生成)
	 * @return 脚本发布路径
	 */
	public String getReleasePath() {
		if(releasePath == null || "".equals(releasePath)) {
			this.releasePath = (isCallByMaven == false ? "." : 
					("." + File.separator + "target" + File.separator + 
					projectName + "-" + projectVersion));
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
			sb.append(projectName);
			if(mainProVersion == true) {
				sb.append('-').append(projectVersion);
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

	public void setProjectName(String projectName) {
		if(isLockConf == false) {
			this.projectName = projectName;
		}
	}

	public void setProjectVersion(String projectVersion) {
		if(isLockConf == false) {
			this.projectVersion = projectVersion;
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
