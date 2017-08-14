package exp.libs.mrp.services;

import java.util.HashMap;
import java.util.Map;

/**
 * <PRE>
 * 项目脚本容器。
 * </PRE>
 * <B>项    目：</B>凯通J2SE开发平台(KTJSDP)
 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2014
 * @version   1.0 2014-9-19
 * @author    廖权斌：liaoquanbin@gdcattsoft.com
 * @since     jdk版本：jdk1.6
 */
public class ProjectScript {

	/**
	 * 项目名称
	 */
	private String proName;
	
	/**
	 * 项目的各个脚本内容
	 * key: 脚本名称
	 * value: 脚本内容
	 */
	private Map<String, String> proScripts;
	
	/**
	 * 构造函数
	 * @param proName 项目名称
	 */
	public ProjectScript(String proName) {
		this.proName = proName;
		this.proScripts = new HashMap<String, String>();
	}

	/**
	 * 添加新脚本到项目
	 * @param scriptName 脚本名称，若为空无效
	 * @param scriptContent 脚本内容，若为空无效
	 */
	public void addScript(String scriptName, String scriptContent) {
		if(scriptName != null && !"".equals(scriptName) && 
				scriptContent != null && !"".equals(scriptContent)) {
			proScripts.put(scriptName, scriptContent);
		}
	}
	
	/**
	 * 获取指定名称的脚本内容
	 * @param scriptName 脚本名称，若为空无效
	 * @return 脚本内容
	 */
	public String getScript(String scriptName) {
		String scriptContent = "";
		if(scriptName != null && !"".equals(scriptName)) {
			scriptContent = proScripts.get(scriptName);
			scriptContent = (scriptContent == null ? "" : scriptContent);
		}
		return scriptContent;
	}
	
	/**
	 * 获取所有脚本内容
	 * @return 所有脚本内容
	 */
	public Map<String, String> getProScripts() {
		return proScripts;
	}

	public String getProName() {
		return proName;
	}

	public void setProName(String proName) {
		this.proName = proName;
	}
	
}
