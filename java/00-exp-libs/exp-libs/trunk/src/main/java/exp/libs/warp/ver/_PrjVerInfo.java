package exp.libs.warp.ver;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import exp.libs.envm.Charset;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.cbg.CheckBoxGroup;
import exp.libs.warp.ui.layout.VFlowLayout;

/**
 * <PRE>
 * 项目(应用)版本信息
 * 	(包括项目信息、当前版本信息、历史版本信息)
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _PrjVerInfo {
	
	private final static String[] API_ITEMS = new String[] {
		"DB", "SOCKET", "WS", "HTTP", "FTP", "OTHER"
	};
	
	private final static String[] CHARSET_ITEMS = new String[] {
		Charset.UTF8, Charset.GBK, Charset.ISO
	};
	
	private final static String DELIMITER = ",";
	
	private String prjName;
	
	private JTextField prjNameTF;
	
	private String prjDesc;
	
	private JTextField prjDescTF;
	
	private String teamName;
	
	private JTextField teamNameTF;
	
	private String prjCharset;
	
	private JComboBox prjCharsetTF;
	
	private String diskSize;
	
	private JTextField diskSizeTF;
	
	private String cacheSize;
	
	private JTextField cacheSizeTF;
	
	private String _APIs;
	
	private CheckBoxGroup<String> _APIsCB;
	
	/** 显示用的历史版本数据(与界面过滤结果一�?) */
	private List<_VerInfo> viewHistoryVers;
	
	/** 实际的历史版本数�?(与库存一�?) */
	private List<_VerInfo> historyVers;

	/** 当前的版本信�?(亦即最后一个版�?) */
	private _VerInfo curVer;
	
	/**
	 * 构造函�?
	 * @param historyVers 历史版本信息
	 */
	protected _PrjVerInfo(List<_VerInfo> historyVers) {
		this.prjName = "";
		this.prjDesc = "";
		this.teamName = "";
		this.prjCharset = "";
		this.diskSize = "";
		this.cacheSize = "";
		this._APIs = "";
		this.viewHistoryVers = new LinkedList<_VerInfo>();
		this.historyVers = (historyVers == null ? 
				new LinkedList<_VerInfo>() : historyVers);
		this.curVer = new _VerInfo();
		updateCurVer();
		
		
		this.prjNameTF = new JTextField();
		this.prjDescTF = new JTextField();
		this.teamNameTF = new JTextField();
		this.prjCharsetTF = new JComboBox(CHARSET_ITEMS);
		this.diskSizeTF = new JTextField();
		this.cacheSizeTF = new JTextField();
		this._APIsCB = new CheckBoxGroup<String>(API_ITEMS);
	}
	
	private void updateCurVer() {
		curVer.clear();
		int size = this.historyVers.size();
		if(size > 0) {
			_VerInfo verInfo = this.historyVers.get(size - 1);
			curVer.setValFromUI(verInfo);
		}
	}
	
	protected void setValToUI() {
		prjNameTF.setText(prjName);
		prjDescTF.setText(prjDesc);
		teamNameTF.setText(teamName);
		prjCharsetTF.setSelectedItem(prjCharset);
		diskSizeTF.setText(diskSize);
		cacheSizeTF.setText(cacheSize);
		
		_APIsCB.unselectAll();
		String[] apis = _APIs.split(DELIMITER);
		for(String api : apis) {
			_APIsCB.select(api);
		}
	}
	
	protected void setValFromUI() {
		prjName = prjNameTF.getText();
		prjDesc = prjDescTF.getText();
		teamName = teamNameTF.getText();
		prjCharset = prjCharsetTF.getSelectedItem().toString();
		diskSize = diskSizeTF.getText();
		cacheSize = cacheSizeTF.getText();
		
		List<String> apis = _APIsCB.getItems(true);
		_APIs = StrUtils.concat(apis, DELIMITER);
	}
	
	protected JScrollPane toPanel() {
		setValToUI();
		
		JPanel panel = new JPanel(new VFlowLayout()); {
			panel.add(SwingUtils.getPairsPanel("应用名称", prjNameTF));
			panel.add(SwingUtils.getPairsPanel("应用描述", prjDescTF));
			panel.add(SwingUtils.getPairsPanel("开发团�?", teamNameTF));
			panel.add(SwingUtils.getPairsPanel("项目编码", prjCharsetTF));
			panel.add(SwingUtils.getPairsPanel("硬盘需�?", diskSizeTF));
			panel.add(SwingUtils.getPairsPanel("内存需�?", cacheSizeTF));
			panel.add(SwingUtils.getPairsPanel("相关接口", _APIsCB.toHGridPanel()));
		}
		return SwingUtils.addAutoScroll(panel);
	}
	
	protected String checkVersion(_VerInfo verInfo) {
		String errDesc = "";
		if(verInfo == null) {
			errDesc = "版本对象�? null";
			
		} else if(StrUtils.isTrimEmpty(verInfo.getAuthor())) {
			errDesc = "[责任人] 不能为空";
			
		} else if(StrUtils.isTrimEmpty(verInfo.getVersion())) {
			errDesc = "[版本号] 不能为空";
			
		} else if(StrUtils.isTrimEmpty(verInfo.getDatetime())) {
			errDesc = "[定版时间] 不能为空";
			
		} else if(StrUtils.isTrimEmpty(verInfo.getUpgradeContent())) {
			errDesc = "[升级内容] 不能为空";
			
		} else {
			final String regex = "^(\\d+)\\.(\\d+)(-SNAPSHOT)?$";
			String newVer = verInfo.getVersion();
			String curVer = this.curVer.getVersion();
			
			List<String> newVerIDs = RegexUtils.findGroups(newVer, regex);
			if(newVerIDs.isEmpty()) {
				errDesc = StrUtils.concat("[版本号] 格式错误\r\n", 
						"参考格�?: Major.Minor{-SNAPSHOT}  (主版�?.次版�?-快照标识)\r\n", 
						"�?:  1.0-SNAPSHOT�? 2.1�? 2.2-SNAPSHOT �?");
				
			} else {
				curVer = (StrUtils.isTrimEmpty(curVer) ? "0.0" : curVer);
				List<String> curVerIDs = RegexUtils.findGroups(curVer, regex);
				if(!isGreater(newVerIDs, curVerIDs)) {
					errDesc = "[版本号] 不能小于最后的版本.";
				}
			}
		}
		return errDesc;
	}
	
	private boolean isGreater(List<String> aVerIDs, List<String> bVerIDs) {
		final int MAJOR = 1, MINOR = 2, SNAPSHOT = 3;
		int aMajor = NumUtils.toInt(aVerIDs.get(MAJOR), 0);
		int aMinor = NumUtils.toInt(aVerIDs.get(MINOR), 0);
		boolean aSnapshot = StrUtils.isNotEmpty(aVerIDs.get(SNAPSHOT));
		int bMajor = NumUtils.toInt(bVerIDs.get(MAJOR), 0);
		int bMinor = NumUtils.toInt(bVerIDs.get(MINOR), 0);
		boolean bSnapshot = StrUtils.isNotEmpty(bVerIDs.get(SNAPSHOT));
		
		boolean isGreater = false;
		if(aMajor > bMajor) {
			isGreater = true;
			
		} else if(aMajor < bMajor) {
			isGreater = false;
			
		} else {
			if(aMinor > bMinor) {
				isGreater = true;
				
			} else if(aMinor < bMinor) {
				isGreater = false;
				
			} else {
				isGreater = (!aSnapshot && bSnapshot);
			}
		}
		return isGreater;
	}
	
	protected boolean addVerInfo(_VerInfo verInfo) {
		if(verInfo == null) {
			return false;
		}
		
		curVer.setValFromUI(verInfo);
		historyVers.add(verInfo);
		return true;
	}
	
	protected boolean modifyCurVerInfo() {
		if(curVer == null) {
			return false;
		}
		
		curVer.setValFromUI(null);
		int size = historyVers.size();
		historyVers.get(size - 1).setValFromUI(curVer);
		return true;
	}
	
	protected boolean delVerInfo(_VerInfo verInfo) {
		boolean isOk = false;
		Iterator<_VerInfo> verIts = historyVers.iterator();
		while(verIts.hasNext()) {
			_VerInfo ver = verIts.next();
			if(ver.getVersion().equals(verInfo.getVersion())) {
				verIts.remove();
				isOk = true;
				break;
			}
		}
		
		if(isOk == true) {
			int size = historyVers.size();
			if(historyVers.size() > 0) {
				curVer.setValFromUI(historyVers.get(size - 1));
			} else {
				curVer.clear();
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @param row 此行数为界面的版本列表行数， 对此处的历史版本列表而言是倒序�?
	 * @return
	 */
	protected _VerInfo getVerInfo(int row) {
		_VerInfo verInfo = null;
		if(row >= 0 && row < viewHistoryVers.size()) {
			verInfo = viewHistoryVers.get(row);
		}
		return verInfo;
	}
	
	protected String getPrjName() {
		return prjName;
	}

	protected void setPrjName(String prjName) {
		this.prjName = (prjName == null ? "" : prjName);
	}

	protected String getPrjDesc() {
		return prjDesc;
	}

	protected void setPrjDesc(String prjDesc) {
		this.prjDesc = (prjDesc == null ? "" : prjDesc);
	}

	protected String getTeamName() {
		return teamName;
	}

	protected void setTeamName(String teamName) {
		this.teamName = (teamName == null ? "" : teamName);
	}

	protected String getPrjCharset() {
		return prjCharset;
	}

	protected void setPrjCharset(String prjCharset) {
		this.prjCharset = (prjCharset == null ? "" : prjCharset);
	}

	protected String getDiskSize() {
		return diskSize;
	}

	protected void setDiskSize(String diskSize) {
		this.diskSize = (diskSize == null ? "" : diskSize);
	}

	protected String getCacheSize() {
		return cacheSize;
	}

	protected void setCacheSize(String cacheSize) {
		this.cacheSize = (cacheSize == null ? "" : cacheSize);
	}

	protected String getAPIs() {
		return _APIs;
	}

	protected void setAPIs(String _APIs) {
		this._APIs = (_APIs == null ? "" : _APIs);
	}

	protected _VerInfo getCurVer() {
		return curVer;
	}
	
	protected List<List<String>> toHisVerTable() {
		return toHisVerTable(null);
	}
	
	protected List<List<String>> toHisVerTable(String keyword) {
		viewHistoryVers.clear();
		List<List<String>> hisVerTable = new LinkedList<List<String>>();
		for(int i = historyVers.size() - 1; i >= 0; i--) {
			_VerInfo verInfo = historyVers.get(i);
			if(StrUtils.isEmpty(keyword) || verInfo.contains(keyword)) {
				List<String> row = new LinkedList<String>();
				row.add(verInfo.getVersion());
				row.add(verInfo.getAuthor());
				row.add(verInfo.getDatetime());
				row.add(StrUtils.showSummary(verInfo.getUpgradeContent().trim()));
				hisVerTable.add(row);
				viewHistoryVers.add(verInfo);
			}
		}
		return hisVerTable;
	}

}
