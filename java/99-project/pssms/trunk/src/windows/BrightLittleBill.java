package windows;

import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 * 明细账
 */

public class BrightLittleBill extends JFrame {

	private static final long serialVersionUID = -1335870587438739893L;
	private int winWidth = 650;
	private int winHeight = 480;
	private int LocationX = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2 - winWidth/2);
	private int LocationY = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2 - winHeight/2);
	public JPanel mainPanel;

	public BrightLittleBill(String goodsName) {
		super( goodsName );
		
		this.setSize(winWidth, winHeight);
		this.setLocation(LocationX, LocationY);
		this.setResizable(false);
		mainPanel = new JPanel();
		this.getContentPane().add(mainPanel);
	}
}











/**
 * 
 * @software 进销存管理系统
 * 
 * @team 邓伟文， 邝泽徽， 廖权斌 ，罗伟聪
 *
 */