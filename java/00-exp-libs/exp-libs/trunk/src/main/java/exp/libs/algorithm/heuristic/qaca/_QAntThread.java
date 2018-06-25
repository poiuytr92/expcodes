package exp.libs.algorithm.heuristic.qaca;

import java.util.concurrent.Callable;

/**
 * <PRE>
 * 量子蚂蚁求解线程
 * </PRE>
 * 
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2017-06-09
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
final class _QAntThread implements Callable<QRst> {

	private final _QAnt qAnt;
	
	private final QRst bestRst;
	
	protected _QAntThread(final _QAnt qAnt, final QRst bestRst) {
		this.qAnt = qAnt;
		this.bestRst = bestRst;
	}

	@Override
	public QRst call() throws Exception {
		qAnt.solve(bestRst);
		return qAnt.getResult();
	}
	
}
