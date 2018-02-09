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
		// XHR- URL注释
		// 节奏风暴：多线程抽奖
		// 跨天后重新查询手机绑定状态
		// 根据老爷/舰长/提督/总督 自动调整发言上限字数, 调整自动弹幕长度
		// 是否为老爷(控制弹幕字数, 老爷/年费老爷为30(所有房间), 提督/总督为40(仅当前房间))、是否为当前房间房管(控制权限)
		// 模拟观看时长
		// GITHUB HTTPS 的TLS协议为2.0 ?? 无法获取页面源码
		// 在线升级， 升级命令：移动文件，删除文件，添加文件,  压缩包转txt
		// 系统托盘好卡
		
	}
	
}
