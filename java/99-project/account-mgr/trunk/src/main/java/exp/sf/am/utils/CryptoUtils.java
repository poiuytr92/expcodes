package exp.sf.am.utils;


public class CryptoUtils {

	private final static String PW_KEY = "&!%EXP@M02*#^";
	
	private CryptoUtils() {}
	
	public static String encode(String str) {
		return exp.libs.utils.encode.CryptoUtils.toDES(str, PW_KEY);
	}
	
	public static String decode(String str) {
		return exp.libs.utils.encode.CryptoUtils.deDES(str, PW_KEY);
	}
	
}
