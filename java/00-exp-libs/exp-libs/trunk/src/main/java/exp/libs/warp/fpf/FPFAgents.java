package exp.libs.warp.fpf;

import java.util.List;

public class FPFAgents {

	private List<FPFAgent> agents; 
	
	public FPFAgents(List<FPFAgentConfig> agentConfs) {
		for(FPFAgentConfig config : agentConfs) {
			FPFAgent agent = new FPFAgent(config);
			agents.add(agent);
		}
	}
	
	protected void _start() {
		for(FPFAgent agent : agents) {
			agent._start();
		}
	}
	
	protected void _stop() {
		for(FPFAgent agent : agents) {
			agent._stop();
		}
	}
	
}
