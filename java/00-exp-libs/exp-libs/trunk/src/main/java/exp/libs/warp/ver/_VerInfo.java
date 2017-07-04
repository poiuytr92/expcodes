package exp.libs.warp.ver;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.layout.VFlowLayout;

// 单个版本细则
class _VerInfo {

	private String author;
	
	private JTextField authorTF;
	
	private String version;
	
	private JTextField versionTF;
	
	private String datetime;
	
	private JTextField datetimeTF;
	
	private String upgradeContent;
	
	private JTextArea upgradeContentTA;
	
	private String upgradeStep;
	
	private JTextArea upgradeStepTA;
	
	protected _VerInfo() {
		this.author = "";
		this.version = "";
		this.datetime = "";
		this.upgradeContent = "";
		this.upgradeStep = "";
		
		this.authorTF = new JTextField();
		this.versionTF = new JTextField();
		this.datetimeTF = new JTextField();
		this.upgradeContentTA = new JTextArea(6, 8);
		this.upgradeStepTA = new JTextArea(6, 8);
	}

	protected JScrollPane toPanel(boolean isEditable) {
		setValToUI();
		
		authorTF.setEditable(isEditable);
		versionTF.setEditable(isEditable);
		datetimeTF.setEditable(isEditable);
		upgradeContentTA.setEditable(isEditable);
		upgradeStepTA.setEditable(isEditable);
		
		JPanel panel = new JPanel(new VFlowLayout()); {
			panel.add(SwingUtils.getPairsPanel(" 责任人 ", authorTF));
			panel.add(SwingUtils.getPairsPanel(" 版本号 ", versionTF));
			panel.add(SwingUtils.getPairsPanel("定版时间", !isEditable ? datetimeTF : 
					SwingUtils.getEBorderPanel(datetimeTF, new JButton("当前时间"))));
			panel.add(SwingUtils.getPairsPanel("升级内容", 
					SwingUtils.addScroll(upgradeContentTA)));
			panel.add(SwingUtils.getPairsPanel("升级步骤", 
					SwingUtils.addScroll(upgradeStepTA)));
		}
		return SwingUtils.addAutoScroll(panel);
	}

	protected void setValToUI() {
		authorTF.setText(author);
		versionTF.setText(version);
		datetimeTF.setText(datetime);
		upgradeContentTA.setText(upgradeContent);
		upgradeStepTA.setText(upgradeStep);
	}
	
	protected void setValFromUI() {
		author = authorTF.getText();
		version = versionTF.getText();
		datetime = datetimeTF.getText();
		upgradeContent = upgradeContentTA.getText();
		upgradeStep = upgradeStepTA.getText();
	}
	
	protected JTextField getAuthorTF() {
		return authorTF;
	}

	protected JTextField getVersionTF() {
		return versionTF;
	}

	protected JTextField getDatetimeTF() {
		return datetimeTF;
	}

	protected JTextArea getUpgradeContentTA() {
		return upgradeContentTA;
	}

	protected String getUpgradeStep() {
		return upgradeStep;
	}

	protected JTextArea getUpgradeStepTA() {
		return upgradeStepTA;
	}

	protected String getAuthor() {
		return author;
	}

	protected void setAuthor(String author) {
		this.author = author;
	}

	protected String getVersion() {
		return version;
	}

	protected void setVersion(String version) {
		this.version = version;
	}

	protected String getDatetime() {
		return datetime;
	}

	protected void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	protected String getUpgradeContent() {
		return upgradeContent;
	}

	protected void setUpgradeContent(String upgradeContent) {
		this.upgradeContent = upgradeContent;
	}

	protected void setUpgradeStep(String upgradeStep) {
		this.upgradeStep = upgradeStep;
	}
	
}
