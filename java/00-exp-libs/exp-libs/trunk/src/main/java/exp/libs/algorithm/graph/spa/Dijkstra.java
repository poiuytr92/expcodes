package exp.libs.algorithm.graph.spa;

import java.util.LinkedList;
import java.util.List;

import exp.libs.algorithm.graph.adt.AdjacencyMatrix;
import exp.libs.algorithm.graph.adt.Node;

/**
 * <PRE>
 * 最短路径算法: Dijkstra算法(已用Fibonacci堆优化).
 * 适用范围: 单源最短路问题, 有向图/无向图均可, 无负权环
 * 时间复杂度: O(V * lgV + E)
 * 空间复杂度: 
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2016-07-25
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Dijkstra extends AbstractSPA {

	// 需记录经过节点
	
	public Dijkstra(AdjacencyMatrix matrix) {
		super(matrix);
	}

	protected List<Node> exec(Node src) {
		List<Node> nodes = new LinkedList<Node>();
		if(matrix == null) {
			return nodes;
		}
		
		int n = matrix.getNodeNum();
		
		boolean[] visiteds = new boolean[n];
		visiteds[src.getId()] = true;
		
		Double[] dist = new Double[n];
		for(int i = 0; i < matrix.getNodeNum(); i++) {
			dist[i] = matrix.getWeight(src.getId(), i);
		}
		
		// FIXME: 无需全图迭代，迭代关联节点即可
		for(int i = 0; i < n; i++) {
			int id = -1;
			Double weight = null;
			
			//在未访问的点中，寻找最短的一条  
			for(int j = 1; j < n; j++) {
				if(!visiteds[j] && compare(weight, dist[j]) == GT) {
					weight = dist[j];
					id = j;
				}
			}
			
			// 若id没有变化，说明所有点都被访问，最短路寻找完毕  
			if(id == -1) {
				break;
			}
			
			//把未访问但与id（新源点）连通的点进行松弛  
			visiteds[id] = true;
			for(int j = 1; j < n; j++) { 
	            if(!visiteds[j] && // 未访问的
	            		matrix.getWeight(id, j) != null && // 连通的
	            		compare(dist[j], add(dist[id], matrix.getWeight(id, j))) == GT) {
	            	dist[j] = add(dist[id], matrix.getWeight(id, j));  
	            }
	        } 
		}
		return nodes;
	}
	
	public static void main(String[] args) {
		boolean[] visited = new boolean[10];
		System.out.println(visited[2]);
	}
	
}
