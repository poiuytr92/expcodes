package exp.libs.algorithm.tsp.qant;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import exp.libs.utils.pub.RandomUtils;

/**
 * 量子蚂蚁
 */
final class _QAnt {

	/** 数学常量π */
	private final static double PI = 3.141592654D;
	
	/** 最小旋转角 */
	private final static double MIN_THETA = 0.001D * PI;
	
	/** 最大旋转角 */
	private final static double MAX_THETA = 0.05D * PI;
	
	/** 旋转角 */
	private final static double DELTA_THETA = MAX_THETA - MIN_THETA;
	
	/** 该蚂蚁已进化的代数 */
	private int generation;
	
	/** 蚂蚁寻路环境 */
	private final _QEnv ENV;
	
	/** 蚂蚁自身禁忌表（已走过的节点列入禁忌表） */
	private boolean[] tabus;

	/** 蚂蚁当前的移动数据(当前局部解) */
	private _QRst curRst;
	
	/** 该蚂蚁累计求得可行解的次数 */
	private int solveCnt;
	
	/** 该蚂蚁连续无解的次数 */
	private int unsolveCnt;
	
	/**
	 * 构造函数
	 * @param ENV
	 */
	protected _QAnt(final _QEnv ENV) {
		this.ENV = ENV;
		this.generation = 0;
		this.tabus = new boolean[ENV.size()];
		this.curRst = new _QRst(ENV);
		this.solveCnt = 0;
		this.unsolveCnt = 0;
	}
	
	protected _QRst getResult() {
		return curRst;
	}
	
	protected int getSolveCnt() {
		return solveCnt;
	}
	
	protected boolean solve(final _QRst bestRst) {
		boolean isFeasible = true;
		evolve();	// 进化(清空父代移动痕迹, 并继承父代量子编码)
		final double pGn = ((double) generation) / ENV.MAX_GENERATION(); // 代数比
		int curId = move(selectFirstId());	// 移动到起始节点
		
		// 计算蚂蚁之后移动的每一步(最大步长为除了起始点之外的图节点数)
		for(int step = 1; step < ENV.size(); step++) {
			int nextId = selectNextId(curId);
			if(nextId < 0) {	// 无路可走时检查是否已得到一个可行解
				isFeasible = checkFeasible();
				
				// FIXME 释放当前路径上的所有信息素x2，保留其他路径上的信息素，
				
				// 有解时增加信息素，挥发其他所有路径的信息素
				// 无解时挥发本段路基的信息素
				if(!isFeasible) {
					int[] route = curRst.getRoutes();
					if(route.length > 1) {
						for(int i = 0; i < curRst.getStep() - 1; i++) {
							minusMoveQPA(route[i], route[i + 1], pGn, bestRst);
						}
					}
				}
				break;
			}

//			updateMoveQPA(curId, nextId, pGn, bestRst);
			addMoveQPA(curId, nextId, pGn, bestRst);
			curId = move(nextId);
		}
		
		// 标记求得可行解
		if(isFeasible == true) {
			solveCnt++;
			unsolveCnt = 0;
			curRst.markVaild();
			// FIXME 挥发可行解以外的其他路径的信息素
			
		// 当连续无解次数越限时，执行量子交叉打乱量子编码，避免搜索陷入停滞
		} else {
			unsolveCnt++;
			if(ENV.isUseQCross() && unsolveCnt >= ENV.QCROSS_THRESHOLD()) {
				qCross();
			}
		}
		return isFeasible;
	}
	
	private void addMoveQPA(int curId, int nextId, double pGn, _QRst bestRst) {
		double deltaBeta = _getDeltaBeta(curId, nextId, bestRst); // 蚂蚁移动时释放的信息素增量
		__QPA curQPA = curRst.QPA(curId, nextId);	// 当前解在本次移动时的量子信息素编码
		__QPA bestQPA = bestRst.QPA(curId, nextId); // 最优解在对应路径上的量子信息素编码(参考值)
		double theta = _getTheta(pGn, deltaBeta, curQPA, bestQPA); // 计算量子旋转门的旋转角θ
		_addQPA(curId, nextId, theta);	// 使用量子旋转门增加本次移动路径上信息素
	}
	
	private void minusMoveQPA(int curId, int nextId, double pGn, _QRst bestRst) {
		double deltaBeta = _getDeltaBeta(curId, nextId, bestRst); // 蚂蚁移动时释放的信息素增量
		__QPA curQPA = curRst.QPA(curId, nextId);	// 当前解在本次移动时的量子信息素编码
		__QPA bestQPA = bestRst.QPA(curId, nextId); // 最优解在对应路径上的量子信息素编码(参考值)
		double theta = _getTheta(pGn, deltaBeta, curQPA, bestQPA); // 计算量子旋转门的旋转角θ
		_addQPA(curId, nextId, -theta);	// 使用量子旋转门增加本次移动路径上信息素   FIXME 为什么有时是负数？
		_addQPA(curId, nextId, -theta);
	}
	
	/**
	 * 进化到下一代蚂蚁
	 *  （量子编码继承遗传，移动痕迹重置）
	 */
	private void evolve() {
		generation++;
		curRst.reset();
		Arrays.fill(tabus, false);
	}

	/**
	 * 移动到下一位置
	 * @param int nextId
	 */
	private int move(int nextId) {
		int curId = curRst.getCurId();
		int moveCost = (curId >= 0 ? ENV.dist(curId, nextId) : 0);
		tabus[nextId] = true;
		curId = curRst.move(nextId, moveCost) ? nextId : -1;
		return curId;
	}
	
	/**
	 * 选择第一个节点ID.
	 *   一般的TSP问题随机选择一个节点即可.
	 *   但若拓扑图有源宿点，则在源端或宿端随机选择一个.
	 * @return
	 */
	private int selectFirstId() {
//		int firstId = (RandomUtils.randomBoolean() ? ENV.srcId() : ENV.snkId());
//		return firstId;
		return ENV.srcId();
	}
	
	public static void main(String[] args) {
		System.out.println(Math.pow(0.5, 0.2));
		System.out.println(Math.pow(0.3, 0.2));
		System.out.println(Math.pow(0.1, 0.2));
	}

	/**
	 * 根据寻路决策的规则选择下一个移动的节点ID
	 * @return 若无路可走则返回-1
	 */
	private int selectNextId(int curId) {
		if(curId < 0) {
			return -1;
		}
		
		if(curId == 1) {
			System.out.println();
		}
		
		// FIXME 这两个值越小越重要， 且和为1？  若蚂蚁代数很大，依然未求得过一个解，则需要适当调整参数
		final double ZETA = 0.8D;	//ζ: 信息启发系数，反映了轨迹的重要性（协作性）
		final double GAMMA = 0.2D;	//γ: 期望启发系数，反映了能见度的重要性（创新性）
		
		int nextId = -1;
		final int SCOPE = 10, RAND_LIMIT = 8;
		int rand = RandomUtils.randomInt(SCOPE);

		// 蚂蚁以80%的概率以信息素作为决策方式进行路径转移（协作性优先）
		if(rand < RAND_LIMIT) {
			double argmax = -1;
			for(int i = curId, j = 0; j < ENV.size(); j++) {
				if(_isTabu(i, j)) {
					continue;
				}

				double arg = Math.pow(_getTau(i, j), ZETA) * 
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
			for(int i = curId, j = 0; j < ENV.size(); j++) {
				if(_isTabu(i, j)) {
					continue;
				}
				
				double arg = Math.pow(_getTau(i, j), ZETA) * 
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
	private boolean _isTabu(int curId, int nextId) {
		boolean isTabu = false;
		
		// 下一节点已处于禁忌表
		if(tabus[nextId]) {
			isTabu = true;
			
		// 当前节点与下一跳节点不连通
		} else if(!ENV.isLinked(curId, nextId)) {
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
	private double _getTau(int srcId, int snkId) {
		double beta = curRst.QPA(srcId, snkId).getBeta();
		double tau = beta * beta;
		return tau;
	}
	
	/**
	 * 检查到目前为止的移动轨迹是否为一个可行解
	 *  (剩余节点均不在必经点集中则认为已得到一个可行解)
	 * @return
	 */
	private boolean checkFeasible() {
		boolean isFeasible = true;
		for(int nodeId = 0; nodeId < ENV.size(); nodeId++) {
			if(!tabus[nodeId] && ENV.isInclude(nodeId)) {
				isFeasible = false;
				break;
			}
		}
		return isFeasible;
	}
	
	/**
	 * 更新移动路径上的量子编码
	 * @param curId
	 * @param nextId
	 * @param pGn
	 * @param bestRst
	 */
	private void updateMoveQPA(int curId, int nextId, double pGn, _QRst bestRst) {
		double deltaBeta = _getDeltaBeta(curId, nextId, bestRst); // 蚂蚁移动时释放的信息素增量
		__QPA curQPA = curRst.QPA(curId, nextId);	// 当前解在本次移动时的量子信息素编码
		__QPA bestQPA = bestRst.QPA(curId, nextId); // 最优解在对应路径上的量子信息素编码(参考值)
		double theta = _getTheta(pGn, deltaBeta, curQPA, bestQPA); // 计算量子旋转门的旋转角θ
		
		_addQPA(curId, nextId, theta);	// 使用量子旋转门增加本次移动路径上信息素
		if(ENV.isUseVolatilize()) {		// 对本次移动时 没有被选择的候选路径上的信息素 进行自然挥发
			_minusQPAs(curRst.getLastId(), curId, nextId, theta);
		}
	}
	
	/**
	 * 计算蚂蚁在srcId->snkId移动时释放的信息素
	 * @param srcId
	 * @param snkId
	 * @param bestRst
	 * @return srcId->snkId路径上的 [量子信息素增量] 的 β概率幅的平方
	 */
	private double _getDeltaBeta(int srcId, int snkId, final _QRst bestRst) {
		double beta = ENV.deltaBeta(srcId, snkId);
		beta = (beta + bestRst.QPA(srcId, snkId).getBeta()) / 2.0D;
		return beta * beta;
	}
	
	/**
	 * 计算量子旋转门的旋转角θ
	 * @param pGn 该量子蚂蚁的代数 与 最大代数 的代数比
	 * @param deltaBeta 某只量子蚂蚁当前从i->j转移时释放的信息素
	 * @param curQPA 该量子蚂蚁当前从i->j转移的量子编码(当前的路径信息素概率幅)
	 * @param bestQPA 最优解路径概率幅矩阵中，路径i->j的信息素概率幅
	 * @return 旋转角θ
	 */
	private double _getTheta(double pGn, double deltaBeta, 
			__QPA curQPA, __QPA bestQPA) {
		double theta = (MAX_THETA - DELTA_THETA * pGn) * deltaBeta;
		return theta;
	}

	// FIXME 这个旋转角方向是 当前解 与 最优解的偏差角，不能这样用
	// 当 当前解的beta概率幅 大于 最优参考解时， 旋转角方向为负，反之为正
	/**
	 * 计算量子旋转角的旋转方向
	 * @param curQPA 某只量子蚂蚁当前从i->j转移的量子编码(当前的路径信息素概率幅)
	 * @param bestQPA 最优解路径概率幅矩阵中，路径i->j的信息素概率幅
	 * @return 顺时针:1; 逆时针:-1
	 */
	private int __getThetaDirection(__QPA curQPA, __QPA bestQPA) {
		double pBest = bestQPA.getBeta() / bestQPA.getAlpha();
		double pCur = curQPA.getBeta() / curQPA.getAlpha();
		double atanBest = Math.atan(pBest);
		double atanCur = Math.atan(pCur);
		int direction = (((pBest / pCur) * (atanBest - atanCur)) >= 0 ? 1 : -1);
		return direction;
	}
	
	/**
	 * 使用量子旋转门更新量子编码: 
	 * 	加强src->snk的路径信息素
	 * @param srcId 路径起点
	 * @param snkId 路径终点
	 * @param theta 旋转角
	 */
	private void _addQPA(final int srcId, final int snkId, final double theta) {
		if(srcId == 0 && snkId == 1) {
			System.out.println();
		}
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
	 *  挥发除了 cur->last 和 cur->next 以外的与cur相关的路径信息素
	 *  (对称路径 last->cur 信息素 等同于 cur->last 信息素)
	 * @param lastId 上一节点
	 * @param curId 当前节点
	 * @param nextId 下一节点
	 * @param theta 旋转角
	 */
	private void _minusQPAs(int lastId, int curId, int nextId, double theta) {
		final double cosTheta = Math.cos(-theta);
		final double sinTheta = Math.sin(-theta);

		for(int j = 0; j < ENV.size(); j++) {
			if(j == lastId || j == nextId || !ENV.isLinked(curId, j)) {
				continue;
			}
			
			final double alpha = curRst.QPA(curId, j).getAlpha();
			final double beta = curRst.QPA(curId, j).getBeta();
			curRst.QPA(curId, j).setAlpha(cosTheta * alpha - sinTheta * beta);
			curRst.QPA(curId, j).setBeta(sinTheta * alpha + cosTheta * beta);
			curRst.QPA(j, curId).setAlpha(curRst.QPA(curId, j).getAlpha());
			curRst.QPA(j, curId).setBeta(curRst.QPA(curId, j).getBeta());
		}
	}

	/**
	 * 使用量子交叉对量子编码做变异处理 
	 */
	private void qCross() {
		for(int i = 0; i < ENV.size(); i++) {
			for(int j = 0; j <= i; j++) {
				if(!ENV.isLinked(i, j)) {
					continue;
				}
				
				final double beta = curRst.QPA(i, j).getBeta();
				curRst.QPA(i, j).setBeta(curRst.QPA(i, j).getAlpha());
				curRst.QPA(i, j).setAlpha(beta);
				curRst.QPA(j, i).setBeta(curRst.QPA(i, j).getBeta());
				curRst.QPA(j, i).setAlpha(curRst.QPA(i, j).getAlpha());			}
		}
	}
	
}
