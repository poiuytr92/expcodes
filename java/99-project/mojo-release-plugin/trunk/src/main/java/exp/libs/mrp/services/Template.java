package exp.libs.mrp.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <PRE>
 * 模板文件加载类。
 * 提供加载模板、替换模板占位符、获取替换后文件内容的方法。
 * 
 * 注意：
 * （1）所有模板文件都必须与TemplateFile在同一目录下，否则无法用read方法加载。
 * （2）模板文件中的占位符为  @{占位符名称}@，而代码替换只需 set(占位符名称, 值)。
 * 
 * </PRE>
 * <B>项    目：</B>凯通J2SE开发平台(KTJSDP)
 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2014
 * @version   1.0 2014-09-12
 * @author    廖权斌：liaoquanbin@gdcattsoft.com
 * @since     jdk版本：jdk1.6
 */
public class Template {
	
	/**
	 * 此模板的名称。
	 * 仅声明这个模板用于生成什么文件，可任意命名。
	 */
	private String name;
	
	/**
	 * 未被替换过占位符的模板内容
	 */
	private String orgContent;
	
	/**
	 * 被替换了部分/全部占位符的模板内容
	 */
	private String newContent;
	
	/**
	 * KV表：占位符名称-值
	 */
	private Map<String, String> kvMap;
	
	/**
	 * 是否需要刷新模板
	 */
	private boolean isFlash;
	
	/**
	 * 模板文件的字符集编码
	 */
	private String fileCharset;
	
	/**
	 * 默认编码
	 */
	public final static String DEFAULT_CHARSET = "ISO-8859-1";
	
	/**
	 * 占位符的左括号
	 */
	private final static String PLACEHOLDER_LEFT = "@{";
	
	/**
	 * 占位符的右括号
	 */
	private final static String PLACEHOLDER_RIGHT = "}@";
	
	/**
	 * 提取占位符的正则表达式
	 */
	private final static String PLACEHOLDER_REGEX = "(@\\{\\w*?\\}@)";
	
	/**
	 * 构造函数
	 */
	public Template() {
		this.name = "NULL";
		this.orgContent = "";
		this.newContent = "";
		this.kvMap = new HashMap<String, String>();
		this.isFlash = true;
		this.fileCharset = DEFAULT_CHARSET;
	}
	
	/**
	 * 构造函数
	 * @param name 模板名称
	 */
	public Template(String name) {
		this.name = name;
		this.orgContent = "";
		this.newContent = "";
		this.kvMap = new HashMap<String, String>();
		this.isFlash = true;
		this.fileCharset = DEFAULT_CHARSET;
	}
	
	/**
	 * 使用 ISO-8859-1 编码读取模板文件。
	 * 默认的模板文件内容为全英文，因此使用 ISO-8859-1编码即可，除非后续添加新模板。
	 * 
	 * @param templateFileName 模板文件名称
	 */
	public void read(String templateFileName) {
		read(templateFileName, DEFAULT_CHARSET);
	}
	
	/**
	 * 使用指定编码读取模板文件。
	 * 
	 * @param templateFileName 模板文件名称
	 * @param charset 指定读取文件用的编码
	 */
	public void read(String templateFileName, String charset) {
		this.fileCharset = charset;
		InputStream is = Template.class.
				getResourceAsStream(templateFileName);
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		
		try {
			byte[] buff = new byte[1024];
			int rc = 0;
			while ((rc = is.read(buff, 0, 1024)) > 0) {
				swapStream.write(buff, 0, rc);
			}
			byte[] arrByte = swapStream.toByteArray();
			orgContent = new String(arrByte, fileCharset);
			
		} catch (Exception e) {
			System.err.println("读取模板文件[" + templateFileName + 
					"]失败,程序终止." + e);
			System.exit(1);
		}
		
		try {
			swapStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 设置各个占位符的值，同名占位符会被后设置的值覆盖
	 * @param placeholder 占位符
	 * @param value 替换值
	 */
	public void set(String placeholder, String value) {
		isFlash = true;
		String key = PLACEHOLDER_LEFT + placeholder + PLACEHOLDER_RIGHT;
		kvMap.put(key, value);
	}
	
	/**
	 * 使用ISO-8859-1编码获取占位符被替换后的模板文件内容
	 * @return 占位符被替换后的模板文件内容
	 */
	public String getContent() {
		return getContent(DEFAULT_CHARSET);
	}
	
	/**
	 * 使用指定编码获取占位符被替换后的模板文件内容.
	 * 只要 setValue 被调用，模板文件的内容会自动刷新。
	 * 
	 * @param charset 指定获取内容的编码
	 * @return 占位符被替换后的模板文件内容
	 */
	public String getContent(String charset) {
		replace();	//每次获取前先检查是否有新内容需要被替换到模板中
		
		try {
			"Test Charset".getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			charset = DEFAULT_CHARSET;	//把异常编码替换为默认编码
			System.err.println("[" + charset + "]编码异常,自动替换为默认编码[" + 
					DEFAULT_CHARSET + "]");
		}
		
		try {
			byte[] byteCntn = newContent.getBytes(fileCharset);
			newContent = new String(byteCntn, charset);
			
		} catch (UnsupportedEncodingException e) {
			//不可能异常
		}
		return newContent;
	}
	
	/**
	 * 把所有占位符的值替换到模板文件中
	 */
	private void replace() {
		if(isFlash == true) {
			isFlash = false;
			
			newContent = orgContent;
			for(Iterator<String> keyIts = kvMap.keySet().iterator();
					keyIts.hasNext();) {
				String key = keyIts.next();
				String val = kvMap.get(key);
				newContent = newContent.replace(key, val);
			}
		}
	}

	/**
	 * 获取模板文件中所有占位符表
	 * @return 模板文件中所有占位符
	 */
	public Set<String> getAllPlaceHolders() {
		Set<String> phList = new HashSet<String>();
		Pattern ptn = Pattern.compile(PLACEHOLDER_REGEX);
		Matcher mth = ptn.matcher(orgContent);
		
		while(mth.find()) {
			phList.add(mth.group(1));
		}
		return phList;
	}
	
	/**
	 * 获取模板文件中未设置值的占位符表
	 * @return 模板文件中未设置值的占位符
	 */
	public Set<String> getPlaceHoldersNotVal() {
		Set<String> allPhList = getAllPlaceHolders();
		Set<String> phNotValList = new HashSet<String>();
		
		for(String ph : allPhList) {
			if(kvMap.get(ph) == null) {
				phNotValList.add(ph);
			}
		}
		allPhList.clear();
		return phNotValList;
	}
	
	/**
	 * 获取模板文件中已设置值的占位符表
	 * @return 模板文件中已设置值的占位符
	 */
	public Set<String> getPlaceHoldersHasVal() {
		Set<String> allPhList = getAllPlaceHolders();
		Set<String> phHasValList = new HashSet<String>();
		
		for(String ph : allPhList) {
			if(kvMap.get(ph) != null) {
				phHasValList.add(ph);
			}
		}
		allPhList.clear();
		return phHasValList;
	}
	
	/**
	 * 获取未被替换任何占位符的模板内容
	 * @return 模板内容
	 */
	public String getTemplate() {
		return orgContent;
	}
	
	/**
	 * 获取模板名称
	 * @return 模板名称
	 */
	public String getName() {
		return name;
	}
	
}
