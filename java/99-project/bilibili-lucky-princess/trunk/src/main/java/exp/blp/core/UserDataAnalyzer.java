package exp.blp.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import exp.blp.AppUI;
import exp.blp.Config;
import exp.blp.bean.UserData;
import exp.blp.utils.TimeUtils;
import exp.blp.utils.UIUtils;
import exp.libs.envm.Charset;
import exp.libs.utils.pub.FileUtils;
import exp.libs.utils.pub.StrUtils;

public class UserDataAnalyzer {

	private Map<String, UserData> userdatas;
	
	private String lastLine;
	
	public UserDataAnalyzer() {
		this.userdatas = new HashMap<String, UserData>();
		this.lastLine = "";
	}
	
	public void statistics(String data) {
		if(StrUtils.isNotEmpty(data)) {
			statistics(Arrays.asList(data.split("\n")));
			write();
		}
	}
	
	private void statistics(List<String> chatMsgList) {
		if(chatMsgList == null || chatMsgList.size() <= 0) {
			return;
		}
		
		int idx = 0;
		if(!StrUtils.isEmpty(lastLine)) {
			idx = chatMsgList.indexOf(lastLine);
			idx = (idx < 0 ? 0 : idx);
		}
		
		for(int i = idx + 1; i < chatMsgList.size(); i++) {
			String line = chatMsgList.get(i);
			UIUtils.log(line);
			FileUtils.write(Config.CHAT_MSG_LIST_FILE_PATH, 
					StrUtils.concat(TimeUtils.getCurTimePrefix(), line), 
					Charset.UTF8, true);
			
			// 空消息
			if(StrUtils.isEmpty(line)) {
				continue;
				
			// 聊天消息
			} else if(line.contains(" : ")) {
				int pos = line.indexOf(" : ");
				String username = line.substring(0, pos).trim();
				String msg = line.substring(pos + " : ".length()).trim();
				UserData userdata = userdatas.get(username);
				if(userdata == null) {
					userdata = new UserData(username);
					userdatas.put(username, userdata);
				}
				
				userdata.addAction();
				if(UserData.KEY_JOIN.equals(msg)) {
					userdata.setJoin(true);
					
				} else if(UserData.KEY_UNJOIN.equals(msg)) {
					userdata.setJoin(false);
				}
					
			// 赠送礼物
			} else if(line.contains("赠送") && !line.contains("】在直播间【")) {
				String username = line.substring(0, line.indexOf("赠送")).trim();
				UserData userdata = userdatas.get(username);
				if(userdata == null) {
					userdata = new UserData(username);
					userdatas.put(username, userdata);
				}
				userdata.addAction();
				
			// 进入房间
			} else if(line.contains(" 老爷进入直播间")) {
				String username = line.substring(0, line.indexOf(" 老爷进入直播间")).trim();
				UserData userdata = new UserData(username);
				userdatas.put(username, userdata);
				
			// 离开房间
			} else if(line.contains(" 老爷离开直播间")) {
				String username = line.substring(0, line.indexOf(" 老爷离开直播间")).trim();
				userdatas.remove(username);
				
			// 无效消息
			} else {
				// Undo
			}
			
			lastLine = line;
		}
		AppUI.getInstn().reflashUserDatas();
	}
	
	private void write() {
		StringBuilder sb = new StringBuilder("<div id=\"tagsList\">\r\n");
		Iterator<UserData> userdataIts = userdatas.values().iterator();
		while(userdataIts.hasNext()) {
			UserData userdate = userdataIts.next();
			sb.append("      <a href=\"#\" title=\"").append(userdate.getUsername());
			sb.append("\">").append(userdate.getUsername()).append("</a>\r\n");
		}
		sb.append("    </div>");
		
		if(userdatas.size() > 0) {
			String web = FileUtils.readFile(Config.HOME_PAGE_PATH, Charset.UTF8);
			web = web.replaceFirst("<div id=\"tagsList\">[\\s\\S]*?</div>", sb.toString());
			FileUtils.write(Config.HOME_PAGE_PATH, web, Charset.UTF8, false);
		}
	}
	
	public Map<String, UserData> getUserdatas() {
		return userdatas;
	}
	
}
