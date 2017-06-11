package exp.libs.algorithm.tsp.qant;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

import exp.libs.utils.os.ThreadUtils;
import exp.libs.warp.other.thread.ThreadPool;


/**
 * 
 * <PRE>
 * 量子蚁群算法
 *  (仅适用于无向对称拓扑图)
 * </PRE>
 * 
 * @author lqb
 * @date 2017年6月8日
 */
public final class QACA {

	/** 默认量子蚂蚁种群规模 */
	public final static int DEFAULT_QANT_SIZE = 10;
	
	/** 量子蚂蚁种群规模 */
	private int qAntSize;
	
	/** 量子蚂蚁种群 */
	private _QAnt[] qAnts;
	
	/** 寻路环境 */
	private final _QEnv ENV;

	/** 最优解的移动数据(全局最优解) */
	private _QRst bestRst;
	
	/**
	 * 构造函数
	 * @param dist 无向拓扑图的邻接矩阵, 不可达节点间距为 整型最大值
	 */
	public QACA(int[][] dist) {
		this(dist, -1, -1, null, 0, 0, true, true);
	}
	
	/**
	 * 构造函数
	 * @param dist 无向拓扑图的邻接矩阵, 不可达节点间距为 整型最大值
	 * @param srcId 无向拓扑图的起点（若无则为-1）
	 * @param snkId 无向拓扑图的重点（若无则为-1）
	 * @param includeIds 无向拓扑图的必经点集（若无则为null）
	 */
	public QACA(int[][] dist, int srcId, int snkId, 
			Collection<Integer> includeIds) {
		this(dist, srcId, snkId, includeIds, 0, 0, true, true);
	}
	
	/**
	 * 构造函数
	 * @param dist 无向拓扑图的邻接矩阵, 不可达节点间距为 整型最大值
	 * @param srcId 无向拓扑图的起点（若无则为-1）
	 * @param snkId 无向拓扑图的重点（若无则为-1）
	 * @param includeIds 无向拓扑图的必经点集（若无则为null）
	 * @param qAntSize 量子蚂蚁数量（种群大小）, 默认值为10
	 * @param maxGeneration 单只量子蚂蚁可遗传的最大代数（单只蚂蚁的求解次数）, 默认值为10
	 * @param useQCross 是否使用量子交叉（可避免搜索陷入局部解或无解， 但降低收敛速度）, 默认启用
	 * @param useVolatilize 信息素是否自然挥发（可避免陷入局部解，但降低收敛速度）, 默认启用
	 */
	public QACA(int[][] dist, int srcId, int snkId, 
			Collection<Integer> includeIds, int qAntSize, 
			int maxGeneration, boolean useQCross, boolean useVolatilize) {
		this.ENV = new _QEnv(dist, srcId, snkId, includeIds, 
				maxGeneration, useQCross, useVolatilize);
		
		this.qAntSize = (qAntSize <= 0 ? DEFAULT_QANT_SIZE : qAntSize);
		this.qAnts = new _QAnt[this.qAntSize];
		for(int i = 0; i < this.qAntSize; i++) {
			qAnts[i] = new _QAnt(ENV);
		}
		
		this.bestRst = new _QRst(ENV);
		bestRst.setCost(Integer.MAX_VALUE);
	}

	public void exec() {
		List<Future<_QRst>> rsts = new LinkedList<Future<_QRst>>();
		for(int gn = 0; gn < ENV.MAX_GENERATION(); gn++) {
			
			// 每代蚂蚁的个体之间使用多线程并行搜索
			ThreadPool<_QRst> tp = new ThreadPool<_QRst>(qAntSize);
			for(_QAnt qAnt : qAnts) {
				rsts.add(tp.submit(new _QAntThread(qAnt, bestRst)));
			}
			
			tp.shutdown();
			while(!tp.isTerminated()) {
				ThreadUtils.tSleep(200);
			}
			
			// 每代蚂蚁更新一次种群的最优解
			for(Future<_QRst> rst : rsts) {
				try {
					_QRst antRst = rst.get();
					if(antRst.isVaild() && antRst.getCost() < bestRst.getCost()) {
						bestRst.copy(antRst);
					}
					System.out.println(antRst.toString());
				} catch (Exception e) {
					System.err.println("获取回调结果失败");	// FIXME
				}
			}
			rsts.clear();
		}
	}
	
	// FIXME 打印最优解
	public void printBestRst() {
		System.out.println(bestRst.toString());
	}

}
