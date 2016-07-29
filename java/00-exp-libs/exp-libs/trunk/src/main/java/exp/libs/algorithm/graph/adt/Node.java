package exp.libs.algorithm.graph.adt;

import exp.libs.utils.pub.StrUtils;

public class Node {

	private Integer id;
	
	private String name;
	
	public Node(String name) {
		this.id = -1;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return StrUtils.concat(id, ":", name);
	}

}
