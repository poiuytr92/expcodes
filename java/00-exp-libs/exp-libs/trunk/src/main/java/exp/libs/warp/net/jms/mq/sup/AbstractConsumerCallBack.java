package exp.libs.warp.net.jms.mq.sup;

/**
 * <pre>
 * 消息消费者回调抽象类
 * </pre>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2016-02-14
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public abstract class AbstractConsumerCallBack {

	/**
	 * 消息消费者自动重连异常
	 * 
	 * @param e
	 *            异常
	 */
	public void onReConnectionException(Throwable e) {

	}

	/**
	 *  MQ 自动重连工具ConsumerHelper提供的异常回调
	 */
}
