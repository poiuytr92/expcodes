package exp.sf.am.bean;

import java.sql.Connection;

import exp.libs.envm.DBType;
import exp.libs.warp.db.sql.SqliteUtils;
import exp.libs.warp.db.sql.bean.DataSourceBean;

public class CreatBean {
	
	private final static String ENV_DB = "./conf/.AM";
	
	public static void main(String[] args) {
		DataSourceBean ds = new DataSourceBean();
		ds.setDriver(DBType.SQLITE.DRIVER);
		ds.setName(ENV_DB);
		
		Connection conn = SqliteUtils.getConn(ds);
		boolean isOk = SqliteUtils.createBeanFromDB(conn, "exp.sf.am.bean", 
				"./src/main/java/exp/sf/am/bean", null);
		SqliteUtils.close(conn);
		
		System.out.println(isOk);
	}
}
