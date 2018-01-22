package exp.bilibili.plugin.core.front;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.cache.ActivityMgr;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.ui.BeautyEyeUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.tbl.NormTable;
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
	
	private final static String[] HEADER = {
		"排名", "账号名称", "活跃值"
	};
	
	private final static int MAX_ROW = 50;
	
	private JTextField lastActiveTF;
	
	private JTextField curActiveTF;
	
	private JLabel dayLabel;
	
	private JButton exportBtn;
	
	private _HisVerTable activeTable;
	
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
		
		this.activeTable = new _HisVerTable();
		activeTable.reflash(getActiveDatas());
	}
	
	private List<List<String>> getActiveDatas() {
		List<List<String>> datas = new ArrayList<List<String>>(MAX_ROW);
		
		int sortId = 1;
		List<Map.Entry<String, Integer>> actives = ActivityMgr.getInstn().getDescActives();
		for(Map.Entry<String, Integer> active : actives) {
			List<String> row = Arrays.asList(new String[] {
				String.valueOf(sortId++), 
				ActivityMgr.getInstn().getUserName(active.getKey()), 
				String.valueOf(active.getValue())
			});
			datas.add(row);
			
			if(sortId >= MAX_ROW) {
				break;
			}
		}
		return datas;
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
		return SwingUtils.addAutoScroll(activeTable);
	}

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		
	}
	
	
	/**
	 * <PRE>
	 * 历史版本表单组件
	 * </PRE>
	 * 
	 * @author Administrator
	 * @date 2017年7月6日
	 */
	private class _HisVerTable extends NormTable {
		
		private static final long serialVersionUID = -3111568334645181825L;
		
		private _HisVerTable() {
			super(HEADER, 100);
		}

		@Override
		protected void initRightBtnPopMenu(JPopupMenu popMenu) {
			// Undo
		}
		
	}

}
