package exp.libs;

import exp.libs.utils.other.LogUtils;
import exp.libs.warp.ver.VersionMgr;

/**
 * <PRE>
 * 版本类.
 *  版本信息记录在 ./src/main/resources/.verinfo 中, 请勿删除该文件.
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-07-11
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
final class Version {

	public static void main(String[] args) {
		LogUtils.loadLogBackConfig();
		VersionMgr.exec(args);
		
		// TODO 注释生成器
		// Swing系统托盘长时间运行后变得好卡
		// 在线升级， 升级命令：移动文件，删除文件，添加文件,  压缩包转txt
	}
	
}
