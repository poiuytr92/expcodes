package exp.libs.warp.conf.xml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

final public class ConfFactory {

	private final static Config DEFAULT_CONFIG = new Config("DEFAULT_CONFIG");
	
	private Map<String, Config> configs;
	
	private static volatile ConfFactory instance;
	
	private ConfFactory() {
		this.configs = new HashMap<String, Config>(2);
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
	
	public static Config createConfig(final String name) {
		return getInstn()._createConfig(name);
	}
	
	private Config _createConfig(final String name) {
		if(name == null) {
			return DEFAULT_CONFIG;
		}
		
		Config conf = configs.get(name);
		if(conf == null) {
			conf = new Config(name);
			
		} else {
			conf.clear();
			conf = new Config(name);
		}
		configs.put(name, conf);
		return conf;
	}
	
	public static Config getDefaultConfig() {
		return getInstn()._getConfig(null);
	}
	
	public static Config getConfig(final String name) {
		return getInstn()._getConfig(name);
	}
	
	private Config _getConfig(final String name) {
		Config conf = DEFAULT_CONFIG;
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
		Config conf = configs.remove(name);
		if(conf != null) {
			conf.clear();
		}
	}
	
	public static void clear() {
		getInstn()._clear();
	}
	
	private void _clear() {
		Iterator<Config> its = configs.values().iterator();
		while(its.hasNext()) {
			Config conf = its.next();
			conf.clear();
		}
		configs.clear();
	}
	
}
