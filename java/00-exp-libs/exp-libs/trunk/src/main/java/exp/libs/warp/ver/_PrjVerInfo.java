package exp.libs.warp.ver;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import exp.libs.envm.Charset;
import exp.libs.utils.StrUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.cbg.CheckBoxGroup;
import exp.libs.warp.ui.layout.VFlowLayout;

// 项目版本信息
class _PrjVerInfo {

	private final static String[] API_ITEMS = new String[] {
		"DB", "SOCKET", "WS", "CORBA", "FTP", "OTHER"
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
	
	private _VerInfo curVer;
	
	private List<_VerInfo> historyVers;

	protected _PrjVerInfo(List<_VerInfo> historyVers) {
		this.prjName = "";
		this.prjDesc = "";
		this.teamName = "";
		this.prjCharset = "";
		this.diskSize = "";
		this.cacheSize = "";
		this._APIs = "";
		this.historyVers = (historyVers == null ? 
				new LinkedList<_VerInfo>() : historyVers);
		if(this.historyVers.size() > 0) {
			this.curVer = this.historyVers.get(0);
		} else {
			this.curVer = new _VerInfo();
		}
		
		this.prjNameTF = new JTextField();
		this.prjDescTF = new JTextField();
		this.teamNameTF = new JTextField();
		this.prjCharsetTF = new JComboBox(CHARSET_ITEMS);
		this.diskSizeTF = new JTextField();
		this.cacheSizeTF = new JTextField();
		this._APIsCB = new CheckBoxGroup<String>(API_ITEMS);
	}
	
	protected JScrollPane toPanel() {
		setValToUI();
		
		JPanel panel = new JPanel(new VFlowLayout()); {
			panel.add(SwingUtils.getPairsPanel("项目名称", prjNameTF));
			panel.add(SwingUtils.getPairsPanel("项目简述", prjDescTF));
			panel.add(SwingUtils.getPairsPanel("开发团队", teamNameTF));
			panel.add(SwingUtils.getPairsPanel("项目编码", prjCharsetTF));
			panel.add(SwingUtils.getPairsPanel("硬盘需求", diskSizeTF));
			panel.add(SwingUtils.getPairsPanel("内存需求", cacheSizeTF));
			panel.add(SwingUtils.getPairsPanel("相关接口", _APIsCB.toDefaultPanel()));
		}
		return SwingUtils.addAutoScroll(panel);
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
	
	protected boolean save() {
		// 保存到外存
		return true;
	}
	
	protected String getPrjName() {
		return prjName;
	}

	protected void setPrjName(String prjName) {
		this.prjName = prjName;
	}

	protected String getPrjDesc() {
		return prjDesc;
	}

	protected void setPrjDesc(String prjDesc) {
		this.prjDesc = prjDesc;
	}

	protected String getTeamName() {
		return teamName;
	}

	protected void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	protected String getPrjCharset() {
		return prjCharset;
	}

	protected void setPrjCharset(String prjCharset) {
		this.prjCharset = prjCharset;
	}

	protected String getDiskSize() {
		return diskSize;
	}

	protected void setDiskSize(String diskSize) {
		this.diskSize = diskSize;
	}

	protected String getCacheSize() {
		return cacheSize;
	}

	protected void setCacheSize(String cacheSize) {
		this.cacheSize = cacheSize;
	}

	protected String get_APIs() {
		return _APIs;
	}

	protected void set_APIs(String _APIs) {
		this._APIs = _APIs;
	}

	protected _VerInfo getCurVer() {
		return curVer;
	}

	protected void setCurVer(_VerInfo curVer) {
		this.curVer = curVer;
	}

	protected List<_VerInfo> getHistoryVers() {
		return historyVers;
	}

	protected void setHistoryVers(List<_VerInfo> historyVers) {
		this.historyVers = historyVers;
	}
	
}
