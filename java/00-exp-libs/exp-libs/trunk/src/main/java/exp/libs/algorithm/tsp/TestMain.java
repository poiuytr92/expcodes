package exp.libs.algorithm.tsp;

import exp.libs.algorithm.tsp.graph.TopoGraph;

/**
 * TODO:
 *  1、计算剩余必经点到源宿点的SP（移除可替代环）
 *  2、压缩图（判断是否存在哈密顿通路）
 *  3、计算压缩图的哈密顿通路
 *  4、路径重整（排序）
 */
// 搜索起点只为源宿点
// 死路补边：若存在非源宿且度为1的节点X， 则找到其最近的度>2的级联邻居Y的邻居集Zs， 求X到Zs不经Y的其他最短路
// 特殊条件： 当下一节点是 源宿点时， 若还存在其他未通过的节点，且那些节点中存在必经点，则不允许选择这个节点;否则得到一个解
public class TestMain {

	public static void main(String[] args) {
		TopoGraph graph = toOrgGrapth();
		TSP.solve(graph);
	}
	
	private static TopoGraph toOrgGrapth() {
		TopoGraph graph = new TopoGraph();
		graph.setSrc("A"); 
		graph.setSnk("F");
		graph.addEdge("A", "B", 2);
		graph.addEdge("B", "C", 2);
		graph.addEdge("C", "D", 1);
		graph.addEdge("D", "E", 1);
		graph.addEdge("E", "F", 2);
		graph.addEdge("C", "G", 2);
		graph.addEdge("G", "E", 2);
		graph.addEdge("C", "G", 3);
		graph.addEdge("G", "J", 1);
		graph.addEdge("J", "E", 2);
		graph.addEdge("G", "I", 3);
		graph.addEdge("I", "J", 2);
		graph.addEdge("I", "K", 2);
		graph.addEdge("B", "K", 3);
		graph.addEdge("K", "F", 3);
		graph.addInclude("C");
		graph.addInclude("G");
		
		graph.addInclude("K");
		graph.addInclude("I");
		return graph;
	}
	
}
