package exp.libs.warp.net.jms.mq.sup;

import java.io.IOException;

/**
 * 实时监控连接的接口基本实现，对外提供4个调用接口<br/>
 * 
 * <pre>
 * public void onCommand(Object command)<br/>
 * public void onException(IOException error)<br/>
 * public void transportInterupted()<br/>
 * public void transportResumed()<br/>
 * 
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2016-02-14
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public abstract class TransportListenerImpl implements ITransportListener {

	/**
	 * called to process a command
	 * 
	 * @param command
	 *            Command
	 */
	public void onCommand(Object command) {
	}

	/**
	 * An unrecoverable exception has occured on the transport
	 * 
	 * @param error
	 *            IOException
	 */
	public void onException(IOException error) {
	}

	/**
	 * The transport has suffered an interuption from which it hopes to recover
	 */
	public void transportInterupted() {
	}

	/**
	 * The transport has resumed after an interuption
	 */
	public void transportResumed() {
	}

	/**
	 * ITransportListener 的默认适配�?
	 */
}
