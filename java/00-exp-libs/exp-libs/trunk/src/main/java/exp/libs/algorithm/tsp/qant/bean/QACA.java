package exp.libs.algorithm.tsp.qant.bean;

import exp.libs.utils.pub.RandomUtils;

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

	/** 最小精度 */
	private final static double PRECISION = 1.0e-6D;
	
	/** 变异处理阀值: 当连续CROSS_LIMIT次求解但没有更新最优解时，执行量子交叉, 避免搜索陷入停滞 */
	private final static int CROSS_LIMIT = 10;
	
	/** 默认量子蚂蚁种群规模 */
	public final static int DEFAULT_QANT_SIZE = 10;
	
	/** 默认最大的蚂蚁代数（迭代次数） */
	public final static int DEFAULT_MAX_GENERATION = 5;
	
	/** 量子蚂蚁种群规模 */
	private int qAntSize;
	
	/** 量子蚂蚁最大代数 */
	private int maxGeneration;
	
	/** 量子蚂蚁种群 */
	private QAnt[] qAnt;
	
	/** 启动变异处理，可避免算法停滞到局部解或无解，但消耗更多时间 */
	public boolean useQCross;
	
	/** 启用信息素自然挥发，可加速收敛到局部解或无解，但消耗更多时间 */
	public boolean useVolatilize;

	/** 寻路环境 */
	private QEnv env;

	/** 最优解的移动路径开销 */
	private int bestCost;
	
	/** 最优解的移动轨迹 */
	private int[] bestTrack;
	
	/** 最优解的所有路径信息素的概率幅 */
	private QPA[][] bestQPAs;

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
	 * @param useCross
	 * @param useVolatilize
	 */
	public QACA(int[][] dist, int srcId, int snkId, 
			int qAntSize, int maxGeneration,
			boolean useCross, boolean useVolatilize) {
		this.env = new QEnv(dist, srcId, snkId);
		this.qAntSize = (qAntSize <= 0 ? DEFAULT_QANT_SIZE : qAntSize);
		this.maxGeneration = (maxGeneration <= 0 ? 
				DEFAULT_MAX_GENERATION : maxGeneration);
		this.useQCross = useCross;
		this.useVolatilize = useVolatilize;
		
		this.qAnt = new QAnt[this.qAntSize];
		for(int i = 0; i < this.qAntSize; i++) {
			qAnt[i] = new QAnt(env);
		}
		
		this.bestCost = 0;
		this.bestTrack = new int[env.size()];
		this.bestQPAs = new QPA[env.size()][env.size()];
		for(int i = 0; i < env.size(); i++) {
			for(int j = 0; j <= i; j++) {
				bestQPAs[i][j] = new QPA();
				if(i != j) {
					bestQPAs[j][i] = new QPA();
				}
			}
		}
	}

		//更新量子蚂蚁信息：分配起点城市、初始化禁忌表
	public void updateAntInfo(QAnt qAnt) {
		//设置蚂蚁代数
		qAnt.evolve();

		//把蚂蚁分配到各个城市
//			int cityNo = (i + qAnt[i].getGeneration()) % env.size();
		int cityNo = (RandomUtils.randomBoolean() ? env.srcId() : env.snkId()); // 只分配到源宿城市
		qAnt.move(cityNo);
	}

		//运行量子蚁群算法求解
	public void runQAnt() {
		bestCost = Integer.MAX_VALUE;	//初始化最优解
		int qtCrossCnt = 0;	//量子交叉计数器

		//第gn代量子蚁群
		for(int gn=0; gn<maxGeneration; gn++) {

			//使用第k只蚂蚁求解
			for(int k=0; k<qAntSize; k++) {
				updateAntInfo(qAnt[k]);
				
				boolean isOk = true;

				//计算第k只蚂蚁的第s步
				for(int s=0; s<env.size(); s++) {
					
					//计算第k只蚂蚁的下一步
					int nextCityNo = qAnt[k].selectNextId();
					if(nextCityNo == -1) {
//						nextCityNo = currentRoute[0];
						isOk = (s != env.size() - 1);
						// FIXME 加入剩余点均为非必经点， 则认为得到一个解
						break;
					}

					//计算蚂蚁从nowCityNo移动到nextCityNo时在路径上释放的信息素
					double beta2 = getMoveQTPA(qAnt[k].getCurLocationId(), nextCityNo);
					double pGeneration = ((double) qAnt[k].getGeneration()) / ((double) maxGeneration);
					QPA curQPA = qAnt[k].getQtpa()[qAnt[k].getCurLocationId()][nextCityNo];
					QPA bestQPA = bestQPAs[qAnt[k].getCurLocationId()][nextCityNo];
					
					//计算量子旋转门的旋转角θ
					double theta = QUtils.getTheta(beta2, pGeneration, curQPA, bestQPA);
					
					//使用量子旋转门更新当前移动路径上信息素
					qAnt[k].updateQPA(qAnt[k].getCurLocationId(), nextCityNo, theta);

					//其他未选择的路径信息素被自然挥发
					if(useQCross == true) {
						int preCityNo = (s==0?-1:qAnt[k].getCurMoveTrack()[s-1]); // FIXME
						qAnt[k].updateQPAs(preCityNo, qAnt[k].getCurLocationId(), nextCityNo, -theta);
					}

					qAnt[k].move(nextCityNo);	//更新当前所在城市
				}

				//更新最优解
				if(isOk && qAnt[k].getCurMoveCost() < bestCost) {
					qtCrossCnt = 0;
					bestCost = qAnt[k].getCurMoveCost();
					for(int i=0; i<env.size(); i++) {
						bestTrack[i] = qAnt[k].getCurMoveTrack()[i];
						for(int j=0; j<env.size(); j++) {
							bestQPAs[i][j].setAlpha(qAnt[k].getQtpa()[i][j].getAlpha());
							bestQPAs[i][j].setBeta(qAnt[k].getQtpa()[i][j].getBeta());
						}
					}

				} else {
					System.out.println("第" + k + "只蚂蚁无解");
					for(int r : qAnt[k].getCurMoveTrack()) {
						System.out.print(r + "<-");
					}
					System.out.println();
				}

				//当超过一定次数没有更新最优解时，执行量子交叉
				qtCrossCnt++;
				if(useVolatilize && (qtCrossCnt > CROSS_LIMIT)) {
					qtCrossCnt = 0;
					qAnt[k].qCross();
				}
			}
		}
	}

	/**
	 * double* QANT::getMoveQTPA(int, int)
	 * 计算蚂蚁在cityA->cityB移动时释放的信息素
	 *
	 * cityA : 当前城市A的编号
	 * cityB : 目的城市B的编号
	 * return: cityA->cityB的量子信息素增量的β概率幅的平方
	 */
	public double getMoveQTPA(int cityA, int cityB) {
		double beta = 0;

		if(cityA == cityB)
			beta = 0;
		else if(true == isZero(env.maxDist(cityA)-env.avgDist(cityA)))
			beta = 0.5;
		else
			beta = (env.dist(cityA, cityB)-env.avgDist(cityA))/
				(2*(env.maxDist(cityA)-env.avgDist(cityA)))+0.5;

		beta = (beta + bestQPAs[cityA][cityB].getBeta()) / 2.0;
		return beta*beta;
	}
	
		//打印最优解
	public void printBestSolution() {
		for(int r : bestTrack) {
			System.out.print(r + "<-");
		}
	}

	//判断浮点数是否为0
	private boolean isZero(double num) {
		return (Math.abs(num) < PRECISION)? true : false;
	}

}
