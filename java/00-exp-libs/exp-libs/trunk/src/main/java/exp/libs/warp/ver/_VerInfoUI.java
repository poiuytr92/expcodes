package exp.libs.warp.ver;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.PopChildWindow;

/**
 * <PRE>
 * 版本信息查看窗口
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-07-05
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _VerInfoUI extends PopChildWindow {

	/** serialVersionUID */
	private static final long serialVersionUID = 7506651321309664209L;

	/** 版本信息 */
	private _VerInfo verInfo;
	
	protected _VerInfoUI(_VerInfo verInfo) {
		super("版本信息", 600, 490, false, verInfo);
	}
	
	@Override
	protected void initComponents(Object... args) {
		if(args != null && args.length > 0 && args[0] instanceof _VerInfo) {
			this.verInfo = (_VerInfo) args[0];
		} else {
			this.verInfo = new _VerInfo();
		}
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		JScrollPane verPanel = SwingUtils.addAutoScroll(verInfo.toPanel(false));
		rootPanel.add(verPanel, BorderLayout.CENTER);
	}

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		// TODO Auto-generated method stub
		
	}

}
