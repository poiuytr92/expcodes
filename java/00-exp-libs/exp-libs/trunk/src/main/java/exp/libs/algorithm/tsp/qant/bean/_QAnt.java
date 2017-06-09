package exp.libs.algorithm.tsp.qant.bean;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import exp.libs.utils.pub.RandomUtils;

/**
 * 量子蚂蚁
 */
class _QAnt {

	/** 该蚂蚁已进化的代数 */
	private int generation;
	
	/** 该蚂蚁求得可行解的次数 */
	private int solveCnt;
	
	/** 蚂蚁寻路环境 */
	private final _QEnv ENV;
	
	/** 蚂蚁当前所在的节点编号 */
	private int curLocationId;
	
	/** 蚂蚁自身禁忌表（已走过的节点列入禁忌表） */
	private boolean[] tabus;

	/** 蚂蚁当前的移动数据(当前局部解) */
	private _QRst curRst;
	
	/**
	 * 构造函数
	 * @param ENV
	 */
	protected _QAnt(final _QEnv ENV) {
		this.ENV = ENV;
		this.generation = 0;
		this.solveCnt = 0;
		this.tabus = new boolean[ENV.size()];
		this.curRst = new _QRst(ENV);
	}
	
	/**
	 * 进化到下一代蚂蚁
	 *  （量子编码继承遗传，移动痕迹重置）
	 */
	protected void evolve() {
		generation++;
		curLocationId = -1;
		curRst.reset();
		Arrays.fill(tabus, false);
	}

	protected int getGeneration() {
		return generation;
	}
	
	@Deprecated
	protected __QPA[][] getQtpa() {
		return curRst.getQPAs();
	}
	
	protected boolean solve(final _QRst bestRst) {
		boolean isOk = true;
		//设置蚂蚁代数
		evolve();

		//把蚂蚁分配到各个城市
//		int cityNo = (i + qAnt[i].getGeneration()) % ENV.size();
		int cityNo = (RandomUtils.randomBoolean() ? ENV.srcId() : ENV.snkId()); // 只分配到源宿城市
		move(cityNo);
				
		//计算第k只蚂蚁的第s步
		for(int s = 0; s < ENV.size(); s++) { // FIXME: 步长-1
			
			//计算第k只蚂蚁的下一步
			int nextCityNo = selectNextId();
			if(nextCityNo == -1) {
//				nextCityNo = currentRoute[0];
				isOk = (s != ENV.size() - 1);
				// FIXME 加入剩余点均为非必经点， 则认为得到一个解
				break;
			}

			//计算蚂蚁从nowCityNo移动到nextCityNo时在路径上释放的信息素
			double beta2 = getMoveQTPA(getCurLocationId(), nextCityNo, bestRst);
			double pGeneration = ((double) getGeneration()) / ((double) ENV.MAX_GENERATION());
			__QPA curQPA = getQtpa()[getCurLocationId()][nextCityNo];
			__QPA bestQPA = bestRst.QPA(getCurLocationId(), nextCityNo);
			
			//计算量子旋转门的旋转角θ
			double theta = QUtils.getTheta(beta2, pGeneration, curQPA, bestQPA);
			
			//使用量子旋转门更新当前移动路径上信息素
			updateQPA(getCurLocationId(), nextCityNo, theta);

			//其他未选择的路径信息素被自然挥发
			if(ENV.isUseVolatilize()) {
				int preCityNo = (s==0?-1:getCurRst().getRoutes()[s-1]); // FIXME
				updateQPAs(preCityNo, getCurLocationId(), nextCityNo, -theta);
			}

			move(nextCityNo);	//更新当前所在城市
		}
		return isOk;
	}

	/**
	 * double* QANT::getMoveQTPA(int, int)
	 * 计算蚂蚁在cityA->cityB移动时释放的信息素
	 *
	 * cityA : 当前城市A的编号
	 * cityB : 目的城市B的编号
	 * return: cityA->cityB的量子信息素增量的β概率幅的平方
	 */
	public double getMoveQTPA(int cityA, int cityB, final _QRst bestRst) {
		double beta = 0;

		if(cityA == cityB)
			beta = 0;
		else if(QUtils.isZero(ENV.maxDist(cityA)-ENV.avgDist(cityA)))
			beta = 0.5;
		else
			beta = (ENV.dist(cityA, cityB)-ENV.avgDist(cityA))/
				(2*(ENV.maxDist(cityA)-ENV.avgDist(cityA)))+0.5;

		beta = (beta + bestRst.QPA(cityA, cityB).getBeta()) / 2.0;
		return beta*beta;
	}
	
	/**
	 * 根据寻路决策的规则选择下一个移动的节点ID
	 * @return 若无路可走则返回-1
	 */
	protected int selectNextId() {
		if(curLocationId < 0) {
			return -1;	// FIXME 随机选一个城市？ 
		}
		
		// FIXME 这两个值越小越重要， 且和为1？  若蚂蚁代数很大，依然未求得过一个解，则需要适当调整参数
		final double ZETA = 0.2D;	//ζ: 信息启发系数，反映了轨迹的重要性（协作性）
		final double GAMMA = 0.8D;	//γ: 期望启发系数，反映了能见度的重要性（创新性）
		
		int nextId = -1;
		final int SCOPE = 10, RAND_LIMIT = 8;
		int rand = RandomUtils.randomInt(SCOPE);

		// 蚂蚁以80%的概率以信息素作为决策方式进行路径转移（协作性优先）
		if(rand < RAND_LIMIT) {
			double argmax = -1;
			for(int i = curLocationId, j = 0; j < ENV.size(); j++) {
				if(isTabu(j)) {
					continue;
				}

				double arg = Math.pow(getTau(i, j), ZETA) * 
						Math.pow(ENV.eta(i, j), GAMMA);
				if(argmax <= arg) {
					argmax = arg;
					nextId = j;
				}
			}
			
		// 蚂蚁以20%的概率以随机方式进行路径转移（保持创新性）
		} else {
			final double fRand = RandomUtils.randomInt(SCOPE) / (SCOPE * 1.0D);
			double sum = 0;
			
			Map<Integer, Double> map = new LinkedHashMap<Integer, Double>();
			for(int i = curLocationId, j = 0; j < ENV.size(); j++) {
				if(isTabu(j)) {
					continue;
				}
				
				double arg = Math.pow(getTau(i, j), ZETA) * 
						Math.pow(ENV.eta(i, j), GAMMA);
				sum += arg;
				map.put(j, arg);
			}
			
			Iterator<Integer> nextIds = map.keySet().iterator();
			while(nextIds.hasNext()) {
				nextId = nextIds.next(); // 预选： 确保至少选到一个转移点
				double arg = map.get(nextId);
				if(arg / sum >= fRand) {
					break;
				}
			}
		}
		return nextId;
	}
	
	/**
	 * 检查下一跳是否可行
	 * @param nodeId
	 * @return
	 */
	private boolean isTabu(int nextId) {
		boolean isTabu = false;
		
		// 下一节点已处于禁忌表
		if(tabus[nextId]) {
			isTabu = true;
			
		// 当前节点与下一跳节点不连通
		} else if(ENV.dist(curLocationId, nextId) == Integer.MAX_VALUE) {
			isTabu = true;
			
		// 当下一跳节点为拓扑图的源宿点时，则下一跳只能是最后一跳
		} else if((nextId == ENV.srcId() || nextId == ENV.snkId())) {
			isTabu = !(curRst.getStep() + 1 == ENV.size());
		}
		return isTabu;
	}
	
	/**
	 * 获取路径 src->snk 的信息素浓度τ（即选择这条路径的概率）
	 *  τ(src, snk) = (_QPAs[src][snk].beta)^2
	 * @param srcId
	 * @param snkId
	 * @return
	 */
	private double getTau(int srcId, int snkId) {
		double beta = curRst.QPA(srcId, snkId).getBeta();
		double tau = beta * beta;
		return tau;
	}
	
	/**
	 * 移动到下一位置
	 * @param nodeId
	 */
	protected void move(int nextId) {
		int moveCost = 0;
		if(curLocationId >= 0) {
			tabus[curLocationId] = true;
			moveCost = ENV.dist(curLocationId, nextId);
		}
		curRst.add(nextId, moveCost);
		
		// TODO: 更新、挥发对应路径上的信息素
		curLocationId = nextId;
	}

	protected int getCurLocationId() {
		return curLocationId;
	}
	
	protected _QRst getCurRst() {
		return curRst;
	}
	
	/**
	 * 使用量子旋转门更新量子编码: 
	 * 	加强src->snk的路径信息素
	 * @param srcId 路径起点
	 * @param snkId 路径终点
	 * @param theta 正向旋转角
	 */
	protected void updateQPA(final int srcId, final int snkId, final double theta) {
		final double cosTheta = Math.cos(theta);
		final double sinTheta = Math.sin(theta);
		final double alpha = curRst.QPA(srcId, snkId).getAlpha();
		final double beta = curRst.QPA(srcId, snkId).getBeta();
		curRst.QPA(srcId, snkId).setAlpha(cosTheta * alpha - sinTheta * beta);
		curRst.QPA(srcId, snkId).setBeta(sinTheta * alpha + cosTheta * beta);
		curRst.QPA(snkId, srcId).setAlpha(curRst.QPA(srcId, snkId).getAlpha());
		curRst.QPA(snkId, srcId).setBeta(curRst.QPA(srcId, snkId).getBeta());
	}

	/**
	 * 使用量子旋转门更新量子编码: 
	 *  挥发除了 cur->pre 和 cur->next 以外的与src相关的路径信息素
	 *  (对称路径 pre->cur 信息素 等同于 cur->pre 信息素)
	 * @param preId 上一节点
	 * @param curId 当前节点
	 * @param nextId 下一节点
	 * @param theta 逆向旋转角
	 */
	protected void updateQPAs(int preId, int curId, int nextId, double theta) {
		final double cosTheta = Math.cos(theta);
		final double sinTheta = Math.sin(theta);

		for(int j = 0; j < ENV.size(); j++) {
			if(j == preId || j == nextId) {
				continue;
			}
			
			final double alphaTmp = curRst.QPA(curId, j).getAlpha();
			final double betaTmp = curRst.QPA(curId, j).getBeta();
			curRst.QPA(curId, j).setAlpha(cosTheta * alphaTmp - sinTheta * betaTmp);
			curRst.QPA(curId, j).setBeta(sinTheta * alphaTmp + cosTheta * betaTmp);
			curRst.QPA(j, curId).setAlpha(curRst.QPA(curId, j).getAlpha());
			curRst.QPA(j, curId).setBeta(curRst.QPA(curId, j).getBeta());
		}
	}

	/**
	 * 使用量子交叉对量子编码做变异处理 
	 */
	protected void qCross() {
		for(int i = 0; i < ENV.size(); i++) {
			for(int j = 0; j <= i; j++) {
				if(ENV.dist(i, j) == Integer.MAX_VALUE) {
					continue;	// 不连通的路径不做量子交叉
				}
				
				final double beta = curRst.QPA(i, j).getBeta();
				curRst.QPA(i, j).setBeta(curRst.QPA(i, j).getAlpha());
				curRst.QPA(i, j).setAlpha(beta);
				curRst.QPA(j, i).setBeta(curRst.QPA(i, j).getBeta());
				curRst.QPA(j, i).setAlpha(curRst.QPA(i, j).getAlpha());			}
		}
	}
		
}
