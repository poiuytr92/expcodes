package exp.libs.algorithm.tsp.qant;

import java.util.concurrent.Callable;

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
