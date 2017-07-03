package exp.libs.utils.verify;

import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

/**
 * <PRE>
 * 测试工具类.
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class TestUtils {

	/** 私有化构造方法 */
	protected TestUtils() { }
	
	/**
	 * 测试是否为json串
	 * @param str 被测字符串
	 * @return 是否为json串
	 */
	public static boolean testJson(String str) {
		boolean isJson = true;
		try {
			ObjectMapper mapper = new ObjectMapper();
			Map<?, ?> map = mapper.readValue(str, LinkedHashMap.class);
			isJson = (map == null ? false : true);
			
		} catch (Exception e) {
			isJson = false;
		}
		return isJson;
	}
	
	/**
	 * 测试xml报文(片段)是否合法
	 * @param str xml报文(片段)
	 * @return 是否合法
	 */
	public static boolean testXml(String str) {
		boolean isLegal = false;
		try {
			DocumentHelper.parseText(str);
			isLegal = true;
		} catch (DocumentException e) {
			isLegal = false;
		}
		return isLegal;
	}
	
}
