package exp.libs.warp.net.sock.nio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.StrUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.warp.net.sock.bean.SocketByteBuffer;
import exp.libs.warp.net.sock.nio.common.envm.Protocol;
import exp.libs.warp.net.sock.nio.common.envm.States;
import exp.libs.warp.net.sock.nio.common.envm.Times;
import exp.libs.warp.net.sock.nio.common.filterchain.impl.FilterChain;
import exp.libs.warp.net.sock.nio.common.interfaze.ISession;

/**
 * <pre>
 * NIOSocket客户端
 * </pre>	
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class NioSocketClient extends Thread {

	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(NioSocketClient.class);
	
	/** Socket重连间隔(ms) */
	private final static long RECONN_INTERVAL = 10000;
	
	/** Socket连续重连次数上限 */
	private final static int RECONN_LIMIT = 30;
	
	/** 事件选择器 */
	private Selector selector;

	/** Socket通讯通道 */
	private SocketChannel clientSocketChannel;

	/** 客户端会话对象 */
	private Session session;
	
	/** Socket配置 */
	private NioClientConfig sockConf = null;

	/**
	 * 构造函数
	 * @param sockConf 客户端名称
	 */
	public NioSocketClient(NioClientConfig sockConf) {
		this.sockConf = (sockConf == null ? NioClientConfig.DEFAULT : sockConf);
		this.setName(this.sockConf.getAlias());
	}

	/**
	 * 获取客户端配置
	 * @return 客户端配置
	 */
	public NioClientConfig getSockConf() {
		return sockConf;
	}
	
	/**
	 * <pre>
	 * 获取客户端会话。
	 * </pre>
	 * @return 若未连接到服务端则会返回null
	 */
	public ISession getSession() {
		return session;
	}
	
	/**
	 * 连接到服务端
	 * @return
	 */
	public boolean conn() {
		if(isClosed() == false) {
			return true;
		}
		
		boolean isOk = true;
		InetSocketAddress socket = new InetSocketAddress(
				sockConf.getIp(), sockConf.getPort());
		try {
			selector = Selector.open();
			clientSocketChannel = SocketChannel.open();
			clientSocketChannel.configureBlocking(true);	// 建立连接时要为阻塞模式
			clientSocketChannel.connect(socket);
			session = new Session(clientSocketChannel, sockConf);
			clientSocketChannel.configureBlocking(false);	// 建立连接后为非阻塞模式
			
			this.start();
			log.info("客户端 [{}] 连接到Socket服务 [{}] 成功", 
					getName(), sockConf.getSocket());
			
		} catch (IOException e) {
			isOk = false;
			log.error("客户端 [{}] 连接到Socket服务 [{}] 失败", 
					getName(), sockConf.getSocket(), e);
		}
		return isOk;
	}
	
	public boolean reconn() {
		int cnt = 0;
		do {
			if(conn() == true) {
				break;
				
			} else {
				_close();
				log.warn("客户端 [{}] {}ms后重连(已重试 {}/{} 次)", 
						getName(), RECONN_INTERVAL, cnt, RECONN_LIMIT);
			}
			
			cnt++;
			ThreadUtils.tSleep(RECONN_INTERVAL);
		} while(RECONN_LIMIT < 0 || cnt < RECONN_LIMIT);
		return !isClosed();
	}
	
	/**
	 * 检查socket连接是否已断开
	 * @return
	 */
	public boolean isClosed() {
		boolean isClosed = true;
		if(session != null) {
			isClosed = session.isClosed();
		}
		return isClosed;
	}
	
	public void close() {
		_close();	// 关闭会话
		sockConf.getFilterChain().clean();	// 清理过滤链
		
		//关闭事件选择器
		try {
			if (selector != null) {
				selector.close();
				selector = null;
			}
		} catch (Exception e) {
			log.error("客户端 [{}] 断开Socket连接异常", getName(), e);
		}
	}
	
	private boolean _close() {
		boolean isClose = true;
		if(session != null) {
			try {
				session.close();
				
			} catch (Exception e) {
				isClose = false;
				log.error("客户端 [{}] 断开Socket连接异常", getName(), e);
			}
		}
		return isClose;
	}
	
	public boolean write(Object msg) {
		boolean isOk = false;
		if(!isClosed()) {
			try {
				session.write(msg);
				isOk = true;
				
			} catch (Exception e) {}
		}
		return isOk;
	}
	
	@Override
	public void run() {
		log.debug(sockConf.toString());
		
		long lastHbTime = 0;
		long curTime = 0;
		do {
			curTime = System.currentTimeMillis();
			
			// 若该会话处于等待关闭状态，但超时仍未被远端机关闭，则本地主动关闭
			if (session.isWaitingToClose() && 
					curTime - session.getNotifyDisconTime() > sockConf.getOvertime()) {
				break;
			}
			
			// 打印本地心跳
            if(curTime - lastHbTime >= Times.HEART_BEAT) {
            	lastHbTime = curTime;
            	log.info("Socket客户端 [{}] 正在监听响应消息...", getName());
            }
			
            // 监听服务端返回消息
			if(listen() == false) {
				break;
			}
            
			ThreadUtils.tSleep(Times.SLEEP);
		} while(!session.isClosed());
		
		close();
		log.info("Socket客户端 [{}] 已停止", getName());
	}

	/**
	 * 监听服务端的返回消息（检查缓冲区）
	 * @return
	 */
	private boolean listen() {
		boolean isListn = true;
		try {
			if (States.SUCCESS.id == checkNewMsg()) {
				
				//提取远端机一次返回的所有消息，交付给handler处理（检查消息队列）
				while (session.getMsgQueue().hasNewMsg()) {
					String msg = session.getMsgQueue().getMsg();
					
					if(StrUtils.isEmpty(msg)) {
						continue;	// 丢弃空消息, 防止被攻击
						
					} else if((Protocol.CONN_LIMIT).equals(msg)) {
						log.warn("客户端 [{}] 被拒绝连接: 连接数受限", getName());
						isListn = false;
						break;
						
					} else if((Protocol.MSG_LIMIT).equals(msg)) {
						log.warn("客户端 [{}] 被丢弃消息: 消息积压(请控制请求频率)", getName());
						
					} else if((Protocol.HEARTBEAT).equals(msg)) {
						log.warn("客户端 [{}] 获得服务端心跳: Socket会话正常", getName());
					}
					
					FilterChain filterChain = sockConf.getFilterChain();
					filterChain.onMessageReceived(session, msg);
				}
			}
			
		} catch (ClosedSelectorException e) {
			// Undo 关闭事件选择器失败, 此为可忽略异常，不影响程序运行
        	
		} catch(ArrayIndexOutOfBoundsException e) {
			log.warn("客户端 [{}] 的本地缓冲区溢出, 上一条消息的数据可能已丢失或缺失.", getName(), e);
			
		} catch (SocketTimeoutException e) {
			log.error("客户端 [{}] 超时无动作, 断开连接.", getName(), e);
			isListn = false;
			
		} catch (Exception e) {
			log.error("客户端 [{}] 异常, 断开连接.", getName(), e);
			isListn = false;
		}
		return isListn;
	}
	
	/**
	 * 检查服务端是否有返回消息
	 * @return 只要返回的消息队列非空，且会话未关闭，则返回成功状态
	 * @throws Exception 异常
	 */
	private int checkNewMsg() throws Exception {
		States exState = States.SUCCESS;

		SocketChannel sc = session.getLayerSession();
		Selector selector = Selector.open();
		sc.configureBlocking(false);
		sc.register(selector, SelectionKey.OP_READ);
		
		int eventNum = selector.select(Times.BLOCK);
		if (eventNum > 0) {
			Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
			while (iterator.hasNext()) {
				SelectionKey sk = iterator.next();
				iterator.remove();
				int rtn = handleKey(sk);

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
			if (!session.getMsgQueue().hasNewMsg()) {
				exState = States.FAIL;
			}
		}
		return exState.id;
	}

	/**
	 * 从会话通道采集数据，返回-1表示通道已断开
	 * 
	 * @param sk 关注事件键
	 * @return -1表示会话已关闭，0则正常通讯
	 * @throws Exception 异常
	 */
	private int handleKey(SelectionKey sk) throws Exception {
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

				String[] readDelimiters = sockConf.getReadDelimiters();
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
					
					// 把原始消息添加到原始消息队列，剔除空消息，防止攻击
					String newMsg = socketBuffer.subString(iEnd).trim();
					if(StrUtils.isNotEmpty(newMsg)) {
						session.getMsgQueue().addNewMsg(newMsg);
					}
					socketBuffer.delete(iEnd);
				}
				channelBuffer.clear();
			}
			
			// Socket通道已断开(服务端主动关闭会话)
			if (count < 0) {
				rtn = -1;
			}
		}
		return rtn;
	}
	
	/**
	 * 重载toString，返回主线程名称
	 * @return 主线程名称
	 */
	@Override
	public String toString() {
		return sockConf.toString();
	}

}
