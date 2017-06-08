package exp.libs.algorithm.tsp.qant.bean;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import exp.libs.utils.pub.RandomUtils;

/**
 * 量子蚂蚁
 */
class QAnt {

	/** 该蚂蚁已进化的代数 */
	private int generation;
	
	/** 该蚂蚁求得可行解的次数 */
	private int solveCnt;
	
	/** 该蚂蚁所携带的所有路径信息素的概率幅(量子基因编码) */
	private QPA[][] _QPAs;
	
	/** 该蚂蚁需要移动的拓扑图规模(节点数) */
	private final int graphSize;
	
	/** 拓扑图源点编号 */
	private final int graphSrcId;
	
	/** 拓扑图宿点编号 */
	private final int graphSnkId;
	
	/** 该蚂蚁需要移动的拓扑图节点间距常量 */
	private final int[][] graphDist;
	
	/** 蚂蚁移动从i->j移动的自启发常量(即能见度, 与i->j的距离成反比) */
	private final double[][] eta;
	
	/** 蚂蚁当前所在的节点编号 */
	private int curLocationId;
	
	/** 蚂蚁自身禁忌表（已走过的节点列入禁忌表） */
	private boolean[] tabus;

	/** 蚂蚁到目前为止的移动轨迹(局部解/当前解), 以及节点顺序集 */
	private int[] moveTrack;
	
	/** 蚂蚁到目前为止的移动步数 */
	private int moveStep;
	
	/** 蚂蚁到目前为止的移动轨迹代价(移动路径的边权总和, 评估当前解是否为最优解的参考值) */
	private int moveCost;
	
	/**
	 * 构造函数
	 * @param graphSrcId 拓扑图源点
	 * @param graphSnkId 拓扑图宿点
	 * @param graphDist 拓扑图节点间距
	 * @param eta 拓扑图节点间的能见度（自启发常量， 为节点间距的倒数）
	 */
	protected QAnt(final int graphSrcId, final int graphSnkId, 
			final int[][] graphDist, final double[][] eta) {
		this.graphSize = graphDist.length;
		this.graphSrcId = graphSrcId;
		this.graphSnkId = graphSnkId;
		this.graphDist = graphDist;
		this.eta = eta;
		
		this.generation = 0;
		this.solveCnt = 0;
		this.tabus = new boolean[graphSize];
		this.moveTrack = new int[graphSize];
		
		initQPAs();
	}
	
	/**
	 * 初始化蚂蚁的量子编码
	 */
	private void initQPAs() {
		this._QPAs = new QPA[graphSize][graphSize];
		for(int i = 0; i < graphSize; i++) {
			for(int j = 0; j <= i; j++) {
				_QPAs[i][j] = new QPA();
				if(eta[i][j] == 0) {
					_QPAs[i][j].setAlpha(1D);
					_QPAs[i][j].setBeta(0D);
				}
				
				if(i != j) {
					_QPAs[j][i] = new QPA();
					if(eta[j][i] == 0) {
						_QPAs[j][i].setAlpha(1D);
						_QPAs[j][i].setBeta(0D);
					}
				}
			}
		}
	}
	
	/**
	 * 进化到下一代蚂蚁
	 *  （量子编码继承遗传，移动痕迹重置）
	 */
	protected void evolve() {
		generation++;
		curLocationId = -1;
		moveStep = 0;
		moveCost = 0;
		
		Arrays.fill(tabus, false);
		Arrays.fill(moveTrack, -1);
	}

	protected int getGeneration() {
		return generation;
	}
	
	@Deprecated
	protected QPA[][] getQtpa() {
		return _QPAs;
	}
	
	/**
	 * 根据寻路决策的规则选择下一个移动的节点ID
	 * @return 若无路可走则返回-1
	 */
	protected int selectNextId() {
		// FIXME 这两个值越小越重要， 且和为1？  若蚂蚁代数很大，依然未求得过一个解，则需要适当调整参数
		final double ZETA = 0.2D;	//ζ: 信息启发系数，反映了轨迹的重要性（协作性）
		final double GAMMA = 0.8D;	//γ: 期望启发系数，反映了能见度的重要性（创新性）
		
		int nextId = -1;
		final int SCOPE = 10, RAND_LIMIT = 8;
		int rand = RandomUtils.randomInt(SCOPE);

		// 蚂蚁以80%的概率以信息素作为决策方式进行路径转移（协作性优先）
		if(rand < RAND_LIMIT) {
			double argmax = -1;
			for(int i = curLocationId, j = 0; j < graphSize; j++) {
				if(isTabu(j)) {
					continue;
				}

				double arg = Math.pow(getTau(i, j), ZETA) * 
						Math.pow(eta[i][j], GAMMA);
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
			for(int i = curLocationId, j = 0; j < graphSize; j++) {
				if(isTabu(j)) {
					continue;
				}
				
				double arg = Math.pow(getTau(i, j), ZETA) * 
						Math.pow(eta[i][j], GAMMA);
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
		} else if(graphDist[curLocationId][nextId] == Integer.MAX_VALUE) {
			isTabu = true;
			
		// 当下一跳节点为拓扑图的源宿点时，则下一跳只能是最后一跳
		} else if((nextId == graphSrcId || nextId == graphSnkId)) {
			isTabu = !(moveStep + 1 == graphSize);
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
		double beta = _QPAs[srcId][snkId].getBeta();
		double tau = beta * beta;
		return tau;
	}
	
	/**
	 * 移动到下一位置
	 * @param nodeId
	 */
	protected void move(int nodeId) {
		if(curLocationId >= 0) {
			tabus[curLocationId] = true;
			moveStep++;
		}
		
		// 哽新、挥发对应路径上的信息素
		
		curLocationId = nodeId;
	}

	protected int getCurLocationId() {
		return curLocationId;
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
		final double alpha = _QPAs[srcId][snkId].getAlpha();
		final double beta = _QPAs[srcId][snkId].getBeta();
		_QPAs[srcId][snkId].setAlpha(cosTheta * alpha - sinTheta * beta);
		_QPAs[srcId][snkId].setBeta(sinTheta * alpha + cosTheta * beta);
		_QPAs[snkId][srcId].setAlpha(_QPAs[srcId][snkId].getAlpha());
		_QPAs[snkId][srcId].setBeta(_QPAs[srcId][snkId].getBeta());
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

		for(int j = 0; j < graphSize; j++) {
			if(j == preId || j == nextId) {
				continue;
			}
			
			final double alphaTmp = _QPAs[curId][j].getAlpha();
			final double betaTmp = _QPAs[curId][j].getBeta();
			_QPAs[curId][j].setAlpha(cosTheta * alphaTmp - sinTheta * betaTmp);
			_QPAs[curId][j].setBeta(sinTheta * alphaTmp + cosTheta * betaTmp);
			_QPAs[j][curId].setAlpha(_QPAs[curId][j].getAlpha());
			_QPAs[j][curId].setBeta(_QPAs[curId][j].getBeta());
		}
	}

	/**
	 * 使用量子交叉对量子编码做变异处理 
	 */
	protected void qCross() {
		for(int i = 0; i < graphSize; i++) {
			for(int j = 0; j <= i; j++) {
				final double beta = _QPAs[i][j].getBeta();
				_QPAs[i][j].setBeta(_QPAs[i][j].getAlpha());
				_QPAs[i][j].setAlpha(beta);
				_QPAs[j][i].setBeta(_QPAs[i][j].getBeta());
				_QPAs[j][i].setAlpha(_QPAs[i][j].getAlpha());			}
		}
	}
		
}
