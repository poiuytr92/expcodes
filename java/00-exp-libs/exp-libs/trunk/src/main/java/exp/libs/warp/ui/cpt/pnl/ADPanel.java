package exp.libs.warp.ui.cpt.pnl;

import java.awt.Component;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;

import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.layout.VFlowLayout;

/**
 * <PRE>
 * swing动态增减行组件的承载面板
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-08-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class ADPanel<T extends Component> {

	/** 承载面板 */
	private JScrollPane scrollPanel;
	
	/**
	 * 构造函数
	 * @param component 每行差异化组件的类(该组件类必须能提供public无参构造函数, 保证组件能够被实例化和唯一性)
	 */
	public ADPanel(Class<T> component) {
		JPanel panel = new JPanel(new VFlowLayout());
		_ADLine<T> firstLine = new _ADLine<T>(panel, component);
		panel.add(firstLine.getJPanel());
		
		// 当出现增减行事件时，刷新滚动面板（使得滚动条动态出现）
		this.scrollPanel = SwingUtils.addAutoScroll(panel);
		panel.addContainerListener(new ContainerListener() {
			
			@Override
			public void componentRemoved(ContainerEvent e) {
				repaint();
			}
			
			@Override
			public void componentAdded(ContainerEvent e) {
				repaint();
			}
		});
	}
	
	/**
	 * 重绘承载面板
	 */
	private void repaint() {
		if(scrollPanel == null) {
			return;
		}
		
		scrollPanel.validate();	// 重构内容面板
		scrollPanel.repaint();	// 重绘内容面板
	}
	
	/**
	 * 获取增减行的承载面板
	 * @return 承载面板
	 */
	public JScrollPane getJScrollPanel() {
		return scrollPanel;
	}
	
	/**
	 * 获取承载面板上当前的所有差异化行组件
	 * @return 差异化行组件集合
	 */
	@SuppressWarnings("unchecked")
	public List<T> getLineComponents() {
		List<T> components = new LinkedList<T>();
		try {
			Component[] linePanels = ((JPanel) ((JViewport) 
					scrollPanel.getComponent(0)).getComponent(0)).getComponents();
			if(linePanels != null) {
				for(Component linePanel : linePanels) {
					T component = (T) ((JPanel) linePanel).getComponent(0);
					components.add(component);
				}
			}
		} catch (Throwable e) {
			// Undo: 自定义行组件 T 实例化失败(没有提供public的无参构造函数), 导致无法获取行组件返回值
		}
		return components;
	}
	
	/**
	 * 获取承载面板上当前的所有行组件.
	 * 	(当自定义行组件失效时，可使用此方法获取默认行组件的值)
	 * @return 默认行组件集合
	 */
	public List<JTextField> getDefaultLineComponents() {
		List<JTextField> components = new LinkedList<JTextField>();
		try {
			Component[] linePanels = ((JPanel) ((JViewport) 
					scrollPanel.getComponent(0)).getComponent(0)).getComponents();
			if(linePanels != null) {
				for(Component linePanel : linePanels) {
					JTextField component = (JTextField) ((JPanel) linePanel).getComponent(0);
					components.add(component);
				}
			}
		} catch (Throwable e) {
			// Undo: 自定义行组件 T 实例化失败(没有提供public的无参构造函数), 导致无法获取行组件返回值
		}
		return components;
	}
	
}
