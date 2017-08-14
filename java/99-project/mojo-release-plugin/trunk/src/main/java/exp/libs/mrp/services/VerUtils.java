package exp.libs.mrp.services;

public class VerUtils {

	public static String cutVer(String jarName) {
		String name = jarName;
		if(name != null && jarName.contains(".jar")) {
			name = name.trim();
			if(!"".equals(name)) {
				name = name.replaceAll("[-_\\.]?\\d+\\..*", ".jar");
			}
		}
		return name;
	}
	
}
