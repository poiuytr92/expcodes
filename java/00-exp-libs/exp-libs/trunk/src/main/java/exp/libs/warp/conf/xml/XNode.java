package exp.libs.warp.conf.xml;

import org.dom4j.Element;

import exp.libs.utils.other.StrUtils;

final class XNode {

	public final static String PATH_SPLIT = "/";
	
	public final static String ID_SPLIT = "@";
	
	public final static String ATT_ID = "id";
	
	public final static String ATT_DEFAULT = "default";
	
	public final static String ATT_CAPTION = "caption";
	
	public final static String ATT_HINT = "hint";
	
	private Element element;
	
	private String ePath;
	
	private String eName;
	
	private String id;
	
	private String defavlt;
	
	private String caption;
	
	private String hint;
	
	private String val;
	
	public XNode(Element element) {
		this.element = element;
		this.ePath = getEPath(element);
		
		if(element == null) {
			this.eName = "";
			this.id = "";
			this.defavlt = "";
			this.caption = "";
			this.hint = "";
			this.val = "";
			
		} else {
			this.eName = element.getName();
			this.id = getAttributeValue(element, ATT_ID);
			this.defavlt = getAttributeValue(element, ATT_DEFAULT);
			this.caption = getAttributeValue(element, ATT_CAPTION);
			this.hint = getAttributeValue(element, ATT_HINT);
			this.val = element.getTextTrim();
		}
	}
	
	// FIXME: 无法定位枚举数组
	private String getEPath(Element e) {
		String ePath = "";
		while (e != null) {
			String eId = getAttributeValue(e, ATT_ID);
			eId = StrUtils.isEmpty(eId) ? "" : StrUtils.concat(ID_SPLIT, eId);
			ePath = StrUtils.isEmpty(ePath) ? "" : StrUtils.concat(PATH_SPLIT, ePath);
			ePath = StrUtils.concat(e.getName(), eId, ePath);
			e = e.getParent();
		}
		return ePath;
	}
	
	private String getAttributeValue(Element e, String attributeName) {
		String val = e.attributeValue(attributeName);
		return (val == null ? "" : val.trim());
	}

	public Element getElement() {
		return element;
	}

	public String getEPath() {
		return ePath;
	}

	public String getEName() {
		return eName;
	}

	public String getId() {
		return id;
	}

	public String getDefault() {
		return defavlt;
	}

	public String getCaption() {
		return caption;
	}

	public String getHint() {
		return hint;
	}

	public String getVal() {
		return val;
	}
	
}
