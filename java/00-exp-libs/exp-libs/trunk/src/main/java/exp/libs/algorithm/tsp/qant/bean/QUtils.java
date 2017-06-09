package exp.libs.algorithm.tsp.qant.bean;


/**
 * 
 * <PRE>
 * 量子计算工具
 * </PRE>
 * 
 * @author lqb
 * @date 2017年6月8日
 */
public class QUtils {

	/** 最小精度 */
	private final static double PRECISION = 1.0e-6D;
	
	/** 数学常量π */
	private final static double PI = 3.141592654D;
	
	/** 最小旋转角 */
	private final static double MIN_THETA = 0.001D * PI;
	
	/** 最大旋转角 */
	private final static double MAX_THETA = 0.05D * PI;
	
	/** 旋转角 */
	private final static double DELTA_THETA = MAX_THETA - MIN_THETA;
	
	/**
	 * 计算量子旋转门的旋转角θ
	 * @param beta2 某只量子蚂蚁当前从i->j转移时释放的信息素
	 * @param generation 该量子蚂蚁的代数 与 最大代数 的代数比
	 * @param curQPA 该量子蚂蚁当前从i->j转移的量子编码(当前的路径信息素概率幅)
	 * @param bestQPA 最优解路径概率幅矩阵中，路径i->j的信息素概率幅
	 * @return 旋转角θ
	 */
	public static double getTheta(double beta2, double pGeneration, 
			__QPA curQPA, __QPA bestQPA) {
		double theta = (MAX_THETA - DELTA_THETA * pGeneration) * 
				beta2 * getThetaDirection(curQPA, bestQPA);
		return theta;
	}

	/**
	 * 计算量子旋转角的旋转方向
	 * @param curQPA 某只量子蚂蚁当前从i->j转移的量子编码(当前的路径信息素概率幅)
	 * @param bestQPA 最优解路径概率幅矩阵中，路径i->j的信息素概率幅
	 * @return 顺时针:1; 逆时针:-1
	 */
	private static int getThetaDirection(__QPA curQPA, __QPA bestQPA) {
		double pBest = bestQPA.getBeta() / bestQPA.getAlpha();
		double pCur = curQPA.getBeta() / curQPA.getAlpha();
		double atanBest = Math.atan(pBest);
		double atanCur = Math.atan(pCur);
		int direction = (((pBest / pCur) * (atanBest - atanCur)) >= 0 ? 1 : -1);
		return direction;
	}
	
	//判断浮点数是否为0
	public static boolean isZero(double num) {
		return (Math.abs(num) < PRECISION)? true : false;
	}
		
}
