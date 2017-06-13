package exp.libs.algorithm.tsp.qant;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(QACA.class);
	
	/** 默认量子蚂蚁种群规模 */
	public final static int DEFAULT_QANT_SIZE = 10;
	
	/** 量子蚂蚁种群规模 */
	private int qAntSize;
	
	/** 量子蚂蚁种群 */
	private _QAnt[] qAnts;
	
	/** 寻路环境 */
	private final _QEnv ENV;

	/** 首次得到可行解的代数 */
	private int firstRstGn;
	
	/** 首次得到最优解的代数 */
	private int firstBestRstGn;
	
	/** 累计得到可行解的次数 */
	private int rstCnt;
	
	/** 最优解的移动数据(全局最优解) */
	private QRst bestRst;
	
	/**
	 * 构造函数
	 * @param dist 无向拓扑图的邻接矩阵, 不可达节点间距为 整型最大值
	 */
	public QACA(int[][] dist) {
		this(dist, -1, -1, null, 0, 0, false);
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
		this(dist, srcId, snkId, includeIds, 0, 0, false);
	}
	
	/**
	 * 构造函数
	 * @param dist 无向拓扑图的邻接矩阵, 不可达节点间距为 整型最大值
	 * @param srcId 无向拓扑图的起点（若无则为-1）
	 * @param snkId 无向拓扑图的重点（若无则为-1）
	 * @param includeIds 无向拓扑图的必经点集（若无则为null）
	 * @param qAntSize 量子蚂蚁数量（种群大小）, 默认值为10
	 * @param maxGeneration 单只量子蚂蚁可遗传的最大代数（单只蚂蚁的求解次数）, 默认值为10
	 * @param useQCross 是否使用量子交叉（可避免搜索陷入局部解或无解， 但降低收敛速度）, 默认不启用
	 */
	public QACA(int[][] dist, int srcId, int snkId, 
			Collection<Integer> includeIds, int qAntSize, 
			int maxGeneration, boolean useQCross) {
		this.ENV = new _QEnv(dist, srcId, snkId, includeIds, 
				maxGeneration, useQCross);
		
		this.qAntSize = (qAntSize <= 0 ? DEFAULT_QANT_SIZE : qAntSize);
		this.qAnts = new _QAnt[this.qAntSize];
		for(int i = 0; i < this.qAntSize; i++) {
			qAnts[i] = new _QAnt(i, ENV);
		}
		
		this.bestRst = new QRst(-1, ENV);
		bestRst.setCost(Integer.MAX_VALUE);
	}

	/**
	 * 执行QACA算法求解
	 * @return 得到的最优解
	 */
	public QRst exec() {
		this.firstRstGn = -1;
		this.firstBestRstGn = -1;
		this.rstCnt = 0;
		long bgnTime = System.currentTimeMillis();
		List<Future<QRst>> rsts = new LinkedList<Future<QRst>>();
		
		for(int gn = 0; gn < ENV.MAX_GENERATION(); gn++) {
			
			// 每代蚂蚁的个体之间使用多线程并行搜索
			ThreadPool<QRst> tp = new ThreadPool<QRst>(qAntSize);
			for(_QAnt qAnt : qAnts) {
				rsts.add(tp.submit(new _QAntThread(qAnt, bestRst)));
			}
			
			tp.shutdown();
			while(!tp.isTerminated()) {
				ThreadUtils.tSleep(200);
			}
			
			// 每代蚂蚁更新一次种群的最优解
			for(Future<QRst> rst : rsts) {
				try {
					QRst antRst = rst.get();
					if(antRst.isVaild()) {
						rstCnt++;
						firstRstGn = (firstRstGn < 0 ? gn : firstRstGn);
						if(antRst.getCost() < bestRst.getCost()) {
							firstBestRstGn = gn;
							bestRst.copy(antRst);
						}
					}
				} catch (Exception e) {
					log.error("获取第 [{}] 代蚂蚁搜索结果异常.", gn, e);
				}
			}
			rsts.clear();
		}
		
		printRst(System.currentTimeMillis() - bgnTime);
		return bestRst;
	}
	
	private void printRst(long useTime) {
		StringBuilder sb = new StringBuilder();
		sb.append("\r\nQACA算法搜索结果 : \r\n");
		sb.append(" [拓扑图规模] : ").append(ENV.size()).append("\r\n");
		sb.append(" [搜索耗时] : ").append(useTime).append("ms\r\n");
		sb.append(" [蚂蚁族群大小] : ").append(qAntSize).append("\r\n");
		sb.append(" [蚂蚁遗传代数] : ").append(ENV.MAX_GENERATION()).append("\r\n");
		sb.append(" [变异处理] : ").append(ENV.isUseQCross()).append("\r\n");
		sb.append(" [总求解次数] : ").append(qAntSize * ENV.MAX_GENERATION()).append("\r\n");
		sb.append(" [得到可行解次数] : ").append(rstCnt).append("\r\n");
		sb.append(" [首次得到可行解代数] : ").append(firstRstGn).append("\r\n");
		sb.append(" [首次得到最优解代数] : ").append(firstBestRstGn).append("\r\n");
		sb.append(" [最优解] : \r\n").append(bestRst.toRouteInfo());
		log.info(sb.toString());
	}

}
