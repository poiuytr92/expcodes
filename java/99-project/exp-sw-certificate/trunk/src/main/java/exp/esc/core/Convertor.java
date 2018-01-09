package exp.esc.core;

import java.util.LinkedList;
import java.util.List;

import exp.esc.Config;
import exp.esc.bean.App;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.tpl.Template;

public class Convertor {

	private final static String PAGE_TPL = "./conf/template/index.tpl";
	
	private final static String TABLE_TPL = "./conf/template/table.tpl";
	
	private final static String PAGE_PATH = "./page/index.html";
	
	public static boolean toPage(List<App> apps) {
		List<String> tables = toTables(apps);
		Template tpl = new Template(PAGE_TPL, Config.DEFAULT_CHARSET);
		tpl.set("tables", StrUtils.concat(tables, ""));
		return FileUtils.write(PAGE_PATH, tpl.getContent(), Config.DEFAULT_CHARSET, false);
	}
	
	private static List<String> toTables(List<App> apps) {
		List<String> tables = new LinkedList<String>();
		Template tpl = new Template(TABLE_TPL, Config.DEFAULT_CHARSET);
		for(App app : apps) {
			tpl.set("name", app.getName());
			tpl.set("versions", CryptoUtils.toDES(app.getVersions()));
			tpl.set("time", CryptoUtils.toDES(app.getTime()));
			tpl.set("blacklist", CryptoUtils.toDES(app.getBlacklist()));
			tables.add(tpl.getContent());
		}
		return tables;
	}
	
	
	
}
