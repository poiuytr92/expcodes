package exp.libs.algorithm.graph.adt;

public class Edge {

	private Node src;
	
	private Node end;
	
	/** 权重: null表示不可达 */
	private Double weight;
	
	/**
	 * 
	 * @param src
	 * @param end
	 * @param weight 权重: null表示不可达 
	 */
	public Edge(Node src, Node end, Double weight) {
		this.src = src;
		this.end = end;
		this.weight = weight;
	}

	public Node getSrc() {
		return src;
	}

	public Node getEnd() {
		return end;
	}

	public Double getWeight() {
		return weight;
	}
	
}
