package exp.libs.warp.upm;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolTip;

import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.PopChildWindow;

public class HelpWin extends PopChildWindow {

	/** serialVersionUID */
	private static final long serialVersionUID = -6529277202660835224L;

	protected HelpWin() {
		super("", LoginWin.WIDTH, 100);
	}
	
	@Override
	protected void initComponents(Object... args) {
		SwingUtils.setNoFrame(this);
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		JToolTip toolTip = new JToolTip();
		toolTip.setTipText("测试: 使用提示信息");
		rootPanel.add(toolTip, BorderLayout.CENTER);
	}

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		// TODO Auto-generated method stub
		
	}

}
