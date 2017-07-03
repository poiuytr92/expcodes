package exp.libs.warp.ui.bean;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * <PRE>
 * swing动态增减行组件
 * </PRE>
 * <B>项    目：</B>凯通J2SE开发平台(KTJSDP)
 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2014
 * @version   1.0 2017-01-17
 * @author    廖权斌：liaoquanbin@gdcattsoft.com
 * @since     jdk版本：jdk1.6
 */
class _ADLine<T extends Component> {

	/** 父面板 */
	private JPanel father;
	
	/** 当前行面板 */
	private JPanel linePanel;
	
	/** 每行的差异化组件（一般用于输入） */
	private Component component;
	
	/** 增加按钮 */
	private JButton addBtn;
	
	/** 删减按钮 */
	private JButton delBtn;
	
	/**
	 * 构造函数
	 * @param father 父面板（用于承载所有行组件的面板, 布局模式建议使用垂直流式布局器）
	 * @param component 每行差异化组件的类(该组件类必须能提供public无参构造函数, 保证组件能够被实例化和唯一性)
	 */
	protected _ADLine(JPanel father, Class<T> component) {
		try {
			this.component = (T) component.newInstance();
		} catch (Throwable e) {
			this.component = new JTextField(
					"警告: 自定义行组件实例化失败(没有提供public的无参构造函数), 已使用JTextField替代自定义行组件");
		}
		
		this.father = father;
		this.addBtn = new JButton("+");
		this.delBtn = new JButton("-");
		
		// 设置按钮内边距
		addBtn.setMargin(new Insets(3, 5, 3, 5));
		delBtn.setMargin(new Insets(3, 5, 3, 5));
	}
	
	/**
	 * 获取当前行面板
	 * @return 若父面板为null则返回null
	 */
	protected JPanel getJPanel() {
		if(father == null) {
			return null;
			
		} else if(linePanel != null) {
			return linePanel;
		}
		
		linePanel = new JPanel(new BorderLayout()); {
			linePanel.add(component, BorderLayout.CENTER);
			JPanel btnPanel = new JPanel(new GridLayout(1, 2)); {
				btnPanel.add(addBtn, 0);
				btnPanel.add(delBtn, 1);
			} linePanel.add(btnPanel, BorderLayout.EAST);
		}
		
		// 在指定行后增加一行
		addBtn.addActionListener(new ActionListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				int idx = father.getComponentZOrder(linePanel);
				_ADLine<T> newLine = new _ADLine<T>(father, (Class<T>) component.getClass());
				father.add(newLine.getJPanel(), idx + 1);
				repaint();
			}
		});
		
		// 删减指定行
		delBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(father.getComponentCount() > 1) {
					father.remove(linePanel);
					repaint();
				}
			}
		});
		return linePanel;
	}
	
	/**
	 * 重绘父面板
	 */
	private void repaint() {
		if(father == null || linePanel == null) {
			return;
		}
		
		father.validate();	// 重构内容面板
		father.repaint();	// 重绘内容面板
	}
	
}
