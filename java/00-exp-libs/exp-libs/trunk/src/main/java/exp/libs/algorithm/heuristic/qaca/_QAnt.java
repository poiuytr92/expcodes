package exp.libs.algorithm.heuristic.qaca;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.num.RandomUtils;

/**
 * <PRE>
 * 量子蚂蚁
 * </PRE>
 * 
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2017-06-09
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
final class _QAnt {

	/** 日志�? */
	private final static Logger log = LoggerFactory.getLogger(_QAnt.class);
	
	/** ζ: 信息启发系数，反映了轨迹的重要性（协作性）, 值越小越重要 */
	private final double ZETA = 0.2D;
	
	/** γ: 期望启发系数，反映了能见度的重要性（创新性）, 值越小越重要 */
	private final double GAMMA = 2D;
	
	/**
	 * 用于计算转移策略的随机�?.
	 *  通常情况下蚂蚁有 80% 的概率使用寻路决策法则转移到下一节点.
	 *  但依然保�? 20%的概率使得蚂蚁随机转移到下一个节�?, 以确保创新�?.
	 */
	private final int RAND_SCOPE = 100, RAND_LIMIT = 80;
	
	/** 蚂蚁编号 */
	private int antId;
	
	/** 该蚂蚁已进化的代�? */
	private int generation;
	
	/** 蚂蚁寻路环境 */
	private final _QEnv ENV;
	
	/** 这只蚂蚁的寻路起点（第一代确定之后，后代不再变化�? */
	private final int SRC_ID;
	
	/** 这只蚂蚁的寻路终点（第一代确定之后，后代不再变化�? */
	private final int SNK_ID;
	
	/** 蚂蚁自身禁忌表（已走过的节点列入禁忌表） */
	private boolean[] tabus;

	/** 蚂蚁当前的移动数�?(当前局部解) */
	private QRst curRst;
	
	/** 该蚂蚁累计求得可行解的次�? */
	private int solveCnt;
	
	/** 该蚂蚁连续无解的次数 */
	private int unsolveCnt;
	
	/**
	 * 构造函�?
	 * @param ENV
	 */
	protected _QAnt(final int antId, final _QEnv ENV) {
		this.antId = antId;
		this.ENV = ENV;
		if(RandomUtils.genBoolean()) {
			this.SRC_ID = ENV.srcId();
			this.SNK_ID = ENV.snkId();
		} else {
			this.SRC_ID = ENV.snkId();
			this.SNK_ID = ENV.srcId();
		}
		
		this.generation = 0;
		this.tabus = new boolean[ENV.size()];
		this.curRst = new QRst(antId, ENV);
		this.solveCnt = 0;
		this.unsolveCnt = 0;
	}
	
	/**
	 * 尝试求一个可行解, 并遗传量子基因编码到下一代蚂�?
	 * @param bestRst 当前最优解（用于计算信息素的参考值）
	 * @return 是否得到可行�?
	 */
	protected boolean solve(final QRst bestRst) {
		boolean isFeasible = false;
		try {
			isFeasible = _solve(bestRst);
		} catch(Exception e) {
			log.error("编号�? [{}] 的蚂蚁在�? [{}] 求解过程中发生异�?.", antId, generation, e);
		}
		return isFeasible;
	}
	
	private boolean _solve(final QRst bestRst) {
		boolean isFeasible = true;
		evolve();	// 进化(清空父代移动痕迹, 并继承父代量子编�?)
		final double pGn = ((double) generation) / ENV.MAX_GENERATION(); // 代数�?
		int curId = move(selectFirstId());	// 移动到起始节�?
		
		// 计算蚂蚁之后移动的每一�?(最大步长为除了起始点之外的图节点数)
		for(int step = 1; step < ENV.size(); step++) {
			int nextId = selectNextId(curId);
			
			// 无路可走
			if(nextId < 0) {
				isFeasible = checkFeasible();	// 检查是否已得到一个可行解(此处只针对无源宿端的拓扑�?)
				if(!isFeasible) {	// 对于非可行解, 2倍挥发掉本次移动轨迹中的所有信息素（负反馈到后代）
					minusRouteQPAs(curRst, pGn, bestRst);
				}
				break;
			}
			
			// 蚂蚁移动到下一节点，并在路径上释放信息�?
			addMoveQPA(curId, nextId, pGn, bestRst);
			curId = move(nextId);
			
			// 若蚂蚁移动到的下一节点就是终点，则退出寻�?
			// (前面寻路决策中已经决定了此处不可能还存在未访问的必经点的情况)
			if(nextId == SNK_ID) {
				isFeasible = true;
				break;
			}
		}
		
		// 标记求得可行�?
		if(isFeasible == true) {
			solveCnt++;
			unsolveCnt = 0;
			curRst.markVaild();
			qCross(); // 得到一个可行解后马上进行变异处理， 提高其他路径的搜索概率， 避免陷入局部最优解
			
		// 当连续无解次数越限时，执行变异处理，避免搜索陷入停滞
		} else {
			unsolveCnt++;
			if(ENV.isUseQCross() && unsolveCnt >= ENV.QCROSS_THRESHOLD()) {
				qCross();
			}
		}
		return isFeasible;
	}
	
	/**
	 * 进化到下一代蚂�?
	 *  （量子编码继承遗传，移动痕迹重置�?
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
		curId = curRst.move(nextId, moveCost) ? nextId : QRst.INVAILD_ID;
		return curId;
	}
	
	/**
	 * 选择第一个节点ID.
	 *   一般的TSP问题随机选择一个节点即�?.
	 *   但若拓扑图有源宿点，则在源端或宿端随机选择一�?.
	 *   (同族蚂蚁的每一代起始点是固定的�? 但是不同族蚂蚁可以有不同的起始点)
	 * @return
	 */
	private int selectFirstId() {
		int firstId = QRst.INVAILD_ID;
		if(SRC_ID > QRst.INVAILD_ID) {
			firstId = SRC_ID;
			
		} else {
			firstId = RandomUtils.genInt(ENV.size());
		}
		return firstId;
	}
	
	/**
	 * 根据寻路决策的规则选择下一个移动的节点ID
	 * @return 
	 */
	private int selectNextId(int curId) {
		int nextId = QRst.INVAILD_ID;
		if(curId < 0) {
			return nextId;
		}
		
		// 蚂蚁�?80%的概率以信息素作为决策方式进行路径转移（协作性优先）
		int rand = RandomUtils.genInt(RAND_SCOPE);
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
			
		// 蚂蚁�?20%的概率以随机方式进行路径转移（保持创新性）
		} else {
			List<Integer> nextIds = new LinkedList<Integer>();
			for(int a = curId, z = 0; z < ENV.size(); z++) {
				if(_isTabu(a, z)) {
					continue;
				}
				nextIds.add(z);
			}
			
			if(nextIds.size() > 0) {
				int idx = RandomUtils.genInt(nextIds.size());
				nextId = nextIds.get(idx);
			}
		}
		return nextId;
	}
	
	/**
	 * 检查下一跳是否可�?
	 * @param nodeId
	 * @return
	 */
	private boolean _isTabu(int curId, int nextId) {
		boolean isTabu = false;
		
		// 下一节点已处于禁忌表
		if(tabus[nextId]) {
			isTabu = true;
			
		// 当前节点与下一跳节点不连�?
		} else if(!ENV.isLinked(curId, nextId)) {
			isTabu = true;
			
		// 当下一跳节点为终点节点�?
		} else if(nextId == SNK_ID) {
			isTabu = true;
			
			// 下一跳是最后一�?
			if(curRst.getStep() + 1 == ENV.size()) {
				isTabu = false;
				
			// 下一跳不是最后一条，但所有必经点已被访问�?
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
	 * 当未遍历完全图时�? 检查到目前为止的移动轨迹是否为一个可行解
	 *  (剩余节点均不在必经点集中则认为已得到一个可行解)
	 * @return
	 */
	private boolean checkFeasible() {
		boolean isFeasible = true;
		
		// 若不存在必经点集�? 则表示需要全图遍历， 亦即当前解必定不是可行解
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
	
	/**
	 * 在移动路径上释放信息�?
	 * @param curId
	 * @param nextId
	 * @param pGn
	 * @param bestRst
	 */
	private void addMoveQPA(int curId, int nextId, double pGn, QRst bestRst) {
		double theta = _getTheta(curId, nextId, pGn, bestRst);
		_updateQPA(curId, nextId, theta);
	}
	
	/**
	 * 当得不到可行解时，对移动轨迹上的所有路径进�?2倍的信息素挥发， 以负反馈到后�?
	 * @param rst
	 * @param pGn
	 * @param bestRst
	 */
	private void minusRouteQPAs(QRst rst, double pGn, QRst bestRst) {
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
	 * 计算量子旋转门的旋转角�?
	 * @param pGn 该量子蚂蚁的代数 �? 最大代�? 的代数比
	 * @param deltaBeta 某只量子蚂蚁当前从i->j转移时释放的信息�?
	 * @param curQPA 该量子蚂蚁当前从i->j转移的量子编�?(当前的路径信息素概率�?)
	 * @param bestQPA 最优解路径概率幅矩阵中，路径i->j的信息素概率�?
	 * @return 旋转角�?
	 */
	private double _getTheta(int curId, int nextId, double pGn, QRst bestRst) {
		double deltaBeta = __getDeltaBeta(curId, nextId, bestRst); // 蚂蚁移动时释放的信息素增�?
		double theta = (_QEnv.MAX_THETA - _QEnv.DELTA_THETA * pGn) * deltaBeta;
		return theta;
	}
	
	/**
	 * 计算蚂蚁在srcId->snkId移动时释放的信息�?
	 * @param srcId
	 * @param snkId
	 * @param bestRst
	 * @return srcId->snkId路径上的 [量子信息素增量] �? β概率幅的平方
	 */
	private double __getDeltaBeta(int srcId, int snkId, final QRst bestRst) {
		double beta = ENV.deltaBeta(srcId, snkId);
		beta = (beta + bestRst.QPA(srcId, snkId).getBeta()) / 2.0D;
		return beta * beta;
	}

	/**
	 * 使用量子旋转门更新量子编�?: 
	 * 	加强src->snk的路径信息素
	 * @param srcId 路径起点
	 * @param snkId 路径终点
	 * @param theta 旋转�?: 正向(>0)为增�?, 逆向(<0)为减�?
	 */
	private void _updateQPA(final int srcId, final int snkId, final double theta) {
		final __QPA azQPA = curRst.QPA(srcId, snkId);
		final __QPA zaQPA = curRst.QPA(snkId, srcId);
		final double cosTheta = Math.cos(theta);
		final double sinTheta = Math.sin(theta);
		final double alpha = azQPA.getAlpha();
		final double beta = azQPA.getBeta();
		azQPA.setAlpha(Math.abs(cosTheta * alpha - sinTheta * beta));
		azQPA.setBeta(Math.abs(sinTheta * alpha + cosTheta * beta));
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
	
	/**
	 * 计算当前解相对于最优解的量子旋转角的旋转方�?
	 *   当前解的概率�? 大于 最优解时， 旋转角方向为负，反之为正
	 * @param curQPA 某只量子蚂蚁当前从i->j转移的量子编�?(当前的路径信息素概率�?)
	 * @param bestQPA 最优解路径概率幅矩阵中，路径i->j的信息素概率�?
	 * @return 顺时�?:1; 逆时�?:-1
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private int __getThetaDirection(__QPA curQPA, __QPA bestQPA) {
		double pBest = bestQPA.getBeta() / bestQPA.getAlpha();
		double pCur = curQPA.getBeta() / curQPA.getAlpha();
		double atanBest = Math.atan(pBest);
		double atanCur = Math.atan(pCur);
		int direction = (((pBest / pCur) * (atanBest - atanCur)) >= 0 ? 1 : -1);
		return direction;
	}
	
	/**
	 * 获取蚂蚁编号
	 * @return
	 */
	protected int ID() {
		return antId;
	}
	
	/**
	 * 获取本次求解过程的结�?
	 * @return
	 */
	protected QRst getResult() {
		return curRst;
	}
	
	/**
	 * 获取蚂蚁在所有代数中得到可行解总数
	 * @return
	 */
	protected int getSolveCnt() {
		return solveCnt;
	}
	
}
