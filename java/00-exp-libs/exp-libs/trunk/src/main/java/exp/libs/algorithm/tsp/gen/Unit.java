package exp.libs.algorithm.tsp.gen;

import java.util.ArrayList;
import java.util.List;

import exp.libs.algorithm.tsp.graph.Node;

// 个体
public class Unit implements Comparable<Unit> {

	// 基因编码
	private List<Node> genCode;
	
	// 基因评分（适度值）
	private int genScore;
	
	public Unit(List<Node> path) {
		this.genCode = (path == null ? 
				new ArrayList<Node>() : new ArrayList<Node>(path));
		this.genScore = Integer.MAX_VALUE;
	}
	
	public List<Node> getGenCode() {
		return new ArrayList<Node>(genCode);
	}
	
	public int getGenScore() {
		return genScore;
	}

	public void setGenScore(int genScore) {
		this.genScore = genScore;
	}

	@Override
	public int compareTo(Unit other) {
		if(other == null) {
			return 0;
		}
		return (this.getGenScore() >= other.getGenScore() ? 1 : -1);
	}
	
}
