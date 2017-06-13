package exp.libs.algorithm.tsp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import exp.libs.algorithm.tsp.graph.Node;
import exp.libs.algorithm.tsp.graph.TopoGraph;
import exp.libs.algorithm.tsp.qant.QACA;
import exp.libs.algorithm.tsp.qant.QRst;
import exp.libs.algorithm.tsp.spa.Dijkstra;
import exp.libs.algorithm.tsp.ui.TopoGraphUI;
import exp.libs.utils.ui.BeautyEyeUtils;

/**
 * <PRE>
 * TSP变种： 求解含必经点的最短路径问题
 * </PRE>
 * 
 * @author lqb
 * @date 2017年6月13日
 */
public class TSP {

	private TSP() {}
	
	private static void draw(TopoGraph graph) {
		BeautyEyeUtils.init();
		TopoGraphUI ui = new TopoGraphUI("拓扑图展示器", 500, 300, graph);
		ui._view();
	}
	
	/**
	 * 求解
	 * @param graph 拓扑图
	 * @return
	 */
	public static TSPRst solve(final TopoGraph graph) {
		TSPRst rst = new TSPRst();
		if(graph == null || graph.isEmpty() || 
				graph.getSrc() == Node.NULL || graph.getSnk() == Node.NULL) {
			return rst;
		}
		
		draw(graph);
		
		// 不存在必经点， 直接用Dijkstra算法求源端到宿端的最短路
		if(!graph.existInclude()) {
			rst = solveBySPA(graph);
			
		// 必经点有序，使用Dijkstra算法按顺序分段求最短路， 然后拼接
		} else if(graph.isOrderInclude()) {
			rst = solveBySegmentSPA(graph);
			
		// 必经点无序, 构造必经点子图后，使用启发式算法求最小连通通道
		} else {
			rst = solveByHeuristicAlgorithm(graph);
		}
		return rst;
	}
	
	/**
	 * 无必经点的最短路问题， 使用最短路算法求解
	 * @param graph
	 * @return
	 */
	private static TSPRst solveBySPA(final TopoGraph graph) {
		Dijkstra dijkstra = new Dijkstra(graph.getAdjacencyMatrix());
		dijkstra.calculate(graph.getSrc().getId());
		List<Integer> routes = dijkstra.getShortPaths(graph.getSnk().getId());
		// TODO
		return null;
	}
	
	/**
	 * 含有序必经点的最短路问题，使用分段最短路算法求解
	 * @param graph
	 * @return
	 */
	private static TSPRst solveBySegmentSPA(final TopoGraph graph) {
		List<Node> includes = graph.getIncludes();
		includes.add(0, graph.getSrc());
		includes.add(includes.size(), graph.getSnk());
		
		// TODO  若结果不存在环则有解, 否则标示哪一段断开 或 存在环
		return null;
	}
	
	/**
	 * 含无需必经点的最短路问题， 使用启发式算法求解
	 * @param graph
	 * @return
	 */
	private static TSPRst solveByHeuristicAlgorithm(final TopoGraph graph) {
		Dijkstra dijkstra = new Dijkstra(graph.getAdjacencyMatrix());
		
		// 压缩图: 计算必经点集（包括源宿点）中的任意两点的最短路, 合并所有相关路径, 得到压缩子图
		TopoGraph subGraph = _compressGraph(graph, dijkstra);
		
		// 子图补边： 对于子图中度数为1、 且非源宿点的节点, 对其增加一条最短回路, 连接到到最近的一个度大于2的节点
		_fillEdges(subGraph, graph, dijkstra);
		draw(subGraph);
		
		// 求子图的哈密顿通路： 最坏的情况是过所有节点， 最好的情况是只过必经点
		QACA qaca = new QACA(subGraph.getAdjacencyMatrix(), 
				subGraph.getSrc().getId(), 
				subGraph.getSnk().getId(), 
				subGraph.getIncludeIds(), 10, 10, true); 
		QRst qRst = qaca.exec();
		System.out.println(qaca.toRstInfo());
		
		// TODO: 转换子图解为原图解（节点ID不同）
		return null;
	}
	
	/**
	 * 压缩图: 计算原图的必经点集（包括源宿点）中的任意两点的最短路, 合并所有相关路径, 得到压缩子图
	 * @param graph 原图
	 * @param dijkstra 根据原图构造的dijkstra算法对象
	 * @return 压缩后的子图（节点名称不变，但节点编号可能发生变化）
	 */
	private static TopoGraph _compressGraph(
			final TopoGraph graph, final Dijkstra dijkstra) {
		
		// 把源宿点作为必经点
		List<Node> includes = graph.getIncludes();
		includes.add(0, graph.getSrc());
		includes.add(includes.size(), graph.getSnk());
		
		// 计算两两必经点间的最短路，构造子图
		TopoGraph subGraph = new TopoGraph();
		for(int size = includes.size(), i = 0; i < size - 1; i++) {
			Node a = includes.get(i);
			dijkstra.calculate(a.getId());
			
			for(int j = i + 1; j < size; j++) {
				Node z = includes.get(j);
				List<Integer> azRoutes = dijkstra.getShortPaths(z.getId());
				__addEdges(subGraph, graph, azRoutes);
			}
		}
		
		// 设置子图的源宿点和必经点
		subGraph.setSrc(graph.getSrc().getName());
		subGraph.setSnk(graph.getSnk().getName());
		subGraph.addIncludes(graph.getIncludeNames());
		return subGraph;
	}
	
	/**
	 * 把原图中某一段路由的相关边添加到子图中
	 * @param subGraph 子图
	 * @param graph 原图
	 * @param routes 原图中的一段路由
	 */
	private static void __addEdges(final TopoGraph subGraph, 
			final TopoGraph graph, final List<Integer> routes) {
		for(int size = routes.size(), i = 0; i < size - 1; i++) {
			Node src = graph.getNode(routes.get(i));
			Node snk = graph.getNode(routes.get(i + 1));
			int weight = graph.getWeight(src, snk);
			subGraph.addEdge(src.getName(), snk.getName(), weight);
		}
	}
	
	/**
	 * 子图死路补边：
	 *  若存在非源宿、且度为1的节点X， 则先找到它的一个的邻居节点Y(度大于2, 可以是级联邻居), 
	 *    (若Y的度<2, 说明子图不连通, 但根据上下文这是不可能的; 
	 *     若Y的度=2, 说明X与Y同属一个死路支路， 则需要找下一个级联邻居Y)
	 *  对于Y的邻居节点集Zs（不包括X）， 依次求X不经过Y到达Z1、Z2、...Zn的最短路，
	 *  把最短的一条 X->Zk 添加到子图， 完成补边.
	 *  
	 * @param subGraph 子图
	 * @param graph 原图
	 * @param dijkstra 根据原图构造的dijkstra算法对象
	 * @return 是否补边成功 (根据上下文逻辑, X必定是一个必经点, 因此若存在任意一个X补边失败， 则此图必定无解)
	 */
	private static boolean _fillEdges(final TopoGraph subGraph, final TopoGraph graph, final Dijkstra dijkstra) {
		boolean isOk = true;
		do {
			isOk = true;
			Set<Integer> tabu = __getTabu(graph, subGraph);
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
					Node[] twoNodes = __getNeighbor(subNode);
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
						dijkstra.calculate(srcId, tmpTabu);
						int cost = dijkstra.getShortPathWeight(snkId);
						if(cost < minCost) {
							minCost = cost;
							minSP = dijkstra.getShortPaths(snkId);
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
		return isOk; // FIXME
	}
	
	private static Node[] __getNeighbor(Node node) {
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
	
	private static Set<Integer> __getTabu(TopoGraph graph, TopoGraph subGraph) {
		Set<Integer> tabuIds = new HashSet<Integer>();
		Set<Node> subNodes = subGraph.getAllNodes();
		for(Node node : subNodes) {
			tabuIds.add(graph.getNode(node.getName()).getId());
		}
		return tabuIds;
	}
	
}
