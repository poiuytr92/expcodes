package exp.bilibili.plugin.cache.test;

import exp.bilibili.plugin.cache.ActivityMgr;
import exp.libs.envm.Charset;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.io.flow.FileFlowReader;


/**
 * <PRE>
 * 用户活跃度管理器.
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class TestActivityMgr {
	
	public static void main(String[] args) {
		read();
	}
	
	private static void read() {
		FileFlowReader ffr = new FileFlowReader(ActivityMgr.COST_PATH, Charset.ISO);
		while(ffr.hasNextLine()) {
			String line = ffr.readLine();
			line = CryptoUtils.deDES(line.trim());
			
			try {
				String[] datas= line.split("=");
				String info = StrUtils.concat("UID:", datas[0], 
						", 用户:", datas[2], ", 活跃度:", datas[1]);
				System.out.println(info);
				
			} catch(Exception e) {}
		}
		ffr.close();
	}
	
}
