package exp.bilibili.plugin.core.front;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import exp.bilibili.plugin.Config;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.ui.BeautyEyeUtils;
import exp.libs.warp.ui.SwingUtils;
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

	public static void main(String[] args) {
		BeautyEyeUtils.init();
		new _ActiveListUI()._view();
	}
	
	/** serialVersionUID */
	private static final long serialVersionUID = -5691969159309932864L;

	private final static int WIDTH = 500;
	
	private final static int HEIGHT = 400;
	
	private JTextField lastActiveTF;
	
	private JTextField curActiveTF;
	
	private JLabel dayLabel;
	
	private JButton exportBtn;
	
	protected _ActiveListUI() {
		super(StrUtils.concat("[", Config.getInstn().ACTIVITY_ROOM_ID(), 
				"直播间] 活跃值排行榜"), WIDTH, HEIGHT);
	}
	
	@Override
	protected void initComponents(Object... args) {
		this.lastActiveTF = new JTextField("0");
		this.curActiveTF = new JTextField("0");
		lastActiveTF.setEditable(false);
		curActiveTF.setEditable(false);
		
		this.exportBtn = new JButton("导出");
		this.dayLabel = new JLabel("0", JLabel.CENTER);
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(_getNorthPanel(), BorderLayout.NORTH);
		rootPanel.add(_getCenterPanel(), BorderLayout.CENTER);
	}
	
	private JPanel _getNorthPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		SwingUtils.addBorder(panel); {
			
			panel.add(SwingUtils.addBorder(SwingUtils.getVGridPanel(
					SwingUtils.getPairsPanel("上期总活跃值", lastActiveTF), 
					SwingUtils.getPairsPanel("本期总活跃值", curActiveTF)
			)), BorderLayout.CENTER);
			
			panel.add(SwingUtils.getHGridPanel(SwingUtils.addBorder(
					SwingUtils.getVGridPanel(new JLabel("可兑换天数", JLabel.CENTER), 
							dayLabel)), exportBtn
			), BorderLayout.EAST);
		}
		return panel;
	}
	
	private JScrollPane _getCenterPanel() {
		return SwingUtils.addAutoScroll(new JTextArea());
	}

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		
	}

}
