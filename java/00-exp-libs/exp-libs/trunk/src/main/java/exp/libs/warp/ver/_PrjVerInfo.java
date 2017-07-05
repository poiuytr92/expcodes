package exp.libs.warp.ver;

import java.util.ArrayList;
import java.util.Iterator;
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

class _PrjVerInfo {

	private final static String[] API_ITEMS = new String[] {
		"DB", "SOCKET", "WS", "CORBA", "FTP", "OTHER"
	};
	
	private final static String[] CHARSET_ITEMS = new String[] {
		Charset.UTF8, Charset.GBK, Charset.ISO
	};
	
	private final static String DELIMITER = ",";
	
	private _VerMgr verMgr;
	
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

	protected _PrjVerInfo(_VerMgr verMgr, List<_VerInfo> historyVers) {
		this.verMgr = verMgr;
		this.prjName = "";
		this.prjDesc = "";
		this.teamName = "";
		this.prjCharset = "";
		this.diskSize = "";
		this.cacheSize = "";
		this._APIs = "";
		this.curVer = new _VerInfo();
		this.historyVers = (historyVers == null ? 
				new LinkedList<_VerInfo>() : historyVers);
		
		int size = this.historyVers.size();
		if(size > 0) {
			_VerInfo verInfo = this.historyVers.get(size - 1);
			curVer.setValFromUI(verInfo);
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
	
	private void setValToUI() {
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
	
	private void setValFromUI() {
		prjName = prjNameTF.getText();
		prjDesc = prjDescTF.getText();
		teamName = teamNameTF.getText();
		prjCharset = prjCharsetTF.getSelectedItem().toString();
		diskSize = diskSizeTF.getText();
		cacheSize = cacheSizeTF.getText();
		
		List<String> apis = _APIsCB.getItems(true);
		_APIs = StrUtils.concat(apis, DELIMITER);
	}
	
	protected boolean savePrjInfo() {
		if(verMgr == null) {
			return false;
		}
		
		setValFromUI();
		return verMgr.savePrjInfo();
	}
	
	protected boolean addVerInfo(_VerInfo verInfo) {
		if(verMgr == null) {
			return false;
		}
		
		boolean isOk = verMgr.addVerInfo(verInfo);
		if(isOk == true) {
			curVer.setValFromUI(verInfo);
			historyVers.add(verInfo);
		}
		return isOk;
	}
	
	protected boolean delVerInfo(_VerInfo verInfo) {
		if(verMgr == null || verInfo == null) {
			return false;
		}
		
		boolean isOk = verMgr.delVerInfo(verInfo);
		if(isOk == true) {
			Iterator<_VerInfo> verIts = historyVers.iterator();
			while(verIts.hasNext()) {
				_VerInfo ver = verIts.next();
				if(ver.getVersion().equals(verInfo.getVersion())) {
					verIts.remove();
					break;
				}
			}
			
			int size = historyVers.size();
			if(historyVers.size() > 0) {
				curVer.setValFromUI(historyVers.get(size - 1));
			} else {
				curVer.clear();
			}
		}
		return isOk;
	}
	
	/**
	 * 
	 * @param row 此行数为界面的版本列表行数， 对此处的历史版本列表而言是倒序的
	 * @return
	 */
	protected _VerInfo getVerInfo(int row) {
		_VerInfo verInfo = null;
		if(verMgr == null || row < 0) {
			return verInfo;
		}
		
		int idx = historyVers.size() - 1 - row;
		if(idx < 0) {
			return verInfo;
		}
		
		return historyVers.get(idx);
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

	protected List<_VerInfo> getHistoryVers() {
		return new ArrayList<_VerInfo>(historyVers);
	}

}
