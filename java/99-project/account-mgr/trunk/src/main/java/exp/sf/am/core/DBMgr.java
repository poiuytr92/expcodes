package exp.sf.am.core;

import java.io.File;
import java.sql.Connection;

import exp.libs.envm.Charset;
import exp.libs.envm.DBType;
import exp.libs.utils.StrUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.warp.db.sql.SqliteUtils;
import exp.libs.warp.db.sql.bean.DataSourceBean;
import exp.sf.am.bean.TUser;
import exp.sf.am.utils.CryptoUtils;

class DBMgr {

	private final static String ENV_DB_SCRIPT = "/exp/sf/am/bean/AM-DB.sql";
	
	private final static String ENV_DB = "./conf/.AM";
	
	private final static DataSourceBean ds = new DataSourceBean();
	static {
		ds.setDriver(DBType.SQLITE.DRIVER);
		ds.setName(ENV_DB);
	}
	
	private DBMgr() {}
	
	protected static boolean initEnv() {
		boolean isOk = true;
		if(!new File(ENV_DB).exists()) {
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
	
	protected static TUser findUser(String username, String password) {
		String enUN = CryptoUtils.encode(username);
		String enPW = CryptoUtils.encode(password);
		String where = StrUtils.concat(
				TUser.getUsername$CN(), " = '", enUN, "'", " AND ", 
				TUser.getPassword$CN(), " = '", enPW, "'");
		
		Connection conn = SqliteUtils.getConn(ds);
		TUser user = TUser.queryOne(conn, where);
		SqliteUtils.close(conn);
		return user;
	}
	
	protected static TUser register(String username, String password) {
		String enUsername = CryptoUtils.encode(username);
		String sql = StrUtils.concat("SELECT COUNT(1) FROM ", TUser.getTableName(),  
				" WHERE ", TUser.getUsername$CN(), " = '", enUsername, "'");
		
		TUser user = null;
		Connection conn = SqliteUtils.getConn(ds);
		if(SqliteUtils.queryInt(conn, sql) == 0) {
			user = new TUser();
			user.encodeUsername(username);
			user.encodePassword(password);
			user.encodeNickname(username);
			if(TUser.insert(conn, user)) {
				user = findUser(username, password);
			} else {
				user = null;
			}
		}
		SqliteUtils.close(conn);
		return user;
	}
	
	protected static void updateNickName(TUser user, String nickName) {
		String where = StrUtils.concat(TUser.getId$CN(), " = ", user.getId());
		user.encodeNickname(nickName);
		
		Connection conn = SqliteUtils.getConn(ds);
		TUser.update(conn, user, where);
		SqliteUtils.close(conn);
	}
	
}
