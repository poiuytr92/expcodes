package exp.fpf.cache;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import exp.libs.warp.net.sock.io.common.ISession;

/**
 * <pre>
 * 代理会话管理器(用于代理服务侧)
 * </pre>	
 * <br/><B>PROJECT : </B> file-port-forwarding
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2017-07-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class SessionMgr {

	/** 当前持有的代理会话队列 */
	private List<ISession> sessions;
	
	private static volatile SessionMgr instance;
	
	private SessionMgr() {
		this.sessions = new LinkedList<ISession>();
	}
	
	public static SessionMgr getInstn() {
		if(instance == null) {
			synchronized (SessionMgr.class) {
				if(instance == null) {
					instance = new SessionMgr();
				}
			}
		}
		return instance;
	}
	
	public void add(ISession session) {
		if(session != null && !session.isClosed()) {
			synchronized (sessions) {
				sessions.add(session);
			}
		}
	}
	
	/**
	 * 清理无效会话
	 */
	public void delInvaildSession() {
		synchronized (sessions) {
			Iterator<ISession> sessionIts = sessions.iterator();
			while(sessionIts.hasNext()) {
				ISession session = sessionIts.next();
				if(session == null) {
					sessionIts.remove();
					
				} else if(session.isClosed()) {
					sessionIts.remove();
				}
			}
		}
	}
	
	/**
	 * 关闭并清除所有会话
	 */
	public void clear() {
		synchronized (sessions) {
			Iterator<ISession> sessionIts = sessions.iterator();
			while(sessionIts.hasNext()) {
				ISession session = sessionIts.next();
				if(session != null) {
					session.close();
				}
			}
			sessions.clear();
		}
	}
	
}
