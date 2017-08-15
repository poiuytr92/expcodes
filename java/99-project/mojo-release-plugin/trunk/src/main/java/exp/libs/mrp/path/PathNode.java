package exp.libs.mrp.path;

import java.io.File;

/**
 * <PRE>
 * 路径树节点
 * </PRE>
 * <B>项    目：</B>凯通J2SE开发平台(KTJSDP)
 * <B>技术支持：</B>广东凯通软件开发技术有限公司 (c) 2014
 * @version   1.0 2014-9-19
 * @author    廖权斌：liaoquanbin@gdcattsoft.com
 * @since     jdk版本：jdk1.6
 */
public class PathNode {

	/**
	 * 父节点
	 */
	private PathNode fNode;
	
	/**
	 * 标记自身在整棵路径树的层数(根节点为第-1层)
	 */
	private int level;
	
	/**
	 * 标记自身是否为叶子节点
	 */
	private boolean isLeaf;
	
	/**
	 * 节点名称
	 */
	private String name;
	
	/**
	 * 节点压缩次数(亦即其所有分支下的叶子数).
	 * 当路径树的相同位置已存在同名节点时,会压缩成一个.每压缩一次计数+1.
	 */
	private int compress;
	
	/**
	 * 构造函数
	 * @param fNode 父节点引用
	 * @param level 所在路径树的层数
	 * @param isLeaf 叶子节点标识
	 * @param name 节点名称
	 */
	public PathNode(PathNode fNode, int level, boolean isLeaf, String name) {
		this.fNode = fNode;
		this.level = level;
		this.isLeaf = isLeaf;
		this.name = (name == null ? "" : name);	//保证名字不为null
		this.compress = 1;
	}

	public PathNode getFNode() {
		return fNode;
	}

	public int getLevel() {
		return level;
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public String getName() {
		return name;
	}

	public int getCompress() {
		return compress;
	}

	public void addCompress() {
		compress++;
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean isSame = true;
		if(obj == null) {
			isSame = false;
			
		} else {
			PathNode that = (PathNode) obj;
			
			// 同层
			isSame = (isSame == true ? 
					(this.getLevel() == that.getLevel()) : false);
			
			// 同名
			isSame = (isSame == true ? 
					(this.getName().equals(that.getName())) : false);
			
			// 同祖先
			if(isSame == true) {
				
				// 向上递归比较
				if(this.getFNode() != null && that.getFNode() != null) {
					isSame = this.getFNode().equals(that.getFNode());
					
				// 同时递归到根节点
				} else if(this.getFNode() == null && that.getFNode() == null) {
					isSame = true;
				
				// 其中一个递归到根节点
				} else {
					isSame = false;
				}
			}
		}
		return isSame;
	}
	
	/**
	 * 打印节点信息
	 * @return 节点信息
	 */
	public String toPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append("Node Info: \r\n");
		sb.append("\tname : ").append(getName()).append("\r\n");
		sb.append("\tlevel : ").append(getLevel()).append("\r\n");
		sb.append("\tisLeaf : ").append(isLeaf()).append("\r\n");
		sb.append("\tcompress : ").append(getCompress()).append("\r\n");
		sb.append("\tpostion : ");
		
		String path = getName();
		for(PathNode fNode = this.getFNode(); 
				fNode != null && fNode.getLevel() != -1; 
				fNode = fNode.getFNode()) {
			path = fNode.getName() + File.separator + path;
		}
		sb.append(path).append("\r\n");
		sb.append("----------\r\n");
		return sb.toString();
	}
	
	/**
	 * 打印节点位置
	 * @return 节点位置
	 */
	@Override
	public String toString() {
		String path = getName();
		for(PathNode fNode = this.getFNode(); 
				fNode != null && fNode.getLevel() != -1; 
				fNode = fNode.getFNode()) {
			path = fNode.getName() + File.separator + path;
		}
		return path;
	}
}
