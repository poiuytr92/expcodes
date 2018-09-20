package exp.ali.math.question1.bean;

public class Coupon {

	private int match;
	
	private int discount;
	
	private boolean shopLimit;
	
	public Coupon(int match, int discount, boolean shopLimit) {
		this.match = match;
		this.discount = discount;
		this.shopLimit = shopLimit;
	}

	public int getMatch() {
		return match;
	}

	public int getDiscount() {
		return discount;
	}

	public boolean isShopLimit() {
		return shopLimit;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(shopLimit ? "店内" : "平台");
		sb.append("优惠券(满").append(match);
		sb.append("减").append(discount).append(")");
		return sb.toString();
	}
	
}
