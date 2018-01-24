package exp.bilibili.plugin.tmp;

import java.util.LinkedList;
import java.util.List;

public class LoginMgr {

	/** 主号cookie */
	public final static String COOKIE_MAIN = "cookie-main.dat";
	
	/** 马甲号cookie(用于扫描) */
	public final static String COOKIE_VEST = "cookie-vest.dat";
	
	/** 小号cookie */
	public final static String COOKIE_MINI = "cookie-mini-k.dat";
	
	/** 主号cookie */
	private HttpCookies mainCookies;
	
	/** 马甲号cookie */
	private HttpCookies vestCookies;
	
	/** 小号cookie集 */
	private List<HttpCookies> miniCookies;
	
	private static volatile LoginMgr instance;
	
	private LoginMgr() {
		this.mainCookies = HttpCookies.NULL;
		this.vestCookies = HttpCookies.NULL;
		this.miniCookies = new LinkedList<HttpCookies>();
	}
	
	public static LoginMgr getInstn() {
		if(instance == null) {
			synchronized (LoginMgr.class) {
				if(instance == null) {
					instance = new LoginMgr();
				}
			}
		}
		return instance;
	}
	
	public void loginMainUser(boolean byQR) {
		if(byQR == true) {
			_loginMainUserByQR();
		} else {
			_loginMainUserByVC();
		}
	}
	
	private void _loginMainUserByQR() {
		
	}
	
	private void _loginMainUserByVC() {
		
	}
	
	public void setMainUser(HttpCookies mainCookies) {
		
	}

	public void setVestUser(HttpCookies vestCookies) {
		
	}
	
	public void addMiniUser(HttpCookies miniCookies) {
		
	}
	
}
