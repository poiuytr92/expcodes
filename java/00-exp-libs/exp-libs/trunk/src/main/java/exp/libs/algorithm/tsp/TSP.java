package exp.libs.algorithm.tsp;

import java.util.List;

import exp.libs.algorithm.tsp.graph.Node;
import exp.libs.algorithm.tsp.graph.TopoGraph;
import exp.libs.algorithm.tsp.qant.QRst;

/**
 * <PRE>
 * 求解含必经点的TSP问题
 * </PRE>
 * 
 * @author lqb
 * @date 2017年6月13日
 */
public class TSP {

	private TSP() {}
	
	public static int[] exec(final TopoGraph graph) {
		int[] rst = new int[0];
		if(graph == null || graph.isEmpty() || 
				graph.getSrc() == Node.NULL || graph.getSnk() == Node.NULL) {
			return rst;
		}
		
		List<Node> includes = graph.getIncludes();
		
		// 不存在必经点， 直接用时Dij算法求 src->snk的最短路
		if(includes.isEmpty()) {
			// TODO 
			
		// 必经点有序，使用Dij算法分段求 src->include->snk的最短路
		} else if(graph.isOrder()) {
			// TODO 
			
		// 必经点无序, 使用启发式算法求最小连通通道
		} else {
			// 把源宿点列入必经点集，使用Dij算法计算必经点集中的任意两点的最短路
			// 合并所有最短路，构造子图
			// 对于子图中度数为1、 且非源宿点的节点, 对其补边，得到补边后的子图
			// 使用启发式算法对子图求解一条从src到snk的哈密顿通路
		}
		
		return rst;
	}
	
}
