package exp.libs.mrp.services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.catt.plugin.Config;
import com.catt.plugin.core.envm.FinalParams;
import com.catt.plugin.utils.MavenUtils;
import com.catt.plugin.utils.PathUtils;
import com.catt.plugin.utils.SearchUtils;
import com.catt.plugin.utils.StandUtils;
import com.catt.util.io.FileUtils;

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

	/**
	 * 内联项目jar包的配置文件名。
	 * 不使用路径是因为该文件嵌入jar包内，且与本类同编译路径。
	 */
	private final static String INNER_PRJ_JARS_FILE = "inner_project_jars";
	
	/**
	 * 版本文件夹名称正则
	 */
	private final static String VER_REGEX = "^\\d+\\.\\d+\\.\\d+\\.\\d+$";
	
	/**
	 * 单例
	 */
	private static volatile JarLoader instance;
	
	/**
	 * 私有化构造函数
	 */
	private JarLoader() {}
	
	/**
	 * 获取单例
	 * @return 单例
	 */
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
	 * 加载主项目记录在classpath文件的依赖包
	 */
	public void loadMainPrjCp() {
		String cpPath = "./.classpath";
		String cpRegex = "classpathentry.*?kind=\"([^\"]+).*?path=\"([^\"]+)";
		String cpContent = "";
		try {
			cpContent = FileUtils.readFileToString(
					new File(cpPath), FinalParams.DEFAULT_CHARSET);
			
		} catch (Exception e) {
			LogMgr.error("加载类路径文件[" + cpRegex + "]失败,程序退出.", e);
			System.exit(1);
		}
		
		String mainProRootPath = PathUtils.getProjectRootPath();
		String mainPrjName = _MvnConfig.getInstn().getProjectName();
		JarMgr.getInstn().initJarSet(mainPrjName);
		
		Pattern ptn = Pattern.compile(cpRegex);
		Matcher mth = ptn.matcher(cpContent);
		while(mth.find()) {
			String kind = mth.group(1).trim().replace('\\', '/');
			String path = mth.group(2).trim().replace('\\', '/');
			
			//全路径或相对项目的路径
			if(FinalParams.CP_KIND_LIB.equalsIgnoreCase(kind)) {
				
				if(false == PathUtils.isFullPath(path)) {
					path = PathUtils.combPath(mainProRootPath, path);
				}
				JarMgr.getInstn().add(
						mainPrjName, StandUtils.toStandPath(path));
				
			//以变量开头的路径(暂时只能解析lib、commonLib和maven变量)
			} else if(FinalParams.CP_KIND_VAR.equalsIgnoreCase(kind)) {
				
				if(path.startsWith(FinalParams.VP_LIB)) {
					path = PathUtils.combPath(mainProRootPath, 
							path.replace(FinalParams.VAR_LIB, 
							_MvnConfig.getInstn().getLibPath()));
					
				} else if(path.startsWith(FinalParams.VP_COMMONLIB)) {
					path = path.replace(FinalParams.VAR_COMMONLIB, 
							_MvnConfig.getInstn().getCommonLibPath());
					
				} else if(path.startsWith(FinalParams.VP_MAVEN)) {
					path = path.replace(FinalParams.VAR_MAVEN, 
							_MvnConfig.getInstn().getMavenLibPath());
					
				} else if(path.startsWith(FinalParams.VP_MAVEN_CMD)) {
					path = path.replace(FinalParams.VAR_MAVEN_CMD, 
							_MvnConfig.getInstn().getMavenLibPath());
					
				//标记主项目jar包不完整
				} else {
					JarMgr.getInstn().markLostJar(mainPrjName);
					LogMgr.warn("主项目[" + mainPrjName + 
							"]缺失jar包[" + path + "]:无法解析的Eclipse路径变量.");
				}
				JarMgr.getInstn().add(
						mainPrjName, StandUtils.toStandPath(path));
				
			//直接引用源代码的不可用路径
			} else if(FinalParams.CP_KIND_SRC.equalsIgnoreCase(kind)) {
				
				//Maven项目会自动打包,仅非Maven项目需要终止
				if(_MvnConfig.getInstn().isCallByMaven() == false) {
					if(path.startsWith("src") == false) {
						LogMgr.warn("主项目直接引用了源码项目[" + path + 
								"],所创建脚本可能无法运行.");
					}
				}
			}
		}
	}
	
	/**
	 * 加载主项目记录在pom.xml文件的依赖包
	 * @param mProject maven项目对象
	 */
	@SuppressWarnings("unchecked")
	public void loadMainPrjPom(org.apache.maven.project.MavenProject mProject) {
		LogMgr.info("正在解析pom依赖...");
		List<org.apache.maven.artifact.Artifact> artifacts = 
				mProject.getCompileArtifacts();
		List<String> jarPathList = new LinkedList<String>();
		
		for(org.apache.maven.artifact.Artifact artifact : artifacts) {
			jarPathList.add(PathUtils.combPath(
					_MvnConfig.getInstn().getMavenLibPath(), 
					MavenUtils.getRelativeJarPath(artifact)));
		}
		JarMgr.getInstn().addMore(
				_MvnConfig.getInstn().getProjectName(), jarPathList);
		LogMgr.info("pom依赖解析完成.");
	}
	
	/**
	 * 检索并加载内联的其他项目记录在inner_project_jars文件的依赖包
	 */
	public void loadInnerPrjJars() {
		String content = "";
		InputStream is = JarLoader.class.
				getResourceAsStream(INNER_PRJ_JARS_FILE);
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		
		try {
			byte[] buff = new byte[1024];
			int rc = 0;
			while ((rc = is.read(buff, 0, 1024)) > 0) {
				swapStream.write(buff, 0, rc);
			}
			byte[] arrByte = swapStream.toByteArray();
			content = new String(arrByte, FinalParams.DEFAULT_CHARSET);
			
		} catch (Exception e) {
			LogMgr.error("读取内联项目依赖记录文件[" + INNER_PRJ_JARS_FILE +
					"]失败,只创建主项目脚本.", e);
			_MvnConfig.getInstn().setCpsAutodb(false);
			_MvnConfig.getInstn().setCpsCrypto(false);
			_MvnConfig.getInstn().setCpsStartcheck(false);
		}
		
		String[] prjCntAry = content.split("#{2,}");
		Pattern ptn = Pattern.compile("#(.*?):");
		
		// 迭代内联项目的配置内容数组,检索依赖包
		for(String prjCnt : prjCntAry) {
			prjCnt = prjCnt.trim();
			
			if(!"".equals(prjCnt)) {
				String[] ary = prjCnt.split("\n");
				Matcher mth = ptn.matcher(ary[0]);
				
				if(mth.find()) {
					
					// <B>内联项目名称强制小写</B>
					String prjName = mth.group(1).trim().toLowerCase();
					JarMgr.getInstn().initJarSet(prjName);
					
					for(int i = 1; i < ary.length; i++) {
						String jarName = ary[i].trim();
						String jarPath = searchInnerPrjJars(jarName);
						
						if(jarPath == null || "".equals(jarPath)) {
							JarMgr.getInstn().markLostJar(prjName);
							LogMgr.warn("项目[" + prjName + 
									"]缺失jar包[" + jarName + "].");
						} else {
							JarMgr.getInstn().add(
									prjName, StandUtils.toStandPath(jarPath));
						}
					}
				}
			}
		}
	}
	
	/**
	 * 检索内联的其他项目的依赖包
	 * @param jarName jar包的名称
	 * @return 检索到的jar包的绝对路径(检索不到则返回空串)
	 */
	private String searchInnerPrjJars(String jarName) {
		String jarPath = "";
		Pattern ptn = Pattern.compile(FinalParams.CATT_JAR_REGEX);
		Matcher mth = ptn.matcher(jarName);
				
		//catt的包
		if(mth.find()) {
			String jarVer = mth.group(2);
			String searchDir = PathUtils.combPath(
					_MvnConfig.getInstn().getCommonLibPath(), "j2se/catt");
			
			//带版本号，保留版本号
			if(jarVer != null && !"".equals(jarVer)) {
				//none
				
			//不带版本号，取最新版本号
			} else {
				jarVer = "";
				File dir = new File(searchDir);
				if(dir != null && dir.exists() && dir.isDirectory()) {
					String[] cDirs = dir.list(new DirFilter());
					List<String> cDirList = Arrays.asList(cDirs);
					
					Collections.sort(cDirList, new DirSort());	//倒序排序
					jarVer = cDirList.get(0);
				}
				
				//检索目录修正到版本目录下,并为jar包名称添加版本号
				if(!"".equals(jarVer)) {
					searchDir = PathUtils.combPath(searchDir, jarVer);
					jarName = jarName.replace(".jar", "-" + jarVer + ".jar");
				}
			}
			
			// 先在commonLib目录检索,取最新
			jarPath = SearchUtils.bfs(jarName, searchDir, false);
				
			//当无法从版本目录找到jar包、或版本目录不存在时,从Maven仓库检索,取最新
			if(jarPath == null || "".equals(jarPath)) {
				searchDir = PathUtils.combPath(
						_MvnConfig.getInstn().getMavenLibPath(), "com/catt");
				jarPath = SearchUtils.dfs(jarName, searchDir, false);
			}
			
		//非catt的包
		} else {
			
			// 先在commonLib目录检索,取第一个
			String searchDir = PathUtils.combPath(
					_MvnConfig.getInstn().getCommonLibPath(), "j2se");
			jarPath = SearchUtils.dfs(jarName, searchDir, true);
			
			// 当从commonLib目录检索失败时,从Maven仓库检索,取第一个
			if(jarPath == null || "".equals(jarPath)) {
				searchDir = _MvnConfig.getInstn().getMavenLibPath();
				jarPath = SearchUtils.dfs(jarName, searchDir, true);
			}
		}
		return jarPath;
	}
	
	/**
	 * <PRE>
	 * 文件(夹)名称过滤器。
	 * </PRE>
	 * <B>项    目：</B>凯通J2SE开发平台(KTJSDP)
	 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2014
	 * @version   1.0 2014-9-16
	 * @author    廖权斌：liaoquanbin@gdcattsoft.com
	 * @since     jdk版本：jdk1.6
	 */
	private class DirFilter implements FilenameFilter {

		@Override
		public boolean accept(File dir, String name) {
			boolean isAccept = false;
			if(name != null && !"".equals(name)) {
				isAccept = name.matches(VER_REGEX);
			}
			return isAccept;
		}
	}
	
	/**
	 * <PRE>
	 * 文件(夹)名称排序器(降序)。
	 * </PRE>
	 * <B>项    目：</B>凯通J2SE开发平台(KTJSDP)
	 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2014
	 * @version   1.0 2014-9-16
	 * @author    廖权斌：liaoquanbin@gdcattsoft.com
	 * @since     jdk版本：jdk1.6
	 */
	private class DirSort implements Comparator<String> {

		@Override
		public int compare(String dirA, String dirB) {
			int rst = 0;
			if(dirA != null && dirB != null && 
					dirA.matches(VER_REGEX) && dirB.matches(VER_REGEX)) {
				String[] eNumA = dirA.split("\\.");
				String[] eNumB = dirB.split("\\.");
				
				//从低位到高位判断版本号
				for(int i = 3; i >= 0; i--) {
					int diff = Integer.parseInt(eNumB[i]) - 
							Integer.parseInt(eNumA[i]);
					rst = (diff == 0 ? rst : diff);	//高位版本号若一致则保持原判定
				}
			}
			return rst;
		}
		
	}
}
