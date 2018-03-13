package exp.bilibili.plugin;

import exp.libs.utils.other.LogUtils;
import exp.libs.warp.ver.VersionMgr;

/**
 * <PRE>
 * 版本类.
 *  版本信息记录在 ./src/main/resources/.verinfo 中, 请勿删除该文件.
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Version {

	/**
	 * 版本管理入口, 任何项目均不需修改此代码.
	 * @param args 入口参数（win下默认为-m, linux下强制为-p）
	 * 		[-p] 打印最后的版本信息（DOS界面）
	 * 		[-m] 版本管理（UI界面）
	 */
	public static void main(String[] args) {
		LogUtils.loadLogBackConfig();
		VersionMgr.exec(args);
		
		// TODO
		// WS- 注释  ,OCR  RSA代码整理
		// 模拟APP观看时长	(如何获取app的cookie ?? 安卓模拟器抓包？？？  手机节奏风暴不需要验证码)
		// cookie到期注销后，小号管理面板没有对应删除该项，只能重启时生效
		// 小号管理面板 悬浮鼠标显示用户ID
		// 自动投喂开关记录到cookie
	}
	
}
