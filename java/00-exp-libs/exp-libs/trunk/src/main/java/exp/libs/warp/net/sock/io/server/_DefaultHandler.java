package exp.libs.warp.net.sock.io.server;

import exp.libs.warp.net.sock.io.common.IHandler;
import exp.libs.warp.net.sock.io.common.ISession;


/**
 * <pre>
 * 默认的Socket业务逻辑处理器(IO-阻塞模式)
 * </pre>	
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: <a href="http://www.exp-blog.com">www.exp-blog.com</a>
 * @since     jdk版本：jdk1.6
 */
class _DefaultHandler implements IHandler {

	@Override
	public boolean _login(ISession session) {
		session.write("login success");
		return true;
	}
	
	@Override
	public void _handle(ISession session) {
		session.write("hello, client:".concat(session.ID()));
	}

	@Override
	public IHandler _clone() {
		return new _DefaultHandler();
	}

}
