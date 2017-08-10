package exp.sf.am.core;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.PopChildWindow;

public class _AccountWin extends PopChildWindow {

	private static final long serialVersionUID = -3227397290475968153L;

	@Override
	protected void initComponents(Object... args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(initTabPanel(), BorderLayout.CENTER);
	}
	
	private Component initTabPanel() {
		JPanel panel = new JPanel(new BorderLayout()); {
			JTabbedPane tabPanel = new JTabbedPane(); {
				tabPanel.addTab("帐密列表", null);
				tabPanel.addTab("查询帐密", null);
				tabPanel.addTab("添加帐密", null);
			}
			panel.add(tabPanel, BorderLayout.CENTER);
		}
		return SwingUtils.addScroll(panel);
	}

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		// TODO Auto-generated method stub
		
	}

}
