package exp.libs.warp.ver;

import java.io.File;

import exp.libs.utils.io.FileUtils;
import exp.libs.warp.ui.BeautyEyeUtils;

class _Version {

	/**
	 * 存储版本信息的文件位置.
	 * 	[src/main/resources] 为Maven项目默认的资源目录位置（即使非Maven项目也可用此位置）
	 */
	private final static String VER_FILE = "./src/main/resources/.verinfo";
	
	/** 版本信息文件 */
	private File ver;
	
	/**
	 * 构造函数：创建版本信息文件
	 */
	protected _Version() {
		this.ver = FileUtils.createFile(VER_FILE);
	}
	
	protected void print() {
		// 打印DOS
	}
	
	protected void manage() {
		BeautyEyeUtils.init();
		
		_PrjVerInfo prjVerInfo = new _PrjVerInfo(null);
		prjVerInfo.setPrjName("测试用项目");
		_VerMgrUI.getInstn(prjVerInfo); // FIXME
	}

}
