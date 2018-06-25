package exp.xp.utils;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.dom4j.Element;

import exp.xp.bean.Node;
import exp.xp.ui.Error;

/**
 * <PRE>
 * UI工具类
 * </PRE>
 * <B>PROJECT : </B> exp-xml-paper
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2015-06-01
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class UIUtils {

	public static void info(String msg) {
		JOptionPane.showMessageDialog(
			    null, msg, "INFO", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static void warn(String msg) {
		JOptionPane.showMessageDialog(
			    null, msg, "WARN", JOptionPane.WARNING_MESSAGE);
	}
	
	public static void error(String msg, Throwable e) {
		Object[] options = {"Close", "Details >>"};
		int option = JOptionPane.showOptionDialog(null, 
				msg, "ERROR", JOptionPane.OK_CANCEL_OPTION, 
				JOptionPane.ERROR_MESSAGE, 
				null, options, options[1]);
			
		if (1 == option) {
			new Error().display(e);
		}
	}
	
	public static boolean confirm(String msg) {
		int option = JOptionPane.showConfirmDialog(null, 
				msg, "CONFIRM", JOptionPane.YES_NO_OPTION);
		return option == JOptionPane.OK_OPTION;
	}
	
	/**
	 * 从JTree的指定节点开始，生成该节点的子树所对应的xml报文.
	 * 
	 * @param treeNode JTree的节�?
	 * @param xmlNode xml的节�?
	 */
	@SuppressWarnings("unchecked")
	public static void createXmlTree(DefaultMutableTreeNode treeNode, Element xmlNode) {
		if(treeNode == null || xmlNode == null) {
			return;
		}
		
		Node node = standardize((Node) treeNode.getUserObject());
		xmlNode.setName(node.getName());
		xmlNode.setText(node.getText());
		
		for(Iterator<String> attributes = node.getAttributeKeys().iterator();
				attributes.hasNext();) {
			String attribute = attributes.next();
			String value = node.getAttributeVal(attribute);
			xmlNode.addAttribute(attribute, value);
		}
		
		final String tmpChildName = "_tmp_child_name_";
		for(Enumeration<DefaultMutableTreeNode> childs = treeNode.children(); 
				childs.hasMoreElements();) {
			DefaultMutableTreeNode treeChildNode = childs.nextElement();
			xmlNode.addElement(tmpChildName);
			Element xmlChildNode = xmlNode.element(tmpChildName);
			createXmlTree(treeChildNode, xmlChildNode);
	    }
	}
	
	/**
	 * 标准化节点内�?.
	 * 主要处理�?:
	 * 1.去除无效属�?: 目前主要是界面用于提示的 NEW ATTRIBUTE 属�?
	 * 2.标准化属性名: 把节点属性中，属性名除了 [字母][下划线][数字] 之外的所有字符替换为下划�?. 且若以数字开�?,在开头补下划�?.
	 * 3.标准化节点名: 把节点名称中，除�? [字母][下划线][数字] 之外的所有字符替换为下划�?. 且若以数字开�?,在开头补下划�?.
	 * 4.标准化属性�?/节点�?: 去头尾空字符(dom4j会自动处理转义字�?) 
	 * 
	 * @param node 标准化前的节�?
	 * @return 标准化后的节�?
	 */
	public static Node standardize(Node node) {
		Node standNode = new Node("");
		
		// 去除无效属�?
		node.delAttribute(Node.NEW_ATTRIBUTE);
		
		// 标准化属性名/属性�?
		for(Iterator<String> attributes = node.getAttributeKeys().iterator();
				attributes.hasNext();) {
			String attribute = attributes.next();
			String value = node.getAttributeVal(attribute);
			
			attribute = standardizeXmlName(attribute);
			standNode.setAttribute(attribute, value.trim());
		}
		
		// 标准化节点名/节点�?
		standNode.setName(standardizeXmlName(node.getName()));
		standNode.setText(node.getText().trim());
		return standNode;
	}
	
	/**
	 * 标准化xml报文中的 节点�?/属性名:
	 * 	把除�? [字母][下划线][数字][第一个冒号] 之外的所有字符替换为下划�?. 
	 * 	且若以冒号开�?,去掉冒号;
	 * 	以数字开�?,在开头补下划�?.
	 * 
	 * @param name 节点�?/属性名
	 * @return 标准化后�? 节点�?/属性名
	 */
	private static String standardizeXmlName(final String name) {
		String xmlName = name;
		if(xmlName != null) {
			xmlName = xmlName.trim().replaceAll("\\W", "_");
			xmlName = (xmlName.startsWith(":") ? ("_" + xmlName) : xmlName);
			xmlName = (xmlName.matches("^\\d.*") ? ("_" + xmlName) : xmlName);
		}
		return xmlName;
	}
	
	/**
	 * 展开�?
	 * @param tree �?
	 */
	public static void expandTree(JTree tree) {
	    TreeNode root = (TreeNode) tree.getModel().getRoot();
	    expandNode(tree, new TreePath(root));
	}
	
	/**
	 * 展开树节�?
	 * @param tree �?
	 * @param parentPath 当前节点的树路径
	 */
	@SuppressWarnings("unchecked")
	private static void expandNode(JTree tree, TreePath parentPath) {
	    TreeNode node = (TreeNode) parentPath.getLastPathComponent();
	    if (node.getChildCount() >= 0) {
	       for(Enumeration<TreeNode> childs = node.children(); childs.hasMoreElements();) {
	           TreeNode child = childs.nextElement();
	           TreePath path = parentPath.pathByAddingChild(child);
	           expandNode(tree, path);
	       }
	    }
	    tree.expandPath(parentPath);
	}
	
	/**
	 * 折叠�?
	 * @param tree �?
	 */
	public static void collapseTree(JTree tree) {
	    TreeNode root = (TreeNode) tree.getModel().getRoot();
	    collapseNode(tree, new TreePath(root));
	}
	
	/**
	 * 折叠树节�?
	 * @param tree �?
	 * @param parentPath 当前节点的树路径
	 */
	@SuppressWarnings("unchecked")
	private static void collapseNode(JTree tree, TreePath parentPath) {
	    TreeNode node = (TreeNode) parentPath.getLastPathComponent();
	    if (node.getChildCount() >= 0) {
	       for (Enumeration<TreeNode> childs = node.children(); childs.hasMoreElements();) {
	           TreeNode child = childs.nextElement();
	           TreePath path = parentPath.pathByAddingChild(child);
	           collapseNode(tree, path);
	       }
	    }
	    tree.collapsePath(parentPath);
	}
	
	/**
	 * 复制树节点（包括其所有子节点�?
	 * @param treeNode 树节�?
	 * @return 复制节点
	 */
	@SuppressWarnings("unchecked")
	public static DefaultMutableTreeNode copyNode(DefaultMutableTreeNode treeNode) {
		Node node = (Node) treeNode.getUserObject();
		Node copyNode = new Node(node.getName());
		copyNode.setText(node.getText());
		
		List<String> attributes = node.getAttributeKeys();
		for(String attribute : attributes) {
			String value = node.getAttributeVal(attribute);
			copyNode.setAttribute(attribute, value);
		}
		
		DefaultMutableTreeNode copyTreeNode = new DefaultMutableTreeNode(copyNode);
		for(Enumeration<DefaultMutableTreeNode> childs = treeNode.children(); 
				childs.hasMoreElements();) {
			DefaultMutableTreeNode treeChildNode = childs.nextElement();
			DefaultMutableTreeNode copyTreeChildNode = copyNode(treeChildNode);
			copyTreeNode.add(copyTreeChildNode);
	    }
		return copyTreeNode;
	}
	
}
