package exp.libs.test;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import exp.libs.warp.conf.xml.ConfBox;
import exp.libs.warp.conf.xml.ConfFactory;
import exp.libs.warp.db.sql.DBUtils;
import exp.libs.warp.db.sql.bean.DataSourceBean;

public class Test {

	public static void main(String[] args) {
		ConfBox cb = ConfFactory.createConfBox("TEST");
		cb.loadConfFile("./conf/wsc_app_cfg.dat");
		cb.loadConfFile("./conf/wsc_monitor_cfg.dat");
		cb.loadConfFile("./conf/wsc_conf.xml");
		
		System.out.println(cb.getBool("config/bases/base@app/useUnstandIf"));
		System.out.println(cb.getVal("pool"));
		System.out.println(cb.getInt("iteratorMode"));
		
		System.out.println(cb.getAttribute("base@ftp", "hint"));
		
		System.out.println(cb.getEnumVals("datasource", "WXP"));
		System.out.println(cb.getChildElements("config/bases/base", "ws").keySet());
		System.out.println(cb.getChildElements("datasource@WXP").keySet());
		
		DataSourceBean ds = cb.getDataSourceBean("TEST");
		Connection conn = DBUtils.getConn(ds);
		List<Map<String, String>> kvs = DBUtils.queryKVSs(conn, "select * from django_admin_log");
		for(Map<String, String> kv : kvs) {
			System.out.println(kv);
		}
		DBUtils.close(conn);
		
		cb.clear();
	}
}
