package exp.libs.warp.net.socket.nio.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.pub.StrUtils;
import exp.libs.warp.net.socket.bean.SocketByteBuffer;
import exp.libs.warp.net.socket.nio.common.envm.Protocol;
import exp.libs.warp.net.socket.nio.common.envm.States;
import exp.libs.warp.net.socket.nio.common.filterchain.impl.FilterChain;

/**
 * <pre>
 * 会话管理器
 * 
 * 用于管理客户端的会话（主要用于会话消息接收）
 * </pre>	
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
final class SessionManager {

	/**
	 * 日志器
	 */
	private final static Logger log = LoggerFactory.getLogger(SessionManager.class);
	
	/**
	 * 会话
	 */
	private Session session;
	
	/**
	 * Socket客户端配置
	 */
	private NioClientConfig sockConf;
	
	/**
	 * 接收消息分隔符集
	 */
	private String[] recvDelimiters;

	/**
	 * 对应 recvDelimiters 中各个分隔符在消息缓冲区中的位置索引
	 */
	private int[] rdIdxs;
	
	/**
	 * 构造函数
	 * @param session 会话
	 * @param sockConf Socket配置
	 */
	public SessionManager(Session session, NioClientConfig sockConf) {
		this.session = session;
		this.sockConf = sockConf;
		
		this.recvDelimiters = StrUtils.split(sockConf.getRecvDelimiter(), "!#@{[", "]}@#!");
		this.rdIdxs = new int[recvDelimiters.length];
	}
	
	/**
	 * 会话管理器核心
	 * @param isStop 控制主线程是否停止标识
	 */
	public void core(boolean isStop) {
		if(isStop == true) {
			log.info("客户端初始化会话失败.服务端超时未响应连接请求");
			return;
		}
		
		log.info("客户端会话管理器启动.");
		long lastLocalHbTime = 0;
		long lastRemoteHbTime = System.currentTimeMillis();
		long currentTime = -1;
		
		while (isStop == false) {
			try {
				
				// 若该会话处于等待关闭状态，但超时仍未被远端机关闭，则本地主动关闭
				currentTime = System.currentTimeMillis();
				if (session.isWaitingToClose() == true) {
					currentTime = System.currentTimeMillis();
					if(currentTime - session.getNotifyDisconTime() > 
							sockConf.getWaitDisconTime()) {
						session.close();
					}
				}
				
				//若会话已关闭，则退出
				if(session.isClosed() == true) {
					break;
				}
				
				//检查远端机是否有返回消息（检查缓冲区）
				if (States.SUCCESS.id == checkRtnMsg()) {
					
					//提取远端机一次返回的所有消息，交付给handler处理（检查消息队列）
					while (session.getMsgQueue() != null && 
							session.getMsgQueue().hasNewMsg()) {
						String msg = session.getMsgQueue().getMsg();
						
						if((Protocol.CONN_LIMIT).equals(msg.trim())) {
							log.warn("客户端会话 [" + session + 
									"] 连接服务端失败:连接数受限.");
							
						} else if((Protocol.HEARTBEAT).equals(msg.trim())) {
							lastRemoteHbTime = System.currentTimeMillis();
						}
						
						FilterChain filterChain = sockConf.getFilterChain();
						filterChain.onMessageReceived(session, msg);
					}
				}
				
				//打印本地心跳
				currentTime = System.currentTimeMillis();
	            if(currentTime - lastLocalHbTime >= sockConf.getHbTime()) {
	            	log.debug("[HeartBeat] Socket客户端 [" + session + "] 活动中...");
	            	lastLocalHbTime = currentTime;
	            }
	            
	            //超过半小时没收到远端心跳,要求同是NioSocket框架才有效,所以只打印debug级别
	            if(currentTime - lastRemoteHbTime >= 1800000L) {
	            	lastRemoteHbTime = System.currentTimeMillis();
	            	
	            	log.debug("客户端会话 [" + session + 
							"] 超过半小时没有收到服务端心跳.");
	            }
	            
	            // 主线程休眠
				Thread.sleep(sockConf.getSleepTime());
				
			} catch (InterruptedException e) {
	        	log.error("客户端会话 [" + session + "] 线程休眠异常.不影响程序运行.", e);
	        	
			} catch (ClosedSelectorException e) {
	        	log.warn("客户端会话 [" + session + "] 关闭事件选择器失败." +
	        			"此为可忽略异常，不影响程序运行.");
				
			} catch (ArrayIndexOutOfBoundsException e) {
				log.error("客户端会话 [" + session + "] 的本地缓冲区溢出." +
	        			"此为异常不影响程序运行.缓冲区被清空.上一条消息的数据可能已丢失或出现缺失.", e);
	        	
			} catch (UnsupportedEncodingException e) {
				log.error("客户端会话 [" + session + "] 设置的字符集非法," +
	        			"不支持此字符集编码.关闭客户端.", e);
	        	break;
	        	
			} catch (CancelledKeyException e) {
				log.error("客户端会话 [" + session + "]  取消事件键时发生异常." +
						"关闭客户端.", e);
				break;
				
			} catch (ClosedChannelException e) {
				log.error("客户端会话 [" + session + "] 关闭通道时异常.关闭客户端.", e);
				break;
				
			} catch (IOException e) {
				log.error("关闭客户端会话 [" + session + "] 读写管道数据异常," +
						"可能服务端已单方面关闭会话.", e);
				break;
				
			} catch (Exception e) {
				log.error("客户端会话 [" + session + "] 抛出未知异常.关闭客户端.", e);
				break;
			}
		}
		log.info("客户端会话管理器停止.");
	}

	/**
	 * 检查服务端是否有返回消息
	 * @return 只要返回的消息队列非空，且会话未关闭，则返回成功状态
	 * @throws Exception 异常
	 */
	private int checkRtnMsg() throws Exception {
		States exState = States.SUCCESS;

		SocketChannel sc = (SocketChannel) session.getLayerSession();
		Selector selector = Selector.open();
		sc.configureBlocking(false);
		sc.register(selector, SelectionKey.OP_READ);
		
		int eventNum = selector.select(sockConf.getBlockTime());
		if (eventNum > 0) {
			Iterator<SelectionKey> iterator = selector.selectedKeys()
					.iterator();

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
			if (false == session.getMsgQueue().hasNewMsg()) {
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
		SocketChannel sc = (SocketChannel) session.getLayerSession();
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

				// 可能一次性收到多条消息，在缓冲区可读时需全部处理完，减少处理迟延
				while (true) {
					
					// 枚举所有分隔符，取索引值最小的分隔符位置（索引值>=0有效）
					int iEnd = -1;
					for(int i = 0; i < recvDelimiters.length; i++) {
						rdIdxs[i] = socketBuffer.indexOf(recvDelimiters[i]);
						
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

					// 把原始消息添加到原始消息队列，剔除空消息，防止攻击
					if (false == "".equals(newMsg)) {
						session.getMsgQueue().addNewMsg(newMsg);
					}
					socketBuffer.delete(iEnd);
				}
				channelBuffer.clear();
			}
			
			// Socket通道已断开
			if (count < 0) {
				log.info("服务端主动关闭会话.");
				rtn = -1;
			}
		}
		return rtn;
	}
	
}
