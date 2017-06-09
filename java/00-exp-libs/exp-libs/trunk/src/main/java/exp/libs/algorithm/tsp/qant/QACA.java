package exp.libs.algorithm.tsp.qant;


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
public class QACA {

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

	// 运行量子蚁群算法求解
	// FIXME: 多线程并行, 每只蚂蚁一条线程
	public void runQAnt() {
		for(int gn = 0; gn < ENV.MAX_GENERATION(); gn++) {  
			for(_QAnt qAnt : qAnts) {
				if(qAnt.solve(bestRst)) {	// FIXME： 多线程取最优解镜像
					if(qAnt.getCurRst().getCost() < bestRst.getCost()) {
						bestRst.clone(qAnt.getCurRst());	//更新最优解
					}
				} else {
					System.out.println("无解");
					for(int r : qAnt.getCurRst().getRoutes()) {
						System.out.print(r + "<-");
					}
					System.out.println();
				}
			}
		}
	}
	
	//打印最优解
	public void printBestSolution() {
		System.out.println("最优解:");
		for(int r : bestRst.getRoutes()) {
			System.out.print(r + "<-");
		}
	}

}
