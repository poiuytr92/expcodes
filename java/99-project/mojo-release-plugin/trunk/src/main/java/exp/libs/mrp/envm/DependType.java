package exp.libs.mrp.envm;

public class DependType {

	public final static DependType MAVEN = new DependType(1, "MAVEN");
	
	public final static DependType SELF = new DependType(2, "SELF");
	
	private int id;
	
	private String type;
	
	private DependType(int id, String type) {
		this.id = id;
		this.type = type;
	}
	
	public int ID() {
		return id;
	}
	
	public String TYPE() {
		return type;
	}
	
	public static DependType toType(String type) {
		DependType dType = SELF;
		if(MAVEN.TYPE().equalsIgnoreCase(type)) {
			dType = MAVEN;
		} else {
			dType = SELF;
		}
		return dType;
	}
	
}
