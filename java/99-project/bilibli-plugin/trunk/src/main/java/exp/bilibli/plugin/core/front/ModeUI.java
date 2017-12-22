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

	private final static int WIDTH = 500;
	
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
			this.frontBtn = new JRadioButton("仿真抽奖 (默认模式, 模拟人工操作, 效率较低但兼容所有抽奖)");
			this.backBtn =  new JRadioButton("注入抽奖 (效率较高, 但仅适用于小电视, 或当季度的高能抽奖)");
			frontBtn.setForeground(Color.BLACK);
			backBtn.setForeground(Color.BLACK);
			
			btnGroup.add(frontBtn);
			btnGroup.add(backBtn);
			
			frontBtn.setSelected(true);
			this.backMode = false;
		}
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(SwingUtils.getVFlowPanel(frontBtn, backBtn), BorderLayout.CENTER);
	}

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		frontBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				backMode = false;
				UIUtils.log("已切换全平台抽奖模式 [浏览器-仿真抽奖]");
				_hide();
			}
		});
		
		backBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				backMode = true;
				UIUtils.log("已切换全平台抽奖模式 [服务器-注入抽奖]");
				_hide();
			}
		});
	}
	
	protected boolean isBackMode() {
		return backMode;
	}

}
