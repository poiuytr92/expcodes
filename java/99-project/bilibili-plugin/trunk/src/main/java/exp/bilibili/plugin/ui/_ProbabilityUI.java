package exp.bilibili.plugin.ui;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import exp.bilibili.plugin.Config;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.PopChildWindow;

/**
 * <PRE>
 * 随机抽奖概率的设置面板
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class _ProbabilityUI extends PopChildWindow {

	/** serialVersionUID */
	private static final long serialVersionUID = -4832819882789246757L;

	private final static int WIDTH = 600;
	
	private final static int HEIGHT = 200;
	
	/** 默认抽奖概率�?100% */
	private final static int DEFAULT_VALUE = 100;
	
	/** 默认参与抽奖的反应时�?(ms) */
	private final static long REACTION_TIME = Config.getInstn().REACTION_TIME();
	
	/** 概率选择滑块 */
	private JSlider slider;
	
	/** 抽奖反映时间设置�? */
	private JTextField reactionTF;
	
	protected _ProbabilityUI() {
		super(getTitle(DEFAULT_VALUE), WIDTH, HEIGHT);
	}
	
	@Override
	protected void initComponents(Object... args) {
		this.slider = new JSlider(JSlider.HORIZONTAL, 0, 100, DEFAULT_VALUE);
		slider.setMajorTickSpacing(10);	// 大刻度�?
		slider.setMinorTickSpacing(5);	// 小刻度�?
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		
		this.reactionTF = new JTextField(String.valueOf(REACTION_TIME));
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(slider, BorderLayout.CENTER);
		rootPanel.add(SwingUtils.addBorder(
				SwingUtils.getWEBorderPanel(
					new JLabel("   参与抽奖的反应时�?:  "), 
					reactionTF, 
					new JLabel("  毫秒   ")
				)), BorderLayout.SOUTH
		);
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
		
		reactionTF.addKeyListener(new KeyListener() {

		    @Override
		    public void keyTyped(KeyEvent e) {
		        String text = reactionTF.getText();  // 当前输入框内�?
		        char ch = e.getKeyChar();   // 准备附加到输入框的字�?

		        // 限制不能输入非数�?
		        if(!(ch >= '0' && ch <= '9')) {
		            e.consume();    // 销毁当前输入字�?

		        // 限制不能�?0开�?
		        } else if("".equals(text) && ch == '0') {   
		            e.consume();
		        }
		    }

		    @Override
		    public void keyReleased(KeyEvent e) {
		        // TODO Auto-generated method stub
		    }

		    @Override
		    public void keyPressed(KeyEvent e) {
		        // TODO Auto-generated method stub
		    }
		});
	}

	@Override
	protected void AfterView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void beforeHide() {
		Config.getInstn().setReactionTime(reactionTF.getText());
	}
	
	private static String getTitle(int curVal) {
		return StrUtils.concat("设置随机参与抽奖的概�?: ", curVal, "%");
	}
	
	protected int PROBABILITY() {
		return slider.getValue();
	}

	protected long REACTION_TIME() {
		return NumUtils.toLong(reactionTF.getText(), REACTION_TIME);
	}
	
}
