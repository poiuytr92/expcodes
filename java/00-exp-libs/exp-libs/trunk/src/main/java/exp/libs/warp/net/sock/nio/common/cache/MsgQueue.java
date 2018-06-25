package exp.libs.warp.net.sock.nio.common.cache;

import java.util.LinkedList;
import java.util.List;

/**
 * <pre>
 * 原始消息封装类
 * 
 * 封装了采集和处理会话所接收到的原始消息的基本方法
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public final class MsgQueue {

	/** 允许缓存的最大未处理消息�? */
	public final static int MAX_MSG_LIMIT = 100;
	
	/**
	 * 原始消息队列
	 */
	private List<String> msgList;

	/**
	 * 历史总消息数
	 */
	private int totalMsgCnt;

	/**
	 * 已处理消息数
	 */
	private int handledMsgCnt;

	/**
	 * 读写�?
	 */
	private byte[] lock;

	/**
	 * <pre>
	 * 构造函�?
	 * </pre>
	 */
	public MsgQueue() {
		msgList = new LinkedList<String>();
		totalMsgCnt = 0;
		handledMsgCnt = 0;
		lock = new byte[1];
	}

	/**
	 * <pre>
	 * 检查是否有新消�?
	 * </pre>
	 * 
	 * @return true:有新消息; false:无新消息
	 */
	public boolean hasNewMsg() {
		boolean flag = false;

		synchronized (lock) {
			if (handledMsgCnt < totalMsgCnt) {
				return true;
			}
		}
		return flag;
	}

	/**
	 * <pre>
	 * 添加新消息到消息队列
	 * </pre>
	 * 
	 * @param newMsg
	 *            新消�?
	 */
	public boolean addNewMsg(String newMsg) {
		boolean isOk = false;
		synchronized (lock) {
			if(getWaitCnt() < MAX_MSG_LIMIT) {
				msgList.add(newMsg);
				totalMsgCnt++;
				isOk = true;
			}
		}
		return isOk;
	}

	/**
	 * <pre>
	 * 获取并移除最先到来的消息
	 * </pre>
	 * 
	 * @return 最先到来的消息
	 */
	public String getMsg() {
		synchronized (lock) {
			handledMsgCnt++;
			return msgList.remove(0);
		}
	}

	/**
	 * <pre>
	 * 释放消息队列对象占用的资�?
	 * </pre>
	 */
	public void clear() {
		synchronized (lock) {
			msgList.clear();
			msgList = null;
		}
	}

	/**
	 * <pre>
	 * 获取历史消息的总数（包括已删除的消息）
	 * </pre>
	 * 
	 * @return 历史消息的总数
	 */
	public int getTotalCnt() {
		synchronized (lock) {
			return totalMsgCnt;
		}
	}

	/**
	 * <pre>
	 * 获取已执行的消息�?
	 * </pre>
	 * 
	 * @return 已执行的消息�?
	 */
	public int getHandledMsgCnt() {
		synchronized (lock) {
			return handledMsgCnt;
		}
	}

	/**
	 * <pre>
	 * 获取等待执行的消息数
	 * </pre>
	 * 
	 * @return 已执行的消息�?
	 */
	private int getWaitCnt() {
		return totalMsgCnt - handledMsgCnt;
	}

}
