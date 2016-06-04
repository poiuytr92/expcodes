package windows;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EtchedBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/*
 * 帮助面板
 */
public class HelpWindow extends JFrame {

	private static final long serialVersionUID = 400064019879131866L;

	private JPanel mainPanel;
	
	public HelpWindow(int winWidth, int winHeight) {
		this.setUndecorated(true);	//窗口无边框
		this.setSize(winWidth, winHeight);
		
		mainPanel = new JPanel();
		mainPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		this.getContentPane().add(mainPanel);
		mainPanel.setLayout(new BorderLayout());
		
		setInfo();
	}

	private void setInfo() {
		JTextPane loginInfoTP = new JTextPane();
		loginInfoTP.setBackground(new Color(225, 225, 225));
		loginInfoTP.setEditable(false);
		mainPanel.add(loginInfoTP, "Center");
		
//		appendText(loginInfoTP, "\n", Color.black, false, 12);
		appendText(loginInfoTP, "  默认账户：\n", Color.black, true, 12);
		appendText(loginInfoTP, "      账户：jingli，密码：1，身份：经理\n", Color.black, false, 12);
		appendText(loginInfoTP, "      账户：caigou，密码：1，身份：采购员\n", Color.black, false, 12);
		appendText(loginInfoTP, "      账户：cangguan，密码：1，身份：仓管员\n", Color.black, false, 12);
		appendText(loginInfoTP, "      账户：xiaoshou，密码：1，身份：销售员\n", Color.black, false, 12);
		appendText(loginInfoTP, "\n", Color.black, false, 12);
		appendText(loginInfoTP, "  进销存管理系统 - 制作团队：\n", Color.black, true, 12);
		appendText(loginInfoTP, "      队长：邓伟文，联系QQ：419961445\n", Color.black, false, 12);
		appendText(loginInfoTP, "      队员：邝泽徽，联系QQ：235176304\n", Color.black, false, 12);
		appendText(loginInfoTP, "      队员：廖权斌，联系QQ：289065406\n", Color.black, false, 12);
		appendText(loginInfoTP, "      队员：罗伟聪，联系QQ：408390184\n", Color.black, false, 12);
		return;
	}
	
	//往JTextPane追加文字
    public void appendText(JTextPane textPane, String appendText, Color color, boolean isBold, int fontSize) {
    	
        SimpleAttributeSet sas = new SimpleAttributeSet();	//设置属性集
        StyleConstants.setForeground(sas, color);			//字体颜色
        if(isBold == true){									//加粗
            StyleConstants.setBold(sas, true);   
        }
        StyleConstants.setFontSize(sas, fontSize);			//字体大小
        
        Document doc = textPane.getDocument();
        try   {   
            doc.insertString(doc.getLength(), appendText, sas);   
        }   
        catch   (BadLocationException   e)   {   
        	e.printStackTrace();
        } 
    }
}



/**
 * 
 * @software 进销存管理系统
 * 
 * @team 邓伟文， 邝泽徽， 廖权斌 ，罗伟聪
 *
 */