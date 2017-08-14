package exp.libs.mrp.envm;

public class DependType {

	public final static DependType MAVEN = new DependType(1, "MAVEN");
	
	public final static DependType LIB = new DependType(2, "LIB");
	
	private int id;
	
	private String desc;
	
	private DependType(int id, String desc) {
		this.id = id;
		this.desc = desc;
	}
	
	public int ID() {
		return id;
	}
	
	public String DESC() {
		return desc;
	}
	
	public static DependType toDependType(String desc) {
		DependType dType = LIB;
		if(MAVEN.DESC().equals(desc)) {
			dType = MAVEN;
		} else {
			dType = LIB;
		}
		return dType;
	}
	
	
}
