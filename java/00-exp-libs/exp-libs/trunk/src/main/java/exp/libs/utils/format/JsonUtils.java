package exp.libs.utils.format;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.BoolUtils;


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

	private final static Logger log = LoggerFactory.getLogger(JsonUtils.class);
	
	/** 私有化构造函数 */
	protected JsonUtils() {}
	
	public static boolean isVaild(String json) {
		boolean isVaild = true;
		try {
			JSONObject.fromObject(json);
		} catch(Throwable e) {
			isVaild = false;
		}
		return isVaild;
	}
	
	public static boolean isInvaild(String json) {
		return !isVaild(json);
	}
	
	public static String getStr(JSONObject json, String key) {
		String val = "";
		try {
			val = json.getString(key);
		} catch(Throwable e) {
			log.error("从JSON中提取 string 类型值 [{}] 失败.", key, e);
		}
		return val;
	}
	
	public static String getStr(JSONObject json, String key, String defavlt) {
		String val = defavlt;
		try {
			val = json.getString(key);
		} catch(Throwable e) {
			log.error("从JSON中提取 string 类型值 [{}] 失败.", key);
		}
		return val;
	}
	
	public static boolean getBool(JSONObject json, String key, boolean defavlt) {
		boolean val = defavlt;
		try {
			val = BoolUtils.toBool(json.getString(key), defavlt);
		} catch(Throwable e) {
			log.error("从JSON中提取 bool 类型值 [{}] 失败.", key, e);
		}
		return val;
	}
	
	public static int getInt(JSONObject json, String key, int defavlt) {
		int val = defavlt;
		try {
			val = NumUtils.toInt(json.getString(key), defavlt);
		} catch(Throwable e) {
			log.error("从JSON中提取 int 类型值 [{}] 失败.", key, e);
		}
		return val;
	}
	
	public static long getLong(JSONObject json, String key, long defavlt) {
		long val = defavlt;
		try {
			val = NumUtils.toLong(json.getString(key), defavlt);
		} catch(Throwable e) {
			log.error("从JSON中提取 long 类型值 [{}] 失败.", key, e);
		}
		return val;
	}
	
	public static JSONObject getObject(JSONObject json, String key) {
		JSONObject val = null;
		try {
			val = json.getJSONObject(key);
		} catch(Throwable e) {
			val = new JSONObject();
			log.error("从JSON中提取 object 对象 [{}] 失败.", key, e);
		}
		return val;
	}
	
	public static JSONArray getArray(JSONObject json, String key) {
		JSONArray val = null;
		try {
			val = json.getJSONArray(key);
		} catch(Throwable e) {
			val = new JSONArray();
			log.error("从JSON中提取 array 对象 [{}] 失败.", key, e);
		}
		return val;
	}
	
	public static String[] toStrArray(JSONArray array) {
		if(array == null) {
			return new String[0];
		}
		
		String[] sArray = new String[array.size()];
		for(int i = 0; i < sArray.length; i++) {
			sArray[i] = array.getString(i);
		}
		return sArray;
	}
	
	public static List<String> toStrList(JSONArray array) {
		if(array == null) {
			return new LinkedList<String>();
		}
		
		List<String> sList = new LinkedList<String>();
		for(int i = 0; i < array.size(); i++) {
			sList.add(array.getString(i));
		}
		return sList;
	}
	
	public static String[] getStrArray(JSONObject json, String key) {
		return toStrArray(getArray(json, key));
	}
	
	public static JSONArray toJsonArray(Collection<String> list) {
		JSONArray array = new JSONArray();
		if(list != null) {
			for(String s : list) {
				array.add(s);
			}
		}
		return array;
	}
	
	public static JSONArray toJsonArray(String[] array) {
		JSONArray jsonArray = new JSONArray();
		if(array != null) {
			for(String s : array) {
				jsonArray.add(s);
			}
		}
		return jsonArray;
	}
	
}
