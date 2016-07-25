package exp.libs.algorithm.graph.adt;

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
	
}
