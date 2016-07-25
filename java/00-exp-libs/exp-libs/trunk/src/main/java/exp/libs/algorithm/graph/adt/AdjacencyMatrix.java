package exp.libs.algorithm.graph.adt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <PRE>
 * 邻接矩阵
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2016-07-25
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class AdjacencyMatrix {

	/** 节点索引 */
	private int nodeId;
	
	/** 相关节点表 */
	private Map<Node, Set<Node>> nodes;
	
	/**
	 * 邻接矩阵.
	 * 	matrix[i][j]: 表示从节点i到节点j的权重
	 */
	private Double[][] matrix;
	
	/** 邻接矩阵大小: 即节点数 */
	private int size;
	
	/** 是否为无向图（反之为有向图） */
	private boolean undirected;
	
	/**
	 * 
	 * @param edges 边集
	 * @param undirected 是否为无向图
	 */
	public AdjacencyMatrix(List<Edge> edges, boolean undirected) {
		this.nodes = new HashMap<Node, Set<Node>>();
		this.undirected = undirected;
		
		generateMatrix(edges);
	}
	
	/**
	 * 生成邻接矩阵
	 */
	private void generateMatrix(List<Edge> edges) {
		if(edges == null) {
			return;
		}
		
		// 构造节点索引
		for(Edge edge : edges) {
			Node src = edge.getSrc();
			Node end = edge.getEnd();
			if(src == null || end == null) {
				continue;
			}
			
			if(src.getId() == null) {
				src.setId(nodeId++);
			}
			
			if(end.getId() == null) {
				end.setId(nodeId++);
			}
			
			addRelations(src, end);
			if(undirected) {
				addRelations(end, src);
			}
		}
		
		// 生成邻接矩阵
		this.size = nodes.size();
		this.matrix = new Double[size][size];
		for(Edge edge : edges) {
			Node src = edge.getSrc();
			Node end = edge.getEnd();
			matrix[src.getId()][end.getId()] = edge.getWeight();
			
			if(undirected) {
				matrix[end.getId()][src.getId()] = edge.getWeight();
			}
		}
	}
	
	private void addRelations(Node node, Node relateNode) {
		Set<Node> relations = nodes.get(node);
		if(relations == null) {
			relations = new HashSet<Node>();
		}
		
		relations.add(relateNode);
		nodes.put(node, relations);
	}

	public int getNodeNum() {
		return size;
	}
	
	public Double getWeight(int srcId, int endId) {
		Double weight = null;
		if(inRange(srcId, endId)) {
			weight = matrix[srcId][endId];
		}
		return weight;
	}
	
	public void setWeight(int srcId, int endId, Double weight) {
		if(inRange(srcId, endId)) {
			matrix[srcId][endId] = weight;
		}
	}
	
	private boolean inRange(int srcId, int endId) {
		return (matrix != null && 
				srcId >= 0 && srcId < size && 
				endId >= 0 && endId < size);
	}
	
	/**
	 * 是否为孤立节点
	 * @param node
	 * @return
	 */
	public boolean isolated(Node node) {
		boolean isolated = true;
		if(node != null) {
			isolated = (nodes.get(node.getName()) == null);
		}
		return isolated;
	}
	
	public Set<Node> getRelations(Node node) {
		return nodes.get(node);
	}
	
	public boolean isUndirected() {
		return undirected;
	}

	// FIXME
	public void print() {
		if(matrix == null) {
			return;
		}
		
		for(int s = 0; s < size; s++) {
			for(int e = 0; e < size; e++) {
				System.out.println(s + "->" + e + " : " + matrix[s][e]);
			}
		}
	}
	
}
