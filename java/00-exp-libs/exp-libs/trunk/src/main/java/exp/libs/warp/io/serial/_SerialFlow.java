package exp.libs.warp.io.serial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.os.OSUtils;
import exp.libs.utils.other.PathUtils;

class _SerialFlow {

	/** 日志器 */
	protected final static Logger log = LoggerFactory.getLogger(SerialFlowReader.class);
	
	/** 默认序列化文件位置 */
	protected final static String DEFAULT_FILEPATH = OSUtils.isRunByTomcat() ? 
			PathUtils.getProjectCompilePath().concat("/serializable.dat") : 
			"./serializable.dat";
			
}
