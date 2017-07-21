package exp.libs.warp.upm;

import javax.swing.JPanel;
import javax.swing.JRootPane;

import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.PopChildWindow;

public class HelpWin extends PopChildWindow {

	/** serialVersionUID */
	private static final long serialVersionUID = -6529277202660835224L;

	protected HelpWin() {
		super("", LoginWin.WIDTH, 200);
	}
	
	@Override
	protected void initComponents(Object... args) {
		SwingUtils.setNoFrame(this);
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		// TODO Auto-generated method stub
		
	}

}
