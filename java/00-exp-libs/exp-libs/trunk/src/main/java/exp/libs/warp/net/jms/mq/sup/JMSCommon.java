package exp.libs.warp.net.jms.mq.sup;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JMS操作基类 提供对ActiveMQ的基类操作。
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2016-02-14
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class JMSCommon {
	
	private final static Logger LOG = LoggerFactory.getLogger(JMSCommon.class);

	/**
	 * 连接字符�?
	 */
	private String sURI;

	/**
	 * 上下文工�?
	 */
	private String contextFactory;

	/**
	 * 连接工厂
	 */
	private String connectionFactory;

	/**
	 * 主题
	 */
	private String sThemeName;

	/**
	 * 回复主题
	 */
	private String sRThemeName;

	/**
	 * 是否采用事务，true为是
	 */
	private boolean isTransaction = false;

	/**
	 * 连接管理�?
	 */
	private static Map<String, Connection> connectionManager = new HashMap<String, Connection>();
	
	
	/**
	 * 消息生产�?
	 */
	private MessageProducer produce;

	/**
	 * 会话
	 */
	private Session session;

	/**
	 * 用户�?
	 */
	private String sUserName;

	/**
	 * 密码
	 */
	private String sPassWord;

	/**
	 * 消息存活时间，单位毫�?
	 */
	private long iTimeToLive;

	/**
	 * 优先�?
	 */
	private int iPriority;

	/**
	 * 客户端ID
	 */
	private String sClientID;

	/**
	 * 消息消费�?
	 */
	private MessageConsumer consumer;

	/**
	 * 订阅者名�?
	 */
	private String sConsumerName;

	/**
	 * 过滤语句
	 */
	private String sSelectors;

	/**
	 * 消息ID
	 */
	private String jmsMessageID;

	/**
	 * 消息发送模�?
	 */
	private int iDeliveryMode = DeliveryMode.PERSISTENT;

	/**
	 * 消息确认机制
	 */
	private int iAcknowledgementMode = Session.AUTO_ACKNOWLEDGE;

	/**
	 * 相关ID
	 */
	private String jmsCorrelationID;

	/**
	 * 回话关闭异常
	 */
	protected static final String STR_SESSION_CLOSE_EXP = "The Session is closed";

	/**
	 * 生产者关闭异�?
	 */
	protected static final String STR_PROD_CLOSE_EXP = "The producer is closed";

	/**
	 * 消息生产�?(回复消息使用)
	 */
	protected MessageProducer replyProducer;

	/**
	 * 队列
	 */
	protected Queue queue = null;

	/**
	 * 主题
	 */
	protected Topic topic = null;

	/**
	 * 消息
	 */
	protected Message messages = null;

	/**
	 * 改造方�?
	 * 
	 * @param sURI
	 *            连接字符�?
	 */
	public JMSCommon(String sURI) {
		this.sURI = sURI;
	}

	
	/**
	 * 建立连接
	 * 
	 * @param isCreate
	 *            true为创�?
	 * @throws Exception
	 */
	protected void createConnection(String clinetId) throws Exception {
		try {
			Connection conn = connectionManager.get(clinetId);
			if(conn == null || isConnectionClosed(conn)){
				JMSFactory factory = new JMSFactory(sURI, contextFactory,
						connectionFactory);
				if (sUserName != null && !sUserName.equals("") && sPassWord != null
						&& !sPassWord.equals("")) {
					conn = factory.createConnection(sUserName, sPassWord);
				} else {
					conn = factory.createConnection();
				}
				if(conn != null){
					if (sClientID != null && !sClientID.equals("")) {
						conn.setClientID(sClientID);
					}
					
					conn.start();
					connectionManager.put(clinetId, conn);
					
					LOG.info("{}创建连接成功",sClientID);
					
				} else {
					LOG.error("{}创建连接失败!连接为空",sClientID);
					
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("{}创建连接失败",sClientID);
			throw e;
		} 
		
	}

	/**
	 * 检验连接是否正常，如连接关闭或断开，则重新连接
	 * 
	 * @throws Exception
	 */
	protected void checkConnection() throws Exception {
		if (isConnectionClosed()) {
			createConnection(sClientID);
		}
	}


	/**
	 * 连接是否关闭
	 * 
	 * @return 是否关闭，true为是
	 */
	protected boolean isConnectionClosed() {

		if (connectionManager.get(sClientID) != null) {

			Session sessionTest = null;
			try {
				sessionTest = connectionManager.get(sClientID).createSession(isTransaction,
						iAcknowledgementMode);
				return false;
			} catch (JMSException e) {
			} finally {
				if (sessionTest != null) {
					try {
						sessionTest.close();
					} catch (JMSException e) {
					}
				}
			}
		}

		return true;
	}
	
	/**
	 * 连接是否关闭
	 * 
	 * @return 是否关闭，true为是
	 */
	protected boolean isConnectionClosed(Connection conn) {

		if (conn != null) {

			Session sessionTest = null;
			try {
				sessionTest = conn.createSession(isTransaction,
						iAcknowledgementMode);
				return false;
			} catch (JMSException e) {
			} finally {
				if (sessionTest != null) {
					try {
						sessionTest.close();
					} catch (JMSException e) {
					}
				}
			}
		}

		return true;
	}

	/**
	 * 会话是否关闭
	 * 
	 * @return 是否关闭，true为是
	 */
	protected boolean isSessionClosed() {
		if (session == null) {
			return true;
		}
		try {
			session.createMessage();
		} catch (JMSException e) {
			if (e.getMessage().indexOf(STR_SESSION_CLOSE_EXP) != -1) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 消息生产者是否关�?
	 * 
	 * @return 是否关闭，true为是
	 */
	protected boolean isProducerClosed() {
		if (produce == null) {
			return true;
		}
		try {
			produce.getDestination();
		} catch (JMSException e) {
			// if (e.getMessage().indexOf(STR_PRODUCER_CLOSE_EXCEPTION) != -1) {
			return true;
			// }
		}
		return false;
	}

	/**
	 * 回复消息的消息生产者是否关�?
	 * 
	 * @return 是否关闭，true为是
	 */
	protected boolean isReplyProducerClosed() {
		if (replyProducer == null) {
			return true;
		}
		try {
			replyProducer.getDestination();
		} catch (JMSException e) {
			if (e.getMessage().indexOf(STR_PROD_CLOSE_EXP) != -1) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 创建会话
	 * 
	 * @param isCreate
	 *            true为创�?
	 * @throws Exception
	 */
	protected void createSession() throws Exception {
		try {
			if(session == null || isSessionClosed()){
				//确保已经创建连接
				createConnection(sClientID);
				Connection conn = connectionManager.get(sClientID);
				if(conn == null ){
					LOG.info("{}连接不存�?", sClientID);
					LOG.info("{}创建会话失败",sClientID);
					return ;
				}
				session = conn.createSession(isTransaction, iAcknowledgementMode);
				LOG.info("{}创建会话成功", sClientID);
				
			}
			
		} catch (Exception e) {
			LOG.info("{}创建会话失败",sClientID);
			throw e;
		}
	}

	/**
	 * 创建生产�?(Topic 方式)
	 * 
	 * @throws Exception
	 *             创建生产者异�?
	 */
	protected void createProducerByTopic() throws Exception {
		if (isProducerClosed()) {
			topic = session.createTopic(sThemeName);
			produce = session.createProducer(topic);
			produce.setTimeToLive(iTimeToLive);
		}
	}

	/**
	 * 创建生产�?(Queue方式)
	 * 
	 * @throws Exception
	 *             创建生产者异�?
	 */
	protected void createProducerByQueue() throws Exception {
		if (isProducerClosed()) {
			queue = session.createQueue(sThemeName);
			produce = session.createProducer(queue);
			produce.setTimeToLive(iTimeToLive);
			produce.setDeliveryMode(iDeliveryMode);
		}
	}

	/**
	 * 回复消息到制定的队列当中
	 * 
	 * @param destination
	 *            目的�?
	 * @param message
	 *            字符串消�?
	 * @throws Exception
	 */
	protected void createProducerReplyto(Destination destination, String message)
			throws JMSException {
		if (isReplyProducerClosed()) {
			replyProducer = session.createProducer(destination);
		}
		messages = session.createTextMessage(message);
		sendReplyMessage(destination, messages);
	}

	/**
	 * 回复消息到制定的队列当中
	 * 
	 * @param destination
	 *            目的�?
	 * @param messages
	 *            Message消息
	 * @throws JMSException
	 */
	protected void createProducerReplyto(Destination destination,
			Message messages) throws JMSException {
		if (isReplyProducerClosed()) {
			replyProducer = session.createProducer(destination);
		}
		sendReplyMessage(destination, messages);
	}

	/**
	 * 发送回复消�?
	 * 
	 * @param destination
	 *            目的�?
	 * @param messages
	 *            Message消息
	 * @throws JMSException
	 */
	private void sendReplyMessage(Destination destination, Message messages)
			throws JMSException {
		setSystemProperty(messages);
		replyProducer.setPriority(iPriority);
		replyProducer.send(messages);
	}

	/**
	 * 创建持久消费�? Topic方式
	 * 
	 * @throws Exception
	 */
	protected void createDurableSubscriber() throws Exception {
		if (consumer == null) {
			try {
				createSession();
				topic = session.createTopic(sThemeName);
				consumer = session.createDurableSubscriber(topic,
						sConsumerName, sSelectors, false);
				
				LOG.info("{}订阅{}主题成功", sClientID, sThemeName);
			} catch (Exception e) {
				LOG.info("{}订阅{}主题失败", sClientID, sThemeName);
				throw e;
			}
			
		}
	}
	
	/**
	 * 创建非持久消费�? Topic方式
	 * 
	 * @throws Exception
	 *             备注：当消息需要手工确认时，请调用Message.acknowledge();方法
	 */
	protected void createConsumerByTopic() throws Exception {
		topic = session.createTopic(sThemeName);
		consumer = session.createConsumer(topic, sSelectors);
	}

	/**
	 * 创建消费�? Queue方式
	 * 
	 * @throws Exception
	 */
	protected void createConsumerByQueue() throws Exception {
		queue = session.createQueue(sThemeName);
		consumer = session.createConsumer(queue, sSelectors);
	}

	/**
	 * 设置监听�?
	 * 
	 * @param iListener
	 *            监听实现�?
	 * @throws Exception
	 */
	protected void setMessageListener(IListener iListener) throws Exception {
		consumer.setMessageListener(iListener);
	}

	/**
	 * 设置连接监听
	 * 
	 * @param iListener
	 */
	// protected void setTransportListener(ITransportListener iListener) {
	// connection.addTransportListener(iListener);
	// }

	/**
	 * 取消持久订阅 备注：当消息需要手工确认时，请调用Message.acknowledge();方法
	 */
	protected void unsubscribe() throws Exception {
		try {
			if (session != null) {
				session.unsubscribe(sThemeName);
			}
			LOG.info("{}取消订阅{}主题成功",sClientID , sThemeName);
		} catch (Exception e) {
			LOG.info("{}取消订阅{}主题失败",sClientID, sThemeName);
			LOG.error("",e);
		}
	}
	
	
	public static String getSessionKey(String clientId, boolean isTransaction, int iAcknowledgementMode){
		return clientId + "_" + isTransaction +  iAcknowledgementMode;
	}
	
	/**
	 * 发送消�?
	 * 
	 * @param message
	 *            消息对象
	 * @throws Exception
	 *             发送异�?
	 */
	protected void send(Message message) throws Exception {
		setSystemProperty(message);
		produce.setPriority(iPriority);
		produce.send(message);
	}

	/**
	 * 设置系统变量，便于消费者获取相关信息�?
	 * 
	 * @param message
	 *            Message消息
	 * @throws JMSException
	 */
	private void setSystemProperty(Message message) {
		try {
			/**
			 * 设置关联ID
			 */
			if (jmsCorrelationID != null && !jmsCorrelationID.equals("")) {
				message.setJMSCorrelationID(jmsCorrelationID);
			}
			/**
			 * 设置回复消息
			 */
			if (sRThemeName != null && !sRThemeName.equals("")) {
				message.setJMSReplyTo(session.createQueue(sRThemeName));
			}
			// message.setLongProperty("Sys@TimeToLive", iTimeToLive);
		} catch (JMSException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 提交事务（采用事务时，必须提交方可发送）
	 * 
	 * @throws Exception
	 *             提交异常
	 */
	protected void commit() throws Exception {
		session.commit();
	}

	/**
	 * 回滚事务（采用事务时，必须提交方可发送）
	 * 
	 * @throws Exception
	 *             回滚异常
	 */
	protected void rollbace() throws Exception {
		session.rollback();
	}

	/**
	 * 关闭消费�?
	 * 
	 * @throws Exception
	 */
	protected void closeConsumer() throws Exception {
		if (consumer != null) {
			consumer.close();
		}
	}
	
	/**
	 * 关闭会话
	 * 
	 * @throws Exception
	 *             关闭会话异常
	 */
	protected void closeSession() throws Exception {
		if (session != null) {
			session.close();
		}
	}

	/**
	 * 关闭连接
	 * 
	 * @throws Exception
	 *             关闭连接异常
	 */
	protected void closeConnection() throws Exception {
		releaseConnection(sClientID);
	}
	
	public static void releaseConnection(String clientId){
		Connection connection = connectionManager.get(clientId);
		if(connection != null){
			try {
				connection.close();
				connectionManager.remove(clientId);
				LOG.info("{}释放连接成功�?" , clientId);
			} catch (JMSException e) {
				LOG.info("{}释放连接失败�?" , clientId);
				LOG.error("", e);
			}
			
		} else {
			LOG.info("{}连接不存在！" , clientId);
			
		}
	}
	
	public static void releaseAllConnection(){
		Set<String> keySet = connectionManager.keySet();
		for (String clientId : keySet) {
			releaseConnection(clientId);
		}
	}

	/**
	 * 获取连接
	 * 
	 * @return Connection
	 */
	protected Connection getconnection() {
		return connectionManager.get(sClientID);
	}

	/**
	 * 获取连接
	 * 
	 * @return ActiveMQConnection连接
	 */
	public Connection getConnection() {
		return connectionManager.get(sClientID);
	}

	/**
	 * 获取生产者对�?
	 * 
	 * @return MessageProducer
	 */
	protected MessageProducer getproduce() {
		return produce;
	}

	/**
	 * 获取会话
	 * 
	 * @return Session
	 */
	protected Session getsession() {
		if(session == null){
			try {
				createSession();
				LOG.info("{}创建会话成功", sClientID);
			} catch (Exception e) {
				LOG.info("{}创建会话失败",sClientID);
				LOG.error("", e);
			}
		}
		return session;
	}
	
	/**
	 * 获取会话
	 * 
	 * @return Session
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * 获取主题
	 * 
	 * @return 主题(String)
	 */
	protected String getsThemeName() {
		return sThemeName;
	}

	/**
	 * 设置主题
	 * 
	 * @param themeName
	 *            主题�?
	 */
	protected void setsThemeName(String themeName) {
		sThemeName = themeName;
	}

	/**
	 * 获取是否采用事务
	 * 
	 * @return �?/true;�?/false
	 */
	protected boolean isisTransaction() {
		return isTransaction;
	}

	/**
	 * 设置是否采用事务，缺省为�?
	 * 
	 * @param transaction
	 *            是否采用事务
	 */
	protected void setisTransaction(boolean transaction) {
		isTransaction = transaction;
	}

	/**
	 * 获取消息确认机制
	 * 
	 * @return 消息确认机制(int) AUTO_ACKNOWLEDGE = 1; CLIENT_ACKNOWLEDGE = 2;
	 *         DUPS_OK_ACKNOWLEDGE = 3; SESSION_TRANSACTED = 0;
	 */
	protected int getiAcknowledgementMode() {
		return iAcknowledgementMode;
	}

	/**
	 * 设置消息确认机制,缺省为自动确�?
	 * 
	 * @param acknowledgementMode
	 *            AUTO_ACKNOWLEDGE = 1; CLIENT_ACKNOWLEDGE = 2;
	 *            DUPS_OK_ACKNOWLEDGE = 3; SESSION_TRANSACTED = 0;
	 */
	protected void setiAcknowledgementMode(int acknowledgementMode) {
		iAcknowledgementMode = acknowledgementMode;
	}

	/**
	 * 获取用户�?
	 * 
	 * @return 用户�?
	 */
	protected String getsUserName() {
		return sUserName;
	}

	/**
	 * 设置用户�?
	 * 
	 * @param userName
	 *            用户�?
	 */
	protected void setsUserName(String userName) {
		sUserName = userName;
	}

	/**
	 * 获取密码
	 * 
	 * @return 密码
	 */
	protected String getsPassWord() {
		return sPassWord;
	}

	/**
	 * 获取密码
	 * 
	 * @param passWord
	 *            密码
	 */
	protected void setsPassWord(String passWord) {
		sPassWord = passWord;
	}

	/**
	 * 获取过期时间
	 * 
	 * @return 过期时间 单位毫秒
	 */
	protected long getiTimeToLive() {
		return iTimeToLive;
	}

	/**
	 * 设置过期时间
	 * 
	 * @param timeToLive
	 *            过期时间，单位毫�?
	 */
	protected void setiTimeToLive(long timeToLive) {
		iTimeToLive = timeToLive;
	}

	/**
	 * 获取客户端ID
	 * 
	 * @return 客户端ID
	 */
	protected String getsClientID() {
		return sClientID;
	}

	/**
	 * 设置客户端ID
	 * 
	 * @param clientID
	 *            客户端ID
	 */
	protected void setsClientID(String clientID) {
		sClientID = clientID;
	}

	/**
	 * 返回消息存储模式
	 * 
	 * @return 1: NON_PERSISTENT ; 2:PERSISTENT
	 */
	protected int getiDeliveryMode() {
		return iDeliveryMode;
	}

	/**
	 * 设置消息存储模式 缺省为持久化模式
	 * 
	 * @param deliveryMode
	 *            1: NON_PERSISTENT ; 2:PERSISTENT
	 */
	protected void setiDeliveryMode(int deliveryMode) {
		iDeliveryMode = deliveryMode;
	}

	/**
	 * 获取过滤条件
	 * 
	 * @return 过滤语句
	 */
	protected String getsSelectors() {
		return sSelectors;
	}

	/**
	 * 设置过滤条件
	 * 
	 * @param selectors
	 *            过滤条件
	 */
	protected void setsSelectors(String selectors) {
		sSelectors = selectors;
	}

	/**
	 * 获取消费者名�?
	 * 
	 * @return 消费者名�?
	 */
	protected String getsConsumerName() {
		return sConsumerName;
	}

	/**
	 * 设置消费者名�?
	 * 
	 * @param name
	 *            消费者名�?
	 */
	protected void setsConsumerName(String name) {
		sConsumerName = name;
	}

	/**
	 * 获取消费者对�?
	 * 
	 * @return 消费者对�?
	 */
	public MessageConsumer getConsumer() {
		return consumer;
	}

	/**
	 * 获取消息ID
	 * 
	 * @return 消息ID
	 */
	protected String getJMSMessageID() {
		return jmsMessageID;
	}

	/**
	 * 设置消息ID
	 * 
	 * @param messageID
	 *            消息ID
	 */
	protected void setJMSMessageID(String messageID) {
		jmsMessageID = messageID;
	}

	/**
	 * 获取相关ID
	 * 
	 * @return 相关ID
	 */
	protected String getJMSCorrelationID() {
		return jmsCorrelationID;
	}

	/**
	 * 设置相关ID
	 * 
	 * @param correlationID
	 *            相关ID
	 */
	protected void setJMSCorrelationID(String correlationID) {
		jmsCorrelationID = correlationID;
	}

	/**
	 * 获取回复主题
	 * 
	 * @return 回复主题
	 */
	protected String getSRThemeName() {
		return sRThemeName;
	}

	protected void setSRThemeName(String themeName) {
		sRThemeName = themeName;
	}

	/**
	 * 获取消息优先�?
	 * 
	 * @return 消息优先�?
	 */
	protected int getiPriority() {
		return iPriority;
	}

	/**
	 * 设置消息优先�?
	 * 
	 * @param priority
	 *            消息优先�?
	 */
	protected void setiPriority(int priority) {
		iPriority = priority;
	}


	/**
	 * 获取上下文工�?
	 * 
	 * @return 上下文工�?
	 */
	public String getContextFactory() {
		return contextFactory;
	}

	/**
	 * 设置上下文工�?
	 * 
	 * @param contextFactory
	 *            上下文工�?
	 */
	public void setContextFactory(String contextFactory) {
		this.contextFactory = contextFactory;
	}

	/**
	 * 获取连接工厂
	 * 
	 * @return 连接工厂
	 */
	public String getConnectionFactory() {
		return connectionFactory;
	}

	/**
	 * 设置连接工厂
	 * 
	 * @param connectionFactory
	 *            连接工厂
	 */
	public void setConnectionFactory(String connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

}
