package exp.ali.math.question1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.ali.math.question1.bean.Coupon;
import exp.ali.math.question1.bean.Goods;

/**
 * <PRE>
 * Qa：【问题描述】
 * "双十一" 期间，一家电商店铺A有满60返5块的优惠券，可叠加使用
 * （比如，买120块的东西，用两张优惠券，只需付120–5×2 = 110块）。
 * 
 * 此外，电商平台全场提供满299返60的优惠券（可凑单），每单限用一张，可与店铺的优惠券叠加使用
 * （比如，原价299块的一单，最终价格是299−5×4−60 = 219。
 * 原价不满299则不能减去全场折扣60。不足299时，用户可以在别家商店凑单。）
 * 
 * 请问：小明打算在这家店铺买一款250块的耳机和一款600块的音箱，怎么买最划算?
 * </PRE>
 * <br/><B>PROJECT : </B> Ali-Math-Competition
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-09-19
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Qa {

	/**
	 * <PRE>
	 * Qa：【解题思路】
	 *  可以尝试抽象成通用的数学模型去解决。
	 * -----------------------------------------------------------
	 * 
	 * 题目【约束条件】如下：
	 * (1) 存在 [店内优惠券] ：满 y1 减 x1
	 * (2) [店内优惠券] 每张订单可复用
	 * (3) 存在 [平台优惠券] ：满 y2 减 x2
	 * (4) [平台优惠券] 每张订单限用一张
	 * (5) [平台优惠券] 可在 [其他店] 凑单
	 * (6) 题目并没有提及运费问题， 因此假设所有商品拆单或合单的运费代价是相同的
	 * (7) [店内优惠券] 与 [平台优惠券] 可在同一订单使用，并独立计算优惠
	 * 
	 * 【推导】：
	 * 由于(4)(5)(6)条件约束，若在 [店内] 购买若干个商品，那么应该 尽可能每个商品一张订单。
	 * 若订单价格不满足 满减要求，
	 *   则优先考虑 店内合并订单（为了使得(1)利益最大化），
	 *   再考虑 在其他店凑单（为了使得(3)利益最大化），
	 * -----------------------------------------------------------
	 * 
	 * 针对上述前提条件【建模】如下：
	 * 
	 * 先考虑最简单的情况：  若 [仅仅] 在店内购买商品 a， 其价格为 p。
	 * 由于(7)，可以分别计算使用 [店内优惠券] 的折扣q1， 以及使用[平台优惠券] 的折扣q2：
	 * 
	 * 对于 [店内优惠券] 的折扣q1：
	 *   若 p >= y1, 则 q1 = p / y1 * x1
	 *   若 p < y1, 则 q1 = 0
	 *   
	 * 对于 [平台优惠券] 的折扣q2：
	 *   若 p >= y2, 则无需凑单，q2 = x2
	 *   若 p < y2, 则需在其他店凑单，显然凑单代价 c = y2 - p， 此时：
	 *     若 c > x2，则 q2 = 0 (即凑单代价比折扣价大，不应凑单)
	 *     若 c == x2，则 q2 = 0 (即凑单代价等于折扣价，虽然无论凑单与否最终价格是一样的，但凑单商品等于白送，应该凑单)
	 *     若 c < x2， 则 q2 = x2 - c (即凑单代价小于折扣价，应该凑单)
	 *     
	 * 综上，商品a 的最终价格 price = p - q1 - q2  
	 * -----------------------------------------------------------
	 * 
	 * 因为题目所求的耳机和音箱，单件价值已经同时满足店内满减和平台满减，
	 * 所以是不需要考虑合并订单的问题的。
	 * 到此，这题其实已经解出来了，为了竞赛时间，不再往下推导。
	 * 
	 * 下面是解题代码：
	 * </PRE>
	 */
	public static void main(String[] args) {
		Goods headset = new Goods("耳机", 250);
		Goods speaker = new Goods("音箱", 600);
		int total = buy(headset) + buy(speaker);
		log.info("最优总价为: [{}]", total);
		
//		结果：
//		[耳机(单价250)] 可使用 [4] 张 [店内优惠券(满60减5)], 共优惠 [20]
//		[耳机(单价250)] 可通过凑单 [49] 使用 [平台优惠券(满299减60)], 共优惠 [11]
//		[音箱(单价600)] 可使用 [10] 张 [店内优惠券(满60减5)], 共优惠 [50]
//		[音箱(单价600)] 可使用 [店内优惠券(满60减5)], 共优惠 [60]
//		最优总价为: [709]
	}
	
	private final static Logger log = LoggerFactory.getLogger(Qa.class);
	
	/** 店内优惠券（满60减5） */
	private final static Coupon SHOP_COUPON = new Coupon(60, 5, true);
	
	/** 平台优惠券（满299减60） */
	private final static Coupon PLATFORM_COUPON = new Coupon(299, 60, false);
	
	/**
	 * 购买商品
	 * @param goods
	 * @return
	 */
	public static int buy(Goods goods) {
		if(goods == null) {
			return 0;
			
		} else if(goods.getPrice() <= 0) {
			log.info("[{}] 是免费赠品, 不享受优惠", goods.toString());
			return 0;
		}
		
		// 店内折扣
		int couponNum = goods.getPrice() / SHOP_COUPON.getMatch();
		int shopDiscount = couponNum * SHOP_COUPON.getDiscount();
		log.info("[{}] 可使用 [{}] 张 [{}], 共优惠 [{}]", goods.toString(), 
				couponNum, SHOP_COUPON.toString(), shopDiscount);
		
		// 平台折扣
		int platformDiscount = 0;
		if(goods.getPrice() >= PLATFORM_COUPON.getMatch()) {
			platformDiscount = PLATFORM_COUPON.getDiscount();
			log.info("[{}] 可使用 [{}], 共优惠 [{}]", goods.toString(), 
					SHOP_COUPON.toString(), platformDiscount);
			
		} else {
			int cost = PLATFORM_COUPON.getMatch() - goods.getPrice(); // 凑单代价
			if(cost <= PLATFORM_COUPON.getDiscount()) {
				platformDiscount = PLATFORM_COUPON.getDiscount() - cost;
				log.info("[{}] 可通过凑单 [{}] 使用 [{}], 共优惠 [{}]", goods.toString(), 
						cost, PLATFORM_COUPON.toString(), platformDiscount);
			}
		}
		
		return goods.getPrice() - shopDiscount - platformDiscount;
	}
	
}
