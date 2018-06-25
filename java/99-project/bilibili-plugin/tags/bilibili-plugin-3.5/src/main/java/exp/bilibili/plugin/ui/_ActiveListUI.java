package exp.bilibili.plugin.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.cache.ActivityMgr;
import exp.bilibili.plugin.utils.TimeUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.tbl.NormTable;
import exp.libs.warp.ui.cpt.win.PopChildWindow;
import exp.libs.warp.xls.Excel;
import exp.libs.warp.xls.Sheet;

/**
 * <PRE>
 * 活跃榜窗口
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _ActiveListUI extends PopChildWindow {

	/** serialVersionUID */
	private static final long serialVersionUID = -5691969159309932864L;

	private final static int WIDTH = 650;
	
	private final static int HEIGHT = 500;

	private final static int ROOM_ID = Config.getInstn().ACTIVITY_ROOM_ID();
	
	/** Excel模板路径 */
	private final static String XLS_TPL_PATH = "./conf/template.xlsx";
	
	/** 导出Excel详单保存路径 */
	private final static String XLS_SAVE_PATH = PathUtils.combine(
			PathUtils.getDesktopPath(), 
			StrUtils.concat("[", ROOM_ID, "] 直播间活跃值排�? - ", 
					TimeUtils.getSysDate("yyyyMMdd"), ".xlsx"));
	
	private final static String[] HEADER = {
		"个人排名", "昵称", "个人活跃�?"
	};
	
	private final static int MAX_ROW = 50;
	
	private JTextField lastActiveTF;
	
	private JTextField curActiveTF;
	
	private JLabel dayLabel;
	
	private JButton exportBtn;
	
	private _HisVerTable activeTable;
	
	private int lastPeriod;
	
	private int curPeriod;
	
	private int lastSumCost;
	
	private int curSumCost;
	
	private int day;
	
	protected _ActiveListUI() {
		super(StrUtils.concat("[", ROOM_ID, "直播间] 活跃值排行榜"), WIDTH, HEIGHT);
	}
	
	@Override
	protected void initComponents(Object... args) {
		this.lastPeriod = ActivityMgr.getInstn().getLastPeriod();
		this.curPeriod = ActivityMgr.getInstn().getCurPeriod();
		this.lastSumCost = ActivityMgr.getInstn().getLastSumCost();
		this.curSumCost = ActivityMgr.getInstn().getCurSumCost();
		this.day = curSumCost / ActivityMgr.DAY_UNIT;
		
		this.lastActiveTF = new JTextField(String.valueOf(lastSumCost));
		this.curActiveTF = new JTextField(String.valueOf(curSumCost));
		lastActiveTF.setEditable(false);
		curActiveTF.setEditable(false);
		
		this.dayLabel = new JLabel(String.valueOf(day), JLabel.CENTER);
		this.exportBtn = new JButton("导出详单");
		exportBtn.setForeground(Color.BLACK);
		
		this.activeTable = new _HisVerTable();
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
					SwingUtils.getPairsPanel(String.valueOf(lastPeriod).concat("�?-总活跃�?"), lastActiveTF), 
					SwingUtils.getPairsPanel(String.valueOf(curPeriod).concat("�?-总活跃�?"), curActiveTF)
			)), BorderLayout.CENTER);
			
			panel.add(SwingUtils.getHGridPanel(SwingUtils.addBorder(
					SwingUtils.getVGridPanel(new JLabel("[可兑换天数]", JLabel.CENTER), 
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
		exportBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(exportActiveDatas()) {
					SwingUtils.info("已导出到桌面");
				} else {
					SwingUtils.warn("导出失败");
				}
			}
		});
	}
	
	@Override
	protected void AfterView() {
		activeTable.reflash(getActiveDatas());
	}

	@Override
	protected void beforeHide() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 查询活跃值数�?
	 * @return
	 */
	private List<List<String>> getActiveDatas() {
		List<List<String>> datas = new ArrayList<List<String>>(MAX_ROW);
		
		int sortId = 1;
		List<Map.Entry<String, Integer>> actives = ActivityMgr.getInstn().getDSortActives();
		for(Map.Entry<String, Integer> active : actives) {
			List<String> row = Arrays.asList(new String[] {
				String.valueOf(sortId++), 
				ActivityMgr.getInstn().getUserName(active.getKey()), 
				String.valueOf(active.getValue())
			});
			datas.add(row);
			
			if(sortId > MAX_ROW) {
				break;
			}
		}
		return datas;
	}
	
	/**
	 * 导出活跃值数�?
	 * @return
	 */
	private boolean exportActiveDatas() {
		Excel excel = new Excel(XLS_TPL_PATH);
		Sheet sheet = excel.getSheet(0);
		
		// 设置头信�?
		sheet.setVal(0, 1, ROOM_ID);
		sheet.setVal(1, 1, lastSumCost);
		sheet.setVal(1, 2, lastPeriod);
		sheet.setVal(2, 1, curSumCost);
		sheet.setVal(2, 2, curPeriod);
		sheet.setVal(3, 2, day);
		
		// 设置单元格风�?
		CellStyle cellStyle = excel.createCellStyle(); 
		cellStyle.setBorderBottom(CellStyle.BORDER_THIN);	// 下边�?    
		cellStyle.setBorderLeft(CellStyle.BORDER_THIN);		// 左边�?    
		cellStyle.setBorderRight(CellStyle.BORDER_THIN);	// 右边�?    
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);				// 水平居中
		cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);	// 垂直居中
		Font font = excel.createFont();
		font.setFontHeightInPoints((short) 9);
		cellStyle.setFont(font);	// 字体大小
		
		// 设置排行�?
		int sortId = 1;
		int row = 5;
		List<Map.Entry<String, Integer>> actives = ActivityMgr.getInstn().getDSortActives();
		for(Map.Entry<String, Integer> active : actives) {
			sheet.setVal(row, 0, sortId++);
			sheet.setVal(row, 1, ActivityMgr.getInstn().getUserName(active.getKey()));
			sheet.setVal(row, 2, active.getValue());
			
			sheet.setStyle(row, 0, cellStyle);
			sheet.setStyle(row, 1, cellStyle);
			sheet.setStyle(row, 2, cellStyle);
			row++;
		}
		return excel.saveAs(XLS_SAVE_PATH);
	}
	
	/**
	 * <PRE>
	 * 历史版本表单组件
	 * </PRE>
	 * 
	 * @author Administrator
	 * @date 2017�?7�?6�?
	 */
	private class _HisVerTable extends NormTable {
		
		private static final long serialVersionUID = -3111568334645181825L;
		
		private _HisVerTable() {
			super(HEADER, MAX_ROW);
		}

		@Override
		protected void initRightBtnPopMenu(JPopupMenu popMenu) {
			// Undo
		}
		
	}

}
