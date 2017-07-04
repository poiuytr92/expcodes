package exp.libs.warp.ver;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.MainWindow;
import exp.libs.warp.ui.layout.VFlowLayout;

class _VerUI extends MainWindow {

	/** serialVersionUID */
	private static final long serialVersionUID = -3365462601777108786L;
	
	/** 界面单例 */
	private static volatile _VerUI instance;
	
	/**
	 * 私有化构造函数
	 */
	private _VerUI() {
		super("版本管理", 600, 400);
	}
	
	/**
	 * 创建程序UI实例
	 * @param isUseExit 是否使用X关闭功能
	 * @return 程序UI实例
	 */
	public static _VerUI getInstn() {
		if(instance == null) {
			synchronized (_VerUI.class) {
				if(instance == null) {
					instance = new _VerUI();
				}
			}
		}
		return instance;
	}
	
	@Override
	protected void initComponents(Object... args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(initTabPanel(), BorderLayout.CENTER);
	}

	private JPanel initTabPanel() {
		JPanel tabPanel = new JPanel(new BorderLayout()); {
			JTabbedPane tabbedPanel = new JTabbedPane(JTabbedPane.TOP); {
				tabbedPanel.add(initPrjInfoPanel(), "项目信息");
				tabbedPanel.add(initHistoryPanel(), "历史版本信息");
				tabbedPanel.add(initCurrentPanel(), "当前版本信息");
				tabbedPanel.add(initNewVerPanel(), "新增版本信息");
			}
			tabbedPanel.setSelectedIndex(2);	// 默认选中 [当前版本信息]
			tabPanel.add(tabbedPanel, BorderLayout.CENTER);
		}
		return tabPanel;
	}
	
	private Component initPrjInfoPanel() {
		JPanel panel = new JPanel(new BorderLayout()); {
			JPanel infoPanel = new JPanel(new VFlowLayout()); {
				infoPanel.add(SwingUtils.getPairsPanel("项目名称"));
				infoPanel.add(SwingUtils.getPairsPanel("项目简述"));
				infoPanel.add(SwingUtils.getPairsPanel("开发团队"));
				infoPanel.add(SwingUtils.getPairsPanel("项目编码"));
				infoPanel.add(SwingUtils.getPairsPanel("硬盘需求"));
				infoPanel.add(SwingUtils.getPairsPanel("内存需求"));
				infoPanel.add(SwingUtils.getPairsPanel("相关接口"));
			}
			panel.add(infoPanel, BorderLayout.CENTER);
			panel.add(new JButton("保存"), BorderLayout.SOUTH);
		}
		return SwingUtils.addAutoScroll(panel);
	}

	private Component initHistoryPanel() {
		// 历史版本数
		// 历史版本列表
		JPanel panel = new JPanel(new BorderLayout()); {
			
		}
		return panel;
	}

	// FIXME: 禁止编辑
	private Component initCurrentPanel() {
		JPanel panel = new JPanel(new VFlowLayout()); {
			panel.add(SwingUtils.getPairsPanel("项目名称"));
			panel.add(SwingUtils.getPairsPanel(" 责任人 "));
			panel.add(SwingUtils.getPairsPanel(" 版本号 "));
			panel.add(SwingUtils.getPairsPanel("定版时间"));
			panel.add(SwingUtils.getPairsPanel("升级内容", 
					SwingUtils.addScroll(new JTextArea(6, 8))));
		}
		return SwingUtils.addAutoScroll(panel);
	}

	private Component initNewVerPanel() {
		JPanel panel = new JPanel(new BorderLayout()); {
			JPanel verPanel = new JPanel(new VFlowLayout()); {
				verPanel.add(SwingUtils.getPairsPanel(" 责任人 "));
				verPanel.add(SwingUtils.getPairsPanel(" 版本号 "));
				verPanel.add(SwingUtils.getPairsPanel("定版时间"));
				verPanel.add(SwingUtils.getPairsPanel("升级内容", 
						SwingUtils.addScroll(new JTextArea(6, 8))));
			}
			panel.add(verPanel, BorderLayout.CENTER);
			panel.add(new JButton("保存"), BorderLayout.SOUTH);
		}
		return SwingUtils.addAutoScroll(panel);
	}

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		// TODO Auto-generated method stub
		
	}

}
