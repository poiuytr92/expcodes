package exp.libs.algorithm.tsp.qant;

public class QUtils {

	// FIXME 这个旋转角方向是 当前解 与 最优解的偏差角，不能这样用
	// 当 当前解的beta概率幅 大于 最优参考解时， 旋转角方向为负，反之为正
	/**
	 * 计算量子旋转角的旋转方向
	 * @param curQPA 某只量子蚂蚁当前从i->j转移的量子编码(当前的路径信息素概率幅)
	 * @param bestQPA 最优解路径概率幅矩阵中，路径i->j的信息素概率幅
	 * @return 顺时针:1; 逆时针:-1
	 */
	private int __getThetaDirection(__QPA curQPA, __QPA bestQPA) {
		double pBest = bestQPA.getBeta() / bestQPA.getAlpha();
		double pCur = curQPA.getBeta() / curQPA.getAlpha();
		double atanBest = Math.atan(pBest);
		double atanCur = Math.atan(pCur);
		int direction = (((pBest / pCur) * (atanBest - atanCur)) >= 0 ? 1 : -1);
		return direction;
	}
	
	/**
	 * 使用量子旋转门更新量子编码: 
	 *  挥发除了 cur->last 和 cur->next 以外的与cur相关的路径信息素
	 *  (对称路径 last->cur 信息素 等同于 cur->last 信息素)
	 * @param lastId 上一节点
	 * @param curId 当前节点
	 * @param nextId 下一节点
	 * @param theta 旋转角
	 */
	private void _minusQPAs(int lastId, int curId, int nextId, double theta) {
//		final double cosTheta = Math.cos(-theta);
//		final double sinTheta = Math.sin(-theta);
//
//		for(int j = 0; j < ENV.size(); j++) {
//			if(j == lastId || j == nextId || !ENV.isLinked(curId, j)) {
//				continue;
//			}
//			
//			final double alpha = curRst.QPA(curId, j).getAlpha();
//			final double beta = curRst.QPA(curId, j).getBeta();
//			curRst.QPA(curId, j).setAlpha(cosTheta * alpha - sinTheta * beta);
//			curRst.QPA(curId, j).setBeta(sinTheta * alpha + cosTheta * beta);
//			curRst.QPA(j, curId).setAlpha(curRst.QPA(curId, j).getAlpha());
//			curRst.QPA(j, curId).setBeta(curRst.QPA(curId, j).getBeta());
//		}
	}
	
	/**
	 * 更新移动路径上的量子编码
	 * @param curId
	 * @param nextId
	 * @param pGn
	 * @param bestRst
	 */
	private void updateMoveQPA(int curId, int nextId, double pGn, _QRst bestRst) {
//		double deltaBeta = _getDeltaBeta(curId, nextId, bestRst); // 蚂蚁移动时释放的信息素增量
//		__QPA curQPA = curRst.QPA(curId, nextId);	// 当前解在本次移动时的量子信息素编码
//		__QPA bestQPA = bestRst.QPA(curId, nextId); // 最优解在对应路径上的量子信息素编码(参考值)
//		double theta = _getTheta(pGn, deltaBeta, curQPA, bestQPA); // 计算量子旋转门的旋转角θ
//		
//		_updateQPA(curId, nextId, theta);	// 使用量子旋转门增加本次移动路径上信息素
//		if(ENV.isUseVolatilize()) {		// 对本次移动时 没有被选择的候选路径上的信息素 进行自然挥发
//			_minusQPAs(curRst.getLastId(), curId, nextId, theta);
//		}
	}
	
//	private void addMoveQPA(int curId, int nextId, double pGn, _QRst bestRst) {
//	double deltaBeta = _getDeltaBeta(curId, nextId, bestRst); // 蚂蚁移动时释放的信息素增量
//	__QPA curQPA = curRst.QPA(curId, nextId);	// 当前解在本次移动时的量子信息素编码
//	__QPA bestQPA = bestRst.QPA(curId, nextId); // 最优解在对应路径上的量子信息素编码(参考值)
//	double theta = _getTheta(pGn, deltaBeta, curQPA, bestQPA); // 计算量子旋转门的旋转角θ
//	_updateQPA(curId, nextId, theta);	// 使用量子旋转门增加本次移动路径上信息素
//}
//
//private void minusMoveQPA(int curId, int nextId, double pGn, _QRst bestRst) {
//	double deltaBeta = _getDeltaBeta(curId, nextId, bestRst); // 蚂蚁移动时释放的信息素增量
//	__QPA curQPA = curRst.QPA(curId, nextId);	// 当前解在本次移动时的量子信息素编码
//	__QPA bestQPA = bestRst.QPA(curId, nextId); // 最优解在对应路径上的量子信息素编码(参考值)
//	double theta = _getTheta(pGn, deltaBeta, curQPA, bestQPA); // 计算量子旋转门的旋转角θ
//	_updateQPA(curId, nextId, -theta);	// 使用量子旋转门增加本次移动路径上信息素
//	_updateQPA(curId, nextId, -theta);
//}
	
}
