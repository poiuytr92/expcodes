package exp.bilibili.plugin.ui;

import java.awt.BorderLayout;

import javax.swing.JEditorPane;
import javax.swing.JPanel;

import exp.libs.utils.other.StrUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.NoticeWindow;

/**
 * <PRE>
 * 直播通知面板
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class _NoticeUI extends NoticeWindow {

	/** serialVersionUID */
	private static final long serialVersionUID = -7835582158711626274L;
	
	private final static int WIDTH = 300;
	
	private final static int HEIGHT = 150;
	
	private JEditorPane editor;
	
	public _NoticeUI(int roomId) {
		super("直播通知", WIDTH, HEIGHT, false, roomId);
	}
	
	@Override
	protected void initComponents(Object... args) {
		this.editor = new JEditorPane();
		editor.setEditable(false);
		editor.setContentType("text/html");	// 将编辑框设置为支持html的编辑格�?
		
		if(args != null && args.length > 0) {
			Integer roomId = (Integer) args[0];
			editor.setText(getNoticeText(roomId));
		}
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(SwingUtils.addBorder(editor), BorderLayout.CENTER);
	}

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		// TODO Auto-generated method stub
		
	}
	
	private String getNoticeText(int roomId) {
		String text = StrUtils.concat(
				"<html>", 
					"<body>", 
						"直播�? [<font color='red'>", roomId, "</font>] 开播啦!!!", 
					"</body>", 
				"</html>"
		);
		return text;
	}

}
