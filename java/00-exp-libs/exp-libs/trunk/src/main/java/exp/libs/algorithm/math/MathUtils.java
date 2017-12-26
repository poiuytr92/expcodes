package exp.libs.algorithm.math;

public class MathUtils {

	protected MathUtils() {}
	
	public static void swap(int a, int b) {
		System.out.println(a);
		System.out.println(b);
		a = a ^ b;
		b = a ^ b;
		a = a ^ b;
		System.out.println(a);
		System.out.println(b);
	}
	
	public static void main(String[] args) {
		swap(4, 6);
	}
	
}
