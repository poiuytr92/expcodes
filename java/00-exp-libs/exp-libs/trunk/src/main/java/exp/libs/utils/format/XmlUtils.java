package exp.libs.utils.format;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.Charset;
import exp.libs.envm.Endline;
import exp.libs.utils.encode.CharsetUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.warp.io.flow.FileFlowReader;
import exp.libs.warp.io.flow.StringFlowReader;

/**
 * <PRE>
 * xml处理工具
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2016-01-19
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class XmlUtils {

	/** 日志�? */
	private final static Logger log = LoggerFactory.getLogger(XmlUtils.class);
	
	/** 默认编码 */
	private final static String DEFAULT_CHARSET = Charset.UTF8;
	
	/** 私有化构造函�? */
	protected XmlUtils() {}
	
	/**
	 * 是否为合法的xml格式字符�?
	 * @param xml xml格式字符�?
	 * @return true:合法; false:非法
	 */
	public static boolean isVaild(String xml) {
		boolean isVaild = true;
		try {
			DocumentHelper.parseText(xml);
		} catch(Throwable e) {
			isVaild = false;
		}
		return isVaild;
	}
	
	/**
	 * 是否为非法的xml格式字符�?
	 * @param xml xml格式字符�?
	 * @return true:非法; false:合法
	 */
	public static boolean isInvaild(String xml) {
		return !isVaild(xml);
	}
	
	/**
	 * 移除xml中的空行
	 * @param xml xml报文
	 * @param charset xml编码
	 * @return 移除空行后的xml
	 */
	public static String removeEmptyLines(String xml, String charset) {
		StringBuilder sb = new StringBuilder();
		StringFlowReader sfr = new StringFlowReader(xml, charset);
		while(sfr.hasNextLine()) {
			String line = sfr.readLine('>');
			sb.append(line.trim());
		}
		sfr.close();
		return sb.toString();
	}
	
	/**
	 * <PRE>
	 * 格式化xml.
	 * 	(缩进、换行、删除子节点中多余的命名空间�?)
	 * </PRE>
	 * @param xml xml报文
	 * @param linePrefix 在每一行前添加的前缀，亦即缩进符
	 * @param newLine 是否为行尾增加换行符
	 * @param charset xml编码
	 * @return 格式化的xml报文
	 */
	public static String formatXml(final String xml, 
			final String linePrefix, boolean newLine, String charset) {
		String fmtXml = xml;
		if(fmtXml != null) {
			try {
				SAXReader reader = new SAXReader();
				Document doc = reader.read(new StringReader(fmtXml));
				
				if (doc != null) {
					StringWriter sWriter = new StringWriter();
					OutputFormat format = new OutputFormat(linePrefix, newLine, charset);
					XMLWriter xmlWriter = new XMLWriter(sWriter, format);
					xmlWriter.write(doc);
					xmlWriter.flush();
					fmtXml = sWriter.getBuffer().toString();
					xmlWriter.close();
				}
			} catch (Exception e) {
				log.error("格式化xml失败: {}", xml, e);
			}
		}
		return fmtXml;
	}
	
	/**
	 * 从xml声明头中截取编码信息
	 * @param xmlFile xml文件对象
	 * @return xml编码(若未声明编码，则返回默认编码UTF-8)
	 */
	public static String getCharset(File xmlFile) {
		String charset = DEFAULT_CHARSET;
		FileFlowReader ffr = new FileFlowReader(xmlFile, Charset.ISO);
		if(ffr.hasNextLine()) {
			String headLine = ffr.readLine(Endline.CR);
			
			Pattern ptn = Pattern.compile("encoding=\"([^\"]+)\"");
			Matcher mth = ptn.matcher(headLine);
			if(mth.find()) {
				charset = mth.group(1);
				if(!CharsetUtils.isVaild(charset)) {
					charset = DEFAULT_CHARSET;
				}
			}
		}
		ffr.close();
		return charset;
	}
	
	/**
	 * 从xml声明头中截取编码信息
	 * @param xmlContent xml内容
	 * @return xml编码(若未声明编码，则返回默认编码UTF-8)
	 */
	public static String getCharset(String xmlContent) {
		String charset = DEFAULT_CHARSET;
		StringFlowReader sfr = new StringFlowReader(xmlContent, Charset.ISO);
		if(sfr.hasNextLine()) {
			String headLine = sfr.readLine(Endline.CR);
			
			Pattern ptn = Pattern.compile("encoding=\"([^\"]+)\"");
			Matcher mth = ptn.matcher(headLine);
			if(mth.find()) {
				charset = mth.group(1);
				if(!CharsetUtils.isVaild(charset)) {
					charset = DEFAULT_CHARSET;
				}
			}
		}
		sfr.close();
		return charset;
	}
	
	/**
	 * 检查节点下是否存在子节�?
	 * @param e 指定节点
	 * @return true:存在子节�?; false:不存在子节点
	 */
	@SuppressWarnings("unchecked")
	public static boolean hasChilds(Element e) {
		boolean hasChilds = false;
		if(e != null) {
			Iterator<Element> childs = e.elementIterator();
			hasChilds = childs.hasNext();
		}
		return hasChilds;
	}
	
	/**
	 * 获取第一个子节点
	 * @param father 父节�?
	 * @return 第一个子节点; 若父节点为null或无子节点，则返回null
	 */
	public static Element getFirstChild(Element father) {
		Element child = null;
		if(father != null) {
			@SuppressWarnings("unchecked")
			Iterator<Element> childs = father.elementIterator();
			if(childs.hasNext()) {
				child = childs.next();
			}
		}
		return child;
	}
	
	/**
	 * 获取元素名称（优先取带命名空间的名称�?
	 * @param e 元素对象
	 * @return 元素名称
	 */
	public static String getName(Element e) {
		String name = "";
		if(e != null) {
			name = e.getQualifiedName();
			if(name == null || "".equals(name)) {
				name = e.getName();
			}
		}
		return (name == null ? "" : name);
	}
	
	/**
	 * 取[指定节点]的节点值（去除前后空字符）.
	 * @param e 指定节点
	 * @return 节点�?, 若为null则替换为""
	 */
	public static String getValue(Element e) {
		String val = "";
		if(e != null) {
			val = e.getTextTrim();
			val = (val == null ? "" : val);
		}
		return val;
	}
	
	/**
	 * 取[指定节点]下[指定路径]的节点值（去除前后空字符）.
	 * @param e 指定节点
	 * @param ePath 指定路径, �? "/" 作为路径分隔�?
	 * @return 节点�?, 若为null则替换为""
	 */
	public static String getValue(Element e, String ePath) {
		String val = "";
		if(e != null) {
			String[] paths = PathUtils.toLinux(ePath).split("/");
			if(paths != null) {
				Element child = e;
				
				for(String path : paths) {
					if(path == null || "".equals(path)) {
						continue;
					}
					
					child = child.element(path);
					if(child == null) {
						break;
					}
				}
				val = (child == null ? "" : child.getTextTrim());
				val = (val == null ? "" : val);
			}
		}
		return val;
	}
	
	/**
	 * 取[指定节点]的子节点的节点值（去除前后空字符）.
	 * @param e 指定节点
	 * @param childName 子节点名�?
	 * @return 节点�?, 若为null则替换为""
	 */
	public static String getChildValue(Element e, String childName) {
		String val = "";
		if(e != null) {
			val = e.elementTextTrim(childName);
			val = (val == null ? "" : val);
		}
		return val;
	}
	
	/**
	 * 取[指定节点]的[指定属性]的属性值（去除前后空字符）.
	 * @param e 指定节点
	 * @param attributeName 指定属性名�?
	 * @return 属性�?, 若为null则替换为""
	 */
	public static String getAttribute(Element e, String attributeName) {
		String val = "";
		if(e != null) {
			val = e.attributeValue(attributeName);
			val = (val == null ? "" : val);
		}
		return val;
	}
	
	/**
	 * 移除元素中的属�?
	 * @param e 元素对象
	 * @param attributeName 属性名�?
	 * @return true:移除成功; false:移除失败
	 */
	public static boolean removeAttribute(Element e, String attributeName) {
		boolean isDel = false;
		Attribute attribute = e.attribute(attributeName);
		if(attribute != null) {
			e.remove(attribute);
			isDel = true;
		}
		return isDel;
	}
	
	/**
	 * 获取某个节点自身所属的命名空间地址�?
	 * @param element 节点
	 * @return 命名空间地址�?
	 */
	public static String getSelfNamespace(Element element) {
		if(element == null) {
			return "";
		}
		return toNamespaceURL(element.getNamespace());
	}
	
	/**
	 * 获取在某个节点上定义的所有命名空间地址�?
	 * @param element 节点
	 * @return 所有命名空间地址�?
	 */
	public static String getAllNamespace(Element element) {
		if(element == null) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		int size = element.nodeCount();
		for (int i = 0; i < size; i++) {
            Node node = element.node(i);

            if (node instanceof Namespace) {
                Namespace ns = (Namespace) node;
                sb.append(toNamespaceURL(ns));
            }
        }
		return sb.toString();
	}
	
	/**
	 * 构造命名空间地址
	 * @param namespace 命名空间
	 * @return 命名空间地址
	 */
	private static String toNamespaceURL(Namespace namespace) {
		if(namespace == null) {
			return "";
		}
        return toNamespaceURL(namespace.getPrefix(), namespace.getURI());
    }
	
	/**
	 * 构造命名空间地址
	 * @param prefix 地址前缀
	 * @param _URI 唯一地址标识�?
	 * @return 命名空间地址
	 */
	private static String toNamespaceURL(String prefix, String _URI)  {
		StringBuilder sb = new StringBuilder();
        if ((prefix != null) && (prefix.length() > 0)) {
            sb.append(" xmlns:");
            sb.append(prefix);
            sb.append("=\"");
            
        } else {
            sb.append(" xmlns=\"");
        }

        sb.append(_URI);
        sb.append("\"");
        return sb.toString();
    }
	
	/**
	 * <PRE>
	 * 构造xml完整路径对应的压缩路�?.
	 * 	如完整路径为 /root/test/one
	 *  则压缩路径为/r/t/one
	 * </PRE>
	 * @param path xml完整路径
	 * @return xml压缩路径
	 */
	public static String toCompressPath(String path) {
		String cmpPath = "";
		if(path != null && !"".equals(path)) {
			String[] nodes = PathUtils.toLinux(path).split("/");
			StringBuilder sb = new StringBuilder();
			
			if(nodes.length > 0) {
				for(int i = 0; i < nodes.length - 1; i++) {
					if(!"".equals(nodes[i])) {
						sb.append(nodes[i].charAt(0));
					}
					sb.append('/');
				}
				sb.append(nodes[nodes.length - 1]);
				
			} else {
				sb.append('/');
			}
			cmpPath = sb.toString();
		}
		return cmpPath;
	}
	
	/**
	 * 校验xml完整路径和压缩路径是否匹�?
	 * @param path xml完整路径
	 * @param compressPath xml压缩路径
	 * @return true:匹配; false:不匹�?
	 */
	public static boolean matches(String path, String compressPath) {
		boolean isMatches = false;
		if(path != null && compressPath != null) {
			String[] nodes = PathUtils.toLinux(path).split("/");
			String[] cmpNodes = PathUtils.toLinux(compressPath).split("/");
			
			if(nodes.length == cmpNodes.length) {
				isMatches = true;
				int size = nodes.length - 1;
				
				if(size >= 0) {
					for(int i = 0; i < size; i++) {
						isMatches &= (nodes[i].startsWith(cmpNodes[i]));
					}
					isMatches &= (nodes[size].equals(cmpNodes[size]));
				}
			}
		}
		return isMatches;
	}
	
}
