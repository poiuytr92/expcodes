package exp.libs.warp.net.pf.file;

import exp.libs.warp.net.sock.bean.SocketBean;

final class _Envm {

	protected final static String PREFIX_SEND = "send";
	
	protected final static String PREFIX_RECV = "recv";
	
	protected final static String SUFFIX = ".txt";
	
	protected final static int PC_CAPACITY = 1024;
	
	protected final static int IO_BUFF = SocketBean.DEFAULT_BUFF_SIZE * 
			SocketBean.DEFAULT_BUFF_SIZE_UNIT;	// 每次最多读写1MB数据
	
	/** 扫描文件间隔: 单位ms */
	protected final static long SCAN_FILE_INTERVAL = 10;
	
	/** 检测文件数据传输完成间隔: 单位ms */
	protected final static long WAIT_DATA_INTERVAL = 1;
	
}
