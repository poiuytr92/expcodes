package exp.libs.warp.ui.cpt.win;

import com.sun.awt.AWTUtilities;

import exp.libs.utils.os.ThreadUtils;



/**
 * <PRE>
 * swing右下角通知窗口
 *   (使用_show方法显示窗体, 可触发自动渐隐消失)
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2017-12-26
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
@SuppressWarnings({ "serial", "restriction" })
public abstract class NoticeWindow extends PopChildWindow implements Runnable {

	private Thread _this;
	
	private boolean isRun = false;
	
	protected NoticeWindow() {
		super("NoticeWindow");
	}
	
	protected NoticeWindow(String name) {
		super(name);
	}
	
	protected NoticeWindow(String name, int width, int height) {
		super(name, width, height);
	}
	
	protected NoticeWindow(String name, int width, int height, boolean relative) {
		super(name, width, height, relative);
	}
	
	protected NoticeWindow(String name, int width, int height, boolean relative, Object... args) {
		super(name, width, height, relative, args);
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
	@Override
	protected final void AfterView() {
		if(isRun == false) {
			isRun = true;
			_this = new Thread(this);
			_this.start();	// 渐隐窗体
		}
	}
	
	@Deprecated
	@Override
	protected final void beforeHide() {
		// Undo
	}
	
	@Override
	public void run() {
		ThreadUtils.tSleep(2000);	// 悬停2�?
		
		// 透明度渐�?(大约持续3�?)
		for(float opacity = 100; opacity > 0; opacity -= 2) {
			AWTUtilities.setWindowOpacity(this, opacity / 100);	// 设置透明�?
			ThreadUtils.tSleep(60);
			
			if(isVisible() == false) {
				break;	// 窗体被提前销毁了(手工点了X)
			}
		}
		
		_hide();	// 销毁窗�?
	}
	
	/**
	 * 阻塞等待渐隐关闭过程
	 */
	public void _join() {
		if(_this == null) {
			return;
		}
		
		try {
			_this.join();
		} catch (Exception e) {}
	}
	
}
