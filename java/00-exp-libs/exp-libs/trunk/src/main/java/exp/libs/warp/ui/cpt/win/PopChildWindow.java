package exp.libs.warp.ui.cpt.win;


/**
 * <PRE>
 * swing弹出式子窗口
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-08-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
@SuppressWarnings("serial")
public abstract class PopChildWindow extends _SwingWindow {

	protected PopChildWindow() {
		super("PopChildWindow");
	}
	
	protected PopChildWindow(String name) {
		super(name);
	}
	
	protected PopChildWindow(String name, int width, int high) {
		super(name, width, high);
	}
	
	protected PopChildWindow(String name, int width, int high, boolean relative) {
		super(name, width, high, relative);
	}
	
	protected PopChildWindow(String name, int width, int high, boolean relative, Object... args) {
		super(name, width, high, relative, args);
	}
	
	@Override
	protected final boolean isMainWindow() {
		return false;
	}
	
	@Deprecated
	@Override
	protected final void beforeExit() {
		// Undo
	}
	
}
