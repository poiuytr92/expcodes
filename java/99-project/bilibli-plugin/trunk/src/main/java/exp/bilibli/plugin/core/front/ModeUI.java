package exp.bilibli.plugin.core.front;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import exp.bilibli.plugin.utils.UIUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.PopChildWindow;

class ModeUI extends PopChildWindow {

	/** serialVersionUID */
	private static final long serialVersionUID = -5691969159309932864L;

	private final static int WIDTH = 550;
	
	private final static int HEIGHT = 170;
	
	private JRadioButton frontBtn;
	
	private JRadioButton backBtn;
	
	/** 
	 * 是否为暗中抽奖模式
	 *   true:使用后台协议抽奖
	 *   false:模拟前端行为抽奖
	 */
	private boolean backMode;
	
	protected ModeUI() {
		super("全平台抽奖模式设置", WIDTH, HEIGHT);
	}
	
	@Override
	protected void initComponents(Object... args) {
		ButtonGroup btnGroup = new ButtonGroup(); {
			this.frontBtn = new JRadioButton("浏览器仿真抽奖 (模拟人工操作, 效率低, 适用所有全频道抽奖)");
			this.backBtn =  new JRadioButton("服务器注入抽奖 (默认, 效率高, 仅适用小电视或当季高能抽奖)");
			frontBtn.setForeground(Color.BLACK);
			backBtn.setForeground(Color.BLACK);
			
			btnGroup.add(frontBtn);
			btnGroup.add(backBtn);
			
			backBtn.setSelected(true);
			this.backMode = true;
		}
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(SwingUtils.getVFlowPanel(backBtn, frontBtn), BorderLayout.CENTER);
	}

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		frontBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				backMode = false;
				UIUtils.log("当前全平台抽奖模式 [浏览器-仿真抽奖]");
				_hide();
			}
		});
		
		backBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				backMode = true;
				UIUtils.log("当前全平台抽奖模式 [服务器-注入抽奖]");
				_hide();
			}
		});
	}
	
	protected boolean isBackMode() {
		return backMode;
	}

}
