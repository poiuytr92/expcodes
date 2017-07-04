package exp.libs.warp.ver;

import java.sql.Connection;

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
	
	/**
	 * 构造函数：创建版本信息文件
	 */
	protected _Version() {
		initDS();
		initVerDB();
	}
	
	private void initDS() {
		this.ds = new DataSourceBean();
		ds.setDriver(DBType.SQLITE.DRIVER);
		ds.setName(VER_DB);
		ds.setCharset(Charset.UTF8);
	}
	
	private void initVerDB() {
		if(!FileUtils.exists(VER_DB)) {
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
				SwingUtils.error("初始化项目版本信息库失败", e);
			}
			
			if(isOk == false) {
				SwingUtils.warn("执行项目版本信息库的初始化脚本失败");
			}
			SqliteUtils.close(conn);
		}
	}
	
	protected void print() {
		// 打印DOS
	}
	
	protected void manage() {
		BeautyEyeUtils.init();
		
		_PrjVerInfo prjVerInfo = new _PrjVerInfo(null);
		prjVerInfo.setPrjName("测试用项目");
		_VerMgrUI.getInstn(prjVerInfo); // FIXME
	}

}
