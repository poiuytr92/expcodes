package exp.libs.warp.conf.xml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

final public class ConfFactory {

	private final static ConfBox DEFAULT_CONF_BOX = new ConfBox("DEFAULT_CONF_BOX");
	
	private Map<String, ConfBox> confBoxes;
	
	private static volatile ConfFactory instance;
	
	private ConfFactory() {
		this.confBoxes = new HashMap<String, ConfBox>(2);
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
	
	public static ConfBox createConfBox(final String name) {
		return getInstn()._createConfBox(name);
	}
	
	private ConfBox _createConfBox(final String name) {
		if(name == null) {
			return DEFAULT_CONF_BOX;
		}
		
		ConfBox cBox = confBoxes.get(name);
		if(cBox == null) {
			cBox = new ConfBox(name);
			
		} else {
			cBox.clear();
			cBox = new ConfBox(name);
		}
		confBoxes.put(name, cBox);
		return cBox;
	}
	
	public static ConfBox getDefaultConfBox() {
		return getInstn()._getConfBox(null);
	}
	
	public static ConfBox getConfBox(final String name) {
		return getInstn()._getConfBox(name);
	}
	
	private ConfBox _getConfBox(final String name) {
		ConfBox cBox = DEFAULT_CONF_BOX;
		if(name != null) {
			cBox = confBoxes.get(name);
			if(cBox == null) {
				cBox = DEFAULT_CONF_BOX;
			}
		}
		return cBox;
	}
	
	public static void removeConfBox(final String name) {
		getInstn()._removeConfBox(name);
	}
	
	private void _removeConfBox(final String name) {
		ConfBox cBox = confBoxes.remove(name);
		if(cBox != null) {
			cBox.clear();
		}
	}
	
	public static void clear() {
		getInstn()._clear();
	}
	
	private void _clear() {
		Iterator<ConfBox> its = confBoxes.values().iterator();
		while(its.hasNext()) {
			ConfBox cBox = its.next();
			cBox.clear();
		}
		confBoxes.clear();
	}
	
}
