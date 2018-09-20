package exp.ali.math.question1.bean;

public class Goods {

	private String name;
	
	private int price;
	
	public Goods(String name, int price) {
		this.name = name;
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public int getPrice() {
		return price;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name).append("(单价");
		sb.append(price).append(")");
		return sb.toString();
	}
	
}
