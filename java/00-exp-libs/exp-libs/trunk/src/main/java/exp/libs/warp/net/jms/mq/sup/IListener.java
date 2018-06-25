package exp.libs.warp.net.jms.mq.sup;

import javax.jms.MessageListener;

/**监听接口<br>
 * 使用场景：获取关注的消息内容时使用<br>
 * 步骤: 监听消息时,需要实现该类,并将实现类制定为监听类。<br>
 * 例：consumer.setMessageListener(new Listener());<br>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2016-02-14
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public interface  IListener extends MessageListener{
	//public void onMessage(Message message);
	
	/**
	 * 或许仅仅是为了使用I****来表示接�?
	 */
}
