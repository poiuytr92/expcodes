package exp.libs.algorithm.graph.spa;

import exp.libs.algorithm.graph.adt.AdjacencyMatrix;


abstract class AbstractSPA {

	protected final static int GT = 1;
	
	protected final static int LT = -1;
	
	protected final static int EQ = 0;
	
	protected AdjacencyMatrix matrix;

	public AbstractSPA(AdjacencyMatrix matrix) {
		this.matrix = matrix;
	}
	
	protected int compare(Double a, Double b) {
		if(a == null && b != null) {
			return GT;
			
		} else if(a != null && b == null) {
			return LT;
			
		} else if(a == null && b == null) {
			return EQ;
			
		} else {
			double diff = a - b;
			return (diff > 0 ? GT : (diff < 0 ? LT : EQ));
		}
	}
	
	protected Double add(Double a, Double b) {
		if(a == null || b == null) {
			return null;
			
		} else {
			return (a + b);
		}
	}
	
	public void print() {
		matrix.print();
	}
	
}
