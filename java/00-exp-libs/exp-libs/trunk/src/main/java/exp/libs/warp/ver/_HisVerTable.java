package exp.libs.warp.ver;

import java.awt.event.MouseEvent;
import java.util.Vector;

import exp.libs.warp.ui.cpt.tbl.Table;

class _HisVerTable extends Table<String> {

	private static final long serialVersionUID = -3111568334645181825L;

	protected _HisVerTable(Vector<String> header, Vector<Vector<String>> datas) {
		super(header, datas);
	}
	
	/**
	 * 鼠标点击事件
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
