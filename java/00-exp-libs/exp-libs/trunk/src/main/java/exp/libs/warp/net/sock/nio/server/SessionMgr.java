package exp.libs.warp.net.sock.nio.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.StrUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.LAUtils;
import exp.libs.warp.net.sock.bean.SocketByteBuffer;
import exp.libs.warp.net.sock.nio.common.cache.MsgQueue;
import exp.libs.warp.net.sock.nio.common.envm.States;
import exp.libs.warp.net.sock.nio.common.envm.Times;
import exp.libs.warp.net.sock.nio.common.filterchain.impl.FilterChain;

/**
 * <pre>
 * 会话管理器线程
 * 
 * 用于管理所有连接到Socket服务端的会话（会话有效性验证、会话消息接收）
 * </pre>	
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
final class SessionMgr implements Runnable {

	/**
	 * 日志器
	 */
	private final static Logger log = LoggerFactory.getLogger(SessionMgr.class);
	
	private final static String A_DELIMITER = "!#@{[";
	
	private final static String Z_DELIMITER = "]}@#!";
	
	/**
	 * 会话列表
	 */
	private List<Session> sessions;

	/**
	 * Socket服务端配置
	 */
	private NioServerConfig sockConf;
	
	/**
	 * 连接会话数
	 */
	private int sessionCnt;

	/**
	 * 工作锁
	 */
	private byte[] lock;
	
	/**
	 * 会话管理线程停止标识
	 */
	private boolean isStop;
	
	/**
	 * 接收消息分隔符集
	 */
	private String[] readDelimiters;

	/**
	 * 构造函数
	 * @param sockConf 服务器配置
	 */
	public SessionMgr(NioServerConfig sockConf) {
		this.sockConf =  sockConf;
		
		this.sessions = new LinkedList<Session>();
		this.sessionCnt = 0;
		this.lock = new byte[1];
		this.isStop = true;
		this.readDelimiters = StrUtils.split(
				sockConf.getReadDelimiter(), A_DELIMITER, Z_DELIMITER);
		if(LAUtils.isEmpty(readDelimiters)) {
			readDelimiters = new String[] { sockConf.getReadDelimiter() };
		}
	}

	/**
	 * 会话管理线程核心
	 */
	@Override
	public void run() {
		isStop = false;
		log.info("服务器会话管理器线程启动.");
		
		while (isStop == false) {
			try {
				long curTime = System.currentTimeMillis();
				
				synchronized (lock) {
					Session session = null;
					for (Iterator<Session> sIts = sessions.iterator();
							sIts.hasNext();) {
						session = sIts.next();
						
						// 若该会话处于等待关闭状态，但超时仍未被远端机关闭，则本地主动关闭
						if (session.isWaitingToClose() == true) {
							if(curTime - session.getNotifyDisconTime() > 
									sockConf.getOvertime()) {
								session.close();
							}
						}
						
						// 检查会话是否超时无动作
						if(session.isOvertime(curTime) == true) {
							log.info("会话 [" + session + "] 超时无动作.断开会话连接.");
							session.close();
						}
						
						// 跳过未验证的会话
						if (session.isVerfied() == false) {
							continue;
							
						// 把发生异常、验证失败或已关闭的会话，进行关闭确认，并从会话维护队列中移除
						} else if (session.isError() == true || 
								session.isPassVerfy() == false || 
								session.isClosed() == true) {

							log.info("成功移除会话 [" + session + "].当前活动会话数" +
									" [" + this.getSessionCnt() + "].");
							
							this.closeSession(session);
							sIts.remove();
							sessionCnt--;
						}
					}
				}

				// 设置陷阱，在阻塞前诱导线程自杀
				if(isStop == true) {
					break;
				}
				
				synchronized (lock) {
					for (Session session : sessions) {
						
						// 把未验证的会话交付到会话级事件处理
						if (session.isVerfied() == false) {
							handleSessionEvent(session);
							
						// 剩下的会话都是允许接收消息的，交付给消息级事件处理
						} else {
							handleMessageEvent(session);
						}
					}
				}
				
				ThreadUtils.tSleep(Times.SLEEP);
				
			} catch (Exception e) {
				log.error("会话管理器发生未知异常.程序即将结束.", e);
				break;
			}
		}
		log.info("服务器会话管理器线程停止.");
		removeAllSessions();
		this.setStop(true);		//若会话管理器线程因异常死亡，则诱导主线程自杀
	}

	/**
	 * 交付会话处理器，处理会话级事件（会话验证）
	 * 
	 * @param session 会话
	 */
	private void handleSessionEvent(Session session) throws Exception {
		log.info("会话管理器开始对会话 [" + session + "] 执行验证.");
		
		FilterChain filterChain = sockConf.getFilterChain();
		filterChain.onSessionCreated(session);
	}

	/**
	 * 交付消息处理器，处理会话中的消息级事件
	 * 
	 * @param session 会话
	 */
	private void handleMessageEvent(Session session) throws Exception {
		try {
			
			// 检查会话是否有需要处理的消息
			if (States.SUCCESS.id == checkNewMsg(session)) {
				
				//这里没有通过while循环一次读完session的消息队列，主要是为了session间的公平性，
				//避免当某个session一次有很多消息到来时，其他session要等很久。
				//但此时如果某个session有很多消息、而另一个几乎没有消息，则会引起处理缓慢的假象。
				//没有消息时的处理时延、主要受事件选择器的blockTime影响，其次是迭代的sleepTime。
				String msg = session.getMsgQueue().getMsg();
				FilterChain filterChain = sockConf.getFilterChain();

				session.flashActionTime();
				filterChain.onMessageReceived(session, msg);
			}
			
		} catch (ClosedSelectorException e) {
			log.warn("会话 [" + session + "] 关闭事件选择器失败." +
        			"此为可忽略异常，不影响程序运行.");
			
		} catch(ArrayIndexOutOfBoundsException e) {
			log.error("会话 [" + session + "] 的本地缓冲区溢出." +
        			"此为异常不影响程序运行.缓冲区被清空.上一条消息的数据可能已丢失或出现缺失.", 
        			e);
        	
		} catch(UnsupportedEncodingException e) {
			log.error("会话 [" + session + "] 设置的字符集非法," +
        			"服务端不支持此字符集编码.关闭会话.", e);
			this.closeSession(session);
        	
		} catch (CancelledKeyException e) {
			log.error("会话 [" + session + "]  取消事件键时发生异常." +
					"关闭会话.", e);
			this.closeSession(session);
			
		} catch (SocketTimeoutException e) {
			log.warn("会话 [" + session + "] 超时无动作.关闭会话.");
			this.closeSession(session);
			
		} catch (ClosedChannelException e) {
			log.error("会话 [" + session + "] 关闭通道时异常.丢弃会话.", 
					e);
			this.closeSession(session);
			
		} catch (IOException e) {
			log.warn("会话 [" + session + "] 读写管道数据异常," +
					"可能客户端已单方面关闭会话.服务端丢弃会话.");
			this.closeSession(session);
			
		} catch (Exception e) {
			log.error("会话 [" + session + "] 抛出未知异常.关闭会话.", 
					e);
			this.closeSession(session);
		}
		
	}

	/**
	 * 检查会话是否有新的待处理消息 首先采集会话通道中的数据，把新到得消息存放在原始消息队列末尾
	 * 然后检查原始消息队列的队头是否为空，非空则将其作为即将处理的消息
	 * 
	 * @param session 会话
	 * @return 只要原始消息队列非空，且会话未关闭，则返回成功状态
	 * @throws Exception 异常
	 */
	private int checkNewMsg(Session session) throws Exception {
		States exState = States.SUCCESS;

		SocketChannel sc = session.getLayerSession();
		Selector selector = Selector.open();
		sc.configureBlocking(false);
		sc.register(selector, SelectionKey.OP_READ);

		int eventNum = selector.select(Times.BLOCK);
		if (eventNum > 0) {
			Iterator<SelectionKey> iterator = selector.selectedKeys()
					.iterator();

			while (iterator.hasNext()) {
				SelectionKey sk = iterator.next();
				iterator.remove();
				int rtn = handleKey(sk, session);

				// 会话通道已断开
				if (rtn < 0) {
					session.close();
					exState = States.FAIL;
					break;
				}
			}

		}
		selector.close();

		//检查消息队列是否存在未处理消息
		if (States.SUCCESS.id == exState.id) {
			if (session.getMsgQueue() == null || 
					false == session.getMsgQueue().hasNewMsg()) {
				exState = States.FAIL;
			}
		}
		return exState.id;
	}

	/**
	 * 从会话通道采集数据，返回-1表示通道已断开
	 * 
	 * @param sk 关注事件键
	 * @param session 会话
	 * @return -1表示会话已关闭，0则正常通讯
	 * @throws Exception 异常
	 */
	private int handleKey(SelectionKey sk, Session session) throws Exception {
		int rtn = 0;
		SocketChannel sc = session.getLayerSession();
		ByteBuffer channelBuffer = session.getChannelBuffer();
		SocketByteBuffer socketBuffer = session.getSocketBuffer();
		
		if (sk.channel().equals(sc) && sk.isReadable()) {
			int count = 0;

			channelBuffer.clear();
			while ((count = sc.read(channelBuffer)) > 0) {
				channelBuffer.flip();
				for (int i = 0; i < count; i++) {
					socketBuffer.append(channelBuffer.get(i));
				}

				int[] rdIdxs = new int[readDelimiters.length];	// 对应每个消息分隔符的索引
				while (true) {	// 可能一次性收到多条消息，在缓冲区可读时需全部处理完，减少处理迟延
					
					// 枚举所有分隔符，取索引值最小的分隔符位置（索引值>=0有效）
					int iEnd = -1;
					for(int i = 0; i < readDelimiters.length; i++) {
						rdIdxs[i] = socketBuffer.indexOf(readDelimiters[i]);
						
						if(rdIdxs[i] >= 0) {
							if(iEnd < 0) {
								iEnd = rdIdxs[i];
								
							} else if(iEnd > rdIdxs[i]) {
								iEnd = rdIdxs[i];
							}
						}
					}
					
					// 所有分隔符都无法截获消息
					if(iEnd < 0) {
						break;
					}
					String newMsg = socketBuffer.subString(iEnd).trim();

					// 把原始消息添加到原始消息队列，并剔除空消息和越限消息(防止攻击)
					if (false == "".equals(newMsg)) {
						if (!session.getMsgQueue().addNewMsg(newMsg)) {
							log.info("会话 [{}] 连续发送超过 [{}] 条未处理消息.消息 [{}] 被服务器抛弃.", 
									session, MsgQueue.MAX_MSG_LIMIT, newMsg);
							session.writeErrMsg("服务端已积压 [" + MsgQueue.MAX_MSG_LIMIT + 
									"] 条未处理消息, 消息 [" + newMsg + "] 被抛弃, 请控制消息发送频率.");
						}
					}
					socketBuffer.delete(iEnd);
				}
				channelBuffer.clear();
			}
			
			// Socket通道已断开
			if (count < 0) {
				log.info("客户端主动关闭会话.");
				rtn = -1;
			}
		}
		return rtn;
	}

	/**
	 * 添加新客户端到会话管理队列
	 * 
	 * @param newSession 新客户端的
	 * @return true:添加成功; false:添加失败
	 * @throws Exception 异常
	 */
	public boolean addSession(Session newSession) throws Exception {
		boolean isOk = false;
		int maxLinkNum = sockConf.getMaxConnectionCount();

		synchronized (lock) {
			if (newSession != null && 
				(maxLinkNum < 0 || sessionCnt < maxLinkNum)) {
				sessions.add(newSession);
				sessionCnt++;
				isOk = true;
				log.info("成功添加新会话 [" + newSession + 
						"] 到会话管理队列.当前活动会话数 [" + this.getSessionCnt() + "].");
			}
		}
		return isOk;
	}
	
	/**
	 * 关闭会话
	 * @param session 会话
	 */
	public void closeSession(Session session) {
		try {
			if(session != null) {
				session.close();
			}
		} catch (NullPointerException e) {
			log.warn("会话[" + session + "] 已经提交过关闭请求.无需再次关闭." +
					"请耐心等待会话关闭.");
			
		} catch (Exception e) {
			session.markError();
			log.error("关闭会话 [" + session + "] 时发生异常.抛弃此会话.", e);
		}
	}

	/**
	 * 移除所有前台客户代理线程
	 * 
	 * @return true:移除成功; false:移除失败
	 * @throws Exception 
	 */
	public boolean removeAllSessions() {
		boolean isOk = false;

		synchronized (lock) {
			if(sessions != null) {
				for(Session session : sessions) {
					closeSession(session);
				}
				
				sessions.clear();
				sessions = null;
				sessionCnt = 0;
				isOk = true;
			}
		}
		return isOk;
	}

	/**
	 * 获取当前活动的客户端连接数
	 * 
	 * @return 当前活动的客户端连接数
	 */
	public int getSessionCnt() {
		return sessionCnt;
	}
	
	/**
	 * 检测会话管理线程是否已死亡
	 * @return 死亡状态
	 */
	public boolean isStop() {
		return isStop;
	}
	/**
	 * 设置陷阱，诱导线程自杀
	 * @param isStop 会话管理器线程死亡标识
	 */
	public void setStop(boolean isStop) {
		this.isStop = isStop;
	}
	
}
