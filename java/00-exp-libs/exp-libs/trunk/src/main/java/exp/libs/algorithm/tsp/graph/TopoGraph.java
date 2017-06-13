package exp.libs.algorithm.tsp.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TopoGraph extends Graph {

	private Node src;
	
	private Node snk;
	
	private boolean order;
	
	private List<Node> includes;
	
	public TopoGraph() {
		this(false, false);
	}
	
	public TopoGraph(boolean arrow) {
		this(arrow, false);
	}
	
	public TopoGraph(boolean arrow, boolean order) {
		super(arrow);
		this.src = Node.NULL;
		this.snk = Node.NULL;
		this.order = order;
		this.includes = (order ? new LinkedList<Node>() : new ArrayList<Node>());
	}

	public Node getSrc() {
		return src;
	}

	public void setSrc(String name) {
		this.src = addNode(name);
	}

	public Node getSnk() {
		return snk;
	}

	public void setSnk(String name) {
		this.snk = addNode(name);
	}
	
	public List<Node> getIncludes() {
		return (order ? new LinkedList<Node>(includes) : new ArrayList<Node>(includes));
	}
	
	public Set<String> getIncludeNames() {
		Set<String> names = new HashSet<String>();
		for(Node include : includes) {
			names.add(include.getName());
		}
		return names;
	}
	
	public Set<Integer> getIncludeIds() {
		Set<Integer> ids = new HashSet<Integer>();
		for(Node include : includes) {
			ids.add(include.getId());
		}
		return ids;
	}
	
	public boolean addIncludes(Collection<String> names) {
		boolean isOk = false;
		if(names != null) {
			isOk = true;
			for(String name : names) {
				isOk &= addInclude(name);
			}
		}
		return isOk;
	}
	
	public boolean addInclude(String name) {
		boolean isOk = false;
		Node node = getNode(name);
		if(node != Node.NULL) {
			includes.add(node);
			isOk = true;
		}
		return isOk;
	}
	
}
