package exp.libs.algorithm.tsp.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TopoGraph extends Graph {

	private Node src;
	
	private Node snk;
	
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
	
	/**
	 * 获取有效路径
	 * @param num 最大的有效路径数
	 * @return
	 */
	public List<List<Node>> findVaildPaths(int num) {
		List<List<Node>> paths = findAllPaths();
		if(paths.size() > num) {
			int cnt = paths.size() - num;
			while(cnt-- > 0) {
				paths.remove(0);
			}
		}
		return paths;
	}
	
	public List<List<Node>> findAllPaths() {
		Set<Node> tabus = new HashSet<Node>();
		tabus.add(src);
		
		List<List<Node>> paths = new LinkedList<List<Node>>();
		List<List<Node>> pathSuffixs = findPathSuffixs(src, snk, tabus);
		for(List<Node> pathSuffix : pathSuffixs) {
			List<Node> path = new LinkedList<Node>();
			path.add(src);
			path.addAll(pathSuffix);
			paths.add(path);
		}
		return paths;
	}
	
	// FIXME: 递归改迭代
	private List<List<Node>> findPathSuffixs(Node cur, Node end, Set<Node> tabus) {
		List<List<Node>> pathSuffixs = new LinkedList<List<Node>>();
		
		Iterator<Node> nexts = cur.getNeighborIterator();
		while(nexts.hasNext()) {
			Node next = nexts.next();
			if(tabus.contains(next)) {
				continue;
			}
			
			if(end.equals(next)) {
				List<Node> pathSuffix = new LinkedList<Node>();
				pathSuffix.add(next);
				pathSuffixs.add(pathSuffix);
				
			} else {
				tabus.add(next);
				List<List<Node>> subPathSuffixs = findPathSuffixs(next, end, tabus);
				for(List<Node> subPathSuffix : subPathSuffixs) {
					subPathSuffix.add(0, next);
					pathSuffixs.add(subPathSuffix);
				}
				tabus.remove(next);
			}
		}
		return pathSuffixs;
	}
	
}
