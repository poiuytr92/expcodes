package exp.libs.algorithm.tsp.qant;

import java.util.concurrent.Callable;

final class _QAntThread implements Callable<_QRst> {

	private _QAnt qAnt;
	
	private _QRst bestRst;
	
	protected _QAntThread(_QAnt qAnt, _QRst bestRst) {
		this.qAnt = qAnt;
		this.bestRst = bestRst;
	}

	@Override
	public _QRst call() throws Exception {
		qAnt.solve(bestRst);
		return qAnt.getResult();
	}
	
}
