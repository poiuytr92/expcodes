package exp.bilibili.plugin.monitor;

import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.net.http.HttpUtils;
import exp.libs.warp.ver.VersionMgr;


public class Monitor {

	/** 软件授权页 */
	private final static String URL = CryptoUtils.deDES(
			"610BEF99CF948F0DB1542314AC977291892B30802EC5BF3B2DCDD5538D66DDA67467CE4082C2D0BC56227128E753555C");
		
	private String appName;

	private String appVersion;
	
	private static volatile Monitor instance;
	
	private Monitor() {
		init();
	}
	
	public static Monitor getInstn() {
		if(instance == null) {
			synchronized (Monitor.class) {
				if(instance == null) {
					instance = new Monitor();
				}
			}
		}
		return instance;
	}
	
	private void init() {
		String verInfo =  VersionMgr.exec("-p");
		this.appName = RegexUtils.findFirst(verInfo, "项目名称[ |]*([a-z|\\-]+)");
		this.appVersion = RegexUtils.findFirst(verInfo, "版本号[ |]*([\\d|\\.]+)");
	}
	
	@SuppressWarnings({ "unchecked" })
	public boolean check() {
		boolean isOk = false;
		String source = HttpUtils.getPageSource(URL);
		try {
			Document doc = DocumentHelper.parseText(source);
			Element html = doc.getRootElement();
			Element body = html.element("body");
			Element div = body.element("div").element("div");
			Iterator<Element> tables = div.elementIterator();
			while(tables.hasNext()) {
				Element table = tables.next();
				String name = table.attributeValue("id");
				if(appName.equals(name)) {
					System.out.println(table.asXML());
					// FIXME
					break;
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isOk;
	}
	
	public static void main(String[] args) {
		Monitor.getInstn().check();
	}
	
}
