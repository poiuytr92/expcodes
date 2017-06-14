package exp.libs.algorithm.np.ispa.test;

import exp.libs.algorithm.np.ispa.ISPA;
import exp.libs.algorithm.np.ispa.ISPARst;
import exp.libs.algorithm.struct.graph.TopoGraph;
import exp.libs.algorithm.struct.graph.ui.TopoGraphUI;
import exp.libs.utils.ui.BeautyEyeUtils;

public class TestISPA {

	public static void main(String[] args) {
		TopoGraph graph = toGraph();
		draw(graph);
		ISPARst rst = ISPA.solve(graph);
		System.out.println(rst.toString());
	}
	
	private static TopoGraph toGraph() {
		TopoGraph graph = new TopoGraph(true);
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
		graph.setInclude("C");
		graph.setInclude("G");
		
		graph.setInclude("K");
		graph.setInclude("I");
		
		graph.setInclude("B");
		graph.setInclude("D");
		graph.setInclude("E");
		graph.setInclude("H");
		graph.setInclude("J");
		return graph;
	}
	
	private static void draw(TopoGraph graph) {
		BeautyEyeUtils.init();
		TopoGraphUI ui = new TopoGraphUI("拓扑图展示器", 700, 300, graph);
		ui._view();
	}
	
}
