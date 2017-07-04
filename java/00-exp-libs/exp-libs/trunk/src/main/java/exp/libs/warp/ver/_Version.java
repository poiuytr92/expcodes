package exp.libs.warp.ver;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import exp.libs.envm.Charset;
import exp.libs.envm.DBType;
import exp.libs.utils.StrUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.warp.db.sql.DBUtils;
import exp.libs.warp.db.sql.SqliteUtils;
import exp.libs.warp.db.sql.bean.DataSourceBean;
import exp.libs.warp.ui.BeautyEyeUtils;
import exp.libs.warp.ui.SwingUtils;

class _Version {

	/** 版本信息库的脚本 */
	private final static String VER_DB_SCRIPT = "/exp/libs/warp/ver/VERSION-INFO-DB.sql";
	
	/**
	 * 存储版本信息的文件数据库位置.
	 * 	[src/main/resources] 为Maven项目默认的资源目录位置（即使非Maven项目也可用此位置）
	 */
	private final static String VER_DB = "./src/main/resources/.verinfo";
	
	/** 版本信息文件的数据源 */
	private DataSourceBean ds;
	
	/** 项目版本信息 */
	private _PrjVerInfo prjVerInfo;
			
	/**
	 * 构造函数：创建版本信息文件
	 */
	protected _Version() {
		initDS();
		if(initVerDB()) {
			this.prjVerInfo = loadPrjVerInfo();
		}
	}
	
	private void initDS() {
		this.ds = new DataSourceBean();
		ds.setDriver(DBType.SQLITE.DRIVER);
		ds.setName(VER_DB);
		ds.setCharset(Charset.UTF8);
	}
	
	private boolean initVerDB() {
		boolean isOk = true;
		Connection conn = SqliteUtils.getConn(ds);
		String script = FileUtils.readFileInJar(VER_DB_SCRIPT, Charset.UTF8);
		try {
			String[] sqls = script.split(";");
			for(String sql : sqls) {
				if(StrUtils.isNotTrimEmpty(sql)) {
					isOk &= DBUtils.execute(conn, sql);
				}
			}
		} catch(Exception e) {
			isOk = false;
			SwingUtils.error("初始化项目版本信息库失败", e);
		}
		
		if(isOk == false) {
			SwingUtils.warn("执行项目版本信息库的初始化脚本失败");
		}
		SqliteUtils.releaseDisk(conn);
		SqliteUtils.close(conn);
		return isOk;
	}
	
	private _PrjVerInfo loadPrjVerInfo() {
		Connection conn = SqliteUtils.getConn(ds);
		String sql = StrUtils.concat("SELECT S_PROJECT_NAME, S_PROJECT_DESC, ", 
				"S_TEAM_NAME, S_PROJECT_CHARSET, S_DISK_SIZE, S_CACHE_SIZE, ",
				"S_APIS FROM T_PROJECT_INFO ORDER BY I_ID DESC LIMIT 1");
		Map<String, String> prjInfo = SqliteUtils.queryFirstRowStr(conn, sql);
		
		sql = StrUtils.concat("SELECT S_AUTHOR, S_VERSION, S_DATETIME, ", 
				"S_UPGRADE_CONTENT, S_UPGRADE_STEP FROM T_HISTORY_VERSIONS ", 
				"ORDER BY I_ID ASC");
		List<_VerInfo> verInfos = toVerInfos(SqliteUtils.queryKVSs(conn, sql));
		SqliteUtils.close(conn);
		
		_PrjVerInfo prjVerInfo = new _PrjVerInfo(this, verInfos);
		prjVerInfo.setPrjName(prjInfo.get("S_PROJECT_NAME"));
		prjVerInfo.setPrjDesc(prjInfo.get("S_PROJECT_DESC"));
		prjVerInfo.setTeamName(prjInfo.get("S_TEAM_NAME"));
		prjVerInfo.setPrjCharset(prjInfo.get("S_PROJECT_CHARSET"));
		prjVerInfo.setDiskSize(prjInfo.get("S_DISK_SIZE"));
		prjVerInfo.setCacheSize(prjInfo.get("S_CACHE_SIZE"));
		prjVerInfo.setAPIs(prjInfo.get("S_APIS"));
		return prjVerInfo;
	}
	
	private List<_VerInfo> toVerInfos(List<Map<String, String>> verDatas) {
		List<_VerInfo> verInfos = new LinkedList<_VerInfo>();
		for(Map<String, String> verData : verDatas) {
			_VerInfo verInfo = new _VerInfo();
			verInfo.setAuthor(verData.get("S_AUTHOR"));
			verInfo.setVersion(verData.get("S_VERSION"));
			verInfo.setDatetime(verData.get("S_DATETIME"));
			verInfo.setUpgradeContent(verData.get("S_UPGRADE_CONTENT"));
			verInfo.setUpgradeStep(verData.get("S_UPGRADE_STEP"));
			verInfos.add(verInfo);
		}
		return verInfos;
	}
	
	protected boolean savePrjInfo() {
		if(prjVerInfo == null) {
			return false;
		}
		
		Connection conn = SqliteUtils.getConn(ds);
		String sql = "DELETE FROM T_PROJECT_INFO";
		SqliteUtils.execute(conn, sql);
		
		sql = StrUtils.concat("INSERT INTO T_PROJECT_INFO(S_PROJECT_NAME, ", 
				"S_PROJECT_DESC, S_TEAM_NAME, S_PROJECT_CHARSET, S_DISK_SIZE, ", 
				"S_CACHE_SIZE, S_APIS) VALUES(?, ?, ?, ?, ?, ?, ?)");
		boolean isOk = SqliteUtils.execute(conn, sql, new Object[] {
				prjVerInfo.getPrjName(), prjVerInfo.getPrjDesc(), 
				prjVerInfo.getTeamName(), prjVerInfo.getPrjCharset(), 
				prjVerInfo.getDiskSize(), prjVerInfo.getCacheSize(),
				prjVerInfo.getAPIs()
		});
		SqliteUtils.close(conn);
		return isOk;
	}
	
	protected boolean addVerInfo(_VerInfo verInfo) {
		if(prjVerInfo == null || verInfo == null) {
			return false;
		}
		
		Connection conn = SqliteUtils.getConn(ds);
		String sql = StrUtils.concat("INSERT INTO T_HISTORY_VERSIONS(", 
				"S_AUTHOR, S_VERSION, S_DATETIME, S_UPGRADE_CONTENT, ", 
				"S_UPGRADE_STEP) VALUES(?, ?, ?, ?, ?)");
		boolean isOk = SqliteUtils.execute(conn, sql, new Object[] {
				verInfo.getAuthor(), verInfo.getVersion(), 
				verInfo.getDatetime(), verInfo.getUpgradeContent(), 
				verInfo.getUpgradeStep()
		});
		SqliteUtils.close(conn);
		return isOk;
	}
	
	protected boolean delVerInfo(_VerInfo verInfo) {
		if(prjVerInfo == null || verInfo == null) {
			return false;
		}
		
		Connection conn = SqliteUtils.getConn(ds);
		String sql = StrUtils.concat("DELETE FROM T_HISTORY_VERSIONS ", 
				"WHERE S_VERSION = '", verInfo.getVersion(), "'");
		boolean isOk = SqliteUtils.execute(conn, sql);
		SqliteUtils.close(conn);
		return isOk;
	}
	
	protected void print() {
		// 打印DOS
	}
	
	protected void manage() {
		BeautyEyeUtils.init();
		_VerMgrUI.getInstn(prjVerInfo);
	}

}
