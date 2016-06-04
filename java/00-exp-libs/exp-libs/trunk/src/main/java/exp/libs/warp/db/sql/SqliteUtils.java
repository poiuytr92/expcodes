package exp.libs.warp.db.sql;

import java.sql.Connection;

public final class SqliteUtils extends DBUtils {

	protected SqliteUtils() {}
	
	/**
	 * 强制释放磁盘库文件占用的多余空间.
	 * @param conn 数据库连接
	 * @return 是否释放成功
	 */
	public static boolean releaseDisk(Connection conn) {
		boolean isOk = false;
		if(conn != null) {
			isOk = execute(conn, "vacuum");
		}
		return isOk;
	}
	
}
