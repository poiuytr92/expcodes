package exp.bilibili.plugin.ui;

import exp.bilibili.plugin.bean.ldm.HttpCookie;

public interface __LoginCallback {

	public void afterLogin(HttpCookie cookie);
	
	public void afterLogout(HttpCookie cookie);
	
}
