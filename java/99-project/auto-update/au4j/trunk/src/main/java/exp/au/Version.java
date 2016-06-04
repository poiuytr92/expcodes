package exp.au;

import exp.libs.utils.pub.NumUtils;
import exp.libs.utils.pub.StrUtils;


public class Version implements Comparable<Version> {

	private int major;
	
	private int minor;
	
	private int inner;
	
	private int revised;
	
	private String version;
	
	public Version() {
		init(0, 0, 0, 0);
	}
	
	public Version(int major, int minor) {
		init(major, minor, -1, -1);
	}
	
	public Version(int major, int minor, int inner) {
		init(major, minor, inner, -1);
	}
	
	public Version(int major, int minor, int inner, int revised) {
		init(major, minor, inner, revised);
	}
	
	public Version(String ver) {
		init(0, 0, 0, 0);
		if(!StrUtils.isEmpty(ver)) {
			String[] vers = ver.split("\\.");
			if(vers.length == 2) {
				init(NumUtils.toInt(vers[0]), NumUtils.toInt(vers[1]), -1, -1);
				
			} else if(vers.length == 3) {
				init(NumUtils.toInt(vers[0]), NumUtils.toInt(vers[1]), 
						NumUtils.toInt(vers[2]), -1);
				
			} else if(vers.length >= 4) {
				init(NumUtils.toInt(vers[0]), NumUtils.toInt(vers[1]), 
						NumUtils.toInt(vers[2]), NumUtils.toInt(vers[3]));
			}
		}
	}
	
	private void init(int major, int minor, int inner, int revised) {
		this.major = major;
		this.minor = minor;
		this.inner = inner;
		this.revised = revised;
		this.version = StrUtils.concat(this.major, ".", this.minor, 
				(this.inner < 0 ? "" : ("." + this.inner)), 
				(this.revised < 0 ? "" : ("." + this.revised)));
	}
	
	public int getMajorVersion() {
		return major;
	}

	public int getMinorVersion() {
		return minor;
	}

	public int getInnerVersion() {
		return inner;
	}

	public int getRevisedVersion() {
		return revised;
	}

	public String getVersion() {
		return version;
	}

	@Override
	public int compareTo(Version ver) {
		int diff = 0;
		if(ver != null) {
			diff = this.getMajorVersion() - ver.getMajorVersion();
			if(diff == 0) { diff = this.getMinorVersion() - ver.getMinorVersion(); }
			if(diff == 0) { diff = this.getInnerVersion() - ver.getInnerVersion(); }
			if(diff == 0) { diff = this.getRevisedVersion() - ver.getRevisedVersion(); }
		}
		return diff;
	}
	
}
