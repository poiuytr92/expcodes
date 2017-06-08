package exp.libs.algorithm.tsp.spa;

import java.util.LinkedList;
import java.util.List;

public class Prim {
	
	public static List<int[]> calculate(int[][] matrix, int srcId) {
		List<int[]> edges = new LinkedList<int[]>();
		int size = matrix.length;
		
		boolean[] visit = new boolean[size];
		int[] sDist = new int[size];	// 源点集到其他点的最小权值
		for(int i = 0; i < size; i++) {
			sDist[i] = matrix[srcId][i];
			visit[i] = false;
		}
		visit[srcId] = true;
		
		int curSrcId = srcId;
		int visitCnt = 1;
		while(visitCnt < size) {
			int minWeight = Integer.MAX_VALUE;
			int nextSrcId = -1;
			for(int i = 0; i < size; i++) {
				if(visit[i] == true) {
					continue;
				}
				
				if(sDist[i] > matrix[curSrcId][i]) {
					sDist[i] = matrix[curSrcId][i];
				}
				
				if(minWeight > sDist[i]) {
					minWeight = sDist[i];
					nextSrcId = i;
				}
			}
			
			if(nextSrcId >= 0) {
				edges.add(new int[] { curSrcId, nextSrcId });
				curSrcId = nextSrcId;
				visit[curSrcId] = true;
				visitCnt++;
			} else {
				break;
			}
		}
		return edges;
	}
	
}
