package exp.libs.algorithm.tsp.qant;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
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
	
	/** 拓扑图的必经点集 */
	private int[] _includeIds;
	
	/** 节点间距 */
	private int[][] dist;
	
	/**
	 * 蚂蚁从节点i->j移动的自启发常量(即节点i的能见度)
	 *  η(i, j) = dist[i][j] / ∑dist[0~size][0~size]
	 *  
	 *  FIXME: 并不时用比例方式
	 *  能见度使用归一化处理：即某路径i->j代价占全图总代价的百分比.
	 *  之所以不使用代价的倒数，是因为当两条邻接路径代价相差过大时，蚂蚁寻路时释放的协作信息素变得可以忽略不计.
	 */
	private double[][] eta;
	
	/**
	 * 某节点到其他节点的平均距离 
	 *  avgDist[i] = ∑distances[i][0~size] / size
	 */
	private double[] avgDist;
	
	/**
	 * 某节点到其他各个节点中的最大距离 
	 * 	maxDist[i] =  max{distances[i][0~size]} 
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
		this._includeIds = new int[includeIds.size()];
		initIncludes();
		
		this.dist = dist;
		this.eta = new double[size][size];
		this.avgDist = new double[size];
		this.maxDist = new int[size];
		initDist();
		
		this.deltaBeta = new double[size][size];
		initBeta();
	}
	
	private void initIncludes() {
		int idx = 0;
		Iterator<Integer> ids = includeIds.iterator();
		while(ids.hasNext()) {
			int id = ids.next();
			_includeIds[idx++] = id;
		}
		
		this.includeIds.add(srcId);
		this.includeIds.add(snkId);
	}
	
	private void initDist() {
		final double arg = 2 / _QEnv.PI;
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
//					final int offset = (isInclude(j) ? 0 : 3);	// 目的点是必经点
					final int offset = 3;
					eta[i][j] = 1 - Math.atan(dist[i][j] + offset) * arg;	
					// 归一化处理(+3是在因为当前旋转角范围为0.001~0.05，Math.atan(x) 若x小于3，相邻的偏差值会大于0.05，导致一次旋转角不可控 )
					
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
	
	/**
	 * 检查节点是否在必经点集中（包括源宿点）
	 * @param nodeId
	 * @return
	 */
	protected boolean isInclude(int nodeId) {
		return includeIds.contains(nodeId);
	}
	
	/**
	 * 获取必经点集
	 * @return 不包括源宿点的必经点集
	 */
	protected int[] getIncludes() {
		return _includeIds;
	}
	
	protected boolean isLinked(int srcId, int snkId) {
		return dist[srcId][snkId] < Integer.MAX_VALUE;
	}

}
