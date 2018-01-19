#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

/**
 * <PRE>
 * 配置加载器
 * </PRE>
 * <B>项    目：</B> xxxxxxx
 * <B>技术支持：</B> xxxxxxx
 * @version   xxxxxxx
 * @author    xxxxxxx
 * @since     jdk版本：jdk1.6
 */
public class Config {
	
	private static volatile Config instance;
	
	private Config() {}
	
	public static Config getInstn() {
		if(instance == null) {
			synchronized (Config.class) {
				if(instance == null) {
					instance = new Config();
				}
			}
		}
		return instance;
	}
	
}
