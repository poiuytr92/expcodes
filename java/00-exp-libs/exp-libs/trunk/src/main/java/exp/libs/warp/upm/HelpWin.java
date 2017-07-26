package exp.libs.warp.upm;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JToolTip;

import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.PopChildWindow;

public class HelpWin extends PopChildWindow {

	/** serialVersionUID */
	private static final long serialVersionUID = -6529277202660835224L;

	protected HelpWin() {
		super("", LoginWin.WIDTH, 270);
	}
	
	@Override
	protected void initComponents(Object... args) {
		SwingUtils.setNoFrame(this);
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		JToolTip toolTip = new JToolTip();
		toolTip.setTipText(
			"<html>"
				+ "<body bgcolor=\"#99CCDD\">In case you thought that tooltips had to be"
					+ "<p>boring, one line descriptions, the <font color=blue size=+2>Swing!</font> team"
					+ "<p>is happy to shatter your illusions."
					+ "<p>In Swing, you can use <b>HTML</b> to "
					+ "<ul>"
						+ "<li>Have Lists<li><b>Bold</b> text"
						+ "<li><em>emphasized</em>text"
						+ "<li>text with <font color=red>Color</font><li>text in different <font size=+3>sizes</font>"
						+ "<li>and <font face=AvantGarde>Fonts</font>"
					+ "</ul>"
					+ "Oh, and they can be <i>multi-line</i>, too."
				+ "</body>"
			+ "</html>");
		
//		toolTip.setToolTipText("");
		rootPanel.add(toolTip, BorderLayout.CENTER);
	}
	
//	private JComponent createToolTipRegion(String text) {
//        JLabel region = new JLabel(text);
//
//        region.setForeground(Color.white);
//        region.setFont(getFont().deriveFont(18f));
//        region.setHorizontalAlignment(JLabel.CENTER);
//        region.setVerticalAlignment(JLabel.CENTER);
//
//        return region;
//    }

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		// TODO Auto-generated method stub
		
	}

}
