package exp.libs.warp.ui.cpt.tbl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * <PRE>
 * 表单渲染器(重新渲染了JTabel的效果)
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
abstract class _TableRenderer extends JTable implements MouseMotionListener, MouseListener {

	/** serialVersionUID */
	private static final long serialVersionUID = 1945991710620583758L;

	/** 表单单元格渲染器(用于设置表单属�?) */
	private NewCellRenderer newCellRenderer;
	
	/** 当前鼠标所在行 */
	private int curMouseRow = -1;
	
	/** 表头 */
	protected Vector<String> headers;
	
	/** 表单数据容器 */
	protected Vector<Vector<String>> dataContainer;
	
	/**
	 * 
	 * @param headers 表头
	 * @param dataContainer 表单数据容器
	 */
	public _TableRenderer(Vector<String> headers, Vector<Vector<String>> dataContainer) {
		super(new DefaultTableModel(dataContainer, headers));
		this.headers = headers;
		this.dataContainer = dataContainer;
		
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	// 设置一次只能选择一�?
		setRenderer();	// 配置表单渲染器（设置表单属性）
		
		this.addMouseListener(this);		//设置鼠标监听（按下、释放、单击、进入或离开�?
		this.addMouseMotionListener(this);	//设置鼠标动作监听（移动、拖动）
	}
	
	/**
	 * 配置表单渲染器（设置表单属性）
	 */
	private void setRenderer() {
		DefaultTableCellRenderer headerRenderer = 	// 获取表头的缺省渲染器（属性配置器�?
				(DefaultTableCellRenderer) this.getTableHeader().getDefaultRenderer();
		headerRenderer.setHorizontalAlignment(		// 设置表头内容的水平对齐方式为"居中"
				DefaultTableCellRenderer.CENTER);
		
		// 使用新的单元格渲染器替换默认的单元格渲染�?(为了设置表单的显示效�?)
		this.newCellRenderer = new NewCellRenderer();
		setDefaultRenderer(Object.class, newCellRenderer);
		newCellRenderer.adjustTableColumnWidths(this);	// 根据单元格内容随时调整表单列�?
	}
	
	/**
	 * 设置单元格内容居中显�?
	 * 	(覆写父类，该方法自动调用)
	 */
	@Override
	public TableCellRenderer getDefaultRenderer(Class<?> colClass) {
		DefaultTableCellRenderer cellRenderer = // 获取当前表格的单元格的缺省渲染器
				(DefaultTableCellRenderer) super.getDefaultRenderer(colClass);
		cellRenderer.setHorizontalAlignment(	// 设置单元格内容的水平对齐方式�?"居中"
				DefaultTableCellRenderer.CENTER);
		return cellRenderer;
	}
	
	public int COL_SIZE() {
		return getColumnCount();
	}
	
	public int ROW_SIZE() {
		return getRowCount();
	}
	
	public int getCurMouseRow() {
		return curMouseRow;
	}
	
	public int getCurSelectRow() {
		return getSelectedRow();
	}
	
	public int getCurSelectCol() {
		return getSelectedColumn();
	}
	
	/**
	 * 设置表格不可编辑.
	 * 	(覆写父类，该方法自动调用)
	 */
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	/**
	 * 鼠标（按下后）拖动事�?
	 */
	@Override
	public abstract void mouseDragged(MouseEvent e);

	/**
	 * 鼠标（没有按下）移动事件
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		Point mouseLocation = e.getPoint();	// 获取鼠标当前坐标
		curMouseRow = rowAtPoint(mouseLocation);	// 根据鼠标坐标获取鼠标当前所在行
		
		// 当鼠标在表格上移动时，设置其所在行的颜�?
		for(int r = 0; r < ROW_SIZE(); r++) {
			if(r == curMouseRow){
				setBackground(Color.GREEN);
				break;
			}
		}
		this.repaint();	// 单元格重绘，目的是激活渲染器（getTableCellRendererComponent方法�?
	}
	
	/**
	 * 鼠标点击事件
	 */
	@Override
	public abstract void mouseClicked(MouseEvent e);

	/**
	 * 鼠标进入（表单）事件
	 */
	@Override
	public abstract void mouseEntered(MouseEvent e);

	/**
	 * 鼠标离开（表单）事件
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		curMouseRow = -1;	//设置悬浮行为-1(使得可以还原悬浮行原本的颜色)
		this.repaint();		//单元格重�?
	}

	/**
	 * 鼠标按下（不放）事件
	 */
	@Override
	public abstract void mousePressed(MouseEvent e);

	/**
	 * 鼠标释放事件
	 */
	@Override
	public abstract void mouseReleased(MouseEvent e);
	
	/**
	 * <PRE>
	 * 单元格渲染器
	 * </PRE>
	 * <B>PROJECT : </B> exp-libs
	 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
	 * @version   1.0 # 2015-12-27
	 * @author    EXP: 272629724@qq.com
	 * @since     jdk版本：jdk1.6
	 */
	private class NewCellRenderer extends DefaultTableCellRenderer {
		
		/** serialVersionUID */
		private static final long serialVersionUID = 2535400091092349214L;

		/** 灰白�? */
		private final Color GREY = new Color(236, 246, 248);
		
		/** 白色 */
		private final Color WHITE = new Color(255, 255, 255);
		
		/** 浅绿�? */
		private final Color LIGHT_GREEN = new Color(154, 221, 151);
		
		/** 天蓝�? */
		private final Color SKY_BLUE = new Color(206, 231, 255);
		
		/** 鲜黄�? */
		private final Color YELLOW = new Color(233, 251, 4);
		
		/** 海蓝�? */
		private final Color SEA_BLUE = new Color(0, 0, 255);
		
		private NewCellRenderer() {}
		
		/**
		 * 返回表单单元格渲染器
		 */
		@Override
		public Component getTableCellRendererComponent(
				JTable table, Object value, boolean isSelected, 
				boolean hasFocus, int row, int column) {
			
			//设置表单奇偶行的背景�?
			if (row % 2 == 0) {
				this.setBackground(GREY);
			} else {
				this.setBackground(WHITE);
			}
			
			// 设置鼠标所在行(悬浮�?)的颜�?
			if (row == curMouseRow) {
				this.setBackground(LIGHT_GREEN);
			}
			
			// 设置table的单元格对齐方式(水平垂直居中)
			this.setHorizontalAlignment((int) Component.CENTER_ALIGNMENT);
			this.setHorizontalTextPosition((int) Component.CENTER_ALIGNMENT);

			table.getTableHeader().setBackground(SKY_BLUE);	//设置表头的背景色:	天蓝�?
			table.setSelectionBackground(YELLOW);			//设置选中行的背景�?:	鲜黄�?
			table.setSelectionForeground(SEA_BLUE);			//设置选中行的前景�?:	深蓝�?
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
		
		/**
		 * 调整列宽
		 * @param table
		 */
		public void adjustTableColumnWidths(JTable table) {
			JTableHeader header = table.getTableHeader(); 		//获取表头
			int rowCount = table.getRowCount(); 				//获取表格的行�?
			TableColumnModel tcm = table.getColumnModel(); 		//获取表格的列模型
			
			// 循环处理每一�?
			for (int col = 0; col < tcm.getColumnCount(); col++) {
				TableColumn column = tcm.getColumn(col); // 获取第col个列对象
				
				// 用表头的渲染器计算第col列表头的宽度
				int colwidth = (int) header.getDefaultRenderer().
						getTableCellRendererComponent(table, column.getIdentifier(), false, false, -1, col).
						getPreferredSize().getWidth();

				// 循环处理第i列的每一行，用单元格渲染器计算第col列第row行的单元格长�?
				for (int row = 0; row < rowCount; row++) {			
					int cellWidth = (int) table.getCellRenderer(row, col).
							getTableCellRendererComponent(table, table.getValueAt(row, col), false, false, row, col).
							getPreferredSize().getWidth();
					colwidth = Math.max(colwidth, cellWidth); 	// 取最大的宽度作为列宽
				}
				
				colwidth += table.getIntercellSpacing().width;	// 加上单元格之间的水平间距（缺省为1像素�?
				column.setPreferredWidth(colwidth);	// 设置第col列的首选宽�?
			}
			table.doLayout();	// 按照上述设置的宽度重新布局各个�?
		}
	}
	
}
