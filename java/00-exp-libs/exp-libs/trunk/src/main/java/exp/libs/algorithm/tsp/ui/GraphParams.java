package exp.libs.algorithm.tsp.ui;

final class GraphParams {

	/** 节点自身宽度 */
	protected final static int NODE_WIDTH = 80;
	
	/** 节点自身高度 */
	protected final static int NODE_HEIGHT = 30;
	
	/** 节点在X轴方向上的占有范围（避免重叠） */
	protected final static int NODE_X_RANGE = 
			(NODE_WIDTH > NODE_HEIGHT ? NODE_WIDTH : NODE_HEIGHT) + 10;
	
	/** 节点在Y轴方向上的占有范围（避免重叠） */
	protected final static int NODE_Y_RANGE = NODE_X_RANGE;
	
	/** 节点间上边界最小间距 */
	protected final static int NODE_TOP_PAD = 25;
	
	/** 节点间下边界最小间距 */
	protected final static int NODE_BOTTOM_PAD = 25;
	
	/** 节点间左边界最小间距 */
	protected final static int NODE_LEFT_PAD = 25;
	
	/** 节点间右边界最小间距 */
	protected final static int NODE_RIGHT_PAD = 25;
	
}

