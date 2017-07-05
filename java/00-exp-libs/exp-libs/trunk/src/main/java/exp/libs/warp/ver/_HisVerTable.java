package exp.libs.warp.ver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.tbl.Table;

class _HisVerTable extends Table<String> {

	private static final long serialVersionUID = -3111568334645181825L;
	
	private int opRow;
	
	private _PrjVerInfo prjVerInfo;
	
	private JPopupMenu popMenu;
	
	protected _HisVerTable(Vector<String> header, 
			Vector<Vector<String>> datas, _PrjVerInfo prjVerInfo) {
		super(header, datas);
		
		this.opRow = -1;
		this.prjVerInfo = prjVerInfo;
		initPopMenu();
	}
	
	private void initPopMenu() {
		this.popMenu = new JPopupMenu();
		JMenuItem detail = new JMenuItem("查看详情");
		JMenuItem delete = new JMenuItem("删除版本");
		popMenu.add(detail);
		popMenu.add(delete);
		
		detail.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				_VerInfo verInfo = prjVerInfo.getVerInfo(opRow);
				if(verInfo != null) {
					new _VerWin(verInfo)._view();
				}
			}
		});
		
		delete.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				_VerInfo verInfo = prjVerInfo.getVerInfo(opRow);
				if(prjVerInfo.delVerInfo(verInfo)) {
					opRow = -1;
					SwingUtils.warn("删除历史版本成功");
					// FIXME: 重绘表单？
				} else {
					SwingUtils.warn("删除历史版本失败");
				}
			}
		});
	}
	
	/**
	 * 鼠标点击事件
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() != MouseEvent.BUTTON3) {	
			return;	// 只处理鼠标右键事件
		}
		
		// 识别操作行（选中行优先，若无选中则为鼠标当前所在行）
		opRow = getCurSelectRow();
		opRow = (opRow < 0 ? getCurMouseRow() : opRow);
		if(opRow < 0) {
			return;
		}
		
		// 呈现浮动菜单
		popMenu.show(e.getComponent(), e.getX(), e.getY());
	}
	
}
