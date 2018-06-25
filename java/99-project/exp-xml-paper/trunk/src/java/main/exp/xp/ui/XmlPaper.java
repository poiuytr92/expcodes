package exp.xp.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.apache.commons.io.FileUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import exp.xp.bean.Node;
import exp.xp.layout.VFlowLayout;
import exp.xp.utils.UIUtils;
import exp.xp.utils.XmlUtils;

/**
 * <PRE>
 * 主界面
 * </PRE>
 * <B>PROJECT : </B> exp-xml-paper
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2015-06-01
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class XmlPaper extends JFrame {

	/** 序列化唯一标识 */
	private static final long serialVersionUID = -2887728919155248815L;

	/** 屏幕宽度 */
	private final int winWidth = 
			(int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	
	/** 屏幕高度 */
	private final int winHigh = 
			(int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	
	/** 顶部菜单�? */
	private JMenuBar menuBar;
	
	/** 浮动菜单�? */
	private JPopupMenu popMenu;
	
	/** 根面�? - 层次:0 */
	private JPanel rootPanel;
	
	/** tab面板 - 层次:1 */
	private JTabbedPane tabbedPanel;
	
	/** xml源码视框 (禁止编辑) - 层次:2 */
	private JTextArea codeTextArea;
	
	/** xml树编辑视�?(可编�?) - 层次:2 */
	private JSplitPane editPanel;
	
	/** xml树属性编辑板 - 层次:3(�?) */
	private JPanel formPanel;
	
	/** xml树形展示�? - 层次:3(�?) */
	private JScrollPane treePanel;
	
	/** xml�? - 层次:4 */
	private JTree xmlTree;
	
	/**
	 * 所编辑的xml文件的字符集编码.
	 * 加载/保存时使�?.
	 */
	private String charset;
	
	/** 存储文件的路�? */
	private String saveFilePath;
	
	/** 单例 */
	private static volatile XmlPaper instance;
	
	/**
	 * 构造函�?
	 * @param uiName 界面名称
	 */
	private XmlPaper(String uiName) {
		super(uiName);
		this.setSize(winWidth, winHigh - 50);	//全屏窗口 - 下方工具栏高�?
		this.setLocation(0, 0);
		
		this.rootPanel = new JPanel(new BorderLayout());
		this.setContentPane(rootPanel);
		initComponents();
		
		this.setVisible(true);
		this.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});
	}

	/**
	 * 创建界面单例
	 * @param uiName 界面名称
	 */
	public static void createInstn(String uiName) {
		if(instance == null) {
			synchronized (XmlPaper.class) {
				if(instance == null) {
					instance = new XmlPaper(uiName);
				}
			}
		}
	}
	
	/**
	 * 初始化组�?
	 */
	private void initComponents() {
		/* 初始化Tab面板 */	
		JPanel tabPanel = new JPanel(new BorderLayout()); {
			JPanel rightPanel = initFormPanel();
			JPanel leftPanel = initTreePanel();
			this.editPanel = initEditPanel(leftPanel, rightPanel);
			JScrollPane codePanel = initCodePanel();
			this.tabbedPanel = new JTabbedPane(JTabbedPane.LEFT);
			tabbedPanel.add(editPanel, "edit");
			tabbedPanel.add(codePanel, "view");
			
			tabbedPanel.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JTabbedPane tabPanel = (JTabbedPane) e.getSource();
					if(0 == tabPanel.getSelectedIndex()) {	// 选中了edit 视图
						xmlTree.updateUI();
						
					} else if(1 == tabPanel.getSelectedIndex()) {	// 选中�? view 视图
						applyChanges(false);// 提交最后一次节点修�?
						reflashXmlCode();	// 刷新xml源码
					}
				}
			});
			tabPanel.add(tabbedPanel, BorderLayout.CENTER);
		}
		rootPanel.add(tabPanel, BorderLayout.CENTER);
		
		/* 初始化顶部菜单栏面板 */	
		JPanel menuPanel = initMenuBar(); 
		rootPanel.add(menuPanel, BorderLayout.NORTH);
		
		/* 初始化浮动菜单栏 */	
		initPopMenu();
	}
	
	/**
	 * 初始�? xml树属性编辑板.
	 * @return xml树属性编辑板
	 */
	private JPanel initFormPanel() {
		this.formPanel = new JPanel(new BorderLayout());
		return formPanel;
	}
	
	/**
	 * 初始�? xml树形展示�?.
	 * @return xml树形展示�?
	 */
	private JPanel initTreePanel() {
		JPanel panel = new JPanel(new BorderLayout()); {
			Node root = new Node("root");
			DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode(root);
			this.xmlTree = new JTree(treeRoot);	// 初始化时默认为空�?
			this.treePanel = new JScrollPane(xmlTree);
			
			setTreeListener(xmlTree);	//每次创建xml树都要配监听�?
			reflashFormPanel(root);		//此时只有根节点，为根接地啊按刷新属性编辑板
		}
		panel.add(treePanel, BorderLayout.CENTER);
		
		JPanel btnPanel = new JPanel(new GridLayout(1, 2)); {
			JButton expandBtn = new JButton("expand");		//展开树按�?
			JButton collapseBtn = new JButton("collapse");	//折叠树按�?
			btnPanel.add(expandBtn, 0);
			btnPanel.add(collapseBtn, 1);
			
			expandBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					UIUtils.expandTree(xmlTree);
				}
			});
			collapseBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					UIUtils.collapseTree(xmlTree);
				}
			});
		}
		panel.add(btnPanel, BorderLayout.NORTH);
		return panel;
	}
	
	/**
	 * 初始�? xml树编辑视�?.
	 * @param leftPanel 左面�?, 即xml树形�?
	 * @param rightPanel 右面�?, 即xml属性编辑版
	 * @return  xml树编辑视�?.
	 */
	private JSplitPane initEditPanel(
			Component leftPanel, Component rightPanel) {
		return new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT, 
				leftPanel, rightPanel);
	}
	
	/**
	 * 初始�? xml源码视框
	 * @return xml源码视框
	 */
	private JScrollPane initCodePanel() {
		this.codeTextArea = new JTextArea("xml source code");
		codeTextArea.setEditable(false);
		return new JScrollPane(codeTextArea);
	}
	
	/**
	 * 根据当前的xml树刷新xml源码
	 */
	private void reflashXmlCode() {
		Document doc = DocumentHelper.createDocument();
		doc.setXMLEncoding(charset);
		doc.addElement("root");
		
		DefaultMutableTreeNode treeRoot = (DefaultMutableTreeNode) xmlTree.getModel().getRoot();
		Element xmlRoot = doc.getRootElement();
		UIUtils.createXmlTree(treeRoot, xmlRoot);
		
		String xml = xmlRoot.asXML();
		xml = XmlUtils.formatXml(xml, "    ", true, charset);	//格式化xml报文
		codeTextArea.setText(xml);
		codeTextArea.setCaretPosition(0);	// 光标放在最开�?
	}
	
	/**
	 * 初始化顶部菜单栏
	 * @return 顶部菜单面板
	 */
	private JPanel initMenuBar() {
		JPanel menuPanel = new JPanel(new BorderLayout());
		this.menuBar = new JMenuBar();
		
		JMenu sysMenu = new JMenu("System"); {
			JMenuItem create = new JMenuItem("New");
			JMenuItem open = new JMenuItem("Open File...");
			JMenuItem save = new JMenuItem("Save");
			JMenuItem saveAs = new JMenuItem("Save As...");
			JMenuItem exit = new JMenuItem("Exit");
			sysMenu.add(create);
			sysMenu.add(open);
			sysMenu.addSeparator();
			sysMenu.add(save);
			sysMenu.add(saveAs);
			sysMenu.addSeparator();
			sysMenu.add(exit);
			
			setSysMenuListener(create, open, save, saveAs, exit);
		}
		menuBar.add(sysMenu);
		
		JMenu charsetMenu = new JMenu("Charset"); {
			JRadioButtonMenuItem utf8Btn = new JRadioButtonMenuItem("UTF-8");
			JRadioButtonMenuItem gbkBtn = new JRadioButtonMenuItem("GBK");
			JRadioButtonMenuItem isoBtn = new JRadioButtonMenuItem("ISO-8859-1");
			charsetMenu.add(utf8Btn);
			charsetMenu.add(gbkBtn);
			charsetMenu.add(isoBtn);
			
			ButtonGroup bg = new ButtonGroup();
			bg.add(utf8Btn);
			bg.add(gbkBtn);
			bg.add(isoBtn);
			utf8Btn.setSelected(true);
			this.charset = "UTF-8";
			
			setCharsetMenuListener(utf8Btn, gbkBtn, isoBtn);
		}
		menuBar.add(charsetMenu);
		
		JMenu helpMenu = new JMenu("Help"); {
			JMenuItem update = new JMenuItem("Update");
			JMenuItem about = new JMenuItem("About");
			helpMenu.add(update);
			helpMenu.addSeparator();
			helpMenu.add(about);
			
			setHelpMenuListener(update, about);
		}
		menuBar.add(helpMenu);
		
		JButton applyBtn = new JButton("Apply Changes");
		applyBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				applyChanges(true);
			}
		});
		
		menuPanel.add(applyBtn, BorderLayout.EAST);
		menuPanel.add(menuBar, BorderLayout.CENTER);
		return menuPanel;
	}
	
	/**
	 * 设置系统菜单监听�?
	 * @param create [新建]菜单
	 * @param open [打开]菜单
	 * @param save [保存]菜单
	 * @param saveAs [另存为]菜单
	 * @param exit [退出]菜单
	 */
	private void setSysMenuListener(JMenuItem create, JMenuItem open, 
			JMenuItem save, JMenuItem saveAs, JMenuItem exit) {
		create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Node root = new Node("root");
				DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode(root);
				xmlTree = new JTree(treeRoot);
				reflashNewTree(xmlTree);
			}
		});

		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new FileFilter() {
					public boolean accept(File file) {
						if(file.isDirectory()) {
							return true;
						}
						if(file.getName().endsWith(".xml") || 
								file.getName().endsWith(".XML")) {
							return true;
						}
						return false;
					}
					public String getDescription() {
						return "*.xml";
					}
				});
				
				if(JFileChooser.APPROVE_OPTION == fc.showOpenDialog(rootPanel)) {
					File file = fc.getSelectedFile();
					saveFilePath = file.getPath();
					try {
						String xml = FileUtils.readFileToString(file, charset);
						createTree(xml);	//重新创建xml�?
					} catch (Exception ex) {
						UIUtils.error("Failed to read the file: " + file.getPath(), ex);
					}
				}
			}
		});
		
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(saveFilePath != null) {
					if(UIUtils.confirm("Cover the file: " + saveFilePath + " ?")) {
						saveXml(saveFilePath);
					}
				} else {
					JFileChooser fc = new JFileChooser();
					if(JFileChooser.APPROVE_OPTION == fc.showSaveDialog(rootPanel)) {
						File file = fc.getSelectedFile();
						saveFilePath = file.getPath();
						saveXml(saveFilePath);
					}
				}
			}
		});
		
		saveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				if(JFileChooser.APPROVE_OPTION == fc.showSaveDialog(rootPanel)) {
					File file = fc.getSelectedFile();
					saveFilePath = file.getPath();
					saveXml(saveFilePath);
				}
			}
		});
		
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(UIUtils.confirm("Exit ?")) {
					dispose();
					System.exit(0);
				}
			}
		});
	}
	
	/**
	 * 设置编码菜单监听�?
	 * @param utf8Btn [utf8编码]菜单
	 * @param gbkBtn [gbk编码]菜单
	 * @param isoBtn [iso编码]菜单
	 */
	private void setCharsetMenuListener(JRadioButtonMenuItem utf8Btn,
			JRadioButtonMenuItem gbkBtn, JRadioButtonMenuItem isoBtn) {
		utf8Btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				charset = "UTF-8";
			}
		});
		
		gbkBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				charset = "GBK";
			}
		});
		
		isoBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				charset = "ISO-8859-1";
			}
		});
	}
	
	/**
	 * 设置帮助菜单监听�?
	 * @param update [软件升级]菜单
	 * @param about [软件声明]菜单
	 */
	private void setHelpMenuListener(JMenuItem update, JMenuItem about) {
		update.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Update().display();
			}
		});
		
		about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new About().display();
			}
		});
	}
	
	/**
	 * 初始化浮动菜单栏
	 */
	private void initPopMenu() {
		this.popMenu = new JPopupMenu();
		JMenuItem addChild = new JMenuItem("Add Child Node");
		JMenuItem addBrother = new JMenuItem("Add Brother Node");
		JMenuItem copy = new JMenuItem("Copy Node");
		JMenuItem modify = new JMenuItem("Modify Name");
		JMenuItem remove = new JMenuItem("Remove Node");
		JMenuItem removeChilds = new JMenuItem("Remove Childs");
		popMenu.add(addChild);
		popMenu.add(addBrother);
		popMenu.add(copy);
		popMenu.add(modify);
		popMenu.add(remove);
		popMenu.add(removeChilds);
		
		addChild.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object node = xmlTree.getLastSelectedPathComponent();
				if(node != null) {
					DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) node;
					String name = JOptionPane.showInputDialog("Input the new node name :");
					if(name != null && !"".equals(name.trim())) {
						Node newNode = new Node(name.trim());
						selectNode.add(new DefaultMutableTreeNode(newNode));
						xmlTree.updateUI();
						UIUtils.expandTree(xmlTree);
					}
				}
			}
		});
		
		addBrother.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object node = xmlTree.getLastSelectedPathComponent();
				if(node != null) {
					DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) node;
					if(selectNode.isRoot()) {
						UIUtils.warn("You can't create a brother node for the root .");
					}
					
					TreeNode parent = selectNode.getParent();
					if(parent != null) {
						DefaultMutableTreeNode father = (DefaultMutableTreeNode) parent;
						String name = JOptionPane.showInputDialog("Input the new node name :");
						if(name != null && !"".equals(name.trim())) {
							Node newNode = new Node(name.trim());
							father.add(new DefaultMutableTreeNode(newNode));
							xmlTree.updateUI();
						}
					}
				}
			}
		});
		
		copy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object node = xmlTree.getLastSelectedPathComponent();
				if(node != null) {
					DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) node;
					if(selectNode.isRoot()) {
						UIUtils.warn("You can't copy the root .");
					}
					
					TreeNode parent = selectNode.getParent();
					if(parent != null) {
						DefaultMutableTreeNode father = (DefaultMutableTreeNode) parent;
						father.add(UIUtils.copyNode(selectNode));
						xmlTree.updateUI();
						UIUtils.expandTree(xmlTree);
					}
				}
			}
		});
		
		modify.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object node = xmlTree.getLastSelectedPathComponent();
				if(node != null) {
					DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) node;
					String name = JOptionPane.showInputDialog("Input a new name of the node :");
					if(name != null && !"".equals(name.trim())) {
						((Node) selectNode.getUserObject()).setName(name);
						xmlTree.updateUI();
					}
				}
			}
		});
		
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object node = xmlTree.getLastSelectedPathComponent();
				if(node != null) {
					DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) node;
					if(UIUtils.confirm("Remove this node ?")) {
						selectNode.removeAllChildren();	// 移除所有子节点
						if(selectNode.isRoot()) {
							UIUtils.warn("You can't remove the root node .");
						} else {
							selectNode.removeFromParent();
						}
						xmlTree.updateUI();
					}
				}
			}
		});
		
		removeChilds.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object node = xmlTree.getLastSelectedPathComponent();
				if(node != null) {
					DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) node;
					if(UIUtils.confirm("Remove all the childs of this node ?")) {
						selectNode.removeAllChildren();
						xmlTree.updateUI();
					}
				}
			}
		});
	}

	/**
	 * 创建xml�?
	 * @param xml xml报文
	 */
	private void createTree(String xml) {
		try {
			Document doc = DocumentHelper.parseText(xml);
			Element xmlRoot = doc.getRootElement();
			
			Node root = new Node(xmlRoot.getName());
			DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode(root);
			createNode(treeRoot, xmlRoot);	// 从根开始创建所有树节点
			xmlTree = new JTree(treeRoot);
			codeTextArea.setText(xml);		// 刷新xml源码
			reflashNewTree(xmlTree);	//刷新新建的xml�?
			
		} catch (DocumentException e) {
			UIUtils.error("Failed to parse xml file.", e);
		}
	}
	
	/**
	 * 根据xml报文的节点，创建对应xml树的每个节点，并为之设定上下级关�?
	 * @param treeNode 当前的树节点
	 * @param element xml报文的节�?
	 */
	@SuppressWarnings("unchecked")
	private void createNode(DefaultMutableTreeNode treeNode, Element element) {
		// 设定当前节点的节点值和属性�?
		Node node = (Node) treeNode.getUserObject();
		node.setText(element.getText());
		Iterator<Attribute> attributes = element.attributeIterator();
		while(attributes.hasNext()) {
			Attribute attribute = attributes.next();
			node.setAttribute(attribute.getName(), attribute.getValue());
		}
		
		// 递归创建当前节点的子节点
		Iterator<Element> childs = element.elementIterator();
		while(childs.hasNext()) {
			Element child = childs.next();
			Node childNode = new Node(child.getName());
			DefaultMutableTreeNode childTreeNode = new DefaultMutableTreeNode(childNode);
			createNode(childTreeNode, child);
			treeNode.add(childTreeNode);	//设定上下级关�?
		}
	}
	
	/**
	 * 刷新新建的xml�?
	 * @param xmlTree 新建的xml�?
	 */
	private void reflashNewTree(JTree newXmlTree) {
		treePanel.setViewportView(newXmlTree);	// 刷新树视�?
		setTreeListener(newXmlTree);		// 为新树配置监听器
		UIUtils.expandTree(newXmlTree);	// 展开所有树节点
	}
	
	/**
	 * 设置xml树的监听�?
	 * @param xmlTree xml树监听器
	 */
	private void setTreeListener(JTree xmlTree) {
		if(xmlTree == null) {
			return;
		}
		
		// 选中树节点监听器 - 对应的刷新属性编辑框�?
		xmlTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent event) {
				JTree tree = (JTree) event.getSource();
				Object selectNode = tree.getLastSelectedPathComponent();
				if(selectNode != null) {
					DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) selectNode;
					Node node = (Node) treeNode.getUserObject();
					reflashFormPanel(node);
				}
			}
		});
		
		// 树节点右键浮动菜单监听器 - 要求在选中节点的前提下
		xmlTree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON3) {	//BUTTON3 对应鼠标右键
					JTree tree = (JTree) e.getSource();
					Object selectNode = tree.getLastSelectedPathComponent();
					if(selectNode != null) {
						popMenu.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}
		});
	}
	
	/**
	 * 根据当前所选中的树节点，刷新xml树属性编辑板
	 * @param node 当前所选中的树节点
	 */
	private void reflashFormPanel(final Node node) {
		if(node == null) {
			return;
		}
		formPanel.removeAll();	// 移除原有元素
		
		// 节点属性编辑面�?
		JPanel attsPanel = new JPanel(new BorderLayout());
		attsPanel.setBorder(new TitledBorder("attributes")); {
			JPanel attKeyPanel = new JPanel(new VFlowLayout());	// �?:属性键
			JPanel attValPanel = new JPanel(new VFlowLayout());	// �?:属性�?
			JPanel attBtnPanel = new JPanel(new VFlowLayout());	// �?:属性控制按�?
			
			List<String> attributes = node.getAttributeKeys();	// 列举节点所有属�?
			attributes.remove(Node.NEW_ATTRIBUTE);
			for(String attribute : attributes) {
				String value = node.getAttributeVal(attribute);
				attKeyPanel.add(new JTextField(attribute));
				attValPanel.add(new JTextField(value));
				attBtnPanel.add(getAttributeCtrlBtnPanel(
						node, attribute, attKeyPanel, attValPanel));
			}
			
			JTextField newAtt = new JTextField(Node.NEW_ATTRIBUTE);	// 至少保有一个新增属�?
			newAtt.setForeground(Color.RED);
			attKeyPanel.add(newAtt);	
			attValPanel.add(new JTextField());
			attBtnPanel.add(getAttributeCtrlBtnPanel(
					node, null, attKeyPanel, attValPanel));
			
			attsPanel.add(attKeyPanel, BorderLayout.WEST);
			attsPanel.add(attValPanel, BorderLayout.CENTER);
			attsPanel.add(attBtnPanel, BorderLayout.EAST);
		}
		formPanel.add(attsPanel, BorderLayout.NORTH);
		
		// 节点值编辑面�?
		JPanel textPanel = new JPanel(new BorderLayout()); 
		textPanel.setBorder(new TitledBorder("text")); {
			final JTextArea nodeValTA = new JTextArea(node.getText());
			textPanel.add(nodeValTA, BorderLayout.CENTER);
			
			nodeValTA.addMouseListener(new MouseAdapter() {	// 鼠标离开时自动保存�?
				public void mouseExited(MouseEvent e) {
					node.setText(nodeValTA.getText());
				}
			});
		}
		formPanel.add(new JScrollPane(textPanel), BorderLayout.CENTER);
		
		// 重绘
		formPanel.validate();
		formPanel.repaint();
		formPanel.updateUI();
	}
	
	/**
	 * 获取控制属性增减的按钮面板
	 * @param node 该面板所属的节点
	 * @param attribute 该面板对应的节点属�?
	 * @param attKeyPanel 存放了当前节点所有属性名的面�?
	 * @param attValPanel 存放了当前节点所有属性值的面板
	 * @return 控制属性增减的按钮面板
	 */
	private JPanel getAttributeCtrlBtnPanel(final Node node, final String attribute, 
			final JPanel attKeyPanel, final JPanel attValPanel) {
		JPanel btnPanel = new JPanel(new GridLayout(1, 2));
		JButton addBtn = new JButton("+");
		JButton delBtn = new JButton("-");
		addBtn.setMargin(new Insets(3, 5, 3, 5));	//设置内边�?
		delBtn.setMargin(new Insets(3, 5, 3, 5));
		btnPanel.add(addBtn, 0);
		btnPanel.add(delBtn, 1);
		
		addBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(attKeyPanel == null || attValPanel == null) {
					return;
				}
				
				// 先保存当前所编辑的属性�?
				Component[] attKeys = attKeyPanel.getComponents();
				Component[] attVals = attValPanel.getComponents();
				node.clearAttributes();
				for(int i = 0; i < attKeys.length; i++) {
					String attKey = ((JTextField) attKeys[i]).getText();
					String attVal = ((JTextField) attVals[i]).getText();
					node.setAttribute(attKey, attVal);
				}
				
				node.setAttribute(Node.NEW_ATTRIBUTE, "");	// 增加新属�?
				reflashFormPanel(node);	// 刷新属性编辑面�?
			}
		});
		
		delBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(attribute != null && !Node.NEW_ATTRIBUTE.endsWith(attribute)) {
					node.delAttribute(attribute);	// 删除对应的属性�?
					reflashFormPanel(node);	// 刷新属性编辑面�?
				}
			}
		});
		return btnPanel;
	}
	
	/**
	 * 提交节点的属性�?/节点值修�?.
	 * 把在UI修改的内容保存到内存.
	 * @param isPrintTips 是否打印提示信息: 当为自动提交时，不需要打印提�?
	 */
	private void applyChanges(boolean isPrintTips) {
		Object selectNode = xmlTree.getLastSelectedPathComponent();
		if(selectNode == null) {
			if(isPrintTips) {
				UIUtils.warn("You must select a node firstly .");
			}
			
		} else {
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) selectNode;
			Node node = (Node) treeNode.getUserObject();
			Component[] formCmps = formPanel.getComponents();
			
			// 应用属性值修�?
			JPanel attsPanel = (JPanel) formCmps[0]; {
				Component[] attCmps = attsPanel.getComponents();
				JPanel attKeyPanel = (JPanel) attCmps[0];
				JPanel attValPanel = (JPanel) attCmps[1];
				Component[] attKeys = attKeyPanel.getComponents();
				Component[] attVals = attValPanel.getComponents();
				node.clearAttributes();
				for(int i = 0; i < attKeys.length; i++) {
					String attKey = ((JTextField) attKeys[i]).getText();
					String attVal = ((JTextField) attVals[i]).getText();
					node.setAttribute(attKey, attVal);
				}
			}
			
			// 应用节点值修�?
			JScrollPane scrollPanel = (JScrollPane) formCmps[1]; {
				JPanel textPanel = (JPanel) scrollPanel.getViewport().getComponent(0);
				JTextArea nodeValTA = (JTextArea) textPanel.getComponent(0);
				node.setText(nodeValTA.getText());
			}
			
			if(isPrintTips) {
				UIUtils.info("Apply Success .");
			}
		}
	}
	
	/**
	 * 保存xml源码到文�?
	 * @param savePath 保存位置
	 */
	private void saveXml(String savePath) {
		applyChanges(false);// 提交最后一次节点修�?
		reflashXmlCode();	// 刷新xml源码
		try {
			String xml = codeTextArea.getText().trim();
			FileUtils.write(new File(saveFilePath), xml, charset, false);
		} catch (IOException ex) {
			UIUtils.error("Fail to save file :" + saveFilePath + " .", ex);
		}
	}
}
