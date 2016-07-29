package exp.libs.algorithm.graph.adt;

import java.util.Iterator;
import java.util.List;

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

	/** 节点工厂：维护邻接矩阵的节点集 */
	private NodeFactory nodeFactory;
	
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
		this.nodeFactory = new NodeFactory();
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
		Iterator<Edge> edgeIts = edges.iterator();
		while(edgeIts.hasNext()) {
			Edge edge = edgeIts.next();
			Node src = edge.getSrc();
			Node end = edge.getEnd();
			if(src == null || end == null) {
				edgeIts.remove();
				continue;
			}
			
			edge.setSrc(nodeFactory.add(src));
			edge.setEnd(nodeFactory.add(end));
		}
		
		// 生成邻接矩阵
		this.size = nodeFactory.size();
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
	
	public Node findNode(String nodeName) {
		return nodeFactory.find(nodeName);
	}
	
	public Node findNode(int nodeId) {
		return nodeFactory.find(nodeId);
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
			isolated = !nodeFactory.contains(node.getId());
		}
		return isolated;
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
