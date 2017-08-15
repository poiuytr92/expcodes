package exp.libs.mrp.path;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import exp.libs.mrp.envm.CmpPathMode;
import exp.libs.mrp.services.StandUtils;

/**
 * <PRE>
 * 路径树.
 * </PRE>
 * <B>项    目：</B>凯通J2SE开发平台(KTJSDP)
 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2014
 * @version   1.0 2014-9-19
 * @author    廖权斌：liaoquanbin@gdcattsoft.com
 * @since     jdk版本：jdk1.6
 */
public class PathTree {

	/**
	 * 树名
	 */
	private String name;
	
	/**
	 * 根节点
	 */
	private PathNode rootNode;
	
	/**
	 * 路径树中所有节点的集合
	 */
	private List<PathNode> nodes;
	
	/**
	 * 构造函数
	 * @param name 树名
	 */
	public PathTree(String name) {
		this.name = name;
		this.nodes = new LinkedList<PathNode>();
		this.rootNode = new PathNode(null, -1, false, "root");
		nodes.add(rootNode);
	}
	
	/**
	 * 添加一条路径到路径树
	 * @param path 路径
	 */
	public void add(String path) {
		
		//linux绝对路径需要特殊处理,否则根路径会丢弃
		boolean isLinuxFullPath = false;
		if(path.startsWith("/")) {
			isLinuxFullPath = true;
			path = path.substring(1);
		}
		
		String[] nodeNames = path.split("[\\\\|/]");
		PathNode fNode = rootNode;
		
		if(isLinuxFullPath == true) {
			nodeNames[0] = "/" + nodeNames[0];
		}
		
		for(int i = 0; i < nodeNames.length; i++) {
			boolean isLeaf = (i == nodeNames.length - 1);
			PathNode cNode = new PathNode(fNode, i, isLeaf, nodeNames[i]);
			fNode = addNode(cNode);
		}
	}
	
	/**
	 * 添加多条路径到路径树
	 * @param paths 路径集
	 */
	public void addMore(List<String> paths) {
		if(paths != null) {
			for(String path : paths) {
				add(path);
			}
		}
	}
	
	/**
	 * 添加一个节点到路径树
	 * @param newNode 新节点
	 * @return 若发生压缩行为,返回已有节点; 否则返回新节点.
	 */
	private PathNode addNode(PathNode newNode) {
		boolean isNeedCompress = false;
		PathNode rtnNode = null;
		
		for(PathNode node : nodes) {
			
			//根节点必唯一,排除
			if(node.getLevel() == -1) {
				continue;
			}
			
			if(node.equals(newNode)) {
				rtnNode = node;
				node.addCompress();
				isNeedCompress = true;
				break;
			}
		}
		
		if(isNeedCompress == false) {
			rtnNode = newNode;
			nodes.add(newNode);
		}
		return rtnNode;
	}
	
	/**
	 * 受路径前缀模式影响：
	 * 获取路径前缀集(路径不以分隔符结尾).集合按路径长度从大到小排序.
	 * 会根据当前路径树实时构造路径前缀集,路径格式符合win的标准.
	 * 
	 * @param pathPrefixMode 路径前缀模式
	 * @return 路径前缀集(路径不以分隔符结尾).集合按路径长度从大到小排序.
	 */
	public List<String> getWinPathPrefixSet(CmpPathMode mode) {
		List<String> winPathPres = new ArrayList<String>();
		List<String> pathPres = getPathPrefixSet(mode);
		
		for(String pathPre : pathPres) {
			winPathPres.add(StandUtils.toWinPath(pathPre));
		}
		return winPathPres;
	}
	
	/**
	 * 受路径前缀模式影响：
	 * 获取路径前缀集(路径不以分隔符结尾).集合按路径长度从大到小排序.
	 * 会根据当前路径树实时构造路径前缀集,路径格式符合linux的标准.
	 * 
	 * @param pathPrefixMode 路径前缀模式
	 * @return 路径前缀集(路径不以分隔符结尾).集合按路径长度从大到小排序.
	 */
	public List<String> getLinuxPathPrefixSet(CmpPathMode mode) {
		List<String> linuxPathPres = new ArrayList<String>();
		List<String> pathPres = getPathPrefixSet(mode);
		
		for(String pathPre : pathPres) {
			linuxPathPres.add(StandUtils.toLinuxPath(pathPre));
		}
		return linuxPathPres;
	}
	
	/**
	 * 受路径前缀模式影响：
	 * 获取路径前缀集(路径不以分隔符结尾).集合按路径长度从大到小排序.
	 * 会根据当前路径树实时构造路径前缀集,路径格式符合当前运行平台的标准.
	 * 
	 * 路径前缀模式：
	 * PRE_MODE_LEAST：提取尽可能少的路径前缀：各路径中相同的节点至少出现2次以上才会被提取前缀，子前缀压缩。
	 * PRE_MODE_STAND：提取标准数量的路径前缀：路径中同层同名的节点至少出现2次以上才会被提取前缀，相同前缀压缩。
	 * PRE_MODE_MOST：提取尽可能多的路径前缀：所有路径都会被提取前缀，相同前缀压缩。
	 * 
	 * --------------------------------------------
	 * 模式1不会存在冗余路径前缀，但是灵活性不高。
	 * 模式2、3存在不同程度的冗余性，但灵活性相对模式1较高。
	 * 
	 * --------------------------------------------
	 * 大前提：
	 * 	路径中除外叶子节点外，至少存在两层才会被提取前缀，如 D:\jep.jar 这种路径是没有前缀的。
	 * 
	 * 例如有4条路径：
	 * 	D:\mavenRepository\org\apache\maven\maven-monitor\maven-core-2.0.6.jar
	 * 	D:\mavenRepository\org\apache\maven\maven-monitor\maven-monitor-2.0.6.jar
	 * 	D:\mavenRepository\org\apache\maven\maven-artifact\maven-artifact-2.0.6.jar
	 * 	D:\commonLib\j2se\catt\1.1.1.0\catt-utils.jar
	 * 
	 * (1) 在模式1下：
	 * 	提到到的路径前缀为：
	 * 		D:\mavenRepository\org\apache\maven\maven-monitor
	 * 		D:\mavenRepository\org\apache\maven
	 * 	由于子前缀会被压缩，最终得到的路径前缀只有：
	 * 		D:\mavenRepository\org\apache\maven
	 * 
	 * (2) 在模式2下：
	 * 	提到到的路径前缀为：
	 * 		D:\mavenRepository\org\apache\maven\maven-monitor
	 * 		D:\mavenRepository\org\apache\maven
	 * 	虽然相同前缀会被压缩，但不存在相同的前缀，最终得到的路径前缀有：
	 * 		D:\mavenRepository\org\apache\maven\maven-monitor
	 * 		D:\mavenRepository\org\apache\maven
	 * 
	 * (3) 在模式3下：
	 * 	所有路径都会被提取前缀：
	 * 		D:\mavenRepository\org\apache\maven\maven-monitor
	 * 		D:\mavenRepository\org\apache\maven\maven-monitor
	 * 		D:\mavenRepository\org\apache\maven\maven-artifact
	 * 		D:\commonLib\j2se\catt\1.1.1.0
	 * 	由于相同的前缀会被压缩，最终得到的路径前缀有：
	 * 		D:\mavenRepository\org\apache\maven\maven-monitor
	 * 		D:\mavenRepository\org\apache\maven\maven-artifact
	 * 		D:\commonLib\j2se\catt\1.1.1.0
	 * 
	 * @param pathPrefixMode 路径前缀模式
	 * @return 路径前缀集(路径不以分隔符结尾).集合按路径长度从大到小排序.
	 */
	public List<String> getPathPrefixSet(CmpPathMode mode) {
		List<String> prePaths = null;
		
		if(CmpPathMode.LEAST == mode) {
			prePaths = getPathPrefixByMode1();
			
		} else if(CmpPathMode.MOST == mode) {
			prePaths = getPathPrefixByMode3();
			
		} else {
			prePaths = getPathPrefixByMode2();
		}
		Collections.sort(prePaths, new StrLenSort());	//按长度倒序排序
		return prePaths;
	}
	
	/**
	 * 获取路径前缀集(路径不以分隔符结尾).集合按路径长度从大到小排序.
	 * 会根据当前路径树实时构造路径前缀集,路径格式符合当前运行平台的标准.
	 * 
	 * 模式1 算法：
	 * 1、前提： 
	 * 	(1) 上层节点的压缩次数必定 >= 下层节点的压缩次数；
	 * 	(2) 叶子节点的压缩次数必定 = 1.
	 * 
	 * 2、步骤(前3步实际上就是模式2)：
	 * 	(1) 迭代每一个叶子节点，从叶子开始往上回溯；
	 * 	(2) 回溯时检查自身节点的压缩次数,只要压缩次数 > 1,则标记为 起始节点；
	 * 	(3) 从起始节点(包括)到根节点(除外)进行回溯拼接,只要得到的路径的拼接次数 > 1(即路径层数至少为2),
	 * 		则作为[候选路径前缀],放入[候选路径前缀集]；
	 * 	(4) 迭代[候选路径前缀集],找到长度最短的一条路径前缀；
	 * 	(5) 用这条最短的路径前缀,比对[候选路径前缀集]的其他路径前缀,
	 * 		若是某条路径前缀的子串,则从[候选路径前缀集]删除那条路径前缀；
	 * 	(6) 把这条最短的路径前缀放入[路径前缀集],重复(4),直到[候选路径前缀集]为空。
	 * 
	 * @return 路径前缀集(路径不以分隔符结尾)
	 */
	private List<String> getPathPrefixByMode1() {
		List<String> mbPrePaths = getPathPrefixByMode2();	//候选路径前缀集
		List<String> prePaths = new ArrayList<String>();	//路径前缀集
		
		// 构造路径前缀集（压缩子前缀）
		while(mbPrePaths.isEmpty() == false) {
			int minLen = Integer.MAX_VALUE;
			String minPath = "";
			
			for(String path : mbPrePaths) {
				int len = path.length();
				if(minLen > len) {
					minLen = len;
					minPath = path;
				}
			}
			
			mbPrePaths.remove(minPath);
			prePaths.add(minPath);
			
			for(Iterator<String> pathIts = mbPrePaths.iterator();
					pathIts.hasNext();) {
				String path = pathIts.next();
				if(path.startsWith(minPath)) {
					pathIts.remove();
				}
			}
		}
		return prePaths;
	}
	
	/**
	 * 获取路径前缀集(路径不以分隔符结尾).集合按路径长度从大到小排序.
	 * 会根据当前路径树实时构造路径前缀集,路径格式符合当前运行平台的标准.
	 * 
	 * 模式2 算法：
	 * 1、前提： 
	 * 	(1) 上层节点的压缩次数必定 >= 下层节点的压缩次数；
	 * 	(2) 叶子节点的压缩次数必定 = 1.
	 * 
	 * 2、步骤：
	 * 	(1) 迭代每一个叶子节点，从叶子开始往上回溯；
	 * 	(2) 回溯时检查自身节点的压缩次数,只要压缩次数 > 1,则标记为 起始节点；
	 * 	(3) 从起始节点(包括)到根节点(除外)进行回溯拼接,只要得到的路径的拼接次数 > 1(即路径层数至少为2),
	 * 		则放入[路径前缀集](利用set集的唯一特性可顺便压缩)；
	 * 
	 * @return 路径前缀集(路径不以分隔符结尾)
	 */
	private List<String> getPathPrefixByMode2() {
		Set<String> prePaths = new HashSet<String>();	//路径前缀集
		
		for(PathNode node : nodes) {
			if(node.isLeaf()) {
				String prePath = "";	//从起始节点到根节点之间的路径前缀
				int cnt = 0;			//回溯次数(即路径层数)
				
				//回溯到根节点
				for(PathNode fNode = node.getFNode(); 
						fNode != null && fNode.getLevel() != -1;
						fNode = fNode.getFNode()) {
					
					//跳过叶子节点前面所有没压缩的节点
					if(fNode.getCompress() <= 1) {
						continue;
					}
					
					cnt++;
					prePath = fNode.getName() + File.separator + prePath;
				}
				
				if(cnt > 1 && !"".equals(prePath)) {
					prePaths.add(prePath.substring(0, 
							prePath.length() - 1));	//去除结尾分隔符;
				}
			}
		}
		return new ArrayList<String>(prePaths);
	}
	
	/**
	 * 获取路径前缀集(路径不以分隔符结尾).集合按路径长度从大到小排序.
	 * 会根据当前路径树实时构造路径前缀集,路径格式符合当前运行平台的标准.
	 * 
	 * 模式3 算法：
	 * 1、前提： 
	 * 	(1) 上层节点的压缩次数必定 >= 下层节点的压缩次数；
	 * 	(2) 叶子节点的压缩次数必定 = 1.
	 * 
	 * 2、步骤：
	 * 	(1) 迭代每一个叶子节点，从叶子开始往上回溯；
	 * 	(2) 回溯时检查自身是否为叶子,只要不是叶子,则标记为 起始节点；
	 * 	(3) 从起始节点(包括)到根节点(除外)进行回溯拼接,只要得到的路径的拼接次数 > 1(即路径层数至少为2),
	 * 		则放入[路径前缀集](利用set集的唯一特性可顺便压缩)；
	 * 
	 * @return 路径前缀集(路径不以分隔符结尾)
	 */
	private List<String> getPathPrefixByMode3() {
		Set<String> prePaths = new HashSet<String>();	//路径前缀集
		
		for(PathNode node : nodes) {
			if(node.isLeaf()) {
				String prePath = "";	//从起始节点到根节点之间的路径前缀
				int cnt = 0;			//回溯次数(即路径层数)
				
				//回溯到根节点
				for(PathNode fNode = node.getFNode(); 
						fNode != null && fNode.getLevel() != -1;
						fNode = fNode.getFNode()) {
					cnt++;
					prePath = fNode.getName() + File.separator + prePath;
				}
				
				if(cnt > 1 && !"".equals(prePath)) {
					prePaths.add(prePath.substring(0, 
							prePath.length() - 1));	//去除结尾分隔符;
				}
			}
		}
		return new ArrayList<String>(prePaths);
	}

	/**
	 * 打印节点树
	 * @return
	 */
	public String toPrintTree() {
		StringBuilder sb = new StringBuilder();
		sb.append("Path Tree [").append(getName()).append("]:\r\n");
		for(PathNode node : nodes) {
			sb.append('\t').append(node.toString()).append("\r\n");
		}
		sb.append("----------\r\n");
		return sb.toString();
	}

	@Override
	public String toString() {
		return toPrintTree();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * <PRE>
	 * 字符串长度排序器(降序,即从最长到最短)。
	 * </PRE>
	 * <B>项    目：</B>凯通J2SE开发平台(KTJSDP)
	 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2014
	 * @version   1.0 2014-9-16
	 * @author    廖权斌：liaoquanbin@gdcattsoft.com
	 * @since     jdk版本：jdk1.6
	 */
	private class StrLenSort implements Comparator<String> {

		@Override
		public int compare(String strA, String strB) {
			int lenA = (strA == null ? 0 : strA.length());
			int lenB = (strB == null ? 0 : strB.length());
			return lenB - lenA;
		}
		
	}
}
