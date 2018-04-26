package exp.libs.warp.ver;

import java.sql.Connection;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import exp.libs.envm.Charset;
import exp.libs.envm.DBType;
import exp.libs.utils.format.ESCUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.io.JarUtils;
import exp.libs.utils.os.OSUtils;
import exp.libs.utils.other.PathUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.db.sql.DBUtils;
import exp.libs.warp.db.sql.SqliteUtils;
import exp.libs.warp.db.sql.bean.DataSourceBean;

/**
 * <PRE>	
 * 版本库管理器
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class _VerDBMgr {

	/** 版本信息库的脚本 */
	private final static String VER_DB_SCRIPT = "/exp/libs/warp/ver/VERSION-INFO-DB.sql";
	
	/** 版本库库名 */
	private final static String DB_NAME = ".verinfo";
	
	/** 资源目录 */
	private final static String RES_DIR = "./src/main/resources";
	
	/**
	 * 存储版本信息的文件数据库位置.
	 * 	[src/main/resources] 为Maven项目默认的资源目录位置（即使非Maven项目也可用此位置）
	 */
	private final static String VER_DB = RES_DIR.concat("/").concat(DB_NAME);
	
	/** 临时版本库位置（仅用于查看版本信息） */
	private final static String TMP_VER_DB = OSUtils.isRunByTomcat() ? 
			PathUtils.getProjectCompilePath().concat("/").concat(DB_NAME) : 
			"./conf/".concat(DB_NAME);
	
	/** 版本信息文件的数据源 */
	private DataSourceBean ds;
	
	/** 是否已初始化 */
	private boolean inited;
	
	/** 单例 */
	private static volatile _VerDBMgr instance;
	
	/**
	 * 私有化构造函数
	 */
	private _VerDBMgr() {
		initDS();
		this.inited = false;
	}
	
	/**
	 * 创建程序UI实例
	 * @param prjName 项目名称
	 * @param verInfos 版本信息
	 * @return
	 */
	protected static _VerDBMgr getInstn() {
		if(instance == null) {
			synchronized (_VerDBMgr.class) {
				if(instance == null) {
					instance = new _VerDBMgr();
				}
			}
		}
		return instance;
	}
	
	private void initDS() {
		this.ds = new DataSourceBean();
		ds.setDriver(DBType.SQLITE.DRIVER);
		ds.setCharset(Charset.UTF8);
		ds.setName(VER_DB);
		
		// 对于非开发环境, Sqlite无法直接读取jar包内的版本库, 需要先将其拷贝到硬盘
		if(!SqliteUtils.testConn(ds)) {
			
			// 对于J2SE项目, 若若同位置存在版本文件, 先删除再复制, 避免复制失败使得显示的版本依然为旧版
			if(!OSUtils.isRunByTomcat()) {
				FileUtils.delete(TMP_VER_DB);	
				JarUtils.copyFile(VER_DB.replace(RES_DIR, ""), TMP_VER_DB);
				
			// 当程序运行在Tomcat时, Tomcat会自动把版本库拷贝到classes目录下, 一般无需再拷贝(但以防万一, 若不存在版本文件还是拷贝一下)
			} else if(!FileUtils.exists(TMP_VER_DB)){
				JarUtils.copyFile(VER_DB.replace(RES_DIR, ""), TMP_VER_DB);
			}
			
			FileUtils.hide(TMP_VER_DB);
			ds.setName(TMP_VER_DB);
		}
	}
	
	protected DataSourceBean getDS() {
		return ds;
	}
	
	protected boolean initVerDB() {
		if(inited == true) {
			return true;
		}
		
		boolean isOk = true;
		Connection conn = SqliteUtils.getConn(ds);
		String script = JarUtils.read(VER_DB_SCRIPT, Charset.UTF8);
		try {
			String[] sqls = script.split(";");
			for(String sql : sqls) {
				if(StrUtils.isNotTrimEmpty(sql)) {
					isOk &= DBUtils.execute(conn, sql);
				}
			}
		} catch(Exception e) {
			isOk = false;
			e.printStackTrace();
		}
		
		inited = isOk;
		if(inited == false) {
			System.err.println("初始化项目版本信息库失败");
		}
		
		SqliteUtils.releaseDisk(conn);
		SqliteUtils.close(conn);
		return isOk;
	}
	
	protected String getCurVerInfo() {
		Connection conn = SqliteUtils.getConn(ds);
		
		final String PRJ_SQL = "SELECT S_PROJECT_NAME, S_PROJECT_DESC FROM T_PROJECT_INFO LIMIT 1";
		Map<String, String> kvs = SqliteUtils.queryFirstRowStr(conn, PRJ_SQL);
		String prjName = kvs.get("S_PROJECT_NAME");
		String prjDesc = kvs.get("S_PROJECT_DESC");
		
		final String VER_SQL = "SELECT S_VERSION, S_DATETIME, S_AUTHOR FROM T_HISTORY_VERSIONS ORDER BY I_ID DESC LIMIT 1";
		kvs = SqliteUtils.queryFirstRowStr(conn, VER_SQL);
		String version = kvs.get("S_VERSION");
		String datetime = kvs.get("S_DATETIME");
		String author = kvs.get("S_AUTHOR");
		
		SqliteUtils.close(conn);
		return _toCurVerInfo(prjName, prjDesc, version, datetime, author);
	}
	
	protected String _toCurVerInfo(String prjName, String prjDesc, 
			String version, String datetime, String author) {
		prjName = (prjName == null ? "" : prjName);
		prjDesc = (prjDesc == null ? "" : prjDesc);
		version = (version == null ? "" : version);
		datetime = (datetime == null ? "" : datetime);
		author = (author == null ? "" : author);
		
		List<List<String>> curVerInfo = new LinkedList<List<String>>();
		curVerInfo.add(Arrays.asList(new String[] { "项目名称", prjName }));
		curVerInfo.add(Arrays.asList(new String[] { "", "" }));
		curVerInfo.add(Arrays.asList(new String[] { "项目描述", prjDesc }));
		curVerInfo.add(Arrays.asList(new String[] { "", "" }));
		curVerInfo.add(Arrays.asList(new String[] { "版本号", version }));
		curVerInfo.add(Arrays.asList(new String[] { "", "" }));
		curVerInfo.add(Arrays.asList(new String[] { "定版时间", datetime }));
		curVerInfo.add(Arrays.asList(new String[] { "", "" }));
		curVerInfo.add(Arrays.asList(new String[] { "最后责任人", author }));
		return ESCUtils.toTXT(curVerInfo, false);
	}
	
}
