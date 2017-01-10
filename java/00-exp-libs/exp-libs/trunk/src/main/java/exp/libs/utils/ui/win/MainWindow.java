package exp.libs.utils.ui.win;


/**
 * <PRE>
 * swing主窗口
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
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
	
	@Override
	protected final boolean isMainWindow() {
		return true;
	}
	
}
