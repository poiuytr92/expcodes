package exp.bilibili.protocol.bean.xhr;

import net.sf.json.JSONObject;
import exp.bilibili.protocol.envm.BiliCmdAtrbt;
import exp.libs.utils.format.JsonUtils;

/**
 * <PRE>
 * 小学数学日常任务
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class MathTask {

	public final static MathTask NULL = new MathTask(null);
	
	private final static int MAX_STEP = 9;
	
	/** 是否存在下一轮任�? */
	private boolean existNext;
	
	private long bgnTime;
	
	private long endTime;
	
	/** 当前任务轮数 */
	private int curRound;
	
	/** 最大任务轮�? */
	private int maxRound;
	
	/** 当前轮的执行阶段:3min/6min/9min */
	private int step;
	
	public MathTask(JSONObject json) {
		this.existNext = true;
		if(json != null) {
			JSONObject data = JsonUtils.getObject(json, BiliCmdAtrbt.data);
			this.bgnTime = JsonUtils.getLong(data, BiliCmdAtrbt.time_start, 0);
			this.endTime = JsonUtils.getLong(data, BiliCmdAtrbt.time_end, 0);
			this.curRound = JsonUtils.getInt(data, BiliCmdAtrbt.times, 0);
			this.maxRound = JsonUtils.getInt(data, BiliCmdAtrbt.max_times, 0);
			this.step = JsonUtils.getInt(data, BiliCmdAtrbt.minute, 0);
			
		} else {
			this.bgnTime = 0;
			this.endTime = 0;
			this.curRound = 0;
			this.maxRound = 0;
			this.step = MAX_STEP;
		}
	}
	
	public boolean existNext() {
		return (existNext && !(curRound >= maxRound && step >= MAX_STEP));
	}
	
	public void setExistNext(boolean exist) {
		this.existNext = exist;
	}

	public long getBgnTime() {
		return bgnTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public int getCurRound() {
		return curRound;
	}

	public int getMaxRound() {
		return maxRound;
	}

	public int getStep() {
		return step;
	}
	
}
