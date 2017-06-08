package exp.libs.algorithm.tsp.qant;

import java.util.Arrays;

import exp.libs.algorithm.tsp.qant.bean.QAnt;
import exp.libs.algorithm.tsp.qant.bean.QPA;
import exp.libs.utils.pub.RandomUtils;

/**
 * 
 * <PRE>
 * 量子蚁群算法
 * </PRE>
 * 
 * @author lqb
 * @date 2017年6月8日
 */
public class QACA {

	/* System Variable */
	public final static double PRECISION = 1.0e-6D;		//最小精度
	public final static double PI = 3.141592654D;			//数学常量π
	public final static double MAX_DOUBLE = Double.MAX_VALUE;//double最大值

	/* Application Variable */
	public final static double ZETA = 1.5D;		//ζ: 信息启发系数，反映了轨迹的重要性（协作性）
	public final static double GAMMA = 4.0D;	//γ: 期望启发系数，反映了能见度的重要性（创新性）
	public final static int SRAND_CRIVAL = 8;		//最优路径转移随机数阀值
	public final static int isUseCross = 1;			//是否启动变异处理，可避免算法停滞，但消耗时间
	public final static int CROSS_CRIVAL = 50;		//变异处理阀值，isUseCross = 1时有效。当连续CROSS_CRIVAL次求解但没有更新最优解时，执行量子交叉
	public final static int isUseVolatilize = 1;	//是否启用信息素自然挥发，可加速收敛，但消耗时间

	public final static double MIN_THETA = 0.001D * PI;	//最小旋转角
	public final static double MAX_THETA = 0.05D * PI;	//最大旋转角
	public final static double DELTA_THETA = MAX_THETA - MIN_THETA;	//旋转角

	/* User Variable */
	public final static int N_QANT = 5;				//量子蚂蚁种群规模
	public final static int MAX_GENERATION = 5;		//最大的蚂蚁代数（迭代次数）
	private int N_CITY;			//城市规模
	private int srcId;
	private int snkId;

	private double[][] distances;	//distances[A][B]: 城市A->B的距离
	private double[] avgDist;		//avgDist[i]: 城市i到各个城市的平均距离 (∑distances[i][0~(N_CITY-1)])/N_CITY
	private double[] maxDist;		//maxDist[i]: 城市i到各个城市的距离中的最大值 max{distances[i][0~(N_CITY-1)]}
	private double[][] eta;			//计算蚂蚁转移时城市A->B的自启发量:η(A, B) = 1/distance[A][B]

	private double theoryDistance;	//理论最优解
	private int THEORY_GENERATION;	//因得到理论最优解而结束搜索时的蚂蚁代数（迭代次数）

	private double currentDistance;	//当前解
	private int[] currentRoute;		//当前解路径

	private double bestDistance;	//最优解
	private int[] bestRoute;		//最优解路径
	private QPA[][] bestPheromone;	//最优解的路径信息素的概率幅

	private QAnt[] qAnt;		//量子蚂蚁种群
	
	public QACA(int nCity, int srcId, int snkId) {
		this.N_CITY = nCity;
		this.srcId = srcId;
		this.snkId = snkId;
		this.theoryDistance = -1;	//理论最优解
		this.THEORY_GENERATION = -1;	//因得到理论最优解而结束搜索时的蚂蚁代数（迭代次数）
		this.currentDistance = -1D;
		this.bestDistance = -1D;
		
		initRoom();
	}

		//初始化内存空间
	public void initRoom() {
		this.distances = new double[N_CITY][N_CITY];
		this.avgDist = new double[N_CITY];
		this.maxDist = new double[N_CITY];
		this.eta = new double[N_CITY][N_CITY];
		this.bestPheromone = new QPA[N_CITY][N_CITY];
		this.bestRoute = new int[N_CITY];
		this.currentRoute = new int[N_CITY];
		this.qAnt = new QAnt[N_QANT];
	}

		//初始化路径信息
	public void initPath(double[][] distances) {
		this.distances = distances;
		
		int i, j;
		//计算最大距离, 平均距离
		for(i=0; i<N_CITY; i++) {
			double sumDist = 0;
			maxDist[i] = -1;
			for(j=0; j<N_CITY; j++) {
				sumDist += distances[i][j];
				if(maxDist[i] < distances[i][j])
					maxDist[i] = distances[i][j];
			}
			avgDist[i] = sumDist / (N_CITY - 1);
		}

		//初始化最优解路径上的概率幅
		final double initVal = 1.0/Math.sqrt(2.0);
		for(i=0; i<N_CITY; i++) 
			for(j=0; j<=i; j++) {
				bestPheromone[i][j] = new QPA();
				bestPheromone[j][i] = new QPA();
				bestPheromone[i][j].setAlpha(initVal);
				bestPheromone[i][j].setBeta(initVal);
				bestPheromone[j][i].setAlpha(initVal);
				bestPheromone[j][i].setBeta(initVal);
			}

		//计算蚂蚁转移时城市A->B的自启发量:η(A, B) = 1/distances[A][B]
		for(i=0; i<N_CITY; i++) {
			for(j=0; j<=i; j++) {
				if(isZero(distances[i][j]) == true)
					eta[i][j] = eta[j][i] = 1.0;
				else
					eta[i][j] = eta[j][i] = 1.0/distances[i][j];
			}
		}
	}

		//初始化量子蚂蚁种群
	public void initQAntGroup() {
		for(int i=0; i<N_QANT; i++) {
			qAnt[i] = new QAnt();
			qAnt[i].initAnt(N_CITY);
		}
	}

		//更新量子蚂蚁信息：分配起点城市、初始化禁忌表
	public void updateAntInfo(int generation) {
		for(int i=0; i<N_QANT; i++) {
			//设置蚂蚁代数
			qAnt[i].setGeneration(generation);

			//把蚂蚁分配到各个城市
//			int cityNo = (i + qAnt[i].getGeneration()) % N_CITY;
			int cityNo = (RandomUtils.randomBoolean() ? srcId : snkId); // 只分配到源宿城市
			qAnt[i].setNowCityNo(cityNo);
			qAnt[i].resetTabuCity();
			qAnt[i].addTabuCity(cityNo);
		}
	}

		//运行量子蚁群算法求解
	public void runQAnt() {
		//初始化最优解
		bestDistance = Double.MAX_VALUE;

		//量子交叉计数器
		int qtCrossCnt = 0;

		//第gn代量子蚁群
		for(int gn=0; gn<MAX_GENERATION; gn++) {
			//更新第i代蚂蚁的初始信息：分配起点城市、初始化禁忌表
			updateAntInfo(gn);

			//使用第k只蚂蚁求解
			for(int k=0; k<N_QANT; k++)
			{
				//初始化当前解
				currentDistance = 0;
				Arrays.fill(currentRoute, -1);
				boolean isOk = true;

				//计算第k只蚂蚁的第s步
				for(int s=0; s<N_CITY; s++)
				{
					//记录第k只蚂蚁的每一步
					currentRoute[s] = qAnt[k].getNowCityNo();
					
					//计算第k只蚂蚁的下一步
					int nextCityNo = qAnt[k].selectNextCity(eta, srcId, snkId);
					if(nextCityNo == -1) {
//						nextCityNo = currentRoute[0];
						isOk = (s != N_CITY - 1);
						// FIXME 加入剩余点均为非必经点， 则认为得到一个解
						break;
					}

					//计算蚂蚁从nowCityNo移动到nextCityNo时在路径上释放的信息素
					double beta2 = getMoveQTPA(qAnt[k].getNowCityNo(), nextCityNo);

					//计算量子旋转门的旋转角θ
					double theta = qAnt[k].getTheta(beta2, qAnt[k].getNowCityNo(), nextCityNo, bestPheromone);

					//使用量子旋转门更新当前移动路径上信息素
					qAnt[k].updateQTPA(qAnt[k].getNowCityNo(), nextCityNo, theta);

					//其他未选择的路径信息素被自然挥发
					if(isUseCross == 1) {
						int preCityNo = (s==0?-1:currentRoute[s-1]);
						qAnt[k].updateOtherQTPA(preCityNo, qAnt[k].getNowCityNo(), nextCityNo, -theta);
					}

					//更新当前解
					currentDistance += distances[qAnt[k].getNowCityNo()][nextCityNo];
					qAnt[k].addTabuCity(nextCityNo);
					qAnt[k].setNowCityNo(nextCityNo);	//更新当前所在城市
				}

				//更新最优解
				if(isOk && currentDistance < bestDistance) {
					qtCrossCnt = 0;
					bestDistance = currentDistance;
					for(int i=0; i<N_CITY; i++) {
						bestRoute[i] = currentRoute[i];
						for(int j=0; j<N_CITY; j++) {
							bestPheromone[i][j].setAlpha(qAnt[k].getQtpa()[i][j].getAlpha());
							bestPheromone[i][j].setBeta(qAnt[k].getQtpa()[i][j].getBeta());
						}
					}

					//判断当前最优解是否等于理论解，若是则结束程序
					if(theoryDistance>0 && 
							(true==isZero(bestDistance-theoryDistance) || 
							bestDistance-theoryDistance<0)) {
						THEORY_GENERATION = gn;
						break;
					}
				} else {
					System.out.println("第" + k + "只蚂蚁无解");
					for(int r : currentRoute) {
						System.out.print(r + "<-");
					}
					System.out.println();
				}

				//当超过一定次数没有更新最优解时，执行量子交叉
				qtCrossCnt++;
				if((isUseVolatilize == 1) && (qtCrossCnt > CROSS_CRIVAL)) {
					qtCrossCnt = 0;
					qAnt[k].qtCross();
				}
			}

			if(THEORY_GENERATION > -1)
				break;
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
		else if(true == isZero(maxDist[cityA]-avgDist[cityA]))
			beta = 0.5;
		else
			beta = (distances[cityA][cityB]-avgDist[cityA])/
				(2*(maxDist[cityA]-avgDist[cityA]))+0.5;

		beta = (beta + bestPheromone[cityA][cityB].getBeta()) / 2.0;
		return beta*beta;
	}
	
		//打印最优解
	public void printBestSolution() {
		for(int r : bestRoute) {
			System.out.print(r + "<-");
		}
	}

		//释放全局内存
	public void destory() {
		
	}

		//判断浮点数是否为0
	public static boolean isZero(double fNum) {
		return (Math.abs(fNum)<PRECISION)?true:false;
	}

}
