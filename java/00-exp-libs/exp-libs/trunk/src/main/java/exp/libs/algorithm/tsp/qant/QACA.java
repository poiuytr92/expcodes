package exp.libs.algorithm.tsp.qant;


/**
 * 
 * <PRE>
 * 量子蚁群算法
 *  (仅适用于无向对称拓扑图)
 * 
 *  FIXME: 设定边权最大值
 * </PRE>
 * 
 * @author lqb
 * @date 2017年6月8日
 */
public class QACA {

	/** 变异处理阀值: 当连续CROSS_LIMIT次求解但没有更新最优解时，执行量子交叉, 避免搜索陷入停滞 */
	private final static int CROSS_LIMIT = 10;
	
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
	 * 
	 * @param dist
	 * @param srcId
	 * @param snkId
	 */
	public QACA(int[][] dist, int srcId, int snkId) {
		this(dist, srcId, snkId, 0, 0, true, true);
	}
	
	/**
	 * 
	 * @param dist
	 * @param srcId
	 * @param snkId
	 * @param qAntSize
	 * @param maxGeneration
	 * @param useQCross
	 * @param useVolatilize
	 */
	public QACA(int[][] dist, int srcId, int snkId, 
			int qAntSize, int maxGeneration,
			boolean useQCross, boolean useVolatilize) {
		this.ENV = new _QEnv(dist, srcId, snkId, 
				maxGeneration, useQCross, useVolatilize);
		this.qAntSize = (qAntSize <= 0 ? DEFAULT_QANT_SIZE : qAntSize);
		
		this.qAnts = new _QAnt[this.qAntSize];
		for(int i = 0; i < this.qAntSize; i++) {
			qAnts[i] = new _QAnt(ENV);
		}
		
		this.bestRst = new _QRst(ENV);
		bestRst.setCost(Integer.MAX_VALUE);
	}

		//运行量子蚁群算法求解
	public void runQAnt() {
		int qtCrossCnt = 0;	//量子交叉计数器(同一只蚂蚁连续N代没有得到最优解，则执行量子交叉)

		//第gn代量子蚁群 FIXME: 多线程并行, 每只蚂蚁一条线程
		for(int gn = 0; gn < ENV.MAX_GENERATION(); gn++) {

			//使用第k只蚂蚁求解
			for(int k=0; k<qAntSize; k++) {
				_QAnt qAnt = qAnts[k];
				if(qAnt.solve(bestRst)) {	// FIXME： 多线程取最优解镜像
					
					//更新最优解
					if(qAnt.getCurRst().getCost() < bestRst.getCost()) {
						qtCrossCnt = 0;
						bestRst.clone(qAnt.getCurRst());

					} else {
						System.out.println("第" + k + "只蚂蚁无解");
						for(int r : qAnt.getCurRst().getRoutes()) {
							System.out.print(r + "<-");
						}
						System.out.println();
					}

					//当超过N代没有更新最优解时，执行量子交叉
					qtCrossCnt++;
					if(ENV.isUseQCross() && (qtCrossCnt > CROSS_LIMIT)) {
						qtCrossCnt = 0;
						qAnt.qCross();
					}
				}
			}
		}
	}
	
		//打印最优解
	public void printBestSolution() {
		for(int r : bestRst.getRoutes()) {
			System.out.print(r + "<-");
		}
	}

}
