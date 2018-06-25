package exp.libs.algorithm.search;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * <PRE>
 * 搜素算法工具
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2017-08-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class SearchUtils {
	
	/**
	 * 递归深度上限�?
	 * 避免无限递归导致堆栈溢出�?
	 * 改为-1则不做深度限制�?
	 */
	private final static int DEPTH_LIMIT = 50;
	
	/**
	 * 在指定目录下执行 DFS 文件搜索，返回符合匹配条件的文件的绝对路径�?
	 * 
	 * @param fileNameRegex 所检索文件名的正则表达式，要求为完全匹配模式
	 * @param dirPath 所检索的起始目录
	 * @param isFirst 是否取第一个检索结果。否则返回最后一个检索结果�?
	 * @return 匹配文件的绝对路径。若无匹配则返回空串�?
	 */
	public static String dfs(String fileNameRegex, String dirPath, 
			boolean isFirst) {
		return search(fileNameRegex, dirPath, isFirst, !isFirst);
	}
	
	/**
	 * 在指定目录下执行 BFS 文件搜索，返回符合匹配条件的文件的绝对路径�?
	 * 
	 * @param fileNameRegex 所检索文件名的正则表达式，要求为完全匹配模式
	 * @param dirPath 所检索的起始目录
	 * @param isFirst 是否取第一个检索结果。否则返回最后一个检索结果�?
	 * @return 匹配文件的绝对路径。若无匹配则返回空串�?
	 */
	public static String bfs(String fileNameRegex, String dirPath, 
			boolean isFirst) {
		return search(fileNameRegex, dirPath, isFirst, isFirst);
	}
	
	/**
	 * 在指定目录下执行文件搜索，返回符合匹配条件的文件的绝对路径�?
	 * 根据参数选择�?4种返回可能：
	 * 
	 * isFirst = true, topPriority = true : BFS 第一�?
	 * isFirst = true, topPriority = false : DFS 第一�?
	 * isFirst = false, topPriority = true �?  DFS 最后一�?
	 * isFirst = false, topPriority = false : BFS 最后一�?
	 * 
	 * @param fileNameRegex 所检索文件名的正则表达式，要求为完全匹配模式
	 * @param dirPath 所检索的起始目录
	 * @param isFirst 是否取第一个检索结果。否则返回最后一个检索结果�?
	 * @param topPriority 是否上层目录的检索结果优先。当父目录和子目录同时存在匹配结果时据此筛选�?
	 * @return 匹配文件的绝对路径。若无匹配则返回空串�?
	 */
	private static String search(String fileNameRegex, String dirPath, 
			boolean isFirst, boolean topPriority) {
		String sPath = "";
		if(fileNameRegex != null && !"".equals(fileNameRegex)) {
			if(false == fileNameRegex.startsWith("^")) {
				fileNameRegex = "^" + fileNameRegex;
			}
			if(false == fileNameRegex.endsWith("$")) {
				fileNameRegex = fileNameRegex + "$";
			}
			
			sPath = search(fileNameRegex, dirPath, 
					isFirst, topPriority, DEPTH_LIMIT);
		}
		return sPath;
	}
	
	/**
	 * 在指定目录下执行文件搜索，返回符合匹配条件的文件的绝对路径�?
	 * 根据参数选择�?4种返回可能：
	 * 
	 * isFirst = true, topPriority = true : BFS 第一�?
	 * isFirst = true, topPriority = false : DFS 第一�?
	 * isFirst = false, topPriority = true �?  DFS 最后一�?
	 * isFirst = false, topPriority = false : BFS 最后一�?
	 * 
	 * @param fileNameRegex 所检索文件名的正则表达式，要求为完全匹配模式
	 * @param dirPath 所检索的起始目录
	 * @param isFirst 是否取第一个检索结果。否则返回最后一个检索结果�?
	 * @param topPriority 是否上层目录的检索结果优先。当父目录和子目录同时存在匹配结果时据此筛选�?
	 * @param depth 递归深度
	 * @return 匹配文件的绝对路径。若无匹配则返回空串�?
	 */
	private static String search(String fileNameRegex, String dirPath, 
			boolean isFirst, boolean topPriority, int depth) {
		//堆栈保护
		if(depth == 0) {
			return "";
		}
		
		String tFirstPath = "";	//本层首个检索到的路�?
		String tLastPath = "";	//本层最后检索到的路�?
		File dir = new File(dirPath);
		
		if(dir.exists() && dir.isDirectory()) {
			File[] files = dir.listFiles();
			
			//检索本�?
			for(File file : files) {
				if(file.exists() && !file.isDirectory()) {
					String tmpPath = file.getAbsolutePath();
					
					if(file.getName().matches(fileNameRegex)) {
						tFirstPath = 
								("".equals(tFirstPath) ? tmpPath : tFirstPath);
						tLastPath = 
								("".equals(tFirstPath) ? tFirstPath : tmpPath);
					}
				}
			}
			
			//当本层检索不到、或下层优先时，检索下�?
			if("".equals(tFirstPath) || topPriority == false) {
				String nFirstPath = "";	//下层首个检索到的路�?
				String nLastPath = "";	//下层最后检索到的路�?
				
				for(File file : files) {
					if(file.exists() && file.isDirectory()) {
						String tmpPath = search(fileNameRegex, 
								file.getAbsolutePath(), isFirst, 
								topPriority, depth - 1);
						
						if(!"".equals(tmpPath)) {
							if(isFirst == true) {
								nFirstPath = ("".equals(nFirstPath) ? 
										tmpPath : nFirstPath);
							} else {
								nLastPath = tmpPath;
							}
						}
					}
				}
				
				//当下层优先时，只要下层检索结果不为空就覆盖本�?
				if(topPriority == false) {
					tFirstPath = 
							("".equals(nFirstPath) ? tFirstPath : nFirstPath);
					tLastPath = ("".equals(nLastPath) ? tLastPath : nLastPath);
					
				//当本层优先时，只有本层检索结果为空才取下�?
				} else {
					tFirstPath = 
							("".equals(tFirstPath) ? nFirstPath : tFirstPath);
					tLastPath = ("".equals(tLastPath) ? nLastPath : tLastPath);
				}
			}
		}
		
		//根据需要返回第一个或最后一个结�?
		return (isFirst == true ? tFirstPath : tLastPath);
	}
	
	/**
	 * 检索指定目录下所有符合条件的文件,返回这些文件的绝对路�?.
	 * 
	 * @param fileNameRegex 所检索文件名的正则表达式，要求为完全匹配模式
	 * @param dirPath 检索目�?
	 * @return 所有被匹配的文件的绝对路径
	 */
	public static List<String> search(String fileNameRegex, String dirPath) {
		List<String> filePaths = new LinkedList<String>();
		if(fileNameRegex != null && !"".equals(fileNameRegex)) {
			if(false == fileNameRegex.startsWith("^")) {
				fileNameRegex = "^" + fileNameRegex;
			}
			if(false == fileNameRegex.endsWith("$")) {
				fileNameRegex = fileNameRegex + "$";
			}
			
			filePaths = search(fileNameRegex, dirPath, 
					filePaths, DEPTH_LIMIT);
		}
		return filePaths;
	}
	
	/**
	 * 检索指定目录下所有符合条件的文件,返回这些文件的绝对路�?.
	 * 
	 * @param fileNameRegex 所检索文件名的正则表达式，要求为完全匹配模式
	 * @param dirPath 检索目�?
	 * @param filePaths 存储被匹配文件路径的队列
	 * @param depth 递归深度
	 * @return 所有被匹配的文件的绝对路径
	 */
	private static List<String> search(String fileNameRegex, String dirPath, 
			List<String> filePaths, int depth) {
		//堆栈保护
		if(depth == 0) {
			return filePaths;
		}
		
		File dir = new File(dirPath);
		if(dir.exists() && dir.isDirectory()) {
			File[] files = dir.listFiles();
			
			for(File file : files) {
				if(file.isDirectory()) {
					filePaths = search(fileNameRegex, file.getAbsolutePath(), 
							filePaths, depth - 1);
				} else {
					if(file.getName().matches(fileNameRegex)) {
						filePaths.add(file.getAbsolutePath());
					}
				}
			}
		}
		return filePaths;
	}
	
}
