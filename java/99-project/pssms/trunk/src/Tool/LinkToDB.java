package Tool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

/*
 * 数据库连接
 */

public class LinkToDB {

	public static Connection connection = null;
	
	private String DBName = "PSSMS";	//The purchase-sell-stock management system
	
	private String DBPath = "./" + DBName + ".db";
	
	public LinkToDB() {
		//
	}
	
	public LinkToDB(String DBName) {
		this.DBName = DBName;
		this.DBPath = "./" + DBName + ".db";
	}
	
	public boolean connection() {
        try {
        	//连接SQLite的JDBC
			Class.forName("org.sqlite.JDBC");
			
			//建立一个数据库名PSSMS.db的连接（在当前目录下），如果不存在就在当前目录下创建之
	        connection = DriverManager.getConnection("jdbc:sqlite:" + DBPath);
	        
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(null, "找不到数据源！请检查数据库是否存在");
			return false;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "连接数据库失败！请检查数据库是否存在");
			return false;
		}
        return true;
	}
	
	public static boolean disConnection() {
	    try {
	    	
			connection.close();  //关闭数据库连接
		    
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}     
		return true;
	}
}

/**
 * 
 * @software 进销存管理系统
 * 
 * @team 邓伟文， 邝泽徽， 廖权斌 ，罗伟聪
 *
 */
