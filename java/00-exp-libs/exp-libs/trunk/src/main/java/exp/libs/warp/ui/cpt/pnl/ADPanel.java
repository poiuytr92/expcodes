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
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2017-08-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class ADPanel<T extends Component> {

	/** 默认最大的行组件数(<=0表示不限) */
	private final static int MAX_ROW = -1;
	
	/** 最大的行组件数(<=0表示不限) */
	private int maxRow;
	
	/** 承载面板 */
	private JScrollPane scrollPanel;
	
	/** 基准组件面板 */
	private	JPanel basePanel; 
	
	/**
	 * 构造函�?
	 * @param component 行组件的�?(该组件类必须能提供public无参构造函�?, 保证组件能够被实例化和唯一�?)
	 */
	public ADPanel(Class<T> component) {
		this(component, MAX_ROW);
	}
	
	/**
	 * 构造函�?
	 * @param component 行组件的�?(该组件类必须能提供public无参构造函�?, 保证组件能够被实例化和唯一�?)
	 * @param maxRow 最多可以添加的行组件数(<=0表示不限)
	 */
	public ADPanel(Class<T> component, int maxRow) {
		this.maxRow = maxRow;
		
		basePanel = new JPanel(new VFlowLayout());
		_ADLine<T> firstLine = new _ADLine<T>(basePanel, component, maxRow);
		basePanel.add(firstLine.getJPanel());
		
		// 当出现增减行事件时，刷新滚动面板（使得滚动条动态出现）
		this.scrollPanel = SwingUtils.addAutoScroll(basePanel);
		basePanel.addContainerListener(new ContainerListener() {
			
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
	 * 获取当前行组件数
	 * @return 行组件数
	 */
	public int size() {
		return basePanel.getComponentCount();
	}
	
	/**
	 * 检查当前行组件的数量是否在允许范围�?
	 * @return true:在范围内; false:数量溢出
	 */
	private boolean rowInRange() {
		return (maxRow <= 0 || size() < maxRow);
	}
	
	/**
	 * 新增行组�?(程序内部接口)
	 * @param component 行组�?
	 * @return 是否添加成功
	 */
	public boolean add(T component) {
		boolean isOk = false;
		if(component != null) {
			if(rowInRange()) {
				_ADLine<T> line = new _ADLine<T>(basePanel, component, maxRow);
				basePanel.add(line.getJPanel());
				isOk = true;
			}
		}
		return isOk;
	}
	
	
	/**
	 * 替换指定行的行组件（若不存在对应�?, 则添加到末尾�?
	 * @param component 行组�?
	 * @return 是否替换成功
	 */
	public boolean set(T component, int index) {
		boolean isOk = false;
		if(component != null && index >= 0) {
			if(index < size()) {
				basePanel.remove(index);
				
			} else if(index >= size() && rowInRange()) {
				index = size();
				
			} else {
				index = -1;
			}
			
			if(index >= 0) {
				_ADLine<T> line = new _ADLine<T>(basePanel, component, maxRow);
				basePanel.add(line.getJPanel(), index);
				isOk = true;
			}
		}
		return isOk;
	}
	
	/**
	 * 删除行组�?(程序内部接口)
	 * @param index 行组件索�?
	 * @return 是否删除成功
	 */
	public boolean del(int index) {
		boolean isOk = false;
		if(size() > 1 && index >= 0 && index < size()) {
			basePanel.remove(index);
			isOk = true;
		}
		return isOk;
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
	 * 获取承载面板上当前的所有差异化行组�?
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
			// Undo: 自定义行组件 T 实例化失�?(没有提供public的无参构造函�?), 导致无法获取行组件返回�?
		}
		return components;
	}
	
	/**
	 * 获取承载面板上当前的所有行组件.
	 * 	(当自定义行组件失效时，可使用此方法获取默认行组件的�?)
	 * @return 默认行组件集�?
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
			// Undo: 自定义行组件 T 实例化失�?(没有提供public的无参构造函�?), 导致无法获取行组件返回�?
		}
		return components;
	}
	
}
