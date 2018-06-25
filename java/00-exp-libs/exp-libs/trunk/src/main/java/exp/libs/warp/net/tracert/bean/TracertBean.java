package exp.libs.warp.net.tracert.bean;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <PRE>
 * Tracert测试返回结果对象
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2016-02-14
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class TracertBean {

	/** tracert主机/ip  */
	private String host;
	
	/** 主机的IP  */
	private String hostIP;
	
	/** 返回结果  */
	private Map<String, Double> ways = new LinkedHashMap<String, Double>();
	
	/** tracert结果：success/fail/time out/not handle host  */
	private String result = "success";

	/** 操作ip  */
	private String localIP;
	
	/** 耗时  */
	private long payTime = 0;
	
	/**
	 * 构造方�?
	 */
	public TracertBean() {
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
	 * ways
	 * @return the ways
	 */
	public Map<String, Double> getWays() {
		return ways;
	}

	/**
	 * ways
	 * @param ways the ways to set
	 */
	public void setWays(Map<String, Double> ways) {
		this.ways = ways;
	}

	/**
	 * result
	 * @return the result
	 */
	public String getResult() {
		return result;
	}

	/**
	 * result
	 * @param result the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}

	/**
	 * localIP
	 * @return the localIP
	 */
	public String getLocalIP() {
		return localIP;
	}

	/**
	 * localIP
	 * @param localIP the localIP to set
	 */
	public void setLocalIP(String localIP) {
		this.localIP = localIP;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new org.apache.commons.lang3.builder.ReflectionToStringBuilder(
				this, 
				org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE)
				.toString();
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

	/**
	 * hostIP
	 * @return the hostIP
	 */
	public String getHostIP() {
		return hostIP;
	}

	/**
	 * hostIP
	 * @param hostIP the hostIP to set
	 */
	public void setHostIP(String hostIP) {
		this.hostIP = hostIP;
	}
	
}
