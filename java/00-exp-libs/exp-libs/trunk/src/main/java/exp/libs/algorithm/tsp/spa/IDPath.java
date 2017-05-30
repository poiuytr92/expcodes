package exp.libs.algorithm.tsp.spa;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

class IDPath {

	protected final static IDPath NULL = new IDPath();
	
	private List<Integer> ids;
	
	protected IDPath() {
		this.ids = new LinkedList<Integer>();
	}

	protected boolean isEmpty() {
		return ids.isEmpty();
	}
	
	protected List<Integer> getIds() {
		return new ArrayList<Integer>(ids);
	}

	protected void add(int id) {
		if(id >= 0) {
			this.ids.add(id);
		}
	}
	
	protected void clear() {
		this.ids.clear();
	}
	
}
