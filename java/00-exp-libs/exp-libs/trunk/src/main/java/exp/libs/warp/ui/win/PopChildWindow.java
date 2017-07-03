package exp.libs.warp.ui.win;


/**
 * <PRE>
 * swing弹出式子窗口
 * </PRE>
 * <B>项    目：</B>凯通J2SE开发平台(KTJSDP)
 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2014
 * @version   1.0 2017-01-10
 * @author    廖权斌：liaoquanbin@gdcattsoft.com
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
	
}
