package exp.libs.warp.conf.xml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <pre>
 * XML文件配置器工厂.
 * </pre>	
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
final public class XConfigFactory {

	private final static XConfig DEFAULT_CONFIG = new XConfig("DEFAULT_CONFIG");
	
	private Map<String, XConfig> configs;
	
	private static volatile XConfigFactory instance;
	
	private XConfigFactory() {
		this.configs = new HashMap<String, XConfig>(2);
	}
	
	private static XConfigFactory getInstn() {
		if(instance == null) {
			synchronized (XConfigFactory.class) {
				if(instance == null) {
					instance = new XConfigFactory();
				}
			}
		}
		return instance;
	}
	
	public static XConfig createConfig(final String name) {
		return getInstn()._createConfig(name);
	}
	
	private XConfig _createConfig(final String name) {
		if(name == null) {
			return DEFAULT_CONFIG;
		}
		
		XConfig conf = configs.get(name);
		if(conf == null) {
			conf = new XConfig(name);
			
		} else {
			conf.destroy();
			conf = new XConfig(name);
		}
		configs.put(name, conf);
		return conf;
	}
	
	public static XConfig getDefaultConfig() {
		return getInstn()._getConfig(null);
	}
	
	public static XConfig getConfig(final String name) {
		return getInstn()._getConfig(name);
	}
	
	private XConfig _getConfig(final String name) {
		XConfig conf = DEFAULT_CONFIG;
		if(name != null) {
			conf = configs.get(name);
			if(conf == null) {
				conf = DEFAULT_CONFIG;
			}
		}
		return conf;
	}
	
	public static void removeConfig(final String name) {
		getInstn()._removeConfig(name);
	}
	
	private void _removeConfig(final String name) {
		XConfig conf = configs.remove(name);
		if(conf != null) {
			conf.destroy();
		}
	}
	
	public static void clear() {
		getInstn()._clear();
	}
	
	private void _clear() {
		Iterator<XConfig> its = configs.values().iterator();
		while(its.hasNext()) {
			XConfig conf = its.next();
			conf.destroy();
		}
		configs.clear();
	}
	
}
