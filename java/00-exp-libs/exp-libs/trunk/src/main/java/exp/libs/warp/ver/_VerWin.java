package exp.libs.warp.ver;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.PopChildWindow;

class _VerWin extends PopChildWindow {

	/** serialVersionUID */
	private static final long serialVersionUID = 7506651321309664209L;

	private _VerInfo verInfo;
	
	protected _VerWin(_VerInfo verInfo) {
		super("历史版本信息", 600, 490, false, verInfo);
	}
	
	@Override
	protected void initComponents(Object... args) {
		this.verInfo = (_VerInfo) args[0];	// FIXME
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(SwingUtils.addAutoScroll(verInfo.toPanel(false)), BorderLayout.CENTER);
	}

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		// TODO Auto-generated method stub
		
	}

}
