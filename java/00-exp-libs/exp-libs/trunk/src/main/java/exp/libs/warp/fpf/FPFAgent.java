package exp.libs.warp.fpf;


class FPFAgent {

	private _FPFServer server;
	
	private _FPFClient client;
	
	protected FPFAgent(_SRFileMgr srFileMgr, FPFConfig config) {
		this.server = new _FPFServer(srFileMgr, config);
		this.client = new _FPFClient(srFileMgr, config.getOvertime());
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
