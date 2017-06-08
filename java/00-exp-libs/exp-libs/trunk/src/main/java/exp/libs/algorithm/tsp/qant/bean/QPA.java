package exp.libs.algorithm.tsp.qant.bean;

import exp.libs.algorithm.tsp.qant.InitParams;

/**
 * Quantum Probability Amplitude
 * 量子概率幅（量子比特，路径信息素的概率幅）
 *  量子态|ψ> = α|0> + β|1>  
 *  (其中α^2 + β^2 = 1, 含义为对量子态|ψ>测量时，有α^2的概率坍缩到基态|0>，有β^2的概率坍缩到基态|1>)
 */
public class QPA {

	/** 路径信息素的α概率幅  */
	private double alpha;
	
	/** 路径信息素的β概率幅  */
	private double beta;
	
	public QPA() {
		this.alpha = InitParams.QPA;
		this.beta = InitParams.QPA;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public double getBeta() {
		return beta;
	}

	public void setBeta(double beta) {
		this.beta = beta;
	}
	
}
