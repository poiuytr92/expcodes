package exp.libs.utils.pub;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * <PRE>
 * Json处理工具包
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2016-01-19
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class JsonUtils {
	
	/** 私有化构造函数 */
	protected JsonUtils() {}
	
	public static JSONArray getJSONArray(JSONObject jsonObj, String arrayName) {
		JSONArray array = new JSONArray();
		try {
			array = jsonObj.getJSONArray(arrayName);
		} catch(Exception e) {
			// 当不存在array节点时会抛出异常
		}
		return array;
	}
	
	public static String getString(JSONObject jsonObj, String name) {
		String str = "";
		try {
			str = jsonObj.getString(name);
		} catch(Exception e) {
			// 当不存在name节点时会抛出异常
		}
		return str;
	}
	
}
