package exp.libs.warp.ui.cpt.tbl;

import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JPopupMenu;

/**
 * <PRE>
 * 常用表单组件（自带右键菜单）
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2017-07-05
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public abstract class NormTable extends AbstractTable {

	/** serialVersionUID */
	private static final long serialVersionUID = -2194275100301409161L;
	
	/** 当前行行�?(优先为选中行、其次是鼠标所在行�? 否则�?-1) */
	private int curRow;
	
	private JPopupMenu popMenu;
	
	/**
	 * 
	 * @param headers 表头
	 * @param maxViewRow 最大呈现的行数
	 */
	public NormTable(List<String> headers, int maxViewRow) {
		super(headers, maxViewRow);
		
		resetCurRow();
		this.popMenu = new JPopupMenu();
		initRightBtnPopMenu(popMenu);
	}
	
	/**
	 * 
	 * @param headers 表头
	 * @param maxViewRow 最大呈现的行数
	 */
	public NormTable(String[] headers, int maxViewRow) {
		super(headers, maxViewRow);
		
		resetCurRow();
		this.popMenu = new JPopupMenu();
		initRightBtnPopMenu(popMenu);
	}

	/**
	 * 初始化右键浮动菜�?
	 */
	protected abstract void initRightBtnPopMenu(JPopupMenu popMenu);
	
	/**
	 * 刷新表单
	 */
	@Override
	public void reflash() {
		super.reflash();
		resetCurRow();
	}
	
	/**
	 * 刷新表单数据
	 * @param datas 新的表单数据
	 */
	@Override
	public void reflash(List<List<String>> datas) {
		super.reflash(datas);
		resetCurRow();
	}
	
	/**
	 * 返回当前行行�?(优先返回选中行、其次是鼠标所在行�? 否则返回-1)
	 * @return
	 */
	protected int getCurRow() {
		return curRow;
	}
	
	private void resetCurRow() {
		curRow = -1;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() != MouseEvent.BUTTON3) {	
			return;	// 只处理鼠标右键事�?
		}
		
		// 识别当前操作行（选中行优先，若无选中则为鼠标当前所在行�?
		curRow = getCurSelectRow();
		curRow = (curRow < 0 ? getCurMouseRow() : curRow);
		if(curRow < 0) {
			return;
		}
		
		// 呈现浮动菜单
		popMenu.show(e.getComponent(), e.getX(), e.getY());
	}

	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}
	
}
