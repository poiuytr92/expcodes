package Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Tool.LinkToDB;

/*
 * 本类与系统无关
 * 仅用于批量添加数据库数据用于测试
 */
public class Test {

	private static Connection connection;
	
	//对结果集进行处理
	private static void work(ResultSet rs) throws SQLException {
		
		while (rs.next()) {
			String name = rs.getString("username");
			System.out.println(name);
		}
		
	}
	
	
	private static void test() {
		String[] role = {"经理", "采购员", "仓管员", "销售员"};
		String[] partner = {"供应商", "客户"};
		String[] billType = {"进货单", "出货单"};
		String[] goodsType = {"电器", "首饰", "书籍"};
		String[] measureWord = {"台", "件", "本"};
		String[] cPartner = {"", "客户1"};
		String[] status = {"编辑中", "待审核", "同意", "驳回"};
		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		
//		Vector<Vector<Object>> data = SqlToJava.select(DBTable.goods, "id");
//		
//		String conditionAttribute = "amount, cost, new_price, goods_id";
//		for (int i=0; i<data.size(); i++) {
//			SqlToJava.insert(DBTable.warehouse, conditionAttribute, 0, 0, 0, data.get(i).get(0));
//		}
		
//		for (int i=0; i<20; i++) {
			
//			String sql = "insert into username(username, password, identity) values('a"+i+"', '"+i+"', '"+role[i%4]+"');";
//			execute(sql);
			
//			SqlToJava.insert(DBTable.partner, "name, type", partner[i%2]+(i/2+1), partner[i%2]);
//			SqlToJava.insert(DBTable.goods, "name, type, measure_word, partner", "商品"+i, goodsType[i%3], measureWord[i%3], "供应商"+i);
//			SqlToJava.insert(DBTable.bill, "type, goods, price, amount, partner, date, status", 
//					billType[i%2], "商品"+i, Math.random(), Math.round(Math.random()*100), cPartner[i%2], sdf.format(new Date()), status[i%4]);
//		}
		
//		Vector<Vector<Object>> data = SqlToJava.select(DBTable.partner, "name, type", "type", "客户");
//		for (Vector<Object> rowData : data) {
//			System.out.println(rowData);
//		}
		
//		SqlToJava.delete(DBTable.partner, "type", "客户");
		
//		String selectSql = "select * from username";
		
		//执行sql的insert, delete, update语句
//		execute("delete from warehouse where id>44");
		
		//执行sql的select语句，在work函数对结果进行处理
//		executeQuerty(selectSql);
		
		System.out.println("sucess");
	}
	
	
	
	
	public static void main(String[] args) {
		
		connect();
		
		test();
		
		disConnection();
		
	}
	
	
	private static void connect() {
		new LinkToDB();
		connection = LinkToDB.connection;
	}
	
	//执行sql的insert, add, delete, update语句
	private static void execute(String sql) {
		try {
			
			Statement statement = connection.createStatement();
			statement.executeUpdate(sql);
			statement.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//执行查询语句
	private static void executeQuerty(String sql) {
		try {
			
			Statement statement = connection.createStatement();
			
			ResultSet rs = statement.executeQuery(sql);

			work(rs);
			
			statement.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void disConnection() {
		LinkToDB.disConnection();
	}

}

/**
 * 
 * @software 进销存管理系统
 * 
 * @team 邓伟文， 邝泽徽， 廖权斌 ，罗伟聪
 *
 */