package exp.bilibili.plugin.core.front;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI.NormalColor;

import exp.bilibili.plugin.cache.RedbagMgr;
import exp.bilibili.plugin.envm.Redbag;
import exp.bilibili.plugin.utils.UIUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.ui.BeautyEyeUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.cbg.CheckBoxGroup;
import exp.libs.warp.ui.cpt.win.PopChildWindow;

/**
 * <PRE>
 * 红包抢兑窗口
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _RedbagUI extends PopChildWindow {

	/** serialVersionUID */
	private static final long serialVersionUID = -5691969159309932864L;

	private final static int WIDTH = 550;
	
	private final static int HEIGHT = 600;
	
	private JButton exchangeBtn;
	
	private JButton reflashBtn;
	
	private CheckBoxGroup<Redbag> redbags;
	
	protected _RedbagUI() {
		super("红包奖池", WIDTH, HEIGHT);
	}
	
	@Override
	protected void initComponents(Object... args) {
		this.exchangeBtn = new JButton("自动兑换 (多选则按花费降序尽可能兑换)");
		this.reflashBtn = new JButton("刷新奖池");
		exchangeBtn.setForeground(Color.BLACK);
		reflashBtn.setForeground(Color.BLACK);
		
		this.redbags = new CheckBoxGroup<Redbag>(new Redbag[] {
				Redbag.SILVER, Redbag.B_CLOD, Redbag.MEOW, 
				Redbag.LANTERN, Redbag.SPRING, Redbag.STUFF1, 
				Redbag.STUFF2, Redbag.STUFF3, Redbag.FIRECRACKER, 
				Redbag.BEAST, Redbag.DOG, Redbag.GUARD, 
				Redbag.GOLD_DANMU, Redbag.GOLD_NAME, Redbag.CALENDAR
		});
	}
	
	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(SwingUtils.addBorder(
				SwingUtils.getEBorderPanel(exchangeBtn, reflashBtn)), BorderLayout.NORTH);
		rootPanel.add(SwingUtils.addBorder(redbags.toVGridPanel(), "兑换列表"), BorderLayout.CENTER);
	}

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		exchangeBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				RedbagMgr.getInstn().setExchange();
				
				if(RedbagMgr.getInstn().isExchange()) {
					redbags.setEnable(false);
					RedbagMgr.getInstn().update(redbags.getItems(true));
					RedbagMgr.getInstn()._start();
					BeautyEyeUtils.setButtonStyle(NormalColor.lightBlue, exchangeBtn);
					UIUtils.log("[红包抽奖姬] 被召唤成功O(∩_�?)O");
					
				} else {
					redbags.setEnable(true);
					BeautyEyeUtils.setButtonStyle(NormalColor.normal, exchangeBtn);
					UIUtils.log("[红包抽奖姬] 被封印啦/(ㄒo�?)/");
				}
			}
		});
		
		reflashBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(SwingUtils.confirm(StrUtils.concat("确认刷新 ? (需花费", 
						Redbag.REDBAG_POOL.PRICE(), "红包, 刷新后马上兑�?)"))) {
					
					if(RedbagMgr.getInstn().reflashPool()) {
						RedbagMgr.getInstn().update(redbags.getItems(true));
						RedbagMgr.getInstn().exchange();
						SwingUtils.info("刷新奖池成功");
						
					} else {
						SwingUtils.info("刷新奖池失败");
					}
				}
			}
		});
	}

	@Override
	protected void AfterView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void beforeHide() {
		// TODO Auto-generated method stub
		
	}
	
}
