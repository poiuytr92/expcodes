package exp.github.tools;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import exp.libs.envm.Charset;
import exp.libs.envm.Delimiter;
import exp.libs.envm.HttpHead;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.num.RandomUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.utils.verify.VerifyUtils;
import exp.libs.warp.net.http.HttpURLUtils;

public class Host {

	private final static String DEFAULT_CHARSET = Charset.ISO;
	
	private final static String DNS_URL = "http://tool.chinaz.com/dns";
	
	private final static String WIN_HOST_PATH = "C:\\Windows\\System32\\drivers\\etc\\hosts";
	
	private final static String[] HOSTS = new String[] {
		"github.global.ssl.fastly.net", 
		"github.com"
	};
	
	// FIXME 注释不读取， 备份HOSTS
	public static void main(String[] args) {
		Map<String, String> hosts = readHosts();
		
		for(String host : HOSTS) {
			String bestDNS = findBestDNS(host);
			if(VerifyUtils.isIP(bestDNS)) {
				hosts.put(host, bestDNS);
			}
		}
		
//		saveHosts(hosts);
	}
	
	private static Map<String, String> readHosts() {
		Map<String, String> hosts = new HashMap<String, String>();
		List<String> lines = FileUtils.readLines(WIN_HOST_PATH, DEFAULT_CHARSET);
		for(String line : lines) {
			if(StrUtils.isTrimEmpty(line)) {
				continue;
			}
			
			String[] vk = line.split(" ");
			if(vk.length == 2) {
				hosts.put(vk[1].trim(), vk[0].trim());
			}
		}
		return hosts;
	}
	
	private static boolean saveHosts(Map<String, String> hosts) {
		StringBuilder sb = new StringBuilder();
		Iterator<String> hostIts = hosts.keySet().iterator();
		while(hostIts.hasNext()) {
			String host = hostIts.next();
			String dns = hosts.get(host);
			sb.append(StrUtils.concat(dns, " ", host, Delimiter.CRLF));
		}
		return FileUtils.write(WIN_HOST_PATH, sb.toString(), DEFAULT_CHARSET, false);
	}
	
	private static String findBestDNS(String host) {
		Map<String, String> header = new HashMap<String, String>();
		header.put(HttpHead.KEY.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		header.put(HttpHead.KEY.ACCEPT_ENCODING, "gzip, deflate, sdch");
		header.put(HttpHead.KEY.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8,en;q=0.6");
		header.put(HttpHead.KEY.CONNECTION, "keep-alive");
		header.put(HttpHead.KEY.HOST, "tool.chinaz.com");
		header.put(HttpHead.KEY.REFERER, "http://tool.chinaz.com/dns?type=1&host=" + host + "&ip=");
		header.put(HttpHead.KEY.USER_AGENT, HttpHead.VAL.USER_AGENT);

		Map<String, String> request = new HashMap<String, String>();
		request.put("type", "1");
		request.put("host", host);
		request.put("ip", host);
		String response = HttpURLUtils.doGet(DNS_URL, header, request);
		
		
		final String SERVER_RGX = "var servers = (.+)";
		String serverJson = RegexUtils.findFirst(response, SERVER_RGX);
		System.out.println(serverJson);	// FIXME: 改成解析JSON？
		// [{id:15381430,ip:"qSaqwKBbIMiHFNDIIIg2VA==",state:0,trytime:0},{id:15381438,ip:"pc5LXpA6Y0LtmOpoKX3zGA==",state:0,trytime:0},{id:15381439,ip:"5n2koVgLsBbs7W8XbIL2NQ==",state:0,trytime:0},{id:15381451,ip:"fL3WGCQ8i0kNrxXqCGAcpg==",state:0,trytime:0},{id:15381437,ip:"SdvPAD3yuuRRgUgJQLfaUQ==",state:0,trytime:0},{id:15381441,ip:"Rv90/Ksj1L6zXUx96XEFbA==",state:0,trytime:0},{id:15381445,ip:"UsXmWPyUMCAl22fytxVeYA==",state:0,trytime:0},{id:15381446,ip:"DiRV3R7jjMVfu0/d6bXYTg==",state:0,trytime:0},{id:15381447,ip:"wfe/baph0aVy7vSzQ8JCew==",state:0,trytime:0},{id:15381433,ip:"zFjFw1wXjPGw24s1pgwUlg==",state:0,trytime:0},{id:15381449,ip:"Tllr7HLQodVpDDFM0Ssc9A==",state:0,trytime:0},{id:15381436,ip:"lsB7oELLLoiNHPwhyWj4YA==",state:0,trytime:0},{id:15381448,ip:"eaWEej3puEKNjremlHa|0w==",state:0,trytime:0},{id:15381442,ip:"fAiZyrOZG6sOpFazf1zdVg==",state:0,trytime:0},{id:15381443,ip:"smuXAjaliTLUrOoVs/MnVQ==",state:0,trytime:0},{id:15381444,ip:"VZWE4uxPBJAKFLPTtoHyyQ==",state:0,trytime:0},{id:15381440,ip:"abaieVMlEG3aU4jEmZOZrg==",state:0,trytime:0},{id:15381434,ip:"/LqwY7|RTOTUAm/8Eln8dQ==",state:0,trytime:0},{id:15381435,ip:"FUFXcwK4d|5goTcqAeJyHA==",state:0,trytime:0},{id:15381431,ip:"kZili0C|QmwVj8/IZI9MOw==",state:0,trytime:0},{id:15381432,ip:"4|LMEILycPoa9DPxVJb3gg==",state:0,trytime:0},{id:15381450,ip:"JACYvxRvL1|CnyK9sCL7/g==",state:0,trytime:0}];
		
		Map<String, String> servers = new HashMap<String, String>();
		final String SERVERS_RGX = "id:([^,]+),ip:\"([^\"]+)\"";
		List<List<String>> datas = RegexUtils.findAll(serverJson, SERVERS_RGX);
		for(List<String> data : datas) {
			String id = data.get(1);
			String server = data.get(2);
			servers.put(id, server);
			System.out.println(id + ":" + server);
		}
		
		Iterator<String> ids = servers.keySet().iterator();
		while(ids.hasNext()) {
			String id = ids.next();
			String server = servers.get(id);
			
			// 注意此网站使用了原生的ajax请求，有一部分参数在URL编码，有一部分在表单传送
			final String URL = "http://tool.chinaz.com/AjaxSeo.aspx?";
			String ajaxURL = StrUtils.concat(URL, 
					"t=dns", 
					"&server=", server, 
					"&id=", id, 
					"&callback=jQuery", getJQueryID());
			System.out.println(ajaxURL);
			
			header.put(HttpHead.KEY.CONTENT_TYPE, HttpHead.VAL.POST_FORM.concat(Charset.UTF8));
			request.clear();
			request.put("host", host);
			request.put("type", "1");
			request.put("total", "18");
			request.put("process", "13");
			request.put("right", "13");
			
			String rst = HttpURLUtils.doPost(ajaxURL, header, request);
			System.out.println(rst);
			// jQuery1103390065854505800381_1527217623098({"state":1,"id":15381435,"list":[{"type":"A","result":"151.101.1.194","ipaddress":"美国 Fastly公司CDN网络节点","ttl":"30"},{"type":"A","result":"151.101.65.194","ipaddress":"美国 Fastly公司CDN网络节点","ttl":"30"},{"type":"A","result":"151.101.129.194","ipaddress":"美国 Fastly公司CDN网络节点","ttl":"30"},{"type":"A","result":"151.101.193.194","ipaddress":"美国 Fastly公司CDN网络节点","ttl":"30"}]})
			
			break;
		}
		
		return "";
	}
	
	private static String getJQueryID() {
		StringBuilder uniqueID = new StringBuilder("11");
		for(int i = 0; i < 20; i++) {
			uniqueID.append(RandomUtils.randomInt(10));
		}
		uniqueID.append("_").append(System.currentTimeMillis());
		return uniqueID.toString();
	}
	
}
