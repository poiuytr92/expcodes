package exp.libs.algorithm.tsp.qant;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import exp.libs.utils.pub.NumUtils;

/**
 * 
 * <PRE>
 * 量子寻路环境
 * </PRE>
 * 
 * @author lqb
 * @date 2017年6月8日
 */
final class __QGraph {

	/** 拓扑图规模(节点数) */
	private int size;
	
	/** 拓扑图源点编号 */
	private int srcId;
	
	/** 拓扑图终点编号 */
	private int snkId;
	
	/** 拓扑图的必经点集 */
	private Set<Integer> includeIds;
	
	/** 节点间距 */
	private int[][] dist;
	
	/**
	 * 蚂蚁从节点i->j移动的自启发常量(即节点i的能见度)
	 *  η(i, j) = 1/distance[i][j]
	 */
	private double[][] eta;
	
	/**
	 * 某节点到其他节点的平均距离 
	 *  avgDist[i] = ∑distances[i][0~(size-1)] / size
	 */
	private double[] avgDist;
	
	/**
	 * 某节点到其他各个节点中的最大距离 
	 * 	maxDist[i] =  max{distances[i][0~(size-1)]}
	 */
	private int[] maxDist;
	
	/** 蚂蚁移动时释放的信息素的计算常量 */
	private double[][] deltaBeta;
	
	/**
	 * 构造函数
	 * @param dist 拓扑图节点间距
	 * @param srcId 拓扑图源点编号
	 * @param snkId 拓扑图终点编号
	 * @param includeIds 拓扑图必经点集
	 */
	protected __QGraph(int[][] dist, int srcId, int snkId, 
			Collection<Integer> includeIds) {
		this.size = (dist == null ? 0 : dist.length);
		this.srcId = srcId;
		this.snkId = snkId;
		this.includeIds = (includeIds == null ? 
				new HashSet<Integer>() : new HashSet<Integer>(includeIds));
		this.includeIds.add(srcId);
		this.includeIds.add(snkId);
		
		this.dist = dist;
		this.eta = new double[size][size];
		this.avgDist = new double[size];
		this.maxDist = new int[size];
		initDist();
		
		this.deltaBeta = new double[size][size];
		initBeta();
	}
	
	private void initDist() {
		for(int i = 0; i < size; i++) {
			int sum = 0, cnt = 0;
			maxDist[i] = -1;
			for(int j = 0; j < size; j++) {
				if(dist[i][j] <= 0) {
					dist[i][j] = 0;
					eta[i][j] = 1.0D;
					
				} else if(!isLinked(i, j)) {
					eta[i][j] = 0.0D;
					
				} else {
					eta[i][j] = 1.0D / dist[i][j];
					
					cnt++;
					sum += dist[i][j];
					if(maxDist[i] < dist[i][j]) {
						maxDist[i] = dist[i][j];
					}
				}
			}
			avgDist[i] = sum / (cnt * 1.0D);
		}
	}
	
	private void initBeta() {
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				if(i == j) {
					deltaBeta[i][j] = 0.0D;
					
				} else if(NumUtils.isZero(maxDist[i] - avgDist[i])) {
					deltaBeta[i][j] = 0.5D;
					
				} else {
					deltaBeta[i][j] = (dist[i][j] - avgDist[i]) / 
						(2 * (maxDist[i] - avgDist[i])) + 0.5D;
				}
			}
		}
	}

	protected int size() {
		return size;
	}

	protected int srcId() {
		return srcId;
	}

	protected int snkId() {
		return snkId;
	}

	protected int dist(int srcId, int snkId) {
		return dist[srcId][snkId];
	}

	protected double eta(int srcId, int snkId) {
		return eta[srcId][snkId];
	}

	protected double avgDist(int nodeId) {
		return avgDist[nodeId];
	}

	protected int maxDist(int nodeId) {
		return maxDist[nodeId];
	}
	
	protected double deltaBeta(int srcId, int snkId) {
		return deltaBeta[srcId][snkId];
	}
	
	protected boolean isInclude(int nodeId) {
		return includeIds.contains(nodeId);
	}
	
	protected boolean isLinked(int srcId, int snkId) {
		return dist[srcId][snkId] < Integer.MAX_VALUE;
	}

}
