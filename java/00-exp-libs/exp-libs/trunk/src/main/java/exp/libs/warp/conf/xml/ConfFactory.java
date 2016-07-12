package exp.libs.warp.conf.xml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

final public class ConfFactory {

	private final static XConfig DEFAULT_CONFIG = new XConfig("DEFAULT_CONFIG");
	
	private Map<String, XConfig> configs;
	
	private static volatile ConfFactory instance;
	
	private ConfFactory() {
		this.configs = new HashMap<String, XConfig>(2);
	}
	
	private static ConfFactory getInstn() {
		if(instance == null) {
			synchronized (ConfFactory.class) {
				if(instance == null) {
					instance = new ConfFactory();
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
			conf.clear();
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
			conf.clear();
		}
	}
	
	public static void clear() {
		getInstn()._clear();
	}
	
	private void _clear() {
		Iterator<XConfig> its = configs.values().iterator();
		while(its.hasNext()) {
			XConfig conf = its.next();
			conf.clear();
		}
		configs.clear();
	}
	
}
