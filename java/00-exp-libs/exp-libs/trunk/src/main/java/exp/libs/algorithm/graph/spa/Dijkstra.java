package exp.libs.algorithm.graph.spa;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import exp.libs.algorithm.graph.adt.AdjacencyMatrix;
import exp.libs.algorithm.graph.adt.Edge;
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

	/** 源节点 */
	private Node src;
	
	/** 源节点到各个节点的最短路所经过的中转节点 */
	private Object[] paths;
	
	/** 源节点到各个节点的最短路权重和 */
	private Double[] dist;
	
	public Dijkstra(AdjacencyMatrix matrix) {
		super(matrix);
	}
	
	public void exec(String srcNodeName) {
		exec(new Node(srcNodeName));
	}

	public void exec(Node src) {	// FIXME: 给名字
		if(matrix == null || src == null) {
			return;
		}
		
		src = matrix.findNode(src.getName());
		this.src = src;
		if(src == null) {
			return;
		}
		
		int n = matrix.getNodeNum();
		boolean[] visiteds = new boolean[n];
		visiteds[src.getId()] = true;
		
		this.dist = new Double[n];
		for(int i = 0; i < matrix.getNodeNum(); i++) {
			dist[i] = matrix.getWeight(src.getId(), i);
		}
		
		// FIXME: 无需全图迭代，迭代关联节点即可(结合斐波那契堆？)
		this.paths = new Object[n];
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
			
			// 把未访问但与id（新源点）连通的点进行松弛  
			visiteds[id] = true;
			for(int j = 1; j < n; j++) { 
				Double relax = add(dist[id], matrix.getWeight(id, j));	// 松弛值
	            if(!visiteds[j] && // 未访问的
	            		matrix.getWeight(id, j) != null && // 连通的
	            		compare(dist[j], relax) == GT) {
	            	dist[j] = relax;
	            	
	            	if(paths[j] == null) {
	            		paths[j] = new ArrayList<Integer>();
	            	}
	            	((ArrayList<Integer>) paths[j]).add(id);
	            }
	        } 
		}
	}
	
	public List<Node> getShortestPath(String endNodeName) {
		return getShortestPath(new Node(endNodeName));
	}
	
	public List<Node> getShortestPath(Node end) {	// FIXME: 给名字
		List<Node> nodes = new LinkedList<Node>();
		if(src == null || end == null || paths == null || matrix == null) {
			return nodes;
		}
		
		end = matrix.findNode(end.getName());
		if(end == null) {
			return nodes;
		}
		
		// 回溯最短路径
		nodes.add(0, end);
		int endId = end.getId();
		do {
			Object oPath = paths[endId];
			if(oPath != null) {
				ArrayList<Integer> path = (ArrayList<Integer>) oPath;
				for(int i = path.size() - 1; i >= 0; i--) {	// FIXME
					int id = path.get(i);
					nodes.add(0, matrix.findNode(id));
					endId = id;
				}
			} else {
				break;
			}
		} while(true);
		nodes.add(0, src);
		return nodes;
	}
	
	public Double getShortestWeight(String endNodeName) {
		return getShortestWeight(new Node(endNodeName));
	}
	
	public Double getShortestWeight(Node end) {
		Double weight = null;
		if(end == null || dist == null || matrix == null) {
			return weight;
		}
		
		end = matrix.findNode(end.getName());
		if(end == null) {
			return weight;
		}
		
		weight = dist[end.getId()];
		return weight;
	}
	
	public static void main(String[] args) {
		Node n0 = new Node("Apple");
		Node n1 = new Node("Boy");
		Node n2 = new Node("Cat");
		Node n3 = new Node("Dog");
		Node n4 = new Node("Egg");
		
		Edge e01 = new Edge(n0, n1, 1D);
		Edge e02 = new Edge(n0, n2, 2D);
		Edge e03 = new Edge(n0, n3, 3D);
		Edge e12 = new Edge(n1, n2, 1D);
		Edge e23 = new Edge(n2, n3, 1D);
		Edge e24 = new Edge(n2, n4, 1D);
		Edge e14 = new Edge(n1, n4, 3D);
		Edge e34 = new Edge(n3, n4, 5D);
		Edge e44 = new Edge(n4, n4, 2D);
		
		List<Edge> edges = new LinkedList<Edge>();
		edges.add(e01);
		edges.add(e02);
		edges.add(e03);
		edges.add(e12);
		edges.add(e23);
		edges.add(e24);
		edges.add(e14);
		edges.add(e34);
		edges.add(e44);
		
		AdjacencyMatrix m = new AdjacencyMatrix(edges, true);	// FIXME:可缓存?
		Dijkstra dijkstra = new Dijkstra(m);
		dijkstra.exec(n0);
		
		List<Node> nodes = dijkstra.getShortestPath(n4);
		for(Node node : nodes) {
			System.out.print(node.getName() + "(" + node.getId() + ")->");
		}
		System.out.println(dijkstra.getShortestWeight(n4));
	}
	
}
