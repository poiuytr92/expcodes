package exp.libs.algorithm.tsp.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Graph {

	/** 是否为有向图 */
	private boolean arrow;
	
	private Map<String, Node> nodes;
	
	private Map<String, Edge> edges;
	
	public Graph() {
		this(false);
	}
	
	public Graph(boolean arrow) {
		this.arrow = arrow;
		this.nodes = new HashMap<String, Node>();
		this.edges = new HashMap<String, Edge>();
	}
	
	public void clear() {
		Iterator<Node> nodeIts = nodes.values().iterator();
		while(nodeIts.hasNext()) {
			nodeIts.next().clear();
		}
		nodes.clear();
		edges.clear();
	}
	
	public boolean isArrow() {
		return arrow;
	}
	
	public boolean isEmpty() {
		return (nodeSize() <= 0);
	}
	
	public int nodeSize() {
		return nodes.size();
	}
	
	public int edgeSize() {
		return edges.size();
	}
	
	public boolean addEdge(String nSrc, String nSnk, int weight) {
		boolean isOk = false;
		if(nSrc != null && nSnk != null && weight >= 0) {
			Node src = addNode(nSrc);
			Node snk = addNode(nSnk);
			src.addNeighbor(snk);
			nodes.put(nSrc, src);
			
			if(arrow == false) {
				snk.addNeighbor(src);
				nodes.put(nSnk, snk);
			}
			
			Edge edge = new Edge(arrow, src, snk, weight);
			edges.put(edge.getKey(), edge);
		}
		return isOk;
	}
	
	protected Node addNode(String name) {
		Node node = nodes.get(name);
		if(node == null) {
			node = new Node(nodes.size(), name);
			nodes.put(name, node);
		}
		return node;
	}
	
	public Node getNode(String name) {
		Node node = nodes.get(name);
		return (node == null ? Node.NULL : node);
	}
	
	public Edge getEdge(String nSrc, String nSnk) {
		return getEdge(getNode(nSrc), getNode(nSnk));
	}
	
	public Edge getEdge(Node src, Node snk) {
		Edge edge = edges.get(Edge.toKey(arrow, src, snk));
		return (edge == null ? Edge.NULL : edge);
	}
	
	public int getWeight(String nSrc, String nSnk) {
		return getEdge(nSrc, nSnk).getWeight();
	}
	
	public int getWeight(Node src, Node snk) {
		return getEdge(src, snk).getWeight();
	}
	
	public Set<Node> getAllNodes() {
		return new HashSet<Node>(nodes.values());
	}
	
	public Set<Edge> getAllEdges() {
		return new HashSet<Edge>(edges.values());
	}

}
