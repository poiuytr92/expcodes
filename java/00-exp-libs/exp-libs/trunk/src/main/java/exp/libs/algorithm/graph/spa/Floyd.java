package exp.libs.algorithm.graph.spa;

import java.util.LinkedList;
import java.util.List;

import exp.libs.algorithm.graph.adt.AdjacencyMatrix;
import exp.libs.algorithm.graph.adt.Edge;
import exp.libs.algorithm.graph.adt.Node;

/**
 * <PRE>
 * 最短路径算法: Floyd算法.
 * 适用范围: 多源最短路问题, 有向图/无向图均可, 无负权环（但可检测负权环）
 * 时间复杂度: O(V^3)
 * 空间复杂度: O(V^2)
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2016-07-25
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Floyd {
	
	private AdjacencyMatrix matrix;

	public Floyd(AdjacencyMatrix matrix) {
		this.matrix = matrix;
	}
	
	public void exec() {
		if(matrix == null) {
			return;
		}
		
		int n = matrix.getSize();
		for(int k = 0; k < n; k++) {
			for(int s = 0; s < n; s++) {	// FIXME: 无向图可以减半迭代
				for(int e = 0; e < n; e++) {
					if(s != e && s != k) {	// 去除环
						Double wSE = matrix.getWeight(s, e);
						Double wSK = matrix.getWeight(s, k);
						Double wKE = matrix.getWeight(k, e);
						
						if(compare(wSE, add(wSK, wKE)) == 1) {
							matrix.setWeight(s, e, add(wSK, wKE));
						}
					}
				}
			}
		}
	}
	
	private int compare(Double a, Double b) {
		if(a == null && b != null) {
			return 1;
			
		} else if(a != null && b == null) {
			return -1;
			
		} else if(a == null && b == null) {
			return 0;
			
		} else {
			double diff = a - b;
			return (diff > 0 ? 1 : (diff < 0 ? -1 : 0));
		}
	}
	
	private Double add(Double a, Double b) {
		if(a == null || b == null) {
			return null;
			
		} else {
			return (a + b);
		}
	}
	
	public Double getShortestPath(Node src, Node end) {
		return matrix.getWeight(src.getId(), end.getId());
	}
	
	public void print() {
		matrix.print();
	}
	
	public static void main(String[] args) {
		Node n0 = new Node("0");
		Node n1 = new Node("1");
		Node n2 = new Node("2");
		Node n3 = new Node("3");
		Node n4 = new Node("4");
		
		Edge e01 = new Edge(n0, n1, 1D);
		Edge e02 = new Edge(n0, n2, 2D);
		Edge e03 = new Edge(n0, n3, 3D);
		Edge e12 = new Edge(n1, n2, 1D);
		Edge e23 = new Edge(n2, n3, 1D);
		Edge e24 = new Edge(n2, n4, 1D);
		Edge e14 = new Edge(n1, n4, 3D);
		Edge e34 = new Edge(n3, n4, 5D);
		
		List<Edge> edges = new LinkedList<Edge>();
		edges.add(e01);
		edges.add(e02);
		edges.add(e03);
		edges.add(e12);
		edges.add(e23);
		edges.add(e24);
		edges.add(e14);
		edges.add(e34);
		
		AdjacencyMatrix m = new AdjacencyMatrix(edges, true);
		Floyd floyd = new Floyd(m);
		floyd.print();
		floyd.exec();
		
		System.out.println("==============");
		floyd.print();
	}
	
}
