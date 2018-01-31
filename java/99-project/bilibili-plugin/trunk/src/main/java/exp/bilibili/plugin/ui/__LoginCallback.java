package exp.bilibili.plugin.ui;

import exp.bilibili.plugin.bean.ldm.HttpCookie;

public interface __LoginCallback {

	public void afterLogin(final HttpCookie cookie);
	
	public void afterLogout(final HttpCookie cookie);
	
}
