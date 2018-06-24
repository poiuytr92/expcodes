package exp.libs.warp.net.ping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exp.libs.envm.Charset;
import exp.libs.utils.os.OSUtils;
import exp.libs.warp.net.ping.bean.PongBean;

/**
 * <pre>
 * 简单ping操作，返回ping字符串
 * </pre>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2016-02-14
 * @author    EXP: www.exp-blog.com
 * @since     jdk版本：jdk1.6
 */
public class Ping {

	/**
	 * test main
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Ping ping = new Ping();
		String str = ping.ping("172.168.10.100");
		System.out.println(str);
		PongBean pr = ping.parsePingReturn(str);
		System.out.println(pr);
	}
	
	/** 本地IP  */
	public static String localIp = "127.0.0.1";

	/** ping命令 */
	public static String PING_COMMAND = "ping";

	/**
	 * 等待时间，单位为毫秒
	 */
	private int deadTime;
	
	/**
	 * ping次数
	 */
	private int count;
	
	/**
	 * ping间隔,单位为秒
	 */
	private double interval = 0.2;
	
	/** Ping ip */
	private String ip = "";
	
	/** 获取本地IP  */
	static {
		InetAddress addr = null;
		try {
			addr = InetAddress.getLocalHost();
			localIp = addr.getHostAddress().toString();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ping操作
	 * 
	 * @param ip
	 * @return String
	 */
	public String ping(String ip) {
		this.ip = ip;
		String command = resolvePingCommand(ip);
	
		Process process = null;
		InputStream is = null;
		try {
			process = Runtime.getRuntime().exec(command);
			is = process.getInputStream();
			StringBuffer sub = new StringBuffer();
			sub = readLine(is);
			return sub.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
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
	 * 
	 * resolvePingCommand
	 * @param ip2
	 * @return
	 */
	private String resolvePingCommand(String ip2) {
		if (OSUtils.isWin()) {
			return resolveWinPing(ip);
		}else{
			return resolveLinuxPing(ip);
		}
	}

	/**
	 * resolveLinuxPing
	 * @param ip2
	 * @return
	 */
	private String resolveLinuxPing(String ip) {
		String result = PING_COMMAND;
		if(this.deadTime != 0){
			result += " -w " + this.deadTime;
		}
		
		if(this.count != 0){
			result += " -c " + this.count;
		}
		
		if(this.interval != 0){
			result += " -i " + this.interval;
		}
		return result + " " + ip;
	}

	/**
	 * resolveWinPing
	 * @param ip2
	 * @return
	 */
	private String resolveWinPing(String ip2) {
		String result = PING_COMMAND;
		if(this.deadTime != 0){
			result += " -w "+this.deadTime;
		}
		
		if(this.count != 0){
			result += " -n "+this.count;
		}
		
//		if(this.interval != 0){
//			
//		}
		return result + " " + ip;
	}

	/**
	 * 读取流信息
	 * 
	 * @param ins
	 *            输入流
	 * @return 读取所有行返回字符串buffer
	 * @throws IOException
	 *             异常
	 */
	private static StringBuffer readLine(InputStream ins) throws IOException {
		StringBuffer buf = new StringBuffer();
		// 读取process信息
		BufferedReader reader = new BufferedReader(new InputStreamReader(ins,
				Charset.DEFAULT));
		String line = null;

		while ((line = reader.readLine()) != null) {
			buf.append(line).append("\n");
		}

		return buf;
	}

	public PongBean parsePingReturn(String result) {
		PongBean prb = new PongBean();
		String discards = "0";
		String maxDelay = "0";
		String minDelay = "0";
		String avgDelay = "0";
		String pksLenght = "0";
		String sentPack = "4";
		String getPack = "0";

		if (find("packets transmitted", result)) { // linux命令行
			sentPack = exec(
					".*([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|[\\d]+) packets.*",
					result);
			getPack = exec(
					".*([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|[\\d]+) received.*",
					result);
			discards = exec(
					".*([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|[\\d]+)% packet loss.*",
					result);
			maxDelay = exec(
					".*\\/([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*)\\/.* ms.*",
					result);
			minDelay = exec(
					".*mdev = ([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|[\\d]+).*",
					result);
			avgDelay = exec(
					".*mdev =.*\\/([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*)\\/.*\\/.*.*",
					result);
			pksLenght = exec(".*(\\d+) bytes from.*", result);
		} else if (find("Reply from", result)) { // win en命令行
			sentPack = exec(".*Sent = ([\\d]+).*", result);
			getPack = exec(".*Received = ([\\d]+).*", result);
			discards = exec(
					".*Lost = ([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|[\\d]+).*",
					result);
			maxDelay = exec(
					".*Maximum = ([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|[\\d]+).*",
					result);
			minDelay = exec(
					".*Minimum = ([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|[\\d]+).*",
					result);
			avgDelay = exec(
					".*Average = ([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|[\\d]+).*",
					result);
			pksLenght = exec(".*bytes=(\\d+).*", result);
		} else { // win 中文命令行
			sentPack = exec(".*已发送 = ([\\d]+).*", result);
			getPack = exec(".*已接收 = ([\\d]+).*", result);
			discards = exec(
					".*丢失 = ([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|[\\d]+).*",
					result);
			maxDelay = exec(
					".*最长 = ([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|[\\d]+).*",
					result);
			minDelay = exec(
					".*最短 = ([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|[\\d]+).*",
					result);
			avgDelay = exec(
					".*平均 = ([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|[\\d]+).*",
					result);
			pksLenght = exec(".*字节=(\\d+).*", result);
		}
		prb.setDiscards(Float.parseFloat(discards));
		prb.setMaxDelay(Float.parseFloat(maxDelay));
		prb.setMinDelay(Float.parseFloat(minDelay));
		prb.setAvgDelay(Float.parseFloat(avgDelay));
		prb.setPksLenght(Float.parseFloat(pksLenght));
		prb.setSentPack(Integer.parseInt(sentPack));
		prb.setGetPack(Integer.parseInt(getPack));
		prb.setCount(Integer.parseInt(sentPack));
		prb.setLocalIp(localIp);
		prb.setIp(ip);
		
		if(prb.getSentPack() == -1 ||prb.getGetPack() == -1){
			prb.setError(true);
			prb.setErrMsg(result);
		}
		return prb;
	}

	/**
	 * exec
	 * 
	 * @param string
	 * @param result
	 * @return
	 */
	private String exec(String string, String result) {
		Pattern pattern = Pattern.compile(string);
		Matcher matcher = pattern.matcher(result);
		if (matcher.find()) {
//			for (int i = 0; i < matcher.groupCount(); i++) {
//				System.out.print(matcher.group(i));
//			}
//			System.out.println(matcher.group(1));
			return matcher.group(1);
		}
		return "-1";
	}

	/**
	 * find
	 * 
	 * @param string
	 * @param result
	 * @return
	 */
	private boolean find(String string, String result) {
		if (result.contains(string)) {
			return true;
		}
		return false;
	}

	/**
	 * ip
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * ip
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * getDeadTime
	 * @return the deadTime
	 */
	public int getDeadTime() {
		return deadTime;
	}

	/**
	 * setDeadTime
	 * @param deadTime the deadTime to set
	 */
	public void setDeadTime(int deadTime) {
		this.deadTime = deadTime;
	}

	/**
	 * getCount
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * setCount
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * getInterval
	 * @return the interval
	 */
	public double getInterval() {
		return interval;
	}

	/**
	 * setInterval
	 * @param interval the interval to set
	 */
	public void setInterval(double interval) {
		if(interval>0.2){
			this.interval = interval;
		}
	}

	
	
}
