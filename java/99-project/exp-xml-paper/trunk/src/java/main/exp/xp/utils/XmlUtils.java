package exp.xp.utils;

import java.io.StringReader;
import java.io.StringWriter;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * <PRE>
 * XML工具类
 * </PRE>
 * <B>PROJECT : </B> exp-xml-paper
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2015-06-01
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class XmlUtils {

	/**
	 * 私有化构造函数，避免被误�?
	 */
	private XmlUtils() {}
	
	/**
	 * 格式化xml.
	 * 用于处理缩进、换行、子节点中多余的命名空间等格式问题�?
	 * 
	 * @param xml xml报文
	 * @param linePrefix 在每一行前添加的前缀，亦即缩进符
	 * @param newLine 是否为行尾增加换行符
	 * @param encode 数据编码
	 * @return 格式化的xml报文
	 */
	public static String formatXml(final String xml, 
			final String linePrefix, boolean newLine, String encode) {
		String rstXml = xml;
		if(rstXml != null) {
			
			try {
				SAXReader reader = new SAXReader();
				Document document = reader.read(new StringReader(xml));
				
				if (document != null) {
					StringWriter sWriter = new StringWriter();
					OutputFormat format = new OutputFormat(linePrefix, newLine, encode);
					XMLWriter xmlWriter = new XMLWriter(sWriter, format);
					xmlWriter.write(document);
					xmlWriter.flush();
					rstXml = sWriter.getBuffer().toString();
					xmlWriter.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return rstXml;
	}
	
}
