package exp.libs.test;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import exp.libs.warp.conf.xml.XConfigFactory;
import exp.libs.warp.conf.xml.XConfig;
import exp.libs.warp.db.sql.DBUtils;
import exp.libs.warp.db.sql.bean.DataSourceBean;

public class Test {

	public static void main(String[] args) {
		XConfig conf = XConfigFactory.createConfig("TEST");
		conf.loadConfFile("./conf/wsc_app_cfg.dat");
		conf.loadConfFile("./conf/wsc_monitor_cfg.dat");
		conf.loadConfFile("./conf/wsc_conf.xml");
		
		System.out.println(conf.getBool("config/bases/base@app/useUnstandIf"));
		System.out.println(conf.getVal("pool"));
		System.out.println(conf.getInt("iteratorMode"));
		
		System.out.println(conf.getAttribute("base@ftp", "hint"));
		
		System.out.println(conf.getEnumVals("datasource", "WXP"));
		System.out.println(conf.getChildElements("config/bases/base", "ws").keySet());
		System.out.println(conf.getChildElements("datasource@WXP").keySet());
		
		DataSourceBean ds = conf.getDataSourceBean("TEST");
		Connection conn = DBUtils.getConn(ds);
		List<Map<String, String>> kvs = DBUtils.queryKVSs(conn, "select * from django_admin_log");
		for(Map<String, String> kv : kvs) {
			System.out.println(kv);
		}
		DBUtils.close(conn);
		
		conf.destroy();
	}
}
