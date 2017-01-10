package exp.libs.utils.ui.win;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * <PRE>
 * swing通用窗口
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
@SuppressWarnings("serial")
abstract class _SwingWindow extends JFrame {

	/** 屏宽 */
	protected final int WIN_WIDTH = 
			(int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	
	/** 屏高 */
	protected final int WIN_HIGH = 
			(int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	
	/** 窗口宽 */
	protected final int width;
	
	/** 窗口高 */
	protected final int high;
	
	/** 基面板 */
	private JPanel basePanel;
	
	/** 根面板 */
	protected final JPanel rootPanel;
	
	/**
	 * 全屏初始化
	 * @param name 窗口名称
	 */
	protected _SwingWindow() {
		this("Window", 0, 0, true);
	}
	
	/**
	 * 全屏初始化
	 * @param name 窗口名称
	 */
	protected _SwingWindow(String name) {
		this(name, 0, 0, true);
	}
	
	/**
	 * 限定大小初始化
	 * @param name 窗口名称
	 * @param width 初始窗宽
	 * @param high 初始窗高
	 */
	protected _SwingWindow(String name, int width, int high) {
		this(name, width, high, false);
	}
	
	/**
	 * 限定大小初始化
	 * @param name 窗口名称
	 * @param width 初始窗宽(relative=true时, width<=0; relative=false时, width>0)
	 * @param high 初始窗高(relative=true时, high<=0; relative=false时, high>0)
	 * @param relative 相对尺寸（当此值为true时, width/high为相对全屏宽度的大小）
	 */
	protected _SwingWindow(String name, int width, int high, boolean relative) {
		super(name);
		if(relative == true) {
			this.width = WIN_WIDTH - (width > 0 ? width : -width);
			this.high = WIN_HIGH - (high > 0 ? high : -high);
		} else {
			this.width = (width <= 0 ? -width : width);
			this.high = (high <= 0 ? -high : high);
		}
		
		// 初始化界面
		this.setSize(this.width, this.high);
		this.setLocation((WIN_WIDTH / 2 - this.width / 2), (WIN_HIGH / 2 - this.high / 2));
		this.basePanel = new JPanel(new GridLayout(1, 1));
		this.rootPanel = new JPanel(new BorderLayout());
		this.setContentPane(basePanel);
		basePanel.add(rootPanel, 0);
		
		initComponentsLayout(rootPanel);	//初始化组件布局
		setComponentsListener(rootPanel);	//设置组件监听器
		initCloseWindowMode();
	}
	
	/**
	 * 是否主窗口, 影响关闭窗口模式.
	 *  是: 使用主窗口模式(点击右上角x会关闭所有进程)
	 *  是: 使用子窗口模式(点击右上角x会隐藏当前窗口)
	 * @return 
	 */
	protected abstract boolean isMainWindow();
	
	/**
	 * 初始化关闭窗口模式
	 */
	private void initCloseWindowMode() {
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //避免一点击x就关闭
		
		// 主窗口模式
		if(isMainWindow()) {
			_view();	// 默认显示窗口
			this.addWindowListener(new WindowAdapter() {
				
				@Override
				public void windowClosing(WindowEvent e) {
					if(0 == JOptionPane.showConfirmDialog(rootPanel, 
							"Exit ?\r\n\r\n", "WARN", JOptionPane.YES_NO_OPTION)) {
						_hide();
						System.exit(0);
					}
				}
			});
			
		// 子窗口模式
		} else {
//			_hide();	// 需要主动调用view显示窗口
			this.addWindowListener(new WindowAdapter() {
				
				@Override
				public void windowClosing(WindowEvent e) {
					_hide();
				}
			});
		}
	}
	
	/**
	 * 显示窗口
	 */
	public final void _view() {
		setVisible(true);
	}
	
	/**
	 * 隐藏窗口
	 */
	public final void _hide() {
		dispose();
	}

	/**
	 * 初始化组件布局
	 * @param rootPanel 根面板（已设定布局样式为BorderLayout）
	 */
	protected abstract void initComponentsLayout(JPanel rootPanel);

	/**
	 * 初始化组件监听器
	 * @param rootPanel 根面板（已设定布局样式为BorderLayout）
	 */
	protected abstract void setComponentsListener(JPanel rootPanel);

}
