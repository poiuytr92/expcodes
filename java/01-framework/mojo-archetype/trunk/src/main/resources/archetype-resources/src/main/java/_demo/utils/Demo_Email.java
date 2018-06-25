#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}._demo.utils;

import exp.libs.envm.Charset;
import exp.libs.envm.SMTP;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.warp.net.mail.Email;

/**
 * <PRE>
 * Email 样例
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-08-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Demo_Email {

	/**
	 * Emal 样例
	 */
	public void demo() {
		Email mail = new Email(SMTP._126, "username@126.com", "password", 
				"123456789@qq.com", "desKey", Charset.UTF8);
		mail.send("无加密无抄送测�?", "测试内容A");
		ThreadUtils.tSleep(2000);
		
		mail.send("加密无抄送测�?", "测试内容B", true);
		ThreadUtils.tSleep(2000);
		
		mail.send("加密抄送测�?", "测试内容C", 
				new String[] { "123456789@qq.com" , "13912345678@139.com" }, true);
		ThreadUtils.tSleep(2000);
		
		mail.debug(true);
		mail.send("重置收件人测�?", "测试内容D", 
				new String[] { "13912345678@139.com" }, 
				new String[] { "123456789@qq.com" }, true);
	}
	
}
