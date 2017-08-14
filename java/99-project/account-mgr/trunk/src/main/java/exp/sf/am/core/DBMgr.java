package exp.sf.am.core;

import java.io.File;
import java.sql.Connection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import exp.libs.envm.Charset;
import exp.libs.envm.DBType;
import exp.libs.utils.StrUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.warp.db.sql.SqliteUtils;
import exp.libs.warp.db.sql.bean.DataSourceBean;
import exp.sf.am.bean.TAccount;
import exp.sf.am.bean.TUser;
import exp.sf.am.utils.CryptoUtils;

class DBMgr {

	private final static String ENV_DB_SCRIPT = "/exp/sf/am/bean/AM-DB.sql";
	
	private final static String ENV_DB_DIR = "./lib/";
	
	private final static String ENV_DB_NAME = ".AM";
	
	private final static String ENV_DB_PATH = ENV_DB_DIR.concat(ENV_DB_NAME);
	
	private final static DataSourceBean ds = new DataSourceBean();
	static {
		ds.setDriver(DBType.SQLITE.DRIVER);
		ds.setName(ENV_DB_PATH);
	}
	
	private DBMgr() {}
	
	protected static boolean initEnv() {
		boolean isOk = true;
		File dbFile = new File(ENV_DB_PATH);
		if(!dbFile.exists()) {
			FileUtils.createDir(ENV_DB_DIR);
			Connection conn = SqliteUtils.getConn(ds);
			String script = FileUtils.readFileInJar(ENV_DB_SCRIPT, Charset.ISO);
			String[] sqls = script.split(";");
			for(String sql : sqls) {
				if(StrUtils.isNotTrimEmpty(sql)) {
					isOk &= SqliteUtils.execute(conn, sql);
				}
			}
			SqliteUtils.close(conn);
			
			FileUtils.hide(dbFile);
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
	
	protected static List<TAccount> queryAccounts(TUser user, String keyword) {
		String where = StrUtils.concat(TAccount.getUserId$CN(), " = ", user.getId());
		Connection conn = SqliteUtils.getConn(ds);
		List<TAccount> accounts = TAccount.querySome(conn, where);
		if(accounts != null && StrUtils.isNotEmpty(keyword)) {
			Iterator<TAccount> its = accounts.iterator();
			while(its.hasNext()) {
				TAccount account = its.next();
				if(!account.contains(keyword)) {
					its.remove();
				}
			}
		}
		SqliteUtils.close(conn);
		return (accounts == null ? new LinkedList<TAccount>() : accounts);
	}
	
	protected static boolean edit(TAccount account) {
		boolean isOk = false;
		Connection conn = SqliteUtils.getConn(ds);
		if(account.getId() == null) {
			isOk = TAccount.insert(conn, account);
			
		} else {
			String where = StrUtils.concat(TAccount.getId$CN(), " = ", account.getId());
			isOk = TAccount.update(conn, account, where);
		}
		SqliteUtils.close(conn);
		return isOk;
	}
	
	protected static boolean delete(TAccount account) {
		Connection conn = SqliteUtils.getConn(ds);
		String where = StrUtils.concat(TAccount.getId$CN(), " = ", account.getId());
		boolean isOk = TAccount.delete(conn, where);
		SqliteUtils.close(conn);
		return isOk;
	}
	
}
