package exp.libs.warp.net.socket.io.server;

import exp.libs.warp.net.socket.io.common.ISession;



class _DefaultHandler implements IHandler {

	@Override
	public void _handle(ISession session) {
		session.write("connect success");
	}

	@Override
	public IHandler _clone() {
		return new _DefaultHandler();
	}

}
