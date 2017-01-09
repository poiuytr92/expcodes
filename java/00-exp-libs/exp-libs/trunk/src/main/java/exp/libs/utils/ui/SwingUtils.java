package exp.libs.utils.ui;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import exp.libs.utils.pub.StrUtils;

public class SwingUtils {

	public static final int AUTO_SCROLL_MODE = 0;
	
	public static final int SHOW_SCROLL_MODE = 1;
	
	public static final int HIDE_SCROLL_MODE = -1;
	
	protected SwingUtils() {}
	
	public static JScrollPane addScroll(JTextArea textArea) {
		return addAutoScroll(textArea);
	}
	
	public static JScrollPane addAutoScroll(JTextArea textArea) {
		return addScroll(textArea, AUTO_SCROLL_MODE);
	}
	
	public static JScrollPane addShowScroll(JTextArea textArea) {
		return addScroll(textArea, SHOW_SCROLL_MODE);
	}
	
	public static JScrollPane addHideScroll(JTextArea textArea) {
		return addScroll(textArea, HIDE_SCROLL_MODE);
	}
	
	public static JScrollPane addScroll(JTextArea textArea, int mode) {
		textArea = (textArea == null ? new JTextArea() : textArea);
		JScrollPane scroll = new JScrollPane(textArea); 

		// 分别设置水平和垂直滚动条总是出现 
		if(mode == SHOW_SCROLL_MODE) {
			scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); 
			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			
		//分别设置水平和垂直滚动条总是隐藏
		} else if(mode == HIDE_SCROLL_MODE) {
			scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
			scroll.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			
		//分别设置水平和垂直滚动条自动出现 
		} else {
			scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
		}
		return scroll;
	}
	
	public static JPanel getPairsPanel(String label) {
		return getPairsPanel(label, "");
	}
	
	public static JPanel getPairsPanel(String label, String defaultInputValue) {
		return getPairsPanel(label, new JTextField(defaultInputValue));
	}
	
	public static JPanel getPairsPanel(String label, JComponent input) {
		return getPairsPanel(new JLabel(StrUtils.concat("  [", label, "]:  ")), input);
	}
	
	public static JPanel getPairsPanel(JComponent label, JComponent input) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(label, BorderLayout.WEST);
		panel.add(input, BorderLayout.CENTER);
		return panel;
	}
	
	public static JComponent addBorder(JComponent component) {
		return addBorder(component, "");
	}
	
	public static JComponent addBorder(JComponent component, String borderTitle) {
		component.setBorder(new TitledBorder(""));
		return component;
	}
	
}
