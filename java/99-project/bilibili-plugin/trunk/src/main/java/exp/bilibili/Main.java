package exp.bilibili;

import exp.bilibili.plugin.cache.CookiesMgr;
import exp.bilibili.plugin.envm.CookieType;

public class Main {

	public static void main(String[] args) {
		CookiesMgr.getInstn().load(CookieType.MAIN);
		System.out.println(CookiesMgr.MAIN().toHeaderCookie());
		
	}
	
}
