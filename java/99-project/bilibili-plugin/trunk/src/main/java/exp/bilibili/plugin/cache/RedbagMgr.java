package exp.bilibili.plugin.cache;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibili.plugin.bean.ldm.Award;
import exp.bilibili.plugin.core.back.MsgSender;
import exp.bilibili.plugin.envm.BiliCmdAtrbt;
import exp.bilibili.plugin.envm.Redbag;
import exp.bilibili.plugin.utils.TimeUtils;
import exp.bilibili.plugin.utils.UIUtils;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.thread.LoopThread;

/**
 * <PRE>
 * 红包兑奖姬
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-01-21
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class RedbagMgr extends LoopThread {

	private final static Logger log = LoggerFactory.getLogger(RedbagMgr.class);
	
	private final static int SLEEP_TIME = 1000;
	
	private List<Redbag> redbags;
	
	private boolean exchange;
	
	/**
	 * 是否到了交换时间
	 * 为了避免时差，每个小时的58分开始，尝试到下个小时的02分
	 */
	private boolean exTime;
	
	private static volatile RedbagMgr instance;
	
	private RedbagMgr() {
		super("红包兑奖姬");
		this.redbags = new LinkedList<Redbag>();
		this.exchange = false;
		this.exTime = false;
	}
	
	public static RedbagMgr getInstn() {
		if(instance == null) {
			synchronized (RedbagMgr.class) {
				if(instance == null) {
					instance = new RedbagMgr();
				}
			}
		}
		return instance;
	}
	
	@Override
	protected void _before() {
		log.info("{} 已启动", getName());
	}

	@Override
	protected void _loopRun() {
		if(isExchange() && isExTime()) {
			synchronized (redbags) {
				exchange();
			}
		}
		
		updateExTime();
		_sleep(SLEEP_TIME);
	}

	@Override
	protected void _after() {
		log.info("{} 已停止", getName());
	}
	
	public boolean isExchange() {
		return exchange;
	}
	
	public void setExchange() {
		exchange = !exchange;
	}
	
	public boolean isExTime() {
		return exTime;
	}
	
	public void updateExTime() {
		int minute = TimeUtils.getCurMinute();
		
		if(exTime == false && minute == 59) {
			exTime = true;
			log.info("已达到兑奖时间点");
			
		} else if(exTime == true && minute == 1) {
			exTime = false;
			
			log.info("兑奖时间已过");
		}
	}
	
	public void exchange() {
		if(redbags.isEmpty()) {
			return;
		}
		
//		int keepRedbagNum = 0;
//		Map<String, Award> awards = new HashMap<String, Award>();
		
		// 查询本轮奖池
//		boolean isOk = true;
//		String response = MsgSender.queryRedbagPool();
//		try {
//			JSONObject json = JSONObject.fromObject(response);
//			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
//			if(code == 0) {
//				JSONObject data = JsonUtils.getObject(json, BiliCmdAtrbt.data);
//				keepRedbagNum = JsonUtils.getInt(data, BiliCmdAtrbt.red_bag_num, 0);
//				JSONArray redbagPool = JsonUtils.getArray(data, BiliCmdAtrbt.pool_list);
//				for(int i = 0; i < redbagPool.size(); i++) {
//					Award award = new Award(redbagPool.getJSONObject(i));
//					awards.put(award.getId(), award);
//				}
//				
//			} else {
//				isOk = false;
//				String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
//				log.warn("查询红包奖池失败: {}", reason);
//			}
//		} catch(Exception e) {
//			isOk = false;
//			log.error("查询红包奖池失败: {}", response, e);
//		}
//		
//		if(isOk == false) {
//			return;
//		}
		
		// 根据期望兑换的奖品列表在奖池中进行兑换
		Iterator<Redbag> redbagIts = redbags.iterator();
		while(redbagIts.hasNext()) {
			Redbag redbag = redbagIts.next();
//			Award award = awards.get(redbag.ID());
//			
//			// 本轮奖池无此奖品
//			if(award == null) {
//				continue;
//				
//			// 该奖品在本轮奖池中已无剩余
//			} else if(award.getStockNum() <= 0) {
//				continue;
//				
//			// 该奖品不能被无限兑换, 且已达到该用户的兑换上限
//			} else if(award.getExchangeLimit() > 0 && 
//					award.getUserExchangeCount() <= 0) {
//				continue;
//				
//			// 用户所持的红包数不足以兑换该奖品
//			} else if(keepRedbagNum < redbag.PRICE()) {
//				continue;
//			}
			
			// 尽可能多地兑换（若兑换成功则更新手持的红包数量）
//			int num = keepRedbagNum / redbag.PRICE();	// 手持红包可以兑换的上限
//			num = (num > award.getUserExchangeCount() ? award.getUserExchangeCount() : num);	//  用户剩余兑换上限
//			num = (num > award.getStockNum() ? award.getStockNum() : num);	// 奖池剩余数量
			int num = 1;
			
			log.info("正在试图兑换 [{}] 个 [{}] ...", num, redbag.DESC());
			if(num > 0) {
				if(exchange(redbag, num)) {
//					keepRedbagNum -= (redbag.PRICE() * num);
				} else {
					redbagIts.remove();
				}
			}
		}
	}
	
	/**
	 * 兑换礼物
	 * @param redbag 兑换的礼物
	 * @param num 兑换数量
	 * @return true:兑换成功; false:兑换失败
	 */
	private boolean exchange(Redbag redbag, int num) {
		String response = MsgSender.exchangeRedbag(redbag.ID(), num);
		
		boolean isOk = false;
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				isOk = true;
				String msg = StrUtils.concat("成功兑换了[", num, "]个[", redbag.DESC(), "]");
				UIUtils.log(msg);
				log.info(msg);
				
			} else {
				String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				String msg = StrUtils.concat("兑换[", redbag.DESC(), "]失败: ", reason);
				UIUtils.log(msg);
				log.warn(msg);
			}
		} catch(Exception e) {
			log.error("兑换 [{}] 失败: {}", redbag.DESC(), response, e);
		}
		return isOk;
	}
	
	/**
	 * 刷新奖池
	 * @return
	 */
	public boolean reflashPool() {
		return exchange(Redbag.REDBAG_POOL, 1);
	}
	
	/**
	 * 更新兑换目标
	 * @param redbags
	 */
	public void update(List<Redbag> redbags) {
		synchronized (this.redbags) {
			this.redbags.clear();
			this.redbags.addAll(redbags);
			descSort(this.redbags);
		}
	}
	
	/**
	 * 对期望兑换的红包列表根据兑换价值从高到低排序
	 * @param redbags 期望兑换的红包礼物列表
	 */
	private void descSort(List<Redbag> redbags) {
		RedbagComparator rc = new RedbagComparator();
		Collections.sort(redbags, rc);
	}
	
	/**
	 * 红包奖品价值比较器
	 */
	private class RedbagComparator implements Comparator<Redbag> {

		@Override
		public int compare(Redbag rb1, Redbag rb2) {
			return rb2.PRICE() - rb1.PRICE();
		}

	}
	
}
