package exp.github.tools;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.Charset;
import exp.libs.envm.Delimiter;
import exp.libs.envm.HttpHead;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.io.JarUtils;
import exp.libs.utils.num.RandomUtils;
import exp.libs.utils.os.OSUtils;
import exp.libs.utils.other.LogUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.utils.verify.VerifyUtils;
import exp.libs.warp.net.http.HttpURLUtils;

/**
 * <PRE>
 * Github的DNS优化器.
 * ------------------------------------
 * 自动测试本地到Github的Host域名中TTL最小的DNS服务器, 并配置到系统的Hosts文件.
 * 此工具由网站 http://tool.chinaz.com 提供支持.
 * 
 * </PRE>
 * <B>PROJECT : </B> github-fill-empty-dir
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2018-04-28
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class DnsOptimizer {

	/**
	 * <PRE>
	 * 需要测试TTL的Host域名主机列表.
	 * ---------------------------
	 *  <B>若需增加Host域名主机请修改此�?</B>
	 * </PRE>
	 */
	private final static String[] HOSTS = new String[] {
		"github.global.ssl.fastly.net", 	// 导致Github访问慢的罪魁祸首
		"github.com", 						// Github的SVN服务器域�?
		"assets-cdn.github.com",
		"www.github.com", 
		"avatars0.githubusercontent.com", 
		"avatars1.githubusercontent.com"
	};
	
	/**
	 * [DNS优化器] 工具入口
	 * @param args
	 */
	public static void main(String[] args) {
		LogUtils.loadLogBackConfig();
		optimizeDNS();
	}
	
	
///////////////////////////////////////////////////////////////////////////////
	
	
	/** 日志�? */
	private final static Logger log = LoggerFactory.getLogger(DnsOptimizer.class);
	
	/** Hosts模板文件 */
	private final static String HOSTS_TPL = "/exp/github/tools/hosts.tpl";
	
	/** Windows本地Hosts文件位置 */
	private final static String WIN_HOST_PATH = "C:\\Windows\\System32\\drivers\\etc\\hosts";
	
	/** 获取DNS服务器列表的URL */
	private final static String DNS_URL = "http://tool.chinaz.com/dns";
	
	/** 测试host在各个DNS服务器的TTL的URL */
	private final static String TTL_URL = "http://tool.chinaz.com/AjaxSeo.aspx";
	
	/** 私有化构造函�? */
	protected DnsOptimizer() {}
	
	/**
	 * 优化Hosts域名主机的DNS绑定
	 */
	public static void optimizeDNS() {
		if(!OSUtils.isWin()) {
			log.warn("此功能暂时只支持windows系统");
			return;
		}
		
		Map<String, String> hosts = readHosts();
		for(String host : HOSTS) {
			log.info("正在查找hosts域名主机 [{}] 的最优DNS服务�?...", host);
			String bestDNS = findBestDNS(host);
			if(VerifyUtils.isIP(bestDNS)) {
				hosts.put(host, bestDNS);
				log.info("优化hosts域名主机 [{}] 的最优DNS服务器为: [{}]", host, bestDNS);
				
			} else {
				log.warn("优化hosts域名主机 [{}] 的DNS服务器失�?", host);
			}
		}
		
		if(backupHosts()) {
			boolean isOk = saveHosts(hosts);
			log.info("保存本次DNS优化结果到Hosts文件 [{}] {}", WIN_HOST_PATH, (isOk ? "成功" : "失败"));
			
		} else {
			log.warn("备份本地Hosts文件失败, 本次DNS优化结果不保存到Hosts文件");
		}
	}
	
	/**
	 * 读取当前Host文件的配置表
	 * @return 当前的有效hosts配置 (ip -> address)
	 */
	private static Map<String, String> readHosts() {
		Map<String, String> hosts = new HashMap<String, String>();
		List<String> lines = FileUtils.readLines(WIN_HOST_PATH, Charset.ISO);
		for(String line : lines) {
			if(StrUtils.isTrimEmpty(line) || line.startsWith("#")) {
				continue;
			}
			
			try {
				String[] vk = line.split(" ");
				if(vk.length == 2) {
					hosts.put(vk[1].trim(), vk[0].trim());
				}
			} catch(Exception e) {
				log.error("读取Hosts文件的配置行失败: {}", line, e);
			}
		}
		return hosts;
	}
	
	/**
	 * 备份本地Host文件
	 * @return
	 */
	private static boolean backupHosts() {
		String bakPath = WIN_HOST_PATH.concat(".bak");
		return FileUtils.copyFile(WIN_HOST_PATH, bakPath);
	}
	
	/**
	 * 更新Hosts文件
	 * @param hosts 更新后的hosts配置 (ip -> address)
	 * @return
	 */
	private static boolean saveHosts(Map<String, String> hosts) {
		String hostHeader = JarUtils.read(HOSTS_TPL, Charset.ISO);
		StringBuilder sb = new StringBuilder(hostHeader);
		Iterator<String> hostIts = hosts.keySet().iterator();
		while(hostIts.hasNext()) {
			String host = hostIts.next();
			String dns = hosts.get(host);
			sb.append(StrUtils.concat(dns, " ", host, Delimiter.CRLF));
		}
		String content = sb.toString();
		log.info("正在更新Hosts文件:\r\n{}", content);
		return FileUtils.write(WIN_HOST_PATH, content, Charset.ISO, false);
	}
	
	/**
	 * 寻找本地到host域名主机的最优DNS服务�?
	 * @param host host域名主机
	 * @return 当前TTL最小的DNS服务器IP
	 */
	private static String findBestDNS(String host) {
		int minTTL = Integer.MAX_VALUE;
		String bestDNS = "";
		
		Map<String, String> servers = _getDnsServers(host);
		log.debug("获取hosts域名主机 [{}] 的候选DNS服务器共 [{}] �?", host, servers.size());
		
		Iterator<String> ids = servers.keySet().iterator();
		while(ids.hasNext()) {
			String id = ids.next();
			String name = servers.get(id);
			
			String rsts = _testTTL(host, id, name);
			try {
				JSONObject json = JSONObject.fromObject(rsts);
				JSONArray list = JsonUtils.getArray(json, "list");
				for(int i = 0; i < list.size(); i++) {
					JSONObject rst = list.getJSONObject(i);
					String ip = JsonUtils.getStr(rst, "result");
					String address = JsonUtils.getStr(rst, "ipaddress");
					int ttl = JsonUtils.getInt(rst, "ttl", Integer.MAX_VALUE);
					
					log.debug("测试: host域名主机 [{}] 到DNS服务�? [{}({})] 的TTL�?: {}", host, ip, address, ttl);
					if(minTTL > ttl) {
						minTTL = ttl;
						bestDNS = ip;
						log.debug("更新: host域名主机 [{}] 到DNS服务�? [{}({})] 的TTL�? [{}] 更优", host, ip, address, ttl);
					}
				}
			} catch(Exception e) {
				log.error("测试host域名主机 [{}] 到DNS服务�? [{}] 的TTL失败: {}", host, id, rsts, e);
			}
			
			// TTL = 1 已经极好�?, 无需再继续测�?
			if(minTTL <= 1) {
				break;
			}
		}
		return bestDNS;
	}
	
	/**
	 * 获取DNS服务器列�?
	 * @param host host域名主机
	 * @return Map: server-id -> server-name
	 */
	private static Map<String, String> _getDnsServers(String host) {
		Map<String, String> header = _GET_HEADER(host);
		Map<String, String> request = new HashMap<String, String>();
		request.put("type", "1");
		request.put("host", host);
		request.put("ip", "");
		
		// 从返回的页面源码中提�? server 列表 (JSON�?):
		// var servers = [{id:15381430,ip:"qSaqwKBbIMiHFNDIIIg2VA==",state:0,trytime:0},{id:15381438,ip:"pc5LXpA6Y0LtmOpoKX3zGA==",state:0,trytime:0},{id:15381439,ip:"5n2koVgLsBbs7W8XbIL2NQ==",state:0,trytime:0},{id:15381451,ip:"fL3WGCQ8i0kNrxXqCGAcpg==",state:0,trytime:0},{id:15381437,ip:"SdvPAD3yuuRRgUgJQLfaUQ==",state:0,trytime:0},{id:15381441,ip:"Rv90/Ksj1L6zXUx96XEFbA==",state:0,trytime:0},{id:15381445,ip:"UsXmWPyUMCAl22fytxVeYA==",state:0,trytime:0},{id:15381446,ip:"DiRV3R7jjMVfu0/d6bXYTg==",state:0,trytime:0},{id:15381447,ip:"wfe/baph0aVy7vSzQ8JCew==",state:0,trytime:0},{id:15381433,ip:"zFjFw1wXjPGw24s1pgwUlg==",state:0,trytime:0},{id:15381449,ip:"Tllr7HLQodVpDDFM0Ssc9A==",state:0,trytime:0},{id:15381436,ip:"lsB7oELLLoiNHPwhyWj4YA==",state:0,trytime:0},{id:15381448,ip:"eaWEej3puEKNjremlHa|0w==",state:0,trytime:0},{id:15381442,ip:"fAiZyrOZG6sOpFazf1zdVg==",state:0,trytime:0},{id:15381443,ip:"smuXAjaliTLUrOoVs/MnVQ==",state:0,trytime:0},{id:15381444,ip:"VZWE4uxPBJAKFLPTtoHyyQ==",state:0,trytime:0},{id:15381440,ip:"abaieVMlEG3aU4jEmZOZrg==",state:0,trytime:0},{id:15381434,ip:"/LqwY7|RTOTUAm/8Eln8dQ==",state:0,trytime:0},{id:15381435,ip:"FUFXcwK4d|5goTcqAeJyHA==",state:0,trytime:0},{id:15381431,ip:"kZili0C|QmwVj8/IZI9MOw==",state:0,trytime:0},{id:15381432,ip:"4|LMEILycPoa9DPxVJb3gg==",state:0,trytime:0},{id:15381450,ip:"JACYvxRvL1|CnyK9sCL7/g==",state:0,trytime:0}];
		String RGX_SERVERS = "var servers = (.+)";
		String response = HttpURLUtils.doGet(DNS_URL, header, request);
		String serverJson = RegexUtils.findFirst(response, RGX_SERVERS);
		
		// 从JSON串中提取 server �? id->name 关系:
		RGX_SERVERS = "id:([^,]+),ip:\"([^\"]+)\"";
		Map<String, String> servers = new HashMap<String, String>();
		List<List<String>> datas = RegexUtils.findAll(serverJson, RGX_SERVERS);
		for(List<String> data : datas) {
			String id = data.get(1);
			String server = data.get(2);
			servers.put(id, server);
		}
		return servers;
	}
	
	/**
	 * 测试host域名主机到指定的DNS服务器（集群）的TTL
	 * @param host host域名主机
	 * @param serverId DNS服务器ID
	 * @param serverName DNS服务器名�?
	 * @return 测试结果列表, 如：{"state":1,"id":15381435,"list":[{"type":"A","result":"151.101.1.194","ipaddress":"美国 Fastly公司CDN网络节点","ttl":"30"},{"type":"A","result":"151.101.65.194","ipaddress":"美国 Fastly公司CDN网络节点","ttl":"30"},{"type":"A","result":"151.101.129.194","ipaddress":"美国 Fastly公司CDN网络节点","ttl":"30"},{"type":"A","result":"151.101.193.194","ipaddress":"美国 Fastly公司CDN网络节点","ttl":"30"}]}
	 */
	private static String _testTTL(String host, String serverId, String serverName) {
		Map<String, String> header = _POST_HEADER(host);
		Map<String, String> request = new HashMap<String, String>();
		request.put("host", host);
		request.put("type", "1");
		request.put("total", "18");
		request.put("process", "13");
		request.put("right", "13");
		
		String jQuery = "jQuery".concat(_getJQueryID());
		String ttlUrl = StrUtils.concat(TTL_URL, 
				"?t=dns", 
				"&server=", serverName, 
				"&id=", serverId, 
				"&callback=", jQuery);
		
		// 测试到DNS服务器（集群）的TTL结果, 返回值形�?: 
		// jQuery1103390065854505800381_1527217623098({"state":1,"id":15381435,"list":[{"type":"A","result":"151.101.1.194","ipaddress":"美国 Fastly公司CDN网络节点","ttl":"30"},{"type":"A","result":"151.101.65.194","ipaddress":"美国 Fastly公司CDN网络节点","ttl":"30"},{"type":"A","result":"151.101.129.194","ipaddress":"美国 Fastly公司CDN网络节点","ttl":"30"},{"type":"A","result":"151.101.193.194","ipaddress":"美国 Fastly公司CDN网络节点","ttl":"30"}]})
		String REGEX = jQuery.concat("\\((.*)\\)");
		String response = HttpURLUtils.doPost(ttlUrl, header, request);
		return RegexUtils.findFirst(response, REGEX);
	}
	
	/**
	 * 生成随机JQueryID
	 * @return
	 */
	private static String _getJQueryID() {
		StringBuilder uniqueID = new StringBuilder("11");
		for(int i = 0; i < 20; i++) {
			uniqueID.append(RandomUtils.genInt(10));
		}
		uniqueID.append("_").append(System.currentTimeMillis());
		return uniqueID.toString();
	}
	
	/**
	 * GET请求�?
	 * @param host
	 * @return
	 */
	private static Map<String, String> _GET_HEADER(String host) {
		Map<String, String> header = new HashMap<String, String>();
		header.put(HttpHead.KEY.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		header.put(HttpHead.KEY.ACCEPT_ENCODING, "gzip, deflate, sdch");
		header.put(HttpHead.KEY.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8,en;q=0.6");
		header.put(HttpHead.KEY.CONNECTION, "keep-alive");
		header.put(HttpHead.KEY.HOST, "tool.chinaz.com");
		header.put(HttpHead.KEY.REFERER, "http://tool.chinaz.com/dns?type=1&host=" + host + "&ip=");
		header.put(HttpHead.KEY.USER_AGENT, HttpHead.VAL.USER_AGENT);
		return header;
	}
	
	/**
	 * POST请求�?
	 * @param host
	 * @return
	 */
	private static Map<String, String> _POST_HEADER(String host) {
		Map<String, String> header = _GET_HEADER(host);
		header.put(HttpHead.KEY.CONTENT_TYPE, HttpHead.VAL.POST_FORM.concat(Charset.UTF8));
		return header;
	}
	
}
