package exp.bilibili.plugin.core.front;

import javax.swing.JPanel;

import exp.libs.warp.ui.cpt.win.PopChildWindow;

/**
 * <PRE>
 * 活跃榜窗口
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _ActiveListUI extends PopChildWindow {

	/** serialVersionUID */
	private static final long serialVersionUID = -5691969159309932864L;

	private final static int WIDTH = 440;
	
	private final static int HEIGHT = 120;
	
	protected _ActiveListUI() {
		super("活跃值排行榜", WIDTH, HEIGHT);
	}
	
	@Override
	protected void initComponents(Object... args) {
		
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		
	}

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		
	}

}
