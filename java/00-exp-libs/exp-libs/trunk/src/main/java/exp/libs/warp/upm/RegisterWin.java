package exp.libs.warp.upm;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.PopChildWindow;

class RegisterWin extends PopChildWindow {

	/** serialVersionUID */
	private static final long serialVersionUID = -5942561235188887487L;

	protected RegisterWin() {
		super("", LoginWin.WIDTH, 210);
	}
	
	@Override
	protected void initComponents(Object... args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(toUNPWPanel(), BorderLayout.CENTER);
	}

	private JPanel toUNPWPanel() {
		JPanel panel = new JPanel(new GridLayout(6, 1)); {
			panel.add(new JLabel(), 0);
			panel.add(SwingUtils.getWEBorderPanel(
					new JLabel("  [账号] :  "), new JTextField(), 
					new JLabel("   ")), 1);
			panel.add(new JLabel(), 2);
			panel.add(SwingUtils.getWEBorderPanel(
					new JLabel("  [密码] :  "), new JTextField(), 
					new JLabel("   ")), 3);
			panel.add(new JLabel(), 4);
			panel.add(SwingUtils.getWEBorderPanel(
					new JLabel("  [昵称] :  "), new JTextField(), 
					new JLabel("   ")), 5);
		} SwingUtils.addBorder(panel);
		return panel;
	}

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		// TODO Auto-generated method stub
		
	}

}
