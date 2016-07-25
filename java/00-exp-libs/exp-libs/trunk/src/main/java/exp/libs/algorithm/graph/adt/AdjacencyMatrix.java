package exp.libs.algorithm.graph.adt;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	private Map<Integer, Node> nodes;
	
	/** 边集 */
	private List<Edge> edges;
	
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
		this.nodes = new HashMap<Integer, Node>();
		this.edges = (edges != null ? edges : new LinkedList<Edge>());
		this.undirected = undirected;
		
		generateMatrix();
	}
	
	/**
	 * 生成邻接矩阵
	 */
	private void generateMatrix() {
		
		// 构造节点索引
		for(Edge edge : edges) {
			Node src = edge.getSrc();
			Node end = edge.getEnd();
			if(src == null || end == null) {
				continue;
			}
			
			nodes.put(src.getId(), src);
			nodes.put(end.getId(), end);
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

	public int getSize() {
		return size;
	}
	
	public Double getWeight(int srcId, int endId) {
		Double w = null;
		if(matrix != null && srcId >= 0 && srcId < size && 
				endId >= 0 && endId < size) {
			w = matrix[srcId][endId];
		}
		return w;
	}
	
	public void setWeight(int srcId, int endId, double weight) {
		if(matrix != null && srcId >= 0 && srcId < size && 
				endId >= 0 && endId < size) {
			matrix[srcId][endId] = weight;
		}
	}
	
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
