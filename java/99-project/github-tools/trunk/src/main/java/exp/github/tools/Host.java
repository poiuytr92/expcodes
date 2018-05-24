package exp.github.tools;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import exp.libs.envm.Charset;
import exp.libs.envm.Delimiter;
import exp.libs.envm.HTTP;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.utils.verify.VerifyUtils;
import exp.libs.warp.net.http.HttpURLUtils;

public class Host {

	private final static String DEFAULT_CHARSET = Charset.ISO;
	
	private final static String DNS_URL = "http://tool.chinaz.com/dns";
	
	private final static String WIN_HOST_PATH = "C:\\Windows\\System32\\drivers\\etc\\hosts";
	
	private final static String GITHUB_GLOBAL_SSL_FASTLY_NET = "github.global.ssl.fastly.net";
	
	private final static String GITHUB_COM = "github.com";
	
	public static void main(String[] args) {
		Map<String, String> hosts = readHosts();
		
		String bestDNS = findBestDNS(GITHUB_GLOBAL_SSL_FASTLY_NET);
		if(VerifyUtils.isIP(bestDNS)) {
			hosts.put(GITHUB_GLOBAL_SSL_FASTLY_NET, bestDNS);
		}
		
		bestDNS = findBestDNS(GITHUB_COM);
		if(VerifyUtils.isIP(bestDNS)) {
			hosts.put(GITHUB_COM, bestDNS);
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
		header.put(HTTP.KEY.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		header.put(HTTP.KEY.ACCEPT_ENCODING, "gzip, deflate, sdch");
		header.put(HTTP.KEY.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8,en;q=0.6");
		header.put(HTTP.KEY.CONNECTION, "keep-alive");
		header.put(HTTP.KEY.HOST, "tool.chinaz.com");
		header.put(HTTP.KEY.REFERER, "http://tool.chinaz.com/dns?type=1&host=" + host + "&ip=");
		header.put(HTTP.KEY.USER_AGENT, HTTP.VAL.USER_AGENT);

		Map<String, String> request = new HashMap<String, String>();
		request.put("type", "1");
		request.put("host", host);
		request.put("ip", host);
		String response = HttpURLUtils.doGet(DNS_URL, header, request);
		
		
		final String SERVER_RGX = "var servers = (.+)";
		String server = RegexUtils.findFirst(response, SERVER_RGX);
		System.out.println(server);
		
		final String SERVERS_RGX = "id:([^,]+),ip:\"([^\"]+)\"";
		List<List<String>> datas = RegexUtils.findAll(server, SERVERS_RGX);
		for(List<String> data : datas) {
			System.out.println(data.get(1) + ":" + data.get(2));
		}
		
		final String UL_RGX = "<ul class=\"DnsResuListWrap fl DnsWL\">([\\S\\s]+?)</ul>";
		String ul = RegexUtils.findFirst(response, UL_RGX);
//		System.out.println(ul);
		return "";
	}
	
}
