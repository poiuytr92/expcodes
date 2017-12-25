package exp.bilibli.plugin;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import exp.bilibli.plugin.cache.Browser;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.warp.net.http.HttpUtils;

public class Test {

	public static void main(String[] args) {
		Browser.init(false);
		Browser.open("http://h.bilibili.com/1446541#comment");
		Browser.backupCookies();
		
		
		WebElement ctrl = Browser.findElement(By.className("comment-ctnr"));
		if(ctrl != null) {
			WebElement textarea = ctrl.findElement(By.tagName("textarea"));
			WebElement btn = ctrl.findElement(By.tagName("button"));
			
			for(int i = 1; i <= 10; i++) {
				textarea.clear();
				textarea.sendKeys("小乔圣诞快乐 O(∩_∩)O 天天开心天天吃~ 我为小乔续 " + i + "秒");
				ThreadUtils.tSleep(1000);
				btn.click();
				
				System.out.println("第" + i + "次");
				
				ThreadUtils.tSleep(5000);
			}
		}
		System.out.println("finish");
		
		Browser.quit();
	}
	
	private static Map<String, String> toPostHeadParams(String cookies) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(HttpUtils.HEAD.KEY.ACCEPT, "application/json, text/javascript, */*; q=0.01");
		params.put(HttpUtils.HEAD.KEY.ACCEPT_ENCODING, "gzip, deflate");
		params.put(HttpUtils.HEAD.KEY.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8,en;q=0.6");
		params.put(HttpUtils.HEAD.KEY.CONNECTION, "keep-alive");
		params.put(HttpUtils.HEAD.KEY.CONTENT_TYPE, // POST的是表单
				HttpUtils.HEAD.VAL.POST_FORM.concat(Config.DEFAULT_CHARSET));
		params.put(HttpUtils.HEAD.KEY.COOKIE, cookies);
		params.put(HttpUtils.HEAD.KEY.HOST, "api.bilibili.com");
		params.put(HttpUtils.HEAD.KEY.ORIGIN, "http://h.bilibili.com");
		params.put(HttpUtils.HEAD.KEY.REFERER, "http://h.bilibili.com/1446541");	// 发送/接收消息的直播间地址
		params.put(HttpUtils.HEAD.KEY.USER_AGENT, Config.USER_AGENT);
		return params;
	}
	
}
