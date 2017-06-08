package exp.libs.algorithm.tsp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import exp.libs.algorithm.tsp.graph.Node;
import exp.libs.algorithm.tsp.graph.TopoGraph;
import exp.libs.algorithm.tsp.qant.bean.QACA;
import exp.libs.algorithm.tsp.spa.Dijkstra;
import exp.libs.algorithm.tsp.ui.TopoGraphUI;
import exp.libs.utils.ui.BeautyEyeUtils;

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
		Dijkstra dij = new Dijkstra(graph.getAdjacencyMatrix());
//		draw(graph);
		
		// 把源宿点也作为必经点
		List<Node> includes = graph.getIncludes();
		includes.add(0, graph.getSrc());
		includes.add(includes.size(), graph.getSnk());
		
		// 计算任意两点间的最短路径, 构造子图
		TopoGraph subGraph = new TopoGraph(false);
		int size = includes.size();
		for(int i = 0; i < size; i++) {
			Node x = includes.get(i);
			dij.calculate(x.getId());
			
			for(int j = i + 1; j < size; j++) {
				Node y = includes.get(j);
				List<Integer> xySP = dij.getShortPaths(y.getId());
				addEdges(subGraph, graph, xySP);
			}
		}
//		draw(subGraph);
		
		// 检查子图是否存在度数为1， 且非源宿点的节点, 对其补边
		boolean isOk = true;
		do {
			isOk = true;
			Set<Integer> tabu = getTabu(graph, subGraph);
			Set<Node> subNodes = subGraph.getAllNodes();
			for(Node subNode : subNodes) {
				if(subNode.getDegree() == 1 && 
						!graph.getSrc().getName().equals(subNode.getName()) && 
						!graph.getSnk().getName().equals(subNode.getName())) {
					isOk = false;
					
					// 重新构造子图(从度1节点K开始，依次断开K的邻居节点X到其所有邻居节点Ys的边，找到最小的代价的那一条并替换之)
					int minCost = Integer.MAX_VALUE;
					List<Integer> minSP = null;
					Node minNode = null;
					Node[] twoNodes = getNeighbor(subNode);
					Node nNode = twoNodes[0];
					Node tabuNode = twoNodes[1];
					List<Node> nodes = nNode.getNeighborList();
					for(Node node : nodes) {
						if(node.equals(tabuNode)) {
							continue;
						}
						int srcId = graph.getNode(subNode.getName()).getId();
						int snkId = graph.getNode(node.getName()).getId();
						Set<Integer> tmpTabu = new HashSet<Integer>(tabu);
						tmpTabu.remove(snkId);
						dij.calculate(srcId, tmpTabu);
						int cost = dij.getShortPathWeight(snkId);
						if(cost < minCost) {
							minCost = cost;
							minSP = dij.getShortPaths(snkId);
							minNode = node;
						}
					}
					
					if(minCost < Integer.MAX_VALUE) {
						for(int i = 0; i < minSP.size() - 1; i++) {
							Node src = graph.getNode(minSP.get(i));
							Node snk = graph.getNode(minSP.get(i + 1));
							int weight = graph.getWeight(src, snk);
							subGraph.addEdge(src.getName(), snk.getName(), weight);
						}
					} else {
						System.out.println("此必经点组合无解");
						isOk = true;
					}
					break;
				}
			}
		} while(isOk == false);
		draw(subGraph);
		
		subGraph.setSrc(graph.getSrc().getName());
		subGraph.setSnk(graph.getSnk().getName());
		int nCity = subGraph.nodeSize();
		subGraph.setAdjacencyMatrix();
		int[][] matrix = subGraph.getAdjacencyMatrix();
		QACA fun = new QACA(nCity, 
				subGraph.getSrc().getId(), subGraph.getSnk().getId());
		fun.initRoom();	//初始化内存空间
		fun.initPath(matrix);	//初始化路径信息
		fun.initQAntGroup();	//初始化量子蚂蚁种群
		fun.runQAnt();	//运行量子蚁群算法求解
		fun.printBestSolution();	//打印最优解
	}
	
	/**
	 * 
	 * @param node 度为1
	 * @return
	 */
	private static Node[] getNeighbor(Node node) {
		Node[] rst = null;
		do {
			Node neighbor = node.getNeighborList().get(0);
			if(neighbor.getDegree() > 2) {
				rst = new Node[] { neighbor, node };
				break;
			}
			node = neighbor;
		} while(true);
		return rst;
	}
	
	private static Set<Integer> getTabu(TopoGraph graph, TopoGraph subGraph) {
		Set<Integer> tabuIds = new HashSet<Integer>();
		Set<Node> subNodes = subGraph.getAllNodes();
		for(Node node : subNodes) {
			tabuIds.add(graph.getNode(node.getName()).getId());
		}
		return tabuIds;
	}
	
	private static void addEdges(TopoGraph subGraph, TopoGraph graph, List<Integer> sp) {
		int size = sp.size();
		for(int i = 0; i < size - 1; i++) {
			Node src = graph.getNode(sp.get(i));
			Node snk = graph.getNode(sp.get(i + 1));
			int weight = graph.getWeight(src, snk);
			subGraph.addEdge(src.getName(), snk.getName(), weight);
		}
	}
	
	private static TopoGraph toOrgGrapth() {
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
		graph.setInclude("C");
//		graph.setInclude("G");
		
		graph.setInclude("K");
		graph.setInclude("I");
		graph.setAdjacencyMatrix();
		return graph;
	}
	
	private static void draw(TopoGraph graph) {
		BeautyEyeUtils.init();
		TopoGraphUI ui = new TopoGraphUI("拓扑图展示器", 500, 300, graph);
		ui._view();
	}
	
	
}
