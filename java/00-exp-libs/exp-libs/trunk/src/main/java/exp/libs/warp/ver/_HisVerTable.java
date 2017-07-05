package exp.libs.warp.ver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import exp.libs.utils.StrUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.tbl.AbstractTable;

class _HisVerTable extends AbstractTable {

	private static final long serialVersionUID = -3111568334645181825L;
	
	private final static String[] HEADER = {
		"版本号", "责任人", "定版时间", "升级内容概要"
	};
	
	private final static int MAX_ROW = 50;
	
	private int opRow;
	
	private _PrjVerInfo prjVerInfo;
	
	private JPopupMenu popMenu;
	
	protected _HisVerTable(_PrjVerInfo prjVerInfo) {
		super(HEADER,  MAX_ROW);
		this.opRow = -1;
		this.prjVerInfo = prjVerInfo;
		reflash(prjVerInfo.toHisVerTable());
		initPopMenu();
	}
	
	private void initPopMenu() {
		this.popMenu = new JPopupMenu();
		JMenuItem detail = new JMenuItem("查看详情");
		JMenuItem delete = new JMenuItem("删除版本");
		JMenuItem reflash = new JMenuItem("刷新列表");
		popMenu.add(detail);
		popMenu.add(delete);
		popMenu.add(reflash);
		
		detail.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				_VerInfo verInfo = prjVerInfo.getVerInfo(opRow);
				if(verInfo != null) {
					verInfo._view();
				}
			}
		});
		
		delete.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				_VerInfo verInfo = prjVerInfo.getVerInfo(opRow);
				if(verInfo == null) {
					return;
				}
				
				if(!SwingUtils.confirm(StrUtils.concat("确认删除版本 [", 
						verInfo.getVersion(), "] ?"))) {
					return;
				}
				
				if(prjVerInfo.delVerInfo(verInfo)) {
					opRow = -1;
					reflashList();	// 刷新表单
					SwingUtils.warn("删除历史版本成功");
					
				} else {
					SwingUtils.warn("删除历史版本失败");
				}
			}
		});
		
		reflash.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				reflash();	// 刷新表单
			}
		});
	}
	
	protected void reflashList() {
		reflash(prjVerInfo.toHisVerTable());
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

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
