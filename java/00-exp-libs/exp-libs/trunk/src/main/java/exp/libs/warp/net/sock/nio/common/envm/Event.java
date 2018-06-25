package exp.libs.warp.net.sock.nio.common.envm;

/**
 * <pre>
 * 枚举类：事件类型
 * 
 * 主要用于程序事件的类型定义，根据不同类型执行不同操作。
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public enum Event {

	/** 通用事件类型 */
	COMMON(0, "通用事件"),

	/** 会话事件类型 */
	SESSION(1, "会话事件"),

	/** 消息接收事件类型 */
	MESSAGE_REVC(2, "消息接收事件"),

	/** 消息发送事件类�? */
	MESSAGE_SEND(3, "消息发送事�?"),

	/** 异常事件类型 */
	EXCEPTION(4, "异常事件"),

	/** 其他事件类型 */
	OTHER(99, "其他事件"),

	;

	/** 类型�? */
	public int id;

	/** 类型描述 */
	public String desc;

	/**
	 * 构造函�?
	 * @param id 类型�?
	 * @param desc 类型描述
	 */
	private Event(int id, String desc) {
		this.id = id;
		this.desc = desc;
	}
	
}
