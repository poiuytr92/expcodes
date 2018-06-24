package exp.rgx.core;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <PRE>
 * 正则实体
 * 用于保存某次正则测试的匹配信息
 * </PRE>
 * <B>PROJECT：</B> ui-regex-debug
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-06-01
 * @author    EXP: www.exp-blog.com
 * @since     jdk版本：jdk1.6
 */
public class RegexEntity {

	/**
	 * 原始内容
	 */
	private String content;
	
	/**
	 * 正则表达式
	 */
	private String regex;
	
	/**
	 * 匹配记录列表
	 */
	private List<List<String>> mthList;
	
	/**
	 * 总匹配项数
	 */
	private int totalFindNum = 0;
	
	/**
	 * 当前选取的匹配项
	 */
	private int curFindNum = 0;
	
	/**
	 * 构造函数
	 */
	public RegexEntity() {
		this.mthList = new ArrayList<List<String>>();
		this.content = "";
		this.regex = "";
	}
	
	/**
	 * 重做正则匹配，更新RegexEntity保存的匹配信息
	 * @param content 原始内容
	 * @param regex 正则表达式
	 */
	public void redoMatch(String content, String regex) {
		mthList.clear();	//清空上次的匹配结果
		curFindNum = 0;
		totalFindNum = 0;
		this.content = content;
		this.regex = regex;
		
		if(content != null && regex != null) {
			Pattern ptn = Pattern.compile(regex);
			Matcher mth = ptn.matcher(content);
			
			while(mth.find()) {
				List<String> tmp = new ArrayList<String>();
				int grpNum = mth.groupCount();
				
				for(int i = 0; i <= grpNum; i++) {
					tmp.add(mth.group(i));
				}
				mthList.add(tmp);
				totalFindNum++;
			}
		} else {
			curFindNum = -1;
			totalFindNum = -1;
		}
	}
	
	/**
	 * 在mth.find()找到的匹配集中，
	 * 获取当前匹配的 前一个匹配的索引
	 * @return mthList的索引号
	 */
	public int backFindIndex() {
		return (curFindNum <= 0 ? 0 : curFindNum - 1);
	}

	/**
	 * 在mth.find()找到的匹配集中，
	 * 获取当前匹配的 后一个匹配的索引
	 * @return mthList的索引号
	 */
	public int nextFindIndex() {
		return (curFindNum >= totalFindNum - 1 ? 
				totalFindNum - 1 : curFindNum + 1);
	}
	
	/**
	 * 通过 find匹配索引号 和 group组号，
	 * 定位某次匹配的指定内容
	 * @param findIndex 匹配索引号，可理解为mth.find()执行的次数
	 * @param gruopIndex 组号，与正则表达式的括号有关
	 * @return 匹配内容
	 */
	public String getGroup(int findIndex, int gruopIndex) {
		String rst = null;
		try {
			findIndex = findIndex < 0 ? 0 : findIndex;
			findIndex = findIndex >= totalFindNum ? totalFindNum - 1 : findIndex;
			
			int grpNum = mthList.get(findIndex).size();
			gruopIndex = gruopIndex < 0 ? 0 : gruopIndex;
			gruopIndex = gruopIndex > grpNum ? grpNum : gruopIndex;
			
			rst = mthList.get(findIndex).get(gruopIndex);
		} catch (Exception e) {
			rst = null;
		}
		return rst;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public List<List<String>> getMthList() {
		return mthList;
	}

	public void setMthList(List<List<String>> mthList) {
		this.mthList = mthList;
	}

	public int getTotalFindNum() {
		return totalFindNum;
	}

	public void setTotalFindNum(int totalFindNum) {
		this.totalFindNum = totalFindNum;
	}

	public int getCurFindNum() {
		return curFindNum;
	}

	public void setCurFindNum(int curFindNum) {
		this.curFindNum = curFindNum;
	}
	
}
