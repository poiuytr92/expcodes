package exp.libs.algorithm.tsp.qant;

import java.util.Arrays;

/**
 * 
 * <PRE>
 * 可行解
 * </PRE>
 * 
 * @author lqb
 * @date 2017年6月9日
 */
class _QRst {

	/** 拓扑图规模 */
	private int size;
	
	/** 蚂蚁所携带的所有路径信息素的概率幅(量子基因编码, 每代求解遗传) */
	private __QPA[][] _QPAs;
	
	/** 蚂蚁在当代的移动轨迹的总开销 */
	private int cost;
	
	/** 蚂蚁在当代的移动步长 */
	private int step;
	
	/** 蚂蚁在当代的移动轨迹 */
	private int[] routes;
	
	/** 是否为可行解 */
	private boolean isVaild;
	
	protected _QRst(final _QEnv env) {
		this.size = env.size();
		this.routes = new int[size];
		initQPAs(env);
		reset();
	}
	
	/**
	 * 初始化蚂蚁的量子编码
	 */
	private void initQPAs(final _QEnv env) {
		this._QPAs = new __QPA[env.size()][env.size()];
		for(int i = 0; i < env.size(); i++) {
			for(int j = 0; j <= i; j++) {
				_QPAs[i][j] = new __QPA();
				if(env.eta(i, j) == 0) {
					_QPAs[i][j].setAlpha(1D);
					_QPAs[i][j].setBeta(0D);
				}
				
				if(i != j) {
					_QPAs[j][i] = new __QPA();
					if(env.eta(j, i) == 0) {
						_QPAs[j][i].setAlpha(1D);
						_QPAs[j][i].setBeta(0D);
					}
				}
			}
		}
	}
	
	protected void reset() {
		this.isVaild = false;
		this.cost = 0;
		this.step = 0;
		Arrays.fill(routes, -1);
	}
	
	protected boolean move(int nextRouteId, int routeCost) {
		boolean isOk = false;
		if(step < size && nextRouteId >= 0 && routeCost >= 0) {
			routes[step++] = nextRouteId;
			cost += routeCost;
			isVaild = (step == size);
			isOk = true;
		}
		return isOk;
	}
	
	@Deprecated
	protected __QPA[][] getQPAs() {
		return _QPAs;
	}
	
	protected __QPA QPA(int srcId, int snkId) {
		return _QPAs[srcId][snkId];
	}

	protected int getCost() {
		return cost;
	}
	
	protected void setCost(int cost) {
		this.cost = cost;
	}

	protected int getStep() {
		return step;
	}
	
	protected int[] getRoutes() {
		return routes;
	}
	
	protected int getCurId() {
		return routes[step];
	}
	
	protected int getLastId() {
		int lastId = -1;
		if(step > 0) {
			lastId = routes[step - 1];
		}
		return lastId;
	}
	
	protected boolean isVaild() {
		return isVaild;
	}

	protected void markVaild() {
		this.isVaild = true;
	}

	protected void clone(_QRst other) {
		if(other != null && this.size == other.size) {
			this.cost = other.cost;
			this.step = other.step;
			for(int i = 0; i < size; i++) {
				this.routes[i] = other.routes[i];
				for(int j = 0; j < size; j++) {
					this._QPAs[i][j].setAlpha(other._QPAs[i][j].getAlpha());
					this._QPAs[i][j].setBeta(other._QPAs[i][j].getBeta());
				}
			}
		}
	}

}
