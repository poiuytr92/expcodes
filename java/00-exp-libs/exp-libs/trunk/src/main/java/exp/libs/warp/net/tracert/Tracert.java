package exp.libs.warp.net.tracert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import exp.libs.utils.num.NumUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.net.tracert.bean.TracertBean;

/**
 * <pre>
 * 简单Tracert操作，返回Tracert字符串
 * </pre>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2016-02-14
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public  class Tracert {

	/** 系统名称 */
	public static final String OS = System.getProperty("os.name");

	/** 系统编码 */
	public static String encoding = System
			.getProperty("sun.jnu.encoding");
	
	/** 本地IP  */
	public static String localIp = "127.0.0.1";

	/** Tracert命令 */
	public static String TRACERT_COMMAND = "tracert -d -w 1 ";
	
	/** 耗时,毫秒  */
	private long payTime = 0;

	/** Tracert ip/主机 */
	private String host = "";
	
	/** 初始化Tracert命令, 获取本地IP  */
	static {
		if (OS.contains("Windows")) { // 判断系统类型
			TRACERT_COMMAND = "tracert -d -w 1 ";
		} else {
			TRACERT_COMMAND = "traceroute -w 0.1 -n -q 1 ";
		}
		
		InetAddress addr = null;
		try {
			addr = InetAddress.getLocalHost();
			localIp = addr.getHostAddress().toString();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Tracert操作
	 * 
	 * @param ip
	 * @return String
	 */
	public String tracert(String ip) {
		return tracert(ip, null);
	}
	
	/**
	 * Tracert操作
	 * 
	 * @param host
	 * @param deal 消息返回处理方法接口
	 * @return String
	 */
	public String tracert(String host, IDealReturn deal) {
		long start = System.currentTimeMillis();
		this.host = host;
		String command = TRACERT_COMMAND + host;
		Process process = null;
		InputStream is = null;
		StringBuffer sub = null;
		try {
			process = Runtime.getRuntime().exec(command);
			is = process.getInputStream();
			
			if (deal != null) {
				sub = readLine(is, deal);
			} else {
				sub = readLine(is);
			}
			
			return sub.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			long end = System.currentTimeMillis();
			payTime = end - start;
			process.destroy();
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}

	/**
	 * 读取流信�?
	 * 
	 * @param ins
	 *            输入�?
	 * @param deal 消息返回处理方法接口
	 * @return 读取所有行返回字符串buffer
	 * @throws IOException
	 *             异常
	 */
	private StringBuffer readLine(InputStream ins ,IDealReturn deal) 
			throws IOException {
		StringBuffer buf = new StringBuffer();
		// 读取process信息
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(ins, encoding));
		String line = null;

		while ((line = reader.readLine()) != null) {
			deal.deal(line);
			buf.append(line).append("\n");
		}

		return buf;
	}
	
	/**
	 * 读取流信�?
	 * 
	 * @param ins
	 *            输入�?
	 * @return 读取所有行返回字符串buffer
	 * @throws IOException
	 *             异常
	 */
	private StringBuffer readLine(InputStream ins) throws IOException {
		StringBuffer buf = new StringBuffer();
		// 读取process信息
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(ins, encoding));
		String line = null;

		while ((line = reader.readLine()) != null) {
			buf.append(line).append("\n");
		}

		return buf;
	}

	/**
	 * test main
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Tracert trc = new Tracert();
		String str = null;
//		str = trc.tracert("www.baidu.com");
		
		str = trc.tracert("www.baidu.com", new IDealReturn() {
			
			@Override
			public void deal(String line) {
				System.out.println("deal: " + line);
			}
		});
		System.out.println(str);
		System.out.println("耗时�?" + trc.getPayTime());
		
		
//		TracertReturnBean pr = Tracert.parserTracertReturn(str);
//		System.out.println(pr);
	}

	/**
	 * 把返回的报文转成bean�?
	 *
	 * @param result tracert返回报文
	 * @return
	 */
	public TracertBean parseTracertReturn(String result) {
		TracertBean trb = null;

		if (result.contains("traceroute to")
				|| result.contains("Cannot handle")) { // linux命令�?
			trb = createBeanForLinuxEn(result);
		} else if (result.contains("Tracing route")
				|| result.contains("Unable to resolve target")) { // win en命令�?
			trb = createBeanForWinEn(result);
		} else if (result.contains("通过最�?")
				|| result.contains("无法解析目标")) { // win 中文命令�?
			trb = createBeanForWinCn(result);
		}
//		trb.setHost(this.host);
		trb.setLocalIP(Tracert.localIp);
		trb.setPayTime(this.payTime);
		return trb;
	}
	
	//
	//通过最�? 30 个跃点跟踪到 115.239.211.110 的路�?
	//
	//  1     4 ms     6 ms     4 ms  172.168.8.1 
	//  2     *        *        *     请求超时�?
	//  3     3 ms     2 ms     3 ms  183.62.48.225 
	//  4     3 ms     2 ms     3 ms  58.62.111.249 
	//  5    27 ms    85 ms    25 ms  183.56.31.17 
	//  6    27 ms    19 ms    14 ms  61.144.3.6 
	//  7    19 ms    19 ms    18 ms  202.97.56.178 
	//  8    25 ms    30 ms    24 ms  61.164.13.166 
	//  9    51 ms    37 ms    59 ms  115.233.23.214 
	// 10    53 ms    37 ms    37 ms  115.239.209.18 
	// 11     *        *        *     请求超时�?
	// 12     *        *       35 ms  115.239.211.110 
	//
	//跟踪完成�?
	//		
	/**
	 * 解析win中文报文
	 *
	 * @param result 解析字符�?
	 * @return
	 */
	public TracertBean createBeanForWinCn(String result) {
		TracertBean trb = new TracertBean();
		if (result.contains("无法解析目标系统名称")) {
			trb.setResult(result);
		} else {
			Map<String, Double> ways = trb.getWays();
			
			String[] datas = result.split("\n");
			int len = datas.length;
			double time = -1;
			int lineNum = 0;
			String ip = null;
			for (int i = 0; i < len; i++) {
				String line = datas[i];
				//不处理空行和结果�?,还有首行
				if (line.length() > 5 && !line.contains("通过最�?")
						&& !line.contains("�?")) {
					lineNum++;
					if (line.contains("请求超时")) {
						ip = lineNum + "";
						time = -1;
					} else {
//						[\s|\d]+ms[\s|\d]+ms[\s|\d]+ms\s+(.*)$
						ip = RegexUtils.findFirst(line, "[\\s|\\d]+ms[\\s|\\d]+ms[\\s|\\d]+ms\\s+(.*)$");
//						\s+\d+\s+(\d+)\sms
						time = NumUtils.toDouble(RegexUtils.findFirst(line, "\\s+\\d+\\s+(\\d+)\\sms"));
					}
					ways.put(ip, time);
				}
			}
			trb.setWays(ways);
			//获取主机和主机IP
//			.*到\s(\S*)\s
			String tracertHost = RegexUtils.findFirst(result, ".*到\\s(\\S*)\\s");
//			.*\[(.*)\]
			String tracertHostIP = RegexUtils.findFirst(result, ".*\\[(.*)\\]");
			if (null == tracertHostIP || "".endsWith(tracertHostIP)) {
				tracertHostIP = tracertHost;
			}
			trb.setHost(tracertHost);
			trb.setHostIP(tracertHostIP);
		}
		
		return trb;
	}
	
//
//		Tracing route to 115.239.211.110 over a maximum of 30 hops
//
	//	  1     4 ms     5 ms     5 ms  172.168.8.1 
	//	  2     *        *        *     Request timed out.
	//	  3     3 ms     4 ms     2 ms  183.62.48.225 
	//	  4     2 ms     2 ms     2 ms  58.62.111.249 
	//	  5     5 ms     2 ms     2 ms  183.56.31.13 
	//	  6     3 ms     4 ms     6 ms  61.144.3.26 
	//	  7    18 ms    77 ms    19 ms  202.97.77.58 
	//	  8    34 ms    30 ms    29 ms  61.164.13.162 
	//	  9    26 ms    25 ms    30 ms  115.233.23.202 
	//	 10    58 ms    49 ms    36 ms  115.239.209.6 
	//	 11     *        *        *     Request timed out.
	//	 12    26 ms    30 ms    31 ms  115.239.211.110 
	//
	//	Trace complete.
	//	
	/**
	 * 解析win英文报文
	 *
	 * @param result 解析字符�?
	 * @return
	 */
	public TracertBean createBeanForWinEn(String result) {
		TracertBean trb = new TracertBean();
		if (result.contains("Unable to resolve target system name")) {
			trb.setResult(result);
		} else {
			Map<String, Double> ways = trb.getWays();
			
			String[] datas = result.split("\n");
			int len = datas.length;
			double time = -1;
			String ip = null;
			int lineNum = 0;
			for (int i = 0; i < len; i++) {
				String line = datas[i];
				//不处理空行和结果�?
				if (line.length() > 15 && !line.contains("Tracing")
						&& !line.contains("over")) {
					lineNum++;
					if (line.contains("Request timed out.")) {
						ip = lineNum + "";
						time = -1;
					} else {
//						[\s|\d]+ms[\s|\d]+ms[\s|\d]+ms\s+(.*)$
						ip = RegexUtils.findFirst(line, "[\\s|\\d]+ms[\\s|\\d]+ms[\\s|\\d]+ms\\s+(.*)$");
//						\s+\d+\s+(\d+)\sms
						time = NumUtils.toDouble(RegexUtils.findFirst(line, "\\s+\\d+\\s+(\\d+)\\sms"));
					}
					ways.put(ip, time);
				}
			}
			trb.setWays(ways);
			//获取主机和主机IP
//			.*到\s(\S*)
			String tracertHost = RegexUtils.findFirst(result, ".*to\\s(\\S*)");
//			.*\[(.*)\]
			String tracertHostIP = RegexUtils.findFirst(result, ".*\\[(.*)\\]");
			if (null == tracertHostIP || "".endsWith(tracertHostIP)) {
				tracertHostIP = tracertHost;
			}
			trb.setHost(tracertHost);
			trb.setHostIP(tracertHostIP);
		}
		return trb;
	}

	
//	traceroute to 192.168.1.1 (192.168.1.1), 30 hops max, 60 byte packets
//	 1  172.168.8.1  4.599 ms
//	 2  172.168.6.2  29.888 ms
//	 3  *
//	30  *	
	
	/**
	 * 解析Linux英文报文
	 *
	 * @param result 解析字符�?
	 * @return
	 */
	public TracertBean createBeanForLinuxEn(String result) {
		TracertBean trb = new TracertBean();
		if (result.contains("Cannot handle ")) {
			trb.setResult(result);
		} else {
			Map<String, Double> ways = trb.getWays();
			
			String[] datas = result.split("\n");
			int len = datas.length;
			double time = -1;
			int lineNum = 0;
			String ip = null;
			for (int i = 0; i < len; i++) {
				String line = datas[i];
				//不处理首�?
				if (!line.contains("traceroute to")) {
					lineNum++;
					if (line.contains("*")) {
						ip = lineNum + "";
						time = -1;
					} else {
//						[\s|\d]\s+(\S*)
						ip = RegexUtils.findFirst(line, "[\\s|\\d]\\s+(\\S*)");
//						[\s|\d]\s+\S*\s(\S*)\sms
						time = NumUtils.toDouble(RegexUtils.findFirst(line, "[\\s|\\d]\\s+\\S*\\s(\\S*)\\sms"));
					}
					ways.put(ip, time);
				}
			}
			trb.setWays(ways);
			//获取主机和主机IP
//			.*到\s(\S*)\s
			String tracertHost = RegexUtils.findFirst(result, ".*to\\s(\\S*)\\s");
//			.*\((.*)\)
			String tracertHostIP = RegexUtils.findFirst(result, ".*\\((.*)\\)");
			if (null == tracertHostIP || "".endsWith(tracertHostIP)) {
				tracertHostIP = tracertHost;
			}
			trb.setHost(tracertHost);
			trb.setHostIP(tracertHostIP);
		}
		return trb;
	}

	/**
	 * host
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * host
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * payTime
	 * @return the payTime
	 */
	public long getPayTime() {
		return payTime;
	}

	/**
	 * payTime
	 * @param payTime the payTime to set
	 */
	public void setPayTime(long payTime) {
		this.payTime = payTime;
	}

}
