package exp.libs.algorithm.tsp.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import exp.libs.utils.pub.StrUtils;

public class Node {

	public final static Node NULL = new Node(-1, "NULL");
	
	private int id;
	
	private String name;
	
	private int degree;
	
	// 仅用于在构图时保持唯一性
	private Set<Node> neighborSet;
	
	// 构图后转用List存储，且Set集不允许再添加元素
	private List<Node> neighbors;
	
	@SuppressWarnings("unused")
	private Node() {}
	
	protected Node(int id, String name) {
		this.id = id;
		this.name = name;
		this.degree = 0;
		this.neighborSet = new HashSet<Node>();
	}
	
	protected void clear() {
		if(neighborSet != null) {
			neighborSet.clear();
		}
		if(neighbors != null) {
			neighbors.clear();
		}
	}
	
	protected boolean addNeighbor(Node node) {
		boolean isOk = false;
		if(neighborSet != null) {
			isOk = neighborSet.add(node);
			if(isOk == true) {
				degree++;
			}
		}
		return isOk;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getDegree() {
		return degree;
	}

	public Iterator<Node> getNeighborIterator() {
		return getNeighbors().iterator();
	}
	
	public List<Node> getNeighborList() {
		return new ArrayList<Node>(getNeighbors());
	}
	
	private List<Node> getNeighbors() {
		if(neighbors == null) {
			neighbors = new ArrayList<Node>(neighborSet);
			neighborSet.clear();
			neighborSet = null;
		}
		return neighbors;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof Node)) {
			return false;
		}
		
		Node other = (Node) obj;
		boolean isEquals = (this.getId() == other.getId());
		return isEquals;
	}
	
	@Override
	public String toString() {
		return StrUtils.concat(getId(), ":", getName());
	}
	
}
