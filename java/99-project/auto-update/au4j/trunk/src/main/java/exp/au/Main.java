package exp.au;

import java.util.Map;

public class Main {

	public static void main(String[] args) {
		stopApp();
		Map<String, String> files = compareVersion();
		download(files);
		backup(files);
		if(!update(files)) {
			rollback(files);
		}
		startApp();
		
		/**
		 * 1.若 version.inf 不存在, 则构造目录下所有文件的 md5+ver，生成version.inf文件
		 *   ver通过md5到远程库确认（SAE版本库，python服务开通ftp和db接口）
		 * 2.首先比对程序总版本号, 若已是最新则不做任何处理
		 * 3.校对本地version.inf各个文件的本地版本号 与 远程库的最新版本号
		 *   若存在更高版本，则下载对应文件到本地某位置打包（addToZip）。
		 * 4.备份本地程序
		 * 5.解包升级
		 * 6.若升级失败，则回滚到备份版本
		 */
		VersionInfo verInfo = new VersionInfo();
		verInfo.setCompany("XYZ有限公司");
		verInfo.setTeam("ABC研发小组");
		verInfo.setAppName("自动化升级程序");
		verInfo.setVersion(new Version(1, 2, 0, 8));
		verInfo.setTime("2016-02-15 14:42:22");
		verInfo.setAuthor("Exp");
		verInfo.setDescription("自动升级其他程序");
		verInfo.addUpdateContent("abcjsdjsadads");
		verInfo.addUpdateContent("asd2143rwefsaqsadada");
		verInfo.addUpdateContent("123124123423412354y");
		verInfo.addUpdateContent("从五十岁的粉红色规范及时等级为随碟附送地方是");
		
		System.out.println(verInfo.toString());
	}
	
	private static void startApp() {
		// TODO Auto-generated method stub
		
	}

	private static void stopApp() {
		// TODO Auto-generated method stub
		
	}

	private static Map<String, String> compareVersion() {
		compareAppVersion();
		compareFileVersion();
		return null;
	}

	private static void compareAppVersion() {
		// TODO Auto-generated method stub
		
	}

	private static void compareFileVersion() {
		// TODO Auto-generated method stub
		
	}
	
	private static void download(Map<String, String> files) {
		
	}
	
	private static void backup(Map<String, String> files) {
		// TODO Auto-generated method stub
		zip();
	}
	
	private static void zip() {
		// TODO Auto-generated method stub
		
	}

	private static boolean update(Map<String, String> files) {
		// TODO Auto-generated method stub
		unzip();
		corver();
		return true;
	}
	
	private static void unzip() {
		// TODO Auto-generated method stub
		
	}

	private static void corver() {
		// TODO Auto-generated method stub
		
	}

	private static void rollback(Map<String, String> files) {
		// TODO Auto-generated method stub
		unzip();
		corver();
	}
	
}
