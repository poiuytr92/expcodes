package exp.libs.algorithm.tsp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import exp.libs.algorithm.tsp.graph.Node;
import exp.libs.algorithm.tsp.graph.TopoGraph;
import exp.libs.algorithm.tsp.spa.Dijkstra;
import exp.libs.algorithm.tsp.ui.TopoGraphUI;
import exp.libs.utils.ui.BeautyEyeUtils;


public class TestMain {

	public static void main(String[] args) {
		TopoGraph graph = toTestGrapth();
//		draw(graph);
		
		Dijkstra dij = new Dijkstra(graph.getAdjacencyMatrix());
		
		/**
		 * TODO:
		 *  1、计算剩余必经点到源宿点的SP（移除可替代环）
		 *  2、压缩图（判断是否存在哈密顿通路）
		 *  3、计算压缩图的哈密顿通路
		 *  4、路径重整（排序）
		 */
		
		Set<Integer> cmpGraphIDs = new HashSet<Integer>();
		
		List<Node> includes = graph.getIncludes();
		includes.add(0, graph.getSrc());
		includes.add(includes.size(), graph.getSnk());
		System.out.println(includes);
		
		for(int i = 0; i < includes.size() - 1; i++) {
			Node src = includes.get(i);
			Node snk = includes.get(i + 1);
			dij.calculate(src.getId(), cmpGraphIDs);
			List<Integer> tmp = dij.getShortPaths(snk.getId());
			System.out.println(tmp);
			
			cmpGraphIDs.addAll(tmp);
		}
		
		System.out.println(cmpGraphIDs);
	}
	
	private static TopoGraph toTestGrapth() {
		TopoGraph graph = new TopoGraph(false);
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
//		graph.setInclude("C");
//		graph.setInclude("E");
		
		graph.setInclude("K");
		graph.setInclude("I");
		graph.toAdjacencyMatrix();
		return graph;
	}
	
	private static void draw(TopoGraph graph) {
		BeautyEyeUtils.init();
		TopoGraphUI ui = new TopoGraphUI("拓扑图展示器", 1366, 700, graph);
		ui._view();
	}
	
	
}
