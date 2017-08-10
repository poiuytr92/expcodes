package exp.sf.am.core;

import java.io.File;
import java.sql.Connection;

import exp.libs.envm.Charset;
import exp.libs.envm.DBType;
import exp.libs.utils.StrUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.warp.db.sql.SqliteUtils;
import exp.libs.warp.db.sql.bean.DataSourceBean;
import exp.libs.warp.ui.SwingUtils;

public class AppMgr {

	private final static String ENV_DB_SCRIPT = "/exp/sf/am/bean/AM-DB.sql";
	
	private final static String ENV_DB = "./conf/.AM.DB";
	
	private AppMgr() {}
	
	public static void createInstn() {
		if(initEnv()) {
			new _LoginWin();
			
		} else {
			SwingUtils.warn("程序无法启动: 初始化失败");
		}
	}
	
	private static boolean initEnv() {
		boolean isOk = true;
		if(!new File(ENV_DB).exists()) {
			DataSourceBean ds = new DataSourceBean();
			ds.setDriver(DBType.SQLITE.DRIVER);
			ds.setName(ENV_DB);
			
			Connection conn = SqliteUtils.getConn(ds);
			String script = FileUtils.readFileInJar(ENV_DB_SCRIPT, Charset.ISO);
			String[] sqls = script.split(";");
			for(String sql : sqls) {
				if(StrUtils.isNotTrimEmpty(sql)) {
					isOk &= SqliteUtils.execute(conn, sql);
				}
			}
			SqliteUtils.close(conn);
		}
		return isOk;
	}
	
}
