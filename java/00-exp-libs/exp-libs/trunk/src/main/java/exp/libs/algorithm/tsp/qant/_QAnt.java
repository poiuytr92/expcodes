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

	private final static int INVAILD_ID = -1;
	
	/** 该蚂蚁已进化的代数 */
	private int generation;
	
	/** 蚂蚁寻路环境 */
	private final _QEnv ENV;
	
	/** 这只蚂蚁的寻路起点（第一代确定之后，后代不再变化） */
	private final int SRC_ID;
	
	/** 这只蚂蚁的寻路终点（第一代确定之后，后代不再变化） */
	private final int SNK_ID;
	
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
		if(RandomUtils.randomBoolean()) {
			this.SRC_ID = ENV.srcId();
			this.SNK_ID = ENV.snkId();
		} else {
			this.SRC_ID = ENV.snkId();
			this.SNK_ID = ENV.srcId();
		}
		
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
			
			// 无路可走
			if(nextId < 0) {
				isFeasible = checkFeasible();	// 检查是否已得到一个可行解(此处只针对无源宿端的拓扑图)
				if(!isFeasible) {	// 对于非可行解, 2倍挥发掉本次移动轨迹中的所有信息素
					minusRouteQPAs(curRst, pGn, bestRst);
				}
				break;
			}
			
			// 蚂蚁移动到下一节点，并在路径上释放信息素
			addMoveQPA(curId, nextId, pGn, bestRst);
			curId = move(nextId);
			
			// 若蚂蚁移动到的下一节点就是终点，则退出寻路
			// (前面寻路决策中已经决定了此处不可能还存在未访问的必经点的情况)
			if(nextId == SNK_ID) {
				isFeasible = true;
				break;
			}
		}
		
		// 标记求得可行解
		if(isFeasible == true) {
			solveCnt++;
			unsolveCnt = 0;
			curRst.markVaild();
			// FIXME 挥发可行解以外的其他路径的信息素(自然挥发)
			
		// 当连续无解次数越限时，执行量子交叉打乱量子编码，避免搜索陷入停滞
		} else {
			unsolveCnt++;
			if(ENV.isUseQCross() && unsolveCnt >= ENV.QCROSS_THRESHOLD()) {
				qCross();
			}
		}
		return isFeasible;
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
		curId = curRst.move(nextId, moveCost) ? nextId : INVAILD_ID;
		return curId;
	}
	
	/**
	 * 选择第一个节点ID.
	 *   一般的TSP问题随机选择一个节点即可.
	 *   但若拓扑图有源宿点，则在源端或宿端随机选择一个.
	 *   (同族蚂蚁的每一代起始点是固定的， 但是不同族蚂蚁可以有不同的起始点)
	 * @return
	 */
	private int selectFirstId() {
		int firstId = INVAILD_ID;
		if(SRC_ID > INVAILD_ID) {
			firstId = SRC_ID;
			
		} else {
			firstId = RandomUtils.randomInt(ENV.size());
		}
		return firstId;
	}
	
	/**
	 * 根据寻路决策的规则选择下一个移动的节点ID
	 * @return 
	 */
	private int selectNextId(int curId) {
		int nextId = INVAILD_ID;
		if(curId < 0) {
			return nextId;
		}
		
		// FIXME 这两个值越小越重要，   若蚂蚁代数很大，依然未求得过一个解，则需要适当调整参数
		final double ZETA = 0.2D;	//ζ: 信息启发系数，反映了轨迹的重要性（协作性）
		final double GAMMA = 2D;	//γ: 期望启发系数，反映了能见度的重要性（创新性）
		final int SCOPE = 10, RAND_LIMIT = 8;
		int rand = RandomUtils.randomInt(SCOPE);

		// 蚂蚁以80%的概率以信息素作为决策方式进行路径转移（协作性优先）
		if(rand < RAND_LIMIT) {
			double argmax = -1;
			for(int a = curId, z = 0; z < ENV.size(); z++) {
				if(_isTabu(a, z)) {
					continue;
				}

				double arg = Math.pow(_getTau(a, z), ZETA) + 
						Math.pow(ENV.eta(a, z), GAMMA);
				if(argmax <= arg) {
					argmax = arg;
					nextId = z;
				}
			}
			
		// 蚂蚁以20%的概率以随机方式进行路径转移（保持创新性）
		} else {
			final double fRand = RandomUtils.randomInt(SCOPE) / (SCOPE * 1.0D);
			double sum = 0;
			
			Map<Integer, Double> map = new LinkedHashMap<Integer, Double>();
			for(int a = curId, z = 0; z < ENV.size(); z++) {
				if(_isTabu(a, z)) {
					continue;
				}
				
				double arg = Math.pow(_getTau(a, z), ZETA) + 
						Math.pow(ENV.eta(a, z), GAMMA);
				sum += arg;
				map.put(z, arg);
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
			
		// 当下一跳节点为终点节点时
		} else if(nextId == SNK_ID) {
			isTabu = true;
			
			// 下一跳是最后一跳
			if(curRst.getStep() + 1 == ENV.size()) {
				isTabu = false;
				
			// 下一跳不是最后一条，但所有必经点已被访问过
			} else {
				int[] includes = ENV.getIncludes();
				if(includes.length > 0) {
					boolean allVisit = true;
					for(int include : includes) {
						if(tabus[include] == false) {
							allVisit = false;
							break;
						}
					}
					isTabu = !allVisit;
				}
			}
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
	 * 当未遍历完全图时， 检查到目前为止的移动轨迹是否为一个可行解
	 *  (剩余节点均不在必经点集中则认为已得到一个可行解)
	 * @return
	 */
	private boolean checkFeasible() {
		boolean isFeasible = true;
		
		// 若不存在必经点集， 则表示需要全图遍历， 亦即当前解必定不是可行解
		if(ENV.getIncludes().length <= 0) {
			isFeasible = false;
			
		// 若存在必经点集，则检查所有未访问节点中（包括源宿点）是否还存在必经点
		} else {
			for(int nodeId = 0; nodeId < ENV.size(); nodeId++) {
				if(!tabus[nodeId] && ENV.isInclude(nodeId)) {
					isFeasible = false;
					break;
				}
			}
		}
		return isFeasible;
	}
	
	private void addMoveQPA(int curId, int nextId, double pGn, _QRst bestRst) {
		double theta = _getTheta(curId, nextId, pGn, bestRst);
		_updateQPA(curId, nextId, theta);
	}
	
	private void minusRouteQPAs(_QRst rst, double pGn, _QRst bestRst) {
		int[] route = rst.getRoutes();
		if(route.length > 1) {
			for(int step = 0; step < rst.getStep() - 1; step++) {
				int aId = route[step];
				int zId = route[step + 1];
				double theta = -2 * _getTheta(aId, zId, pGn, bestRst);
				_updateQPA(aId, zId, theta);
			}
		}
	}
	
	/**
	 * 计算量子旋转门的旋转角θ
	 * @param pGn 该量子蚂蚁的代数 与 最大代数 的代数比
	 * @param deltaBeta 某只量子蚂蚁当前从i->j转移时释放的信息素
	 * @param curQPA 该量子蚂蚁当前从i->j转移的量子编码(当前的路径信息素概率幅)
	 * @param bestQPA 最优解路径概率幅矩阵中，路径i->j的信息素概率幅
	 * @return 旋转角θ
	 */
	private double _getTheta(int curId, int nextId, double pGn, _QRst bestRst) {
		double deltaBeta = __getDeltaBeta(curId, nextId, bestRst); // 蚂蚁移动时释放的信息素增量
		double theta = (_QEnv.MAX_THETA - _QEnv.DELTA_THETA * pGn) * deltaBeta;
		return theta;
	}
	
	/**
	 * 计算蚂蚁在srcId->snkId移动时释放的信息素
	 * @param srcId
	 * @param snkId
	 * @param bestRst
	 * @return srcId->snkId路径上的 [量子信息素增量] 的 β概率幅的平方
	 */
	private double __getDeltaBeta(int srcId, int snkId, final _QRst bestRst) {
		double beta = ENV.deltaBeta(srcId, snkId);
		beta = (beta + bestRst.QPA(srcId, snkId).getBeta()) / 2.0D;
		return beta * beta;
	}

	/**
	 * 使用量子旋转门更新量子编码: 
	 * 	加强src->snk的路径信息素
	 * @param srcId 路径起点
	 * @param snkId 路径终点
	 * @param theta 旋转角: 正向(>0)为增加, 逆向(<0)为减少
	 */
	private void _updateQPA(final int srcId, final int snkId, final double theta) {
		final __QPA azQPA = curRst.QPA(srcId, snkId);
		final __QPA zaQPA = curRst.QPA(snkId, srcId);
		final double cosTheta = Math.cos(theta);
		final double sinTheta = Math.sin(theta);
		final double alpha = azQPA.getAlpha();
		final double beta = azQPA.getBeta();
		azQPA.setAlpha(Math.abs(cosTheta * alpha - sinTheta * beta)); // 此处可能为负， 避免影响beta值， 取绝对值
		azQPA.setBeta(sinTheta * alpha + cosTheta * beta);
		zaQPA.setAlpha(azQPA.getAlpha());
		zaQPA.setBeta(azQPA.getBeta());
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
				
				final __QPA azQPA = curRst.QPA(i, j);
				final __QPA zaQPA = curRst.QPA(j, i);
				final double beta = azQPA.getBeta();
				azQPA.setBeta(azQPA.getAlpha());
				azQPA.setAlpha(beta);
				zaQPA.setBeta(azQPA.getBeta());
				zaQPA.setAlpha(azQPA.getAlpha());
			}
		}
	}
	
}
