package exp.libs.algorithm.tsp.gen;

import java.util.List;
import java.util.Set;

import exp.libs.algorithm.tsp.graph.Node;
import exp.libs.algorithm.tsp.graph.TopoGraph;
import exp.libs.utils.pub.RandomUtils;

/**
 * 遗传算法：最短路算法
 * <PRE>
 * </PRE>
 * 
 * @author lqb
 * @date 2017年5月19日
 */
public class GenSPA {

	/** 基因最大长度（图节点数） */
	private final static int SIZE = 3000;
	
	/** 繁衍代数上限 */
	private final static int OFFSPRING = 500;
	
	/**
	 * 种群规模（同一代中的最大基因数，亦即可行解数）
	 *  FIXME: 根据总族群计算， 种群越大，越高概率得到最优解，但收敛越慢
	 */
	private final static int SCALE = 10;
	
	// 被搜索的图模型
	private TopoGraph graph;
	
	public GenSPA(TopoGraph graph) {
		this.graph = graph;
	}
	
	/**
	 * 基因繁衍
	 */
	public void breed() {
		Population population = init(SCALE);	// 初始化种群（源宿连通解集, 不一定是可行解）
		for(int i = 0; i < OFFSPRING; i++) {
			if(population.isEmpty()) {	// 种群绝种： 无解
				break;	
			} else {	// 根据父代衍生子代
				System.out.println("第" + i + "代");
				population = next(population);
			}
		}
		
//		scoreFitness(population); // 评分，找到当前代的满足条件的最优解， 校验是否能够收敛
	}
	
	public Population init(int scale) {
		Population population = new Population();
		List<List<Node>> paths = graph.findVaildPaths(scale);
		for(List<Node> path : paths) {
			Unit unit = new Unit(path);
			fitnessScore(unit);
			population.add(unit);
		}
		return population;
	}
	
	// FIXME
	public Population next(Population father) {
		Population child = cross(father);	// 天择：父代交叉繁衍子代（子代族群大小不一定等于父代，保留可行解，直接排除无效解）
		
//		variation(p0);	// 子代变异（变异率前期较大，保证检索范围，后期较小，保证收敛精度。子代仅保留可行解，直接排除无效解）
		
		// FIXME : 中途收敛： 所有必经点被经过，且路径只有必经点，且有序
		// 校验子代个数
		
		return new Population();
	}
	
	/**
	 * 物竟：个体适度评分
	 * @param population
	 */
	public void fitnessScore(Unit unit) {
		int score = 0;
		List<Node> genCode = unit.getGenCode();
		for(int i = 0; i < genCode.size() - 1; i++) {
			Node a = genCode.get(i);
			Node z = genCode.get(i + 1);
			int w = graph.getWeight(a, z);
			if(w == Integer.MAX_VALUE) {
				score = Integer.MAX_VALUE;
				break;
			} else {
				score += w;
			}
		}
		unit.setGenScore(score);
	}
	
	/**
	 * 天择： 根据个体适度，尽可能选择优质解进行交叉繁衍
	 * 	父代交叉繁衍子代（子代族群大小不一定等于父代，保留可行解，直接排除无效解）
	 * @param population
	 */
	public Population cross(Population population) {
		List<Unit> units = population.getAllUnits();
		for(int i = 0; i < units.size(); i++) {
			int fatherIdx = getRandomIdx(units.size(), -1);
			int motherIdx = getRandomIdx(units.size(), fatherIdx);
			
			Unit father = units.get(fatherIdx);
			Unit mother = units.get(motherIdx);
			Unit child = cross(father, mother);
		}
		return null;
	}
	
	private Unit cross(Unit father, Unit mother) {
		Unit child = new Unit(null);
		
		return child;
	}
	
	private int getRandomIdx(int max, int exclude) {
		int idx = exclude;
		while(idx == exclude) {
			idx = RandomUtils.randomInt(max);
		}
		return idx;
	}
	
	public void cross(Set<String> set) {
		
	}
	
	public void variation(Set<String> set) {
		
	}
	
}
