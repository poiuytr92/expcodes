package exp.libs.mrp.cache;

import java.io.File;

import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 路径树节点
 * </PRE>
 * <B>PROJECT : </B> mojo-release-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-08-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _PathNode {

	/**
	 * 父节�?
	 */
	private _PathNode parent;
	
	/**
	 * 标记自身在整棵路径树的层�?(根节点为�?-1�?)
	 */
	private int level;
	
	/**
	 * 标记自身是否为叶子节�?
	 */
	private boolean isLeaf;
	
	/**
	 * 节点名称
	 */
	private String name;
	
	/**
	 * 节点压缩次数(亦即其所有分支下的叶子数).
	 * 当路径树的相同位置已存在同名节点�?,会压缩成一�?.每压缩一次计�?+1.
	 */
	private int compress;
	
	/**
	 * 构造函�?
	 * @param parent 父节点引�?
	 * @param level 所在路径树的层�?
	 * @param isLeaf 叶子节点标识
	 * @param name 节点名称
	 */
	public _PathNode(_PathNode parent, int level, boolean isLeaf, String name) {
		this.parent = parent;
		this.level = level;
		this.isLeaf = isLeaf;
		this.name = (name == null ? "" : name);	
		this.compress = 1;
	}

	public _PathNode getParent() {
		return parent;
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
	
	public String getPath() {
		String path = getName();
		for(_PathNode parent = this.getParent(); 
				parent != null && parent.getLevel() != -1; 
				parent = parent.getParent()) {
			path = StrUtils.concat(parent.getName(), File.separator, path);
		}
		return path;
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean isSame = true;
		if(obj == null) {
			isSame = false;
			
		} else {
			_PathNode that = (_PathNode) obj;
			
			// 同层
			isSame = (isSame == true ? 
					(this.getLevel() == that.getLevel()) : false);
			
			// 同名
			isSame = (isSame == true ? 
					(this.getName().equals(that.getName())) : false);
			
			// 同祖�?
			if(isSame == true) {
				
				// 向上递归比较
				if(this.getParent() != null && that.getParent() != null) {
					isSame = this.getParent().equals(that.getParent());
					
				// 同时递归到根节点
				} else if(this.getParent() == null && that.getParent() == null) {
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
	public String toInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("Node Info: \r\n");
		sb.append("\tname : ").append(getName()).append("\r\n");
		sb.append("\tlevel : ").append(getLevel()).append("\r\n");
		sb.append("\tisLeaf : ").append(isLeaf()).append("\r\n");
		sb.append("\tcompress : ").append(getCompress()).append("\r\n");
		sb.append("\tpostion : ").append(getPath()).append("\r\n");
		sb.append("----------\r\n");
		return sb.toString();
	}
	
	/**
	 * 打印节点位置
	 * @return 节点位置
	 */
	@Override
	public String toString() {
		return getPath();
	}
}
