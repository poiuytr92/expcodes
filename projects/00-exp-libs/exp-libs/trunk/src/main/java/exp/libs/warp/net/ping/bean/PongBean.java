package exp.libs.warp.net.ping.bean;

/**
 * <PRE>
 * ping测试返回结果对象
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2016-02-14
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class PongBean {

	/** ping时间  */
	private float pingTime;
	
	/** ping Ip  */
	private String ip;
	
	/** 发起ip  */
	private String localIp = "localhost";
	
	/** 已接收  */
	private int getPack;
	
	/** 已发送  */
	private int sentPack = 4;
	
	/** 丢包率%  */
	private float discards;
	
	/** 包长度，单位byte  */
	private float pksLenght;
	
	/** 最长返回时间 ms  */
	private float maxDelay;
	
	/** 最短返回时间ms  */
	private float minDelay;
	
	/** 平均返回时间  */
	private float avgDelay;
	
	/** ping次数  */
	private int count;

	/**
	 * 错误信息
	 */
	private String errMsg;
	
	/**
	 * 错误标志
	 */
	private boolean isError = false;
	
	
	
	/**
	 * isError
	 * @return the isError
	 */
	public boolean isError() {
		return isError;
	}

	/**
	 * setError
	 * @param isError the isError to set
	 */
	public void setError(boolean isError) {
		this.isError = isError;
	}

	/**
	 * getErrMsg
	 * @return the errMsg
	 */
	public String getErrMsg() {
		return errMsg;
	}

	/**
	 * setErrMsg
	 * @param errMsg the errMsg to set
	 */
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	/**
	 * pingTime
	 * @return the pingTime
	 */
	public float getPingTime() {
		return pingTime;
	}

	/**
	 * pingTime
	 * @param pingTime the pingTime to set
	 */
	public void setPingTime(float pingTime) {
		this.pingTime = pingTime;
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
	 * localIp
	 * @return the localIp
	 */
	public String getLocalIp() {
		return localIp;
	}

	/**
	 * localIp
	 * @param localIp the localIp to set
	 */
	public void setLocalIp(String localIp) {
		this.localIp = localIp;
	}

	/**
	 * discards
	 * @return the discards
	 */
	public float getDiscards() {
		return discards;
	}

	/**
	 * discards
	 * @param discards the discards to set
	 */
	public void setDiscards(float discards) {
		this.discards = discards;
	}

	/**
	 * pksLenght
	 * @return the pksLenght
	 */
	public float getPksLenght() {
		return pksLenght;
	}

	/**
	 * pksLenght
	 * @param pksLenght the pksLenght to set
	 */
	public void setPksLenght(float pksLenght) {
		this.pksLenght = pksLenght;
	}

	/**
	 * maxDelay
	 * @return the maxDelay
	 */
	public float getMaxDelay() {
		return maxDelay;
	}

	/**
	 * maxDelay
	 * @param maxDelay the maxDelay to set
	 */
	public void setMaxDelay(float maxDelay) {
		this.maxDelay = maxDelay;
	}

	/**
	 * minDelay
	 * @return the minDelay
	 */
	public float getMinDelay() {
		return minDelay;
	}

	/**
	 * minDelay
	 * @param minDelay the minDelay to set
	 */
	public void setMinDelay(float minDelay) {
		this.minDelay = minDelay;
	}

	/**
	 * avgDelay
	 * @return the avgDelay
	 */
	public float getAvgDelay() {
		return avgDelay;
	}

	/**
	 * avgDelay
	 * @param avgDelay the avgDelay to set
	 */
	public void setAvgDelay(float avgDelay) {
		this.avgDelay = avgDelay;
	}

	/**
	 * count
	 * @return the count
	 */
	public float getCount() {
		return count;
	}

	/**
	 * count
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
//		return "PingReturnBean [localIp=" + localIp + ", ip=" + ip
//				+ ", pingTime=" + pingTime + ", discards=" + discards
//				+ ", pksLenght=" + pksLenght + ", maxDelay=" + maxDelay
//				+ ", minDelay=" + minDelay + ", avgDelay=" + avgDelay
//				+ ", count=" + count + "]";
		return new org.apache.commons.lang3.builder.ReflectionToStringBuilder(
				this, 
				org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE)
				.toString();
	}

	/**
	 * getPack
	 * @return the getPack
	 */
	public int getGetPack() {
		return getPack;
	}

	/**
	 * getPack
	 * @param getPack the getPack to set
	 */
	public void setGetPack(int getPack) {
		this.getPack = getPack;
	}

	/**
	 * sentPack
	 * @return the sentPack
	 */
	public int getSentPack() {
		return sentPack;
	}

	/**
	 * sentPack
	 * @param sentPack the sentPack to set
	 */
	public void setSentPack(int sentPack) {
		this.sentPack = sentPack;
	}
	
}
