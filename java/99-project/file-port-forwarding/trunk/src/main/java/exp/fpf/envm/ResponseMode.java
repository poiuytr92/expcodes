package exp.fpf.envm;

/**
 * <pre>
 * 响应数据的接收模式:
 *  1: sock监听模式 (需隔离装置开放TCP转发端口)
 *  2: file扫描模式 (需隔离装置主动扫描文件目录转发)
 *  
 * 注：隔离装置的请求数据只能通过文件扫描模式发送
 * </pre>	
 * <B>PROJECT : </B> file-port-forwarding
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-07-28
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class ResponseMode {

	/** Socket监听模式 */
	public final static int SOCKET = 1;
	
	/** 文件扫描模式 */
	public final static int FILE = 2;
	
}
