package exp.bilibili.plugin.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import exp.libs.utils.other.StrUtils;
import exp.libs.warp.ui.cpt.win.PopChildWindow;

/**
 * <PRE>
 * 随机抽奖概率的设置面板
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class _ProbabilityUI extends PopChildWindow {

	/** serialVersionUID */
	private static final long serialVersionUID = -4832819882789246757L;

	private final static int WIDTH = 600;
	
	private final static int HEIGHT = 150;
	
	/** 默认抽奖概率：50% */
	private final static int DEFAULT_VALUE = 50;
	
	private JSlider slider;
	
	protected _ProbabilityUI() {
		super(getTitle(DEFAULT_VALUE), WIDTH, HEIGHT);
	}
	
	@Override
	protected void initComponents(Object... args) {
		this.slider = new JSlider(JSlider.HORIZONTAL, 0, 100, DEFAULT_VALUE);
		slider.setMajorTickSpacing(10);	// 大刻度值
		slider.setMinorTickSpacing(5);	// 小刻度值
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(slider, BorderLayout.CENTER);
	}

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		slider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				int curVal = ((JSlider) e.getSource()).getValue();
				setTitle(getTitle(curVal));
			}
		});
	}

	@Override
	protected void AfterView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void beforeHide() {
		// TODO Auto-generated method stub
		
	}
	
	private static String getTitle(int curVal) {
		return StrUtils.concat("设置随机参与抽奖的概率: ", curVal, "%");
	}
	
	protected int VAL() {
		return slider.getValue();
	}

}
