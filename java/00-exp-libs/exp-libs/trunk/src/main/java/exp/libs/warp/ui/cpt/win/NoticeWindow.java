package exp.libs.warp.ui.cpt.win;

import com.sun.awt.AWTUtilities;

import exp.libs.utils.os.ThreadUtils;



/**
 * <PRE>
 * swing右下角通知窗口
 *   (使用_show方法显示窗体, 可触发自动渐隐消失)
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-26
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
@SuppressWarnings({ "serial", "restriction" })
public abstract class NoticeWindow extends PopChildWindow implements Runnable {

	protected NoticeWindow() {
		super("NoticeWindow");
	}
	
	protected NoticeWindow(String name) {
		super(name);
	}
	
	protected NoticeWindow(String name, int width, int high) {
		super(name, width, high);
	}
	
	protected NoticeWindow(String name, int width, int high, boolean relative) {
		super(name, width, high, relative);
	}
	
	protected NoticeWindow(String name, int width, int high, boolean relative, Object... args) {
		super(name, width, high, relative, args);
	}
	
	@Override
	protected int LOCATION() {
		return LOCATION_RB;	// 出现坐标为右下角
	}
	
	@Override
	protected boolean WIN_ON_TOP() {
		return true;	// 设置窗口置顶
	}
	
	/**
	 * 以渐隐方式显示通知消息
	 */
	public void _show() {
		_view();	// 显示通知消息
		new Thread(this).start();	// 渐隐窗体
	}
	
	@Override
	public void run() {
		ThreadUtils.tSleep(2000);	// 悬停2秒
		
		// 透明度渐隐(大约持续3秒)
		for(float opacity = 100; opacity > 0; opacity -= 2) {
			AWTUtilities.setWindowOpacity(this, opacity / 100);	// 设置透明度
			ThreadUtils.tSleep(60);
			
			if(isVisible() == false) {
				break;	// 窗体被提前销毁了(手工点了X)
			}
		}
		
		_hide();	// 销毁窗体
	}
	
}
