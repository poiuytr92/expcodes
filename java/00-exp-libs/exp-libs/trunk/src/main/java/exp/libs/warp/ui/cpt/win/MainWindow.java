package exp.libs.warp.ui.cpt.win;


/**
 * <PRE>
 * swing主窗口
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-08-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
@SuppressWarnings("serial")
public abstract class MainWindow extends _SwingWindow {

	protected MainWindow() {
		super("MainWindow");
	}
	
	protected MainWindow(String name) {
		super(name);
	}
	
	protected MainWindow(String name, int width, int high) {
		super(name, width, high);
	}
	
	protected MainWindow(String name, int width, int high, boolean relative) {
		super(name, width, high, relative);
	}
	
	protected MainWindow(String name, int width, int high, boolean relative, Object... args) {
		super(name, width, high, relative, args);
	}
	
	@Override
	protected final boolean isMainWindow() {
		return true;
	}
	
}
