package exp.crawler.qq.ui;

import exp.libs.utils.io.FileUtils;
import exp.libs.utils.num.IDUtils;
import exp.libs.utils.other.StrUtils;

public class PicUtils {

	public final static String SUFFIX = ".png";
	
	protected PicUtils() {}
	
	public static String getPicName(String idx, String desc) {
		String name = StrUtils.concat("[", IDUtils.getTimeID(), "]-[", idx, "] ", desc);
		name = FileUtils.delForbidCharInFileName(name, "");
		name = StrUtils.showSummary(name);
		name = name.concat(SUFFIX);
		return name;
	}
	
}
