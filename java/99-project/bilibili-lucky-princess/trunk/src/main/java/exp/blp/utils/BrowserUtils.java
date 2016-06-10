package exp.blp.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exp.blp.Config;
import exp.libs.utils.os.CmdUtils;
import exp.libs.utils.pub.StrUtils;

public class BrowserUtils {

	public static void stopChromBrowser() {
		Pattern ptn = Pattern.compile(
				StrUtils.concat(Config.BROWSER_DRIVER_TASK_NAME, "\\s+?(\\d+)\\s"));
		String tasklist = CmdUtils.execute("tasklist");
		String[] tasks = tasklist.split("\n");
		
		for(String task : tasks) {
			if(task.startsWith(Config.BROWSER_DRIVER_TASK_NAME)) {
				Matcher mth = ptn.matcher(task);
				if(mth.find()) {
					String pid = mth.group(1);
					CmdUtils.execute(StrUtils.concat("taskkill /f /t /im ", pid));
				}
			}
		}
	}
	
}
