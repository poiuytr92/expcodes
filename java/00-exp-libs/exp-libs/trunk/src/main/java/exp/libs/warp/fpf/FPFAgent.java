package exp.libs.warp.fpf;

import exp.libs.utils.io.FileUtils;

class FPFAgent {

	private _FPFServer server;
	
	private _FPFClient client;
	
	protected FPFAgent(FPFAgentConfig config) {
		this.server = new _FPFServer(config);
		this.client = new _FPFClient(config.getSrDir(), config.getOvertime());
		
		// 清空所有残留的数据流文件
		FileUtils.delete(config.getSrDir());
		FileUtils.createDir(config.getSrDir());
	}
	
	protected void _start() {
		server._start();
		client._start();
	}
	
	protected void _stop() {
		server._stop();
		client._stop();
	}
	
}
