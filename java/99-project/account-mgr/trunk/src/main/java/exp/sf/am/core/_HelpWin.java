package exp.sf.am.core;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JToolTip;

import exp.libs.utils.StrUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.PopChildWindow;

public class _HelpWin extends PopChildWindow {

	/** serialVersionUID */
	private static final long serialVersionUID = -6529277202660835224L;

	protected _HelpWin() {
		super("", _LoginWin.WIDTH, 220);
	}
	
	@Override
	protected void initComponents(Object... args) {
		SwingUtils.setNoFrame(this);
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		JToolTip toolTip = new JToolTip();
		toolTip.setTipText(StrUtils.concat(
			"<html>", 
				"<body bgcolor=\"#99CCDD\">", 
					"<p>", 
					"<p> <b>欢迎使用 <font color=\"#AA2222\" size=+1>帐密管理器</font></b>", 
					"<p>", 
					"<p>  ● 此软件 <b>不会联网</b>  <em>(欢迎检测网络端口)</em>", 
					"<p>  ● 此软件 <b>不含病毒</b> <em>(若报毒忽略即可)</em>", 
					"<p>  ● 首次使用需在 <b>本地注册</b> 以保护你的帐密", 
					"<p>  ● 登记的帐密会 <b>加密存储</b> 以确保帐密存档安全", 
					"<p>", 
					"<p> 释放你的记忆~ 更方便地管理你的密码吧 O(∩_∩)O", 
					"<p> <em> -- 版权所有: EXP</em>", 
					"<p>", 
				"</body>", 
			"</html>")
		);
		rootPanel.add(toolTip, BorderLayout.CENTER);
	}
	
	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		// TODO Auto-generated method stub
		
	}

}
