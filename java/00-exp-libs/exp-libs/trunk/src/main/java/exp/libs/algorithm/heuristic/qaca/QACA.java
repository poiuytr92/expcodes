package exp.libs.algorithm.heuristic.qaca;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.os.ThreadUtils;
import exp.libs.warp.thread.BaseThreadPool;


/**
 * <PRE>
 * 量子蚁群算法
 *  (仅适用于无向对称拓扑图)
 * </PRE>
 * 
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2017-06-09
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public final class QACA {

	/** 日志�? */
	private final static Logger log = LoggerFactory.getLogger(QACA.class);
	
	/** 默认量子蚂蚁种群规模 */
	public final static int DEFAULT_QANT_SIZE = 10;
	
	/** 默认求解的收敛超时时�?(ms) */
	private final static long DEFAULT_TIMEOUT = 60000;
	
	/** 量子蚂蚁种群规模 */
	private int qAntSize;
	
	/** 量子蚂蚁种群 */
	private _QAnt[] qAnts;
	
	/** 寻路环境 */
	private final _QEnv ENV;

	/** 搜索耗时 */
	private long useTime;
	
	/** 首次得到可行解的代数 */
	private int firstRstGn;
	
	/** 首次得到最优解的代�? */
	private int firstBestRstGn;
	
	/** 累计得到可行解的次数 */
	private int rstCnt;
	
	/** 最优解的移动数�?(全局最优解) */
	private QRst bestRst;
	
	/**
	 * 构造函�?
	 * @param dist 无向拓扑图的邻接矩阵, 不可达节点间距为 整型最大�?
	 */
	public QACA(int[][] dist) {
		this(dist, -1, -1, null, 0, 0, false);
	}
	
	/**
	 * 构造函�?
	 * @param dist 无向拓扑图的邻接矩阵, 不可达节点间距为 整型最大�?
	 * @param srcId 无向拓扑图的起点（若无则�?-1�?
	 * @param snkId 无向拓扑图的重点（若无则�?-1�?
	 * @param includeIds 无向拓扑图的必经点集（若无则为null�?
	 */
	public QACA(int[][] dist, int srcId, int snkId, 
			Collection<Integer> includeIds) {
		this(dist, srcId, snkId, includeIds, 0, 0, false);
	}
	
	/**
	 * 构造函�?
	 * @param dist 无向拓扑图的邻接矩阵, 不可达节点间距为 整型最大�?
	 * @param srcId 无向拓扑图的起点（若无则�?-1�?
	 * @param snkId 无向拓扑图的重点（若无则�?-1�?
	 * @param includeIds 无向拓扑图的必经点集（若无则为null�?
	 * @param qAntSize 量子蚂蚁数量（种群大小）, 默认值为10, (蚂蚁数量越多, 越注重创新�?)
	 * @param maxGeneration 单只量子蚂蚁可遗传的最大代数（单只蚂蚁的求解次数）, 默认值为10, (代数越大, 越注重协作�?)
	 * @param useQCross 是否使用量子交叉（可避免搜索陷入局部解或无解， 但降低收敛速度�?, 默认不启�?
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
		
		resetStatistics();
		this.bestRst = new QRst(-1, ENV);
		bestRst.setCost(Integer.MAX_VALUE);
	}

	/**
	 * 重置统计参数
	 */
	private void resetStatistics() {
		this.useTime = -1;
		this.firstRstGn = -1;
		this.firstBestRstGn = -1;
		this.rstCnt = 0;
	}
	
	/**
	 * 执行QACA算法求解(默认超时�?1分钟)
	 * @return 得到的最优解
	 */
	public QRst exec() {
		return exec(DEFAULT_TIMEOUT);
	}
	
	/**
	 * 执行QACA算法求解
	 * @param timeout 求解超时(<=0表示无限�?), 避免收敛过慢
	 * @return 得到的最优解
	 */
	public QRst exec(long timeout) {
		resetStatistics();
		long bgnTime = System.currentTimeMillis();
		List<Future<QRst>> rsts = new LinkedList<Future<QRst>>();
		
		for(int gn = 0; gn < ENV.MAX_GENERATION(); gn++) {
			if(timeout > 0 && System.currentTimeMillis() - bgnTime > timeout) {
				log.error("QACA 收敛到可行解超时, 已检索代�?:[{}] (TIMEOUT={}ms, ANT={})", gn, timeout, qAntSize);
				break;
			}
			
			// 每代蚂蚁的个体之间使用多线程并行搜索
			BaseThreadPool<QRst> tp = new BaseThreadPool<QRst>(qAntSize);
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
					log.error("获取�? [{}] 代蚂蚁搜索结果异�?.", gn, e);
				}
			}
			rsts.clear();
		}
		
		useTime = System.currentTimeMillis() - bgnTime;
		return bestRst;
	}
	
	public String getBestRstInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("\r\nQACA搜索结果 : \r\n");
		sb.append(" [拓扑图规模] : ").append(ENV.size()).append("\r\n");
		sb.append(" [搜索耗时] : ").append(useTime).append("ms\r\n");
		sb.append(" [蚂蚁族群大小] : ").append(qAntSize).append("\r\n");
		sb.append(" [蚂蚁遗传代数] : ").append(ENV.MAX_GENERATION()).append("\r\n");
		sb.append(" [变异处理] : ").append(ENV.isUseQCross()).append("\r\n");
		sb.append(" [总求解次数] : ").append(qAntSize * ENV.MAX_GENERATION()).append("\r\n");
		sb.append(" [得到可行解次数] : ").append(rstCnt).append("\r\n");
		sb.append(" [首次得到可行解代数] : ").append(firstRstGn + 1).append("\r\n");
		sb.append(" [首次得到最优解代数] : ").append(firstBestRstGn + 1).append("\r\n");
		sb.append(" [最优解] : \r\n").append(bestRst.toRouteInfo());
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return getBestRstInfo();
	}

}
