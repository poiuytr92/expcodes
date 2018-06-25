package exp.libs.algorithm.struct.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 拓扑图的节点模型
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2017-12-25
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Node {

	public final static Node NULL = new Node(-1, "NULL");
	
	private int id;
	
	private String name;
	
	private int degree;
	
	// 仅用于在构图时保持唯一�?
	private Set<Node> neighbors;
	
	@SuppressWarnings("unused")
	private Node() {}
	
	protected Node(int id, String name) {
		this.id = id;
		this.name = name;
		this.degree = 0;
		this.neighbors = new HashSet<Node>();
	}
	
	protected void clear() {
		neighbors.clear();
	}
	
	protected boolean addNeighbor(Node node) {
		boolean isOk = neighbors.add(node);
		if(isOk == true) {
			degree++;
		}
		return isOk;
	}
	
	protected boolean delNeighbor(Node node) {
		boolean isOk = neighbors.remove(node);
		if(isOk == true) {
			degree--;
		}
		return isOk;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAliasName() {
		return StrUtils.concat(getId(), ":", getName());
	}
	
	public int getDegree() {
		return degree;
	}

	public Iterator<Node> getNeighborIterator() {
		return getNeighborList().iterator();
	}
	
	public List<Node> getNeighborList() {
		return new ArrayList<Node>(neighbors);
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
		return getAliasName();
	}
	
}
