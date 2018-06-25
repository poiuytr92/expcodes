package exp.libs.warp.net.sock.nio.common.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.warp.net.sock.nio.common.filterchain.INextFilter;
import exp.libs.warp.net.sock.nio.common.filterchain.impl.BaseFilter;
import exp.libs.warp.net.sock.nio.common.interfaze.ISession;

/**
 * <pre>
 * 敏感词过滤器
 * 
 * 当消息触犯敏感词规则时过滤。
 * 若达到触犯敏感词规则的上限，则断开会话。
 * 此过滤器暂时只能针对String类型的msg处理。
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class SensitiveFilter extends BaseFilter{

	/**
	 * 日志�?
	 */
	private final static Logger log = LoggerFactory.getLogger(SensitiveFilter.class);
	
	/**
	 * 敏感词表
	 */
	private List<String> sensitiveTable;
	
	/**
	 * 敏感词计数器属性的 键�?
	 */
	private static final String SENSITIVE_KEY = "sensitiveKey";
	
	/**
	 * 每个会话最多允许触犯敏感词的次数，-1表示不限制触犯次�?
	 */
	private int maxSensitiveNum;
	
	/**
	 * 构造函�?
	 */
	public SensitiveFilter() {
		maxSensitiveNum = 3;
		this.sensitiveTable = new ArrayList<String>();
	}
	
	/**
	 * 构造函�?
	 * @param sensitiveRules 过滤规则
	 * @param maxSensitiveNum 允许触犯敏感规则的次数，超过则强制关闭会话，-1表示不限制触犯次�?
	 */
	public SensitiveFilter(List<String> sensitiveRules, int maxSensitiveNum) {
		this.sensitiveTable = new ArrayList<String>();
		this.addSensitiveRules(sensitiveRules);
		this.maxSensitiveNum = maxSensitiveNum;
	}
	
	/**
	 * 构造函�?
	 * @param sensitiveRules 过滤规则
	 * @param maxSensitiveNum 允许触犯敏感规则的次数，超过则强制关闭会�?
	 */
	public SensitiveFilter(String[] sensitiveRules, int maxSensitiveNum) {
		this.sensitiveTable = new ArrayList<String>();
		this.addSensitiveRules(sensitiveRules);
		this.maxSensitiveNum = maxSensitiveNum;
	}

	@Override
	public void onSessionCreated(INextFilter nextFilter, ISession session)
			throws Exception {
				
		// 添加session的属性键�?
		session.getProperties().put(SENSITIVE_KEY, new SensitiveCounter());
				
		nextFilter.onSessionCreated(session);
	}
	
	@Override
	public void onMessageReceived(INextFilter nextFilter, ISession session,
			Object msg) throws Exception {

		boolean isFilter = false;
		String strMsg = msg.toString();
				
		//当消息中包含敏感词时，断开过滤链，不处理该消息
		for(String regRule : sensitiveTable) {
			if(true == strMsg.matches(regRule)) {
				isFilter = true;
				break;
			}
		}
		
		if(isFilter == true) {
			log.info("会话 [" + session + "] 的消�? [" + strMsg + 
					"] 因触犯敏感词规则被过�?.");
			
			//这里不打印消息，是为了避免客户端和服务端都使用了相同的敏感词过滤，而产生无限循�?
			session.write("因触犯敏感词规则,有消息被过滤.若超�? [" + 
					maxSensitiveNum + "] 次则断开连接.");
			
			Map<String, Object> property = session.getProperties();
			SensitiveCounter senCnt = (SensitiveCounter) property.get(SENSITIVE_KEY);
			senCnt.up();
			
			if(maxSensitiveNum > 0 && 
					senCnt.getCnt() >= maxSensitiveNum) {
				
				log.info("会话 [" + session + "] 因触犯敏感词规则达到上限.会话关闭.");
				session.closeNotify();
			}
		}
		else {
			nextFilter.onMessageReceived(session, msg);
		}
	}

	/**
	 * 添加多个敏感词规�?
	 * @param newSensitiveRules 敏感词规�?
	 */
	public void addSensitiveRules(List<String> newSensitiveRules) {
		this.sensitiveTable.addAll(newSensitiveRules);
	}

	/**
	 * 添加多个敏感词规�?
	 * @param newSensitiveRules 敏感词规�?
	 */
	public void addSensitiveRules(String[] newSensitiveRules) {
		for(String rule : newSensitiveRules) {
			this.addSensitiveRule(rule);
		}
	}
	
	/**
	 * 添加一个敏感词规则
	 * @param newSensitiveRule 敏感词规�?
	 */
	public void addSensitiveRule(String newSensitiveRule) {
		this.sensitiveTable.add(newSensitiveRule);
	}

	/**
	 * 设置触犯敏感词规则的上限，超过上限作出则断开会话�?-1表示不限制触犯次�?
	 * @param maxSensitiveNum 触犯次数
	 */
	public void setMaxSensitiveNum(int maxSensitiveNum) {
		this.maxSensitiveNum = maxSensitiveNum;
	}


	/**
	 * 敏感词计数器
	 * @author 廖权�?
	 */
	private static class SensitiveCounter {

		/**
		 * 违反敏感词规则次�?
		 */
		private int cnt = 0;

		/**
		 * 更新违反次数
		 */
		public synchronized void up() {
			this.cnt += 1;
		}

		/**
		 * 返回违反次数
		 * @return 违反次数
		 */
		public int getCnt() {
			return this.cnt;
		}
	}
}

