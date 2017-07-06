package exp.libs.utils.format;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.DateFormat;
import exp.libs.utils.StrUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.ObjUtils;

/**
 * <PRE>
 * 数据格式转换工具包.
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class ESCUtils {

	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(ESCUtils.class);
	
	/** 私有化构造函数. */
	protected ESCUtils() {}

	/**
	 * 把字符串中的特殊字符转义为xml的转义字符.
	 * 
	 * [&] 转义 [&amp;] 意为 [地址符].
	 * [<] 转义 [&lt;] 意为 [小于].
	 * [>] 转义 [&gt;] 意为 [大于号].
	 * ['] 转义 [&apos;] 意为 [单引号].
	 * ["] 转义 [&quot;] 意为 [双引号].
	 * [ ] 转义 [&nbsp;] 意为 [空格].
	 * [©] 转义 [&copy;] 意为 [版权符].
	 * [®] 转义 [&reg;] 意为 [注册符].
	 * 
	 * @return 转义后的字符串
	 */
	public static String toXmlESC(final String javaStr) {
		String xml = "";
		if(javaStr != null) {
			xml = javaStr;
			xml = xml.replace("&", "&amp;");
			xml = xml.replace("<", "&lt;");
			xml = xml.replace(">", "&gt;");
			xml = xml.replace("'", "&apos;");
			xml = xml.replace("\"", "&quot;");
			xml = xml.replace("©", "&copy;");
			xml = xml.replace("®", "&reg;");
		}
		return xml;
	}
	
	/**
	 * 把含有xml特殊字符的字符串进行转义.
	 * 
	 * [&amp;] 转义 [&] 意为 [地址符].
	 * [&lt;] 转义 [<] 意为 [小于].
	 * [&gt;] 转义 [>] 意为 [大于号].
	 * [&apos;] 转义 ['] 意为 [单引号].
	 * [&quot;] 转义 ["] 意为 [双引号].
	 * [&nbsp;] 转义 [ ] 意为 [空格].
	 * [&copy;] 转义 [©] 意为 [版权符].
	 * [&reg;] 转义 [®] 意为 [注册符].
	 * 
	 * @return 转义后的字符串
	 */
	public static String unXmlESC(final String xmlStr) {
		String str = "";
		if(xmlStr != null) {
			str = xmlStr;
			str = str.replace("&amp;", "&");
			str = str.replace("&lt;", "<");
			str = str.replace("&gt;", ">");
			str = str.replace("&apos;", "'");
			str = str.replace("&quot;", "\"");
			str = str.replace("&copy;", "©");
			str = str.replace("&reg;", "®");
		}
		return str;
	}
	
	/**
	 * 把普通字符串中由[两个连续字符构成的特殊字符]转换为java的[转义字符].
	 * 
	 *  [\\\\] 转义 [\\].
	 * 	[\\t] 转义 [\t].
	 *  [\\r] 转义 [\r].
	 *  [\\n] 转义 [\\n].
	 *  [\\\"] 转义 [\"].
	 *  [\\0] 转义 [\0].
	 *  [\\b] 转义 [\b].
	 *  [\\f] 转义 [\f].
	 *  
	 * @param xml
	 * @return
	 */
	public static String toJavaESC(final String escStr) {
		String str = "";
		if(escStr != null) {
			str = escStr;
			str = str.replace("\\\\", "\\");
			str = str.replace("\\t", "\t");
			str = str.replace("\\r", "\r");
			str = str.replace("\\n", "\n");
			str = str.replace("\\\"", "\"");
			str = str.replace("\\0", "\0");
			str = str.replace("\\b", "\b");
			str = str.replace("\\f", "\f");
		}
		return str;
	}
	
	public static String toRegexESC(final String escStr) {
		String str = "";
		if(escStr != null) {
			str = escStr;
			str = str.replace("\\", "\\\\");
			str = str.replace("(", "\\(");
			str = str.replace(")", "\\)");
			str = str.replace("[", "\\[");
			str = str.replace("]", "\\]");
			str = str.replace("{", "\\{");
			str = str.replace("}", "\\}");
			str = str.replace("+", "\\+");
			str = str.replace("-", "\\-");
			str = str.replace(".", "\\.");
			str = str.replace("*", "\\*");
			str = str.replace("?", "\\?");
			str = str.replace("^", "\\^");
			str = str.replace("$", "\\$");
		}
		return str;
	}
	
	/**
	 * 对json中的键值字符串的特殊字符进行转义。
	 * 
	 * 	[\] 转义 [\\].
	 * 	[,] 转义 [\,].
	 *  ["] 转义 [\"].
	 *  [:] 转义 [\:].
	 *  [\r] 转义 [].
	 *  [\n] 转义 [].
	 *  [\b] 转义 [].
	 *  [\t] 转义 [].
	 *  [\f] 转义 [].
	 *  
	 * @param jsonKV java字符
	 * @return json转义字符
	 */
	public static String toJsonESC(final String jsonKV) {
		String str = "";
		if(jsonKV != null) {
			str = jsonKV;
			str = str.replace("\\", "\\\\");
			str = str.replace(",", "\\,");
			str = str.replace("\"", "\\\"");
			str = str.replace(":", "\\:");
			str = str.replace("\r", "");
			str = str.replace("\n", "");
			str = str.replace("\b", "");
			str = str.replace("\t", "");
			str = str.replace("\f", "");
		}
		return str;
	}
	
	@SuppressWarnings("deprecation")
	public static String toJson(Object obj) {
		String json = "";
		if (obj == null) {
			return json;
		}
			
		StringWriter writer = new StringWriter();
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.YMDHMSS);
		ObjectMapper mapper = new ObjectMapper();
		mapper.getSerializationConfig().setDateFormat(sdf);
		
		try {
			mapper.writeValue(writer, obj);
		} catch (Exception e) {
			log.error("转换JSON失败: {}", obj, e);
		}
		json = writer.toString();
		
		try {
			writer.close();
		} catch (IOException e) {
			log.error("转换JSON异常: 关闭输入流失败.", e);
		}
		return json;
	}
	
	@SuppressWarnings("deprecation")
	public static String toJson(Object obj, String dateFormat) {
		String json = "";
		if (obj == null) {
			return json;
		}
			
		StringWriter writer = new StringWriter();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		ObjectMapper mapper = new ObjectMapper();
		mapper.getSerializationConfig().setDateFormat(sdf);
		
		try {
			mapper.writeValue(writer, obj);
		} catch (Exception e) {
			log.error("转换JSON失败: {}", obj, e);
		}
		json = writer.toString();
		
		try {
			writer.close();
		} catch (IOException e) {
			log.error("转换JSON异常: 关闭输入流失败.", e);
		}
		return json;
	}

	public static Map<?, ?> unJson(String json) {
		Map<?, ?> map = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			map = mapper.readValue(json, LinkedHashMap.class);
			
		} catch (Exception e) {
			log.error("转换JSON失败: {}", json, e);
		}
		return map;
	}
	
	public static String toBCP(List<List<Object>> table, 
			String rowDelimiter, String colDelimiter) {
		if(table == null) {
			return "";
		}
		
		rowDelimiter = (rowDelimiter == null ? "" : rowDelimiter);
		colDelimiter = (colDelimiter == null ? "" : colDelimiter);
		
		StringBuilder sb = new StringBuilder();
		for (List<Object> row : table) {
			for (Object col : row) {
				String data = (col == null ? "" : col.toString());
				sb.append(data).append(colDelimiter);
			}

			if (row.size() > 0) {
				sb.setLength(sb.length() - colDelimiter.length());
			}
			sb.append(rowDelimiter);
		}

		if (table.size() > 0) {
			sb.setLength(sb.length() - rowDelimiter.length());
		}
		return sb.toString();
	}

	public static String toBCP(List<Object> list, String delimiter) {
		if(list == null || list.size() <= 0) {
			return "";
		}
		
		delimiter = (delimiter == null ? "" : delimiter);
		StringBuilder sb = new StringBuilder();
		for (Object o : list) {
			String data = (o == null ? "" : o.toString());
			sb.append(data).append(delimiter);
		}
		sb.setLength(sb.length() - delimiter.length());
		return sb.toString();
	}
	
	public static List<List<String>> unBCP(String bcpTableString, 
			String rowDelimiter, String colDelimiter) {
		bcpTableString = (bcpTableString == null ? "" : bcpTableString);
		rowDelimiter = (rowDelimiter == null ? "" : rowDelimiter);
		
		String[] bcpTable = bcpTableString.split(rowDelimiter);
		List<List<String>> table = new ArrayList<List<String>>(bcpTable.length);
		for(String bcpListString : bcpTable) {
			table.add(unBCP(bcpListString, colDelimiter));
		}
		return table;
	}
	
	public static List<String> unBCP(String bcpListString, String delimiter) {
		bcpListString = (bcpListString == null ? "" : bcpListString);
		delimiter = (delimiter == null ? "" : delimiter);
		String[] bcpList = bcpListString.split(delimiter);
		return Arrays.asList(bcpList);
	}

	public static String toCSV(List<List<Object>> table) {
		if(table == null) {
			return "";
		}
		
		final String rowDelimiter = "\r\n";
		final String colDelimiter = ",";
		
		StringBuilder sb = new StringBuilder();
		for (List<Object> row : table) {
			for (Object col : row) {
				String data = (col == null ? "" : col.toString());
				sb.append(toCsvString(data)).append(colDelimiter);
			}

			if (row.size() > 0) {
				sb.setLength(sb.length() - colDelimiter.length());
			}
			sb.append(rowDelimiter);
		}

		if (table.size() > 0) {
			sb.setLength(sb.length() - rowDelimiter.length());
		}
		return sb.toString();
	}

	public static String toCsv(List<Object> list) {
		if(list == null || list.size() <= 0) {
			return "";
		}
		
		final String delimiter = ",";
		StringBuilder sb = new StringBuilder();
		for (Object o : list) {
			String data = (o == null ? "" : o.toString());
			sb.append(toCsvString(data)).append(delimiter);
		}
		sb.setLength(sb.length() - delimiter.length());
		return sb.toString();
	}

	/**
	 * 把普通字符串转换成CSV格式字符串
	 * @param s
	 * @return
	 */
	private static String toCsvString(String s) {
		return StringEscapeUtils.escapeCsv(s);
	}
	
	public static List<List<String>> unCSV(String csvTableString) {
		csvTableString = (csvTableString == null ? "" : csvTableString);
		final String rowDelimiter = "\r\n";
		
		String[] csvTable = csvTableString.split(rowDelimiter);
		List<List<String>> table = new ArrayList<List<String>>(csvTable.length);
		for(String csvListString : csvTable) {
			table.add(unCsv(csvListString));
		}
		return table;
	}
	
	public static List<String> unCsv(String csvListString) {
		csvListString = (csvListString == null ? "" : csvListString);
		final String delimiter = ",";
		
		String[] csvList = csvListString.split(delimiter);
		List<String> list = new ArrayList<String>(csvList.length);
		for(String csvData : csvList) {
			list.add(unCsvString(csvData));
		}
		return list;
	}
	
	/**
	 * 把CSV格式字符串转换成普通字符串
	 * @param s
	 * @return
	 */
	private static String unCsvString(String s) {
		return StringEscapeUtils.unescapeCsv(s);
	}
	
	public static String toTSV(List<List<Object>> table) {
		return toBCP(table, "\r\n", "\t");
	}

	public static String toTsv(List<Object> list) {
		return toBCP(list, "\t");
	}
	
	public static List<List<String>> unTSV(String tsvTableString) {
		return unBCP(tsvTableString, "\r\n", "\t");
	}
	
	public static List<String> unTsv(String tsvListString) {
		return unBCP(tsvListString, "\t");
	}
	
	public static <T> String toTXT(List<List<T>> table, boolean header) {
		if(table == null || table.size() <= 0) {
			return "";
		}
		
		int rowNum = table.size();
		int colNum = 0;
		for(List<T> row : table) {
			colNum = NumUtils.max(colNum, row.size());
		}
		
		if(colNum <= 0) {
			return "";
		}
		
		String[][] txtTable = new String[rowNum][colNum];
		int[] colLens = new int[colNum];
		Arrays.fill(colLens, 0);
		
		for(int r = 0; r < rowNum; r++) {
			List<T> row = table.get(r);
			for(int c = 0; c < row.size(); c++) {
				T col = row.get(c);
				txtTable[r][c] = ObjUtils.toStr(col);
				colLens[c] = NumUtils.max(colLens[c], 
						StrUtils.chineseLen(txtTable[r][c]));
			}
		}
		
		StringBuffer txt = new StringBuffer();
		txt.append(getTxtLine(colLens));
		for(int r = 0; r < rowNum; r++) {
			for(int c = 0; c < colNum; c++) {
				if(txtTable[r][c] == null) {
					txtTable[r][c] = "";
				}
				
				txt.append("| ");
				txt.append(txtTable[r][c]);
				int whitespace = colLens[c] - 
						StrUtils.chineseLen(txtTable[r][c]) + 1;
				txt.append(StrUtils.multiChar(' ', whitespace));
			}
			txt.append("|\r\n");
			
			if(header && r == 0) {
				txt.append(getTxtLine(colLens));
			}
		}
		txt.append(getTxtLine(colLens));
		return txt.toString();
	}
	
	private static String getTxtLine(int[] colLens) {
		StringBuffer line = new StringBuffer();
		for(int i = 0; i < colLens.length; i++) {
			line.append('+');
			line.append(StrUtils.multiChar('-', (colLens[i] + 2)));
		}
		line.append("+\r\n");
		return line.toString();
	}
	
}
