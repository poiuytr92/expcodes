package exp.libs.algorithm.tsp.qant.bean;

/**
 * 
 * <PRE>
 * 量子寻路环境
 * </PRE>
 * 
 * @author lqb
 * @date 2017年6月8日
 */
class QEnv {

	/** 拓扑图规模(节点数) */
	private int size;
	
	/** 拓扑图源点编号 */
	private int srcId;
	
	/** 拓扑图终点编号 */
	private int snkId;
	
	/** 节点间距 */
	private int[][] dist;
	
	/**
	 * 蚂蚁从i->j移动的自启发常量(即节点i的能见度)
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
	
	/**
	 * 构造函数
	 * @param dist 拓扑图节点间距
	 * @param srcId 拓扑图源点编号
	 * @param snkId 拓扑图终点编号
	 */
	protected QEnv(int[][] dist, int srcId, int snkId) {
		this.size = (dist == null ? 0 : dist.length);
		this.dist = dist;
		this.srcId = srcId;
		this.snkId = snkId;
		initDist();
	}
	
	private void initDist() {
		for(int i = 0; i < size; i++) {
			int sum = 0, cnt = 0;
			maxDist[i] = -1;
			for(int j = 0; j < size; j++) {
				if(dist[i][j] <= 0) {
					dist[i][j] = 0;
					eta[i][j] = 1.0D;
					
				} else if(dist[i][j] == Integer.MAX_VALUE) {
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

	protected int getSize() {
		return size;
	}

	protected int getSrcId() {
		return srcId;
	}

	protected int getSnkId() {
		return snkId;
	}

	protected int getDist(int srcId, int snkId) {
		return dist[srcId][snkId];
	}

	protected double getEta(int srcId, int snkId) {
		return eta[srcId][snkId];
	}

	protected double getAvgDist(int nodeId) {
		return avgDist[nodeId];
	}

	protected int getMaxDist(int nodeId) {
		return maxDist[nodeId];
	}

}
