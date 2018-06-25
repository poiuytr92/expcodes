package exp.libs.warp.ui.cpt.tray;

import java.awt.TrayIcon;

/**
 * <PRE>
 * 系统托盘 (使得程序出现在系统桌面右下角)
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2017-08-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class SystemTray {

	/** 系统托盘 */
	private java.awt.SystemTray sysTray;
	
	/** 单例 */
	private static volatile SystemTray instance;
	
	/**
	 * 私有化构造函�?
	 */
	private SystemTray() {
		if(java.awt.SystemTray.isSupported()) {
			sysTray = java.awt.SystemTray.getSystemTray();
		}
	}
	
	/**
	 * 获取单例
	 * @return 单例
	 */
	private static SystemTray getInstn() {
		if(instance == null) {
			synchronized (SystemTray.class) {
				if(instance == null) {
					instance = new SystemTray();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 检查当前系统是否支持系统托�?
	 * @return true:支持; false:不支�?
	 */
	public static boolean isSupported() {
		return java.awt.SystemTray.isSupported();
	}
	
	/**
	 * 添加一个图标到系统托盘
	 * @param trayIcon 系统托盘图标
	 * @return 是否添加成功
	 */
	public static boolean add(TrayIcon trayIcon) {
		return getInstn()._add(trayIcon);
	}
	
	/**
	 * 添加一个图标到系统托盘
	 * @param trayIcon 系统托盘图标
	 * @return 是否添加成功
	 */
	private boolean _add(TrayIcon trayIcon) {
		boolean isOk = true;
		try {
			sysTray.add(trayIcon);
			
		} catch(Exception e) {
			isOk = false;
		}
		return isOk;
	}
	
	/**
	 * 从系统托盘移除一个图�?
	 * @param trayIcon 系统托盘图标
	 * @return 是否移除成功
	 */
	public static boolean del(TrayIcon trayIcon) {
		return getInstn()._del(trayIcon);
	}
	
	/**
	 * 从系统托盘移除一个图�?
	 * @param trayIcon 系统托盘图标
	 * @return 是否移除成功
	 */
	private boolean _del(TrayIcon trayIcon) {
		boolean isOk = true;
		try {
			sysTray.remove(trayIcon);
			
		} catch(Exception e) {
			isOk = false;
		}
		return isOk;
	}
	
}
