package exp.libs.algorithm.tsp.gen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// 种群
public class Population {

	private Set<Unit> units;
	
	public Population() {
		this.units = new HashSet<Unit>();
	}
	
	public boolean isEmpty() {
		return units.isEmpty();
	}
	
	public boolean add(Unit unit) {
		boolean isOk = false;
		if(unit != null) {
			isOk = units.add(unit);
		}
		return isOk;
	}
	
	public List<Unit> getAllUnits() {
		return new ArrayList<Unit>(units);
	}
	
}
