package exp.bilibili.plugin.core.front.login.win;

import exp.bilibili.protocol.cookie.HttpCookie;

public interface LoginCallback {

	public void afterLogin(HttpCookie cookie);
	
	public void afterLogout(HttpCookie cookie);
	
}
