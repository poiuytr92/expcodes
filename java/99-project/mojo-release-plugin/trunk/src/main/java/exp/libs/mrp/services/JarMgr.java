package exp.libs.mrp.services;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.catt.plugin.Config;
import com.catt.plugin.core.envm.FinalParams;
import com.catt.plugin.utils.StandUtils;
import com.catt.util.regex.ValidateUtils;

/**
 * <PRE>
 * Jar包管理器
 * </PRE>
 * <B>项    目：</B>凯通J2SE开发平台(KTJSDP)
 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2014
 * @version   1.0 2014-9-19
 * @author    廖权斌：liaoquanbin@gdcattsoft.com
 * @since     jdk版本：jdk1.6
 */
public class JarMgr {

	/**
	 * jar包禁忌表的配置文件名。
	 * 不使用路径是因为该文件嵌入jar包内，且与本类同编译路径。
	 */
	private final static String TABU_JARS_FILE = "tabu_jars";
	
	/**
	 * jar包禁忌表
	 */
	private List<String> tabuJars;
	
	/**
	 * 多个项目的依赖包集合。
	 * 记录其他项目的所有依赖包的完整路径。
	 * 
	 * 项目名称 -> 依赖包集合
	 */
	private Map<String, Set<String>> allJarMap;
	
	/**
	 * 标记项目的jar包是否不完整
	 * 	项目名称 -> true:不全;  false/null:齐全;  
	 */
	private Map<String, Boolean> lostJarMarks;
	
	/**
	 * 标记本次运行是否缺失jar包
	 */
	private boolean isLostJar;
	
	/**
	 * 单例
	 */
	private static volatile JarMgr instance;
	
	/**
	 * 私有化构造函数
	 */
	private JarMgr() {
		this.allJarMap = new HashMap<String, Set<String>>();
		this.tabuJars = new LinkedList<String>();
		this.lostJarMarks = new HashMap<String, Boolean>();
		this.isLostJar = false;
	}
	
	/**
	 * 获取单例
	 * @return 单例
	 */
	public static JarMgr getInstn() {
		if(instance == null) {
			synchronized (JarMgr.class) {
				if(instance == null) {
					instance = new JarMgr();
					instance.loadTabuJars();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 加载jar包禁忌表。
	 * 记录在禁忌表中的jar包无论如何也不会被添加进管理器中。
	 */
	private void loadTabuJars() {
		String content = "";
		InputStream is = JarMgr.class.
				getResourceAsStream(TABU_JARS_FILE);
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
			LogMgr.error("读取jar禁忌表记录文件[" + TABU_JARS_FILE + 
					"]失败,不影响程序运行.", e);
		}
		
		String[] jars = content.split("[,|;|\r|\n]");
		for(String jar : jars) {
			tabuJars.add(jar);
		}
	}
	
	/**
	 * 初始化存储项目jar包的集合.
	 * 仅为了在打印清单时,即使搜索不到jar包,也至少可以打印一个空集.
	 * 
	 * @param projectName 项目名称
	 */
	public void initJarSet(String projectName) {
		add(projectName, null);
	}
	
	/**
	 * 添加指定项目的单个依赖包
	 * @param projectName 项目名称
	 * @param jarPath 依赖包的完整路径
	 */
	public void add(String projectName, String jarPath) {
		synchronized (allJarMap) {
			Set<String> jarSet = allJarMap.get(projectName);
			
			if(jarSet == null) {
				jarSet = new HashSet<String>();
				allJarMap.put(projectName, jarSet);
			}
			
			if(jarPath != null && !"".equals(jarPath)) {
				
				jarPath = jarPath.trim();
				boolean isFind = false;
				for(String jar : tabuJars) {
					if(jarPath.endsWith(jar)) {
						isFind = true;
						break;
					}
				}
				
				if(isFind == false) {
					jarSet.add(jarPath);
				}
			}
		}
	}
	
	/**
	 * 添加指定项目的多个依赖包
	 * @param projectName 项目名称
	 * @param jarPaths 各依赖包的完整路径,若为null则操作无效
	 */
	public void addMore(String projectName, List<String> jarPaths) {
		if(jarPaths != null) {
			for(String jarPath : jarPaths) {
				add(projectName, jarPath);
			}
		}
	}
	
	/**
	 * 覆写指定项目的所有依赖包
	 * @param projectName 项目名称
	 * @param jarPaths 各依赖包的完整路径,若为null则操作无效
	 */
	public void cover(String projectName, List<String> jarPaths) {
		if(jarPaths != null) {
			synchronized (allJarMap) {
				allJarMap.remove(projectName);
				Set<String> jarSet = new HashSet<String>(jarPaths);
				allJarMap.put(projectName, jarSet);
			}
		}
	}
	
	/**
	 * 获取指定项目的依赖包路径集合.
	 * 自动根据运行平台的OS返回对应规范格式的路径.
	 * 
	 * @param projectName 项目名称
	 * @return 指定项目的所有依赖包路径集合
	 */
	public List<String> getJarPaths(String projectName) {
		return (ValidateUtils.isWin() ? 
				getWinJarPaths(projectName) : getLinuxJarPaths(projectName));
	}
	
	/**
	 * 获取指定项目的依赖包路径集合.
	 * 返回的路径符合win平台格式.
	 * 
	 * @param projectName 项目名称
	 * @return 指定项目的所有依赖包路径集合
	 */
	public List<String> getWinJarPaths(String projectName) {
		Set<String> jarPathSet = getJarPathSet(projectName);
		List<String> winJars = new LinkedList<String>();
		
		for(String jarPath : jarPathSet) {
			
			// 把主项目放在最前面，以便于其可以覆写支撑包的类
			if(jarPath.contains(_MvnConfig.getInstn().getReleaseName())) {
				winJars.add(0, StandUtils.toWinPath(jarPath));
			} else {
				winJars.add(StandUtils.toWinPath(jarPath));
			}
		}
		return winJars;
	}
	
	/**
	 * 获取指定项目的依赖包路径集合.
	 * 返回的路径符合linux平台格式.
	 * 
	 * @param projectName 项目名称
	 * @return 指定项目的所有依赖包路径集合
	 */
	public List<String> getLinuxJarPaths(String projectName) {
		Set<String> jarPathSet = getJarPathSet(projectName);
		List<String> linuxJars = new LinkedList<String>();
		
		for(String jarPath : jarPathSet) {
			
			// 把主项目放在最前面，以便于其可以覆写支撑包的类
			if(jarPath.contains(_MvnConfig.getInstn().getReleaseName())) {
				linuxJars.add(0, StandUtils.toLinuxPath(jarPath));
			} else {
				linuxJars.add(StandUtils.toLinuxPath(jarPath));
			}
		}
		return linuxJars;
	}
	
	/**
	 * 获取指定项目的依赖包路径集合.
	 * 这些依赖包路径未做任何处理，为写入map时的原始路径。
	 * 
	 * @param projectName 项目名称
	 * @return 指定项目的所有依赖包路径集合
	 */
	private Set<String> getJarPathSet(String projectName) {
		synchronized (allJarMap) {
			Set<String> jarSet = allJarMap.get(projectName);
			
			if(jarSet == null) {
				jarSet = new HashSet<String>();
				allJarMap.put(projectName, jarSet);
			}
			return jarSet;
		}
	}
	
	/**
	 * 标记项目缺包
	 * @param projectName 项目名称
	 */
	public void markLostJar(String projectName) {
		this.lostJarMarks.put(projectName, true);
		this.isLostJar = true;
	}
	
	/**
	 * 检查项目是否缺包
	 * @param projectName 项目名称
	 * @return true:项目缺包; false:项目不缺包
	 */
	public boolean isLostJar(String projectName) {
		Boolean isLostJar = lostJarMarks.get(projectName);
		return (isLostJar == null || isLostJar == false) ? false : true;
	}
	
	/**
	 * 检查本次运行是否存在项目缺失jar包
	 * @return true:存在缺失; false:不存在缺失
	 */
	public boolean isLostJar() {
		return isLostJar;
	}
	
	/**
	 * 打印各个项目的依赖包
	 * @return 各个项目的依赖包清单
	 */
	public String toPrintJars() {
		StringBuilder sb = new StringBuilder();
		sb.append("\r\n+++++++++++++++ All Jars List +++++++++++++++\r\n");
		for(Iterator<String> prjNameIts = allJarMap.keySet().iterator();
				prjNameIts.hasNext();) {
			String prjName = prjNameIts.next();
			sb.append("[").append(prjName).append("] jars list");
			
			if(isLostJar(prjName) == true) {
				sb.append(" (lost some jars)");
			}
			sb.append(":\r\n");
			
			Set<String> jarSet = allJarMap.get(prjName);
			if(jarSet != null) {
				for(Iterator<String> jarIts = jarSet.iterator(); 
						jarIts.hasNext();) {
					sb.append('\t').append(jarIts.next()).append("\r\n");
				}
			}
			sb.append("\r\n");
		}
		sb.append("---------------------------------------------\r\n");
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return toPrintJars();
	}
}
