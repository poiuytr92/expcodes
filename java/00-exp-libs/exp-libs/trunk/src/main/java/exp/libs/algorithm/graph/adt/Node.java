package exp.libs.algorithm.graph.adt;

public class Node {

	private static Integer autoId = 0;	// FIXME： 这种矩阵只能用一次，要变更ID获取方式w
	
	private Integer id;
	
	private String name;
	
	public Node(String name) {
		this(null, name);
	}

	public Node(Integer id, String name) {
		this.id = (id == null ? autoId++ : id);
		this.name = name;
	}

	public Integer getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
}
