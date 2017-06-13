package exp.libs.algorithm.tsp;

import java.util.LinkedList;
import java.util.List;

import exp.libs.algorithm.tsp.graph.Node;

public final class TSPRst {

	/** 无效解 */
	public final static TSPRst NULL = new TSPRst();
	
	/** 是否为可行解 */
	private boolean isVaild;
	
	/** 解的总开销 */
	private int cost;
	
	/** 移动轨迹路由 */
	private List<Node> routes;
	
	protected TSPRst() {
		this.isVaild = false;
		this.cost = -1;
		this.routes = new LinkedList<Node>();
	}

	public boolean isVaild() {
		return isVaild;
	}

	protected void setVaild(boolean isVaild) {
		this.isVaild = isVaild;
	}

	public int getCost() {
		return cost;
	}

	protected void setCost(int cost) {
		this.cost = cost;
	}

	public List<Node> getRoutes() {
		return new LinkedList<Node>(routes);
	}

	protected void setRoutes(List<Node> routes) {
		this.routes = routes;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[vaild] : ").append(isVaild);
		sb.append("\r\n[cost] : ").append(cost);
		sb.append("\r\n[route] : ");
		if(isVaild == true) {
			int size = routes.size();
			for(int i = 0; i < size - 1; i++) {
				sb.append(routes.get(i)).append(" -> ");
			}
			sb.append(routes.get(size - 1));
		} else {
			sb.append("null");
		}
		return sb.toString();
	}
	
}
