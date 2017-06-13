package exp.libs.algorithm.tsp.qant;

import java.util.concurrent.Callable;

final class _QAntThread implements Callable<QRst> {

	private _QAnt qAnt;
	
	private QRst bestRst;
	
	protected _QAntThread(_QAnt qAnt, QRst bestRst) {
		this.qAnt = qAnt;
		this.bestRst = bestRst;
	}

	@Override
	public QRst call() throws Exception {
		qAnt.solve(bestRst);
		return qAnt.getResult();
	}
	
}
