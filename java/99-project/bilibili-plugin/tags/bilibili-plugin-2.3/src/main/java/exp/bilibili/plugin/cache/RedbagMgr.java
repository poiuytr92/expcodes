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
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2018-01-21
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class RedbagMgr extends LoopThread {

	private final static Logger log = LoggerFactory.getLogger(RedbagMgr.class);
	
	private long sleepTime;
	
	/** 期望兑换列表 */
	private List<Redbag> redbags;
	
	/** 是否执行兑换 */
	private boolean exchange;
	
	/**
	 * 是否到了交换时间
	 * 为了避免时差，每个小时的58分开始，尝试到下个小时的02�?
	 */
	private boolean exTime;
	
	/** 本轮手持红包�? */
	private int keepRedbagNum;
	
	/** 本轮奖池信息 */
	private Map<String, Award> pool;
	
	private static volatile RedbagMgr instance;
	
	private RedbagMgr() {
		super("红包兑奖�?");
		this.sleepTime = 1000;
		this.redbags = new LinkedList<Redbag>();
		this.exchange = false;
		this.exTime = false;
		
		this.keepRedbagNum = 0;
		this.pool = new HashMap<String, Award>();
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
		log.info("{} 已启�?", getName());
	}

	@Override
	protected void _loopRun() {
		updateExTime();
		
		if(isExchange() && isExTime()) {
			synchronized (redbags) {
				exchange();
			}
		}
		
		_sleep(sleepTime);
	}

	@Override
	protected void _after() {
		log.info("{} 已停�?", getName());
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
	
	/**
	 * 更新兑奖的执行时间段:
	 * 	从每个小时的55分开�?, 一直持续到下一个小时的02�?
	 */
	public void updateExTime() {
		int minute = TimeUtils.getCurMinute();
		
		if(exTime == false && minute == 55) {
			sleepTime = 1000;
			exTime = true;
			UIUtils.log("红包兑奖时间已到, 正在尝试兑奖...");
			
		} else if(exTime == true && minute == 2) {
			sleepTime = 60000;
			exTime = false;
			UIUtils.log("红包兑奖时间已过, 已停止兑�?.");
		}
	}
	
	/**
	 * 查询奖池.
	 * 	每轮只查询一次奖�?
	 * @return 手持红包�?
	 */
	public int queryPool() {
		pool.clear();
		String response = MsgSender.queryRedbagPool();
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				JSONObject data = JsonUtils.getObject(json, BiliCmdAtrbt.data);
				keepRedbagNum = JsonUtils.getInt(data, BiliCmdAtrbt.red_bag_num, 0);
				JSONArray redbagPool = JsonUtils.getArray(data, BiliCmdAtrbt.pool_list);
				for(int i = 0; i < redbagPool.size(); i++) {
					Award award = new Award(redbagPool.getJSONObject(i));
					pool.put(award.getId(), award);
				}
				
			} else {
				String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				log.warn("查询红包奖池失败: {}", reason);
			}
		} catch(Exception e) {
			log.error("查询红包奖池失败: {}", response, e);
		}
		return keepRedbagNum;
	}
	
	/**
	 * 兑换奖品
	 */
	public void exchange() {
		if(redbags.isEmpty() || queryPool() <= 0) {
			return;
		}
		
		// 根据期望兑换的奖品列表在奖池中进行兑�?
		Iterator<Redbag> redbagIts = redbags.iterator();
		while(redbagIts.hasNext()) {
			Redbag redbag = redbagIts.next();
			Award award = pool.get(redbag.ID());
			
			// 本轮奖池无此奖品
			if(award == null) {
				continue;
				
			// 该奖品在本轮奖池中已无剩�?
			} else if(award.getStockNum() <= 0) {
				continue;
				
			// 该奖品不能被无限兑换, 且已达到该用户的兑换上限
			} else if(award.getExchangeLimit() > 0 && 
					award.getUserExchangeCount() <= 0) {
				continue;
				
			// 用户所持的红包数不足以兑换该奖�?
			} else if(keepRedbagNum < redbag.PRICE()) {
				continue;
			}
			
			// 尽可能多地兑换（若兑换成功则更新手持的红包数量）
			int num = keepRedbagNum / redbag.PRICE();	// 手持红包可以兑换的上�?
			num = (num > award.getUserExchangeCount() ? award.getUserExchangeCount() : num);	//  用户剩余兑换上限
			num = (num > award.getStockNum() ? award.getStockNum() : num);	// 奖池剩余数量
			
			log.info("正在试图兑换 [{}] �? [{}] ...", num, redbag.DESC());
			if(num > 0) {
				if(exchange(redbag, num)) {
					keepRedbagNum -= (redbag.PRICE() * num);
					
				} else {
//					redbagIts.remove();
				}
			}
		}
	}
	
	/**
	 * 兑换礼物
	 * @param redbag 兑换的礼�?
	 * @param num 兑换数量
	 * @return true:兑换成功; false:兑换失败
	 */
	private boolean exchange(Redbag redbag, int num) {
		boolean isOk = false;
		
		String response = MsgSender.exchangeRedbag(redbag.ID(), num);
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
				log.warn("兑换 [{}] 失败: {}", redbag.DESC(), reason);
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
	 * 对期望兑换的红包列表根据兑换价值从高到低排�?
	 * @param redbags 期望兑换的红包礼物列�?
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
