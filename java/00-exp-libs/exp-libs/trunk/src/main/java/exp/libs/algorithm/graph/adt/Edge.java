package exp.libs.algorithm.graph.adt;

public class Edge {

	private Node src;
	
	private Node end;
	
	/** 权重: null表示不可达 */
	private Double weight;
	
	public Edge(String srcNodeName, String endNodeName, Double weight) {
		this.src = new Node(srcNodeName);
		this.end = new Node(endNodeName);
		this.weight = weight;
	}
	
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
	
	public void setSrc(Node src) {
		this.src = src;
	}

	public Node getEnd() {
		return end;
	}
	
	public void setEnd(Node end) {
		this.end = end;
	}

	public Double getWeight() {
		return weight;
	}
	
}
