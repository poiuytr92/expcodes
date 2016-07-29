package exp.libs.algorithm.graph.adt;

import java.util.HashMap;
import java.util.Map;

class NodeFactory {

	/** 自增节点ID */
	private int autoId;
	
	/** 节点索引映射表： name -> id */
	private Map<String, Integer> nodeIdxs;
	
	/** 节点表： id -> node */
	private Map<Integer, Node> nodes;
	
	protected NodeFactory() {
		this.autoId = 0;
		this.nodeIdxs = new HashMap<String, Integer>();
		this.nodes = new HashMap<Integer, Node>();
	}
	
	protected int size() {
		return nodes.size();
	}
	
	protected boolean contains(Integer id) {
		return (id == null || id < 0 ? false : nodes.containsKey(id));
	}
	
	protected boolean contains(String name) {
		return nodeIdxs.containsKey(name);
	}
	
	protected Node find(Integer id) {
		return (id == null || id < 0 ? null : nodes.get(id));
	}
	
	protected Node find(String name) {
		return nodes.get(nodeIdxs.get(name));
	}
	
	/**
	 * 往工厂添加一个节点.
	 * 	若已存在同名节点，则返回工厂中的节点索引.
	 * @param node
	 * @return
	 */
	protected Node add(Node node) {
		if(node == null || node.getName() == null) {
			return node;
		}
		
		Integer id = nodeIdxs.get(node.getName());
		if(id == null) {
			id = autoId++;
			node.setId(id);
			
			nodeIdxs.put(node.getName(), node.getId());
			nodes.put(node.getId(), node);
			
		} else {
			node = nodes.get(id);
		}
		return node;
	}
	
	public void clear() {
		nodeIdxs.clear();
		nodes.clear();
	}
	
}
