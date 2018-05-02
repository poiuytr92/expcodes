package exp.fpf.envm;

import exp.fpf.Config;
import exp.libs.warp.net.sock.bean.SocketBean;

/**
 * <pre>
 * 常量参数表
 * </pre>	
 * <B>PROJECT：</B> file-port-forwarding
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-07-28
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public final class Param {

	/** 建立连接标识 */
	public final static String MARK_CONN = "#conn#";
	
	public final static String PREFIX_SEND = "send";
	
	public final static String PREFIX_RECV = "recv";
	
	public final static String SUFFIX = ".txt";
	
	public final static int PC_CAPACITY = 1024;
	
	/** 每次最多读写1MB数据 */
	public final static int IO_BUFF = SocketBean.DEFAULT_BUFF_SIZE * 
			SocketBean.DEFAULT_BUFF_SIZE_UNIT;
	
	public final static String SID = "sid";
	
	public final static String DATA = "data";
	
	/** 扫描文件/监听数据间隔: 单位ms */
	public final static long SCAN_DATA_INTERVAL = Config.getInstn().getScanInterval();
	
	/** 检测文件数据传输完成间隔: 单位ms */
	public final static long WAIT_DATA_INTERVAL = 1;
	
}
