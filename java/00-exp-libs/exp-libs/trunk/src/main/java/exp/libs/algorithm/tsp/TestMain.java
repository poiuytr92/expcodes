package exp.libs.algorithm.tsp;

import exp.libs.algorithm.tsp.graph.TopoGraph;
import exp.libs.algorithm.tsp.ui.TopoGraphUI;
import exp.libs.utils.ui.BeautyEyeUtils;


public class TestMain {

	public static void main(String[] args) {
		BeautyEyeUtils.init();
		TopoGraph graph = new TopoGraph(false);
		graph.setSrc("9527");
		graph.setSnk("FFFFFF");
		graph.addEdge("9527", "BB", 2);
		graph.addEdge("BB", "CCC", 2);
		graph.addEdge("CCC", "DDDD", 1);
		graph.addEdge("DDDD", "EEEEE", 1);
		graph.addEdge("EEEEE", "FFFFFF", 2);
		graph.addEdge("CCC", "GGGGGGG", 2);
		graph.addEdge("GGGGGGG", "EEEEE", 2);
		graph.addEdge("CCC", "HHHHHHHH", 3);
		graph.addEdge("HHHHHHHH", "J", 1);
		graph.addEdge("J", "EEEEE", 2);
		graph.addEdge("HHHHHHHH", "~EMS=Huawei/U2000~ManagedElement=3145802", 3);
		graph.addEdge("~EMS=Huawei/U2000~ManagedElement=3145802", "J", 2);
		graph.addEdge("~EMS=Huawei/U2000~ManagedElement=3145802", "K", 2);
		graph.addEdge("BB", "K", 3);
		graph.addEdge("K", "FFFFFF", 3);
		
		TopoGraphUI ui = new TopoGraphUI("拓扑图展示器", 1366, 700, graph);
		ui._view();
	}
	
}
