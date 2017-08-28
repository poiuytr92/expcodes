#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}._demo;

import exp.libs.utils.other.LogUtils;

/**
 * <PRE>
 * 程序入口
 * </PRE>
 * <B>项    目：</B> xxxxxxx
 * <B>技术支持：</B> xxxxxxx
 * @version   xxxxxxx
 * @author    xxxxxxx
 * @since     jdk版本：jdk1.6
 */
public class Main {
	
	public static void main(String[] args) {
		LogUtils.loadLogBackConfig();		// 加载日志配置
		Config.getInstn().reflash(60000);	// 初始化配置项(每隔60秒刷新一次)
		
		// TODO: 程序入口
	}
	
}
