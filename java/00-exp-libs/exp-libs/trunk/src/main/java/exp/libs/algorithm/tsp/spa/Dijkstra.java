package exp.libs.algorithm.tsp.spa;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Dijkstra {

	public final static int MAX_WEIGHT = Integer.MAX_VALUE;
	
	private int size;
	
	private int[][] matrix;
	
	private int srcId;
	
	private int[] sDists;
	
	private IDPath[] sPaths;
	
	public Dijkstra(int[][] matrix) {
		this.matrix = (matrix == null ? new int[0][0] : matrix);
		this.size = this.matrix.length;
		clear();
	}
	
	private void clear() {
		this.srcId = -1;
		this.sDists = new int[size];
		this.sPaths = new IDPath[size];
	}
	
	private boolean inRange(final int idx) {
		return (idx >= 0 && idx < size);
	}
	
	public boolean calculate(final int srcId) {
		return calculate(srcId, null);
	}
	
	public boolean calculate(final int srcId, Set<Integer> tabu) {
		if(!inRange(srcId)) {
			return false;
		}
		clear();
		this.srcId = srcId;
		tabu = (tabu == null ? new HashSet<Integer>() : tabu);
		for(int i = 0; i < size; i++) {
			sDists[i] = matrix[srcId][i];
		}
		
		boolean[] visit = new boolean[size];
		Arrays.fill(visit, false);
		visit[srcId] = true;
		
		while(true) {
			int next = -1;
			int minDist = MAX_WEIGHT;
			for(int i = 0; i < size; i++) {
				if(!visit[i] && !tabu.contains(i) && sDists[i] < minDist) {
					minDist = sDists[i];
					next = i;
				}
			}
			
			if(next < 0) {
				break;
			} else {
				visit[next] = true;
			}
			
			for(int i = 0; i < size; i++) {
				if(visit[i] || matrix[next][i] == MAX_WEIGHT) {
					continue;
				}
				
				int relex = add(sDists[next], matrix[next][i]);
				if(sDists[i] > relex) {
					sDists[i] = relex;
					if(sPaths[i] == null) {
						sPaths[i] = new IDPath();
					}
					sPaths[i].add(next);
				}
			}
		}
		return true;
	}
	
	private int add(int a, int b) {
		return ((a == MAX_WEIGHT || b == MAX_WEIGHT) ? MAX_WEIGHT : (a + b));
	}
	
	public List<Integer> getShortPaths(final int snkId) {
		List<Integer> routeIds = new LinkedList<Integer>();
		if(inRange(srcId) && inRange(snkId)) {
			int endId = snkId;
			routeIds.add(0, endId);
			do {
				IDPath idPath = sPaths[endId];
				if(idPath != null && !idPath.isEmpty()) {
					List<Integer> ids = idPath.getIds();
					for(int i = ids.size() - 1; i >= 0; i--) {
						int id = ids.get(i);
						routeIds.add(0, id);
						endId = id;
					}
				} else {
					break;	// 源点的IDPath必定为null
				}
			} while(true);
			routeIds.add(0, srcId);
		}
		return routeIds;
	}
	
	public int getShortPathWeight(final int snkId) {
		return (inRange(snkId) ? sDists[snkId] : MAX_WEIGHT);
	}
	
}
