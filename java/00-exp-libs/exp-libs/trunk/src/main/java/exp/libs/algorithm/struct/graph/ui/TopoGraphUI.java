package exp.libs.algorithm.struct.graph.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.DirectedGraphLayout;
import org.jgraph.JGraph;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;

import com.realpersist.gef.layout.NodeJoiningDirectedGraphLayout;

import exp.libs.algorithm.struct.graph.Edge;
import exp.libs.algorithm.struct.graph.Node;
import exp.libs.algorithm.struct.graph.TopoGraph;
import exp.libs.utils.ui.SwingUtils;
import exp.libs.utils.ui.win.PopChildWindow;

/**
 * 
 * <PRE>
 * 拓扑图呈现界面
 * </PRE>
 * 
 * @author lqb
 * @date 2017年5月25日
 */
public class TopoGraphUI extends PopChildWindow {

	/** serialVersionUID */
	private static final long serialVersionUID = -8326111607034563163L;

	/** 建议最大的节点数（超过这个数量演算速度会极慢, 导致陷入长时间无法生成拓扑图的假死状态） */
	private final static int MAX_NODE_NUM = 100;
	
	/** 源端颜色 */
	private final static Color COLOR_SRC = Color.BLUE;
	
	/** 宿端颜色 */
	private final static Color COLOR_SNK = Color.MAGENTA;
	
	/** 必经点颜色 */
	private final static Color COLOR_INCLUSIVE = Color.ORANGE;
	
	/** 普通节点颜色 */
	private final static Color COLOR_NORMAL = Color.GREEN;
	
	/** 拓扑图数据 */
	private TopoGraph graphData;
	
	/** 拓扑图展示模型 */
	private GraphModel graphViewModel;
	
	private JGraph jGraph;
	
	public TopoGraphUI(String name, int width, int high, TopoGraph topoGraph) {
		super(name, width, high, false, topoGraph);
	}

	@Override
	protected void initComponents(Object... args) {
		if(args != null && args.length == 1) {
			this.graphData = (TopoGraph) args[0];
		} else {
			this.graphData = new TopoGraph();
		}
		this.graphViewModel = new DefaultGraphModel();
		this.jGraph = new JGraph(graphViewModel);
		this.jGraph.setJumpToDefaultPort(true);
		this.jGraph.setSelectionEnabled(true);
	}

	@Override
	protected void setComponentsLayout(JPanel root) {
		paintGraph(graphData);	// 绘图
		
		JPanel tips = new JPanel(new FlowLayout()); {
			JLabel srcTips = new JLabel("[ ■ : 源端 ]");
			srcTips.setForeground(COLOR_SRC);
			tips.add(srcTips);
			
			JLabel snkTips = new JLabel("[ ■ : 宿端 ]");
			snkTips.setForeground(COLOR_SNK);
			tips.add(snkTips);
			
			JLabel inclusiveTips = new JLabel("[ ■ : 必经节点 ]");
			inclusiveTips.setForeground(COLOR_INCLUSIVE);
			tips.add(inclusiveTips);
			
			JLabel normalTips = new JLabel("[ ■ : 普通节点 ]");
			normalTips.setForeground(COLOR_NORMAL);
			tips.add(normalTips);
		}
		rootPanel.add(tips, BorderLayout.NORTH);
		rootPanel.add(SwingUtils.addAutoScroll(jGraph), BorderLayout.CENTER);
	}

	@Override
	protected void setComponentsListener(JPanel root) {}
	
	/**
	 * 绘图
	 * @param graphData 拓扑图数据
	 */
	private void paintGraph(TopoGraph graphData) {
		if(graphData == null || graphData.isEmpty()) {
			SwingUtils.warn("输入的拓扑图为空, 无法演算.");
			return;
			
		} else if(graphData.nodeSize() > MAX_NODE_NUM) {
			if(!SwingUtils.confirm("输入的拓扑图规模过大(NODE=" + graphData.nodeSize() 
					+ "), 可能导致演算过程假死, 是否继续?")) {
				return;
			}
		}
		
//		List<GraphEdge> graphEdges = calculatePosition(		// 计算坐标
//				graphData.getAllEdges(), graphData.getIncludeNames(), 
//				graphData.getSrc(), graphData.getSnk());
		
		List<GraphEdge> graphEdges = calculatePosition(graphData);
		createViewModel(graphEdges, graphData.isArrow());	// 绘制视图
	}

	private List<GraphEdge> calculatePosition(TopoGraph graphData) {
		final int XOFFSET = 400;//屏幕中心
		final int YOFFSET = 400;
		
		int size = graphData.nodeSize();
		Node src = graphData.getSrc();
		
		boolean[] visit = new boolean[size];
		Arrays.fill(visit, false);
		visit[src.getId()] = true;
		
		Map<String, GraphNode> map = new HashMap<String, GraphNode>();
		GraphNode srcNode = new GraphNode(src.getName(), 0 + XOFFSET, 0 + YOFFSET);
		map.put(src.getName(), srcNode);
		
		
		
		List<GraphEdge> graphEdges = new LinkedList<GraphEdge>();
		List<Node> queue = new ArrayList<Node>(size);
		queue.add(src);
		
		for(int idx = 0; idx < size; idx++) {
			Node cur = queue.get(idx);
			List<Node> neighbors = cur.getNeighborList();
			final int num = neighbors.size();
			final int baseTheta = 360 / num;
			for(int i = 0; i < num; i++) {
				Node neighbor = neighbors.get(i);
				if(visit[neighbor.getId()]) {
					continue;
				}
				
				queue.add(neighbor);
				visit[neighbor.getId()] = true;
				
				int cost = graphData.getWeight(cur, neighbor);//归一化函数处理边权
				int r = cost * 50;
				int theta = baseTheta * i;
				int x = (int) (r * Math.cos(theta) + 0.5 + XOFFSET);
				int y = (int) (r * Math.sin(theta) + 0.5 + YOFFSET);
				GraphNode neighborNode = new GraphNode(neighbor.getName());
				map.put(neighbor.getName(), neighborNode);
				
				neighborNode.setX(x);
				neighborNode.setY(y);
				GraphEdge edge = new GraphEdge(map.get(cur.getName()), neighborNode, cost);
				graphEdges.add(edge);
			}
		}
		return graphEdges;
	}
	
	/**
	 * 利用GEF框架内置功能自动计算拓扑图各个节点的XY坐标
	 *  (当节点数超过50时，计算非常慢)
	 *  
	 * @param edges 理论拓扑图的抽象边集（每条边的源宿节点只有边权衡量的相对距离）
	 * @param includes 必经点名称集
	 * @param graphSrc 理论拓扑图的源点
	 * @param graphSnk 理论拓扑图的宿点
	 * @return 用于实际呈现的拓扑图边集（每条边的源宿节点具有实际的XY坐标值）
	 */
	@SuppressWarnings("unchecked")
	private List<GraphEdge> calculatePosition(Set<Edge> edges, 
			Set<String> includes, Node graphSrc, Node graphSnk) {
		DirectedGraph graphCalculator = new DirectedGraph(); // 拓扑图点边坐标计算器
		List<GraphEdge> graphEdges = new LinkedList<GraphEdge>();
		Map<String, GraphNode> uniqueNodes = // 唯一性点集，避免重复放入同一节点到GEF造成拓扑图离散
				new HashMap<String, GraphNode>();
		
		// 枚举每条边的源宿点，存储到拓扑图的坐标计算模型
		for(Edge edge : edges) {
			Node src = edge.getSrc();
			GraphNode gnSrc = uniqueNodes.get(src.getName());
			if(gnSrc == null) {
				gnSrc = new GraphNode(src.toString());
				graphCalculator.nodes.add(gnSrc.getGefNode());	// 源端放入GEF模型
				uniqueNodes.put(src.getName(), gnSrc);
				
				// 标记是否为拓扑图的源宿点/必经点
				if(graphSrc.equals(src)) { gnSrc.markGraphSrc(); }
				if(graphSnk.equals(src)) { gnSrc.markGraphSnk(); }
				if(includes.contains(src.getName())) { gnSrc.markInclusive(); }
			}
			
			Node snk = edge.getSnk();
			GraphNode gnSnk = uniqueNodes.get(snk.getName());
			if(gnSnk == null) {
				gnSnk = new GraphNode(snk.toString());
				graphCalculator.nodes.add(gnSnk.getGefNode());	// 宿端放入GEF模型
				uniqueNodes.put(snk.getName(), gnSnk);
				
				// 标记是否为拓扑图的源宿点/必经点
				if(graphSrc.equals(snk)) { gnSnk.markGraphSrc(); }
				if(graphSnk.equals(snk)) { gnSnk.markGraphSnk(); }
				if(includes.contains(snk.getName())) { gnSnk.markInclusive(); }
			}
			
			GraphEdge graphEdge = new GraphEdge(gnSrc, gnSnk, edge.getWeight());
			graphCalculator.edges.add(graphEdge.getGefEdge());	// 边放入GEF模型
			graphEdges.add(graphEdge);
		}
		uniqueNodes.clear();
		
		
		// 自动计算GEF模型内各个点边的坐标
		try {
			// 仅适用于连通图的自动布局（推荐，计算效果最好， 但非连通图会抛出异常）
			new DirectedGraphLayout().visit(graphCalculator);
			
		} catch(Throwable e){
			try {
				// 适用于非连通图（原理是填充虚拟边使图连通后再计算，最后删除虚拟边，但效果略差）
				new NodeJoiningDirectedGraphLayout().visit(graphCalculator);
				
			} catch(Throwable ex) {
				SwingUtils.error("计算拓扑图坐标失败", ex);
			}
		}
		return graphEdges;
	}
	
	/**
	 * 创建拓扑图的展示模型
	 * @param graphEdges 拓扑图边集（每条边的源宿节点具有实际的XY坐标值）
	 * @param arrow 是否为有向图
	 */
	private void createViewModel(List<GraphEdge> graphEdges, boolean arrow) {
		Map<DefaultGraphCell, Object> graphAttribute = 
				new Hashtable<DefaultGraphCell, Object>();	// 拓扑图属性集
		final Map<DefaultGraphCell, Object> EDGE_ATTRIBUTE = 
				getEdgeAttribute(arrow); // 边属性集（所有边可共用同一个属性集）
		
		// 设置每条边的 点、边 属性， 并写到 拓扑图展示模型
		for(GraphEdge graphEdge : graphEdges) {
			GraphNode gnSrc = graphEdge.getSrc();
			GraphNode gnSnk = graphEdge.getSnk();
			
			DefaultEdge viewEdge = graphEdge.getCellEdge();
			DefaultGraphCell viewSrc = gnSrc.getCellNode();
			DefaultGraphCell viewSnk = gnSnk.getCellNode();
			
			// 设置边、点属性
			graphAttribute.put(viewEdge, EDGE_ATTRIBUTE);
			graphAttribute.put(viewSrc, getNodeAttribute(gnSrc));
			graphAttribute.put(viewSnk, getNodeAttribute(gnSnk));
			
			// 把边、点约束集写到展示模型
			ConnectionSet connSet = new ConnectionSet(viewEdge, 
					viewSrc.getChildAt(0), viewSnk.getChildAt(0));
			Object[] cells = new Object[] { viewEdge, viewSrc, viewSnk };
			graphViewModel.insert(cells, graphAttribute, connSet, null, null);
		}
	}
	
	/**
	 * 获取边属性集（所有边可共用同一个属性集）
	 * @param arrow 是否存在方向
	 * @return
	 */
	private Map<DefaultGraphCell, Object> getEdgeAttribute(boolean arrow) {
		Map<DefaultGraphCell, Object> edgeAttribute = 
				new Hashtable<DefaultGraphCell, Object>();
		GraphConstants.setLineColor(edgeAttribute, Color.LIGHT_GRAY);	// 线体颜色
		if(arrow == true) {
			GraphConstants.setLineEnd(edgeAttribute, GraphConstants.ARROW_CLASSIC);	// 线末增加箭头样式
			GraphConstants.setEndFill(edgeAttribute, true);	// 实心箭头
		}
		return edgeAttribute;
	}
	
	/**
	 * 获取节点属性集
	 * @param node 拓扑图节点
	 * @return
	 */
	private Map<DefaultGraphCell, Object> getNodeAttribute(GraphNode node) {
		Map<DefaultGraphCell, Object> nodeAttribute = 
				new Hashtable<DefaultGraphCell, Object>();
		
		final int OFFSET_Y = 10;	// Y轴方向的坐标偏移量（主要为了生成的拓扑图不要贴近X轴）
		Rectangle2D bound = new Rectangle2D.Double(
				node.getY(), (node.getX() + OFFSET_Y), // 节点左上角的顶点坐标（反转XY是为了使得拓扑图整体成横向呈现）
				node.getWidth(), node.getHeight());	// 强制设定所呈现节点的宽高
		GraphConstants.setBounds(nodeAttribute, bound);	// 设置节点坐标
		
		// 设置节点边框颜色
		Color backGround = (node.isGraphSrc() ? COLOR_SRC : 
				(node.isGraphSnk() ? COLOR_SNK : 
				(node.isInclusive() ? COLOR_INCLUSIVE : COLOR_NORMAL)));
		GraphConstants.setBorderColor(nodeAttribute, backGround);
		return nodeAttribute;
	}
	
}