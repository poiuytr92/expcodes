package exp.bilibli.plugin.core.lottery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.warp.thread.LoopThread;

public class LotteryMgr extends LoopThread {

	private final static Logger log = LoggerFactory.getLogger(LotteryMgr.class);
	
	protected LotteryMgr() {
		super("自动抽奖姬");
	}

	@Override
	protected void _before() {
		log.info("{} 已启动", getName());
	}

	@Override
	protected void _loopRun() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void _after() {
		log.info("{} 已停止", getName());
	}

}
