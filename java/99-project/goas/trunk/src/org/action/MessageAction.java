package org.action;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.model.Message;
import org.model.User;
import org.service.MessageService;
import org.sys.base.BaseAction;

@SuppressWarnings("serial")
public class MessageAction extends BaseAction {

	@Resource
	private MessageService messageService;
	
	public void showMessagelist() throws Exception{
		List<User> userList = messageService.getUserList();
		
		JSONArray jArray = new JSONArray();
		
		Integer id = 1;
		Integer pid = userList.size()+1;
		Map<String, Integer> deptId = new HashMap<String, Integer>();
		String leaderIcon = "../jqueryLiger/lib/ligerUI/skins/icons/leader2.png";
		String staffIcon = "../jqueryLiger/lib/ligerUI/skins/icons/staff2.png";
		String deptIcon = "../jqueryLiger/lib/ligerUI/skins/icons/dept2.png";
		for (User user : userList) {
			JSONObject object = new JSONObject();
			if (!deptId.containsKey(user.getDept())) {
				deptId.put(user.getDept(), pid);
				JSONObject dept = new JSONObject();
				dept.put("id", pid);
				dept.put("pid", 0);
				dept.put("text", user.getDept());
				dept.put("icon", deptIcon);
				jArray.put(dept);
				pid++;
			}
			object.put("id", id);
			object.put("pid", deptId.get(user.getDept()));
			object.put("userId", user.getId());
			object.put("text", user.getName());
			if ("领导".equals(user.getRole()))
				object.put("icon", leaderIcon);
			else
				object.put("icon", staffIcon);
			jArray.put(object);
			id++;
		}

		sendMSG(jArray.toString());
	}
	
	public void sendMessage() throws JSONException, IOException {
		
		String theme = (String)request.getParameter("theme");
		String content = (String)request.getParameter("content");
		String idListStr = (String)request.getParameter("idListStr");
		String[] ids = idListStr.split(";");
		
		Integer sendId = (Integer)getSession().get("logId");
		
		Boolean suc = false;
		for (String id : ids) {
			
			Message message = new Message();
//			message.setAccessoryUrl(accessoryUrl);
			message.setContent(content);
			message.setRecId(Integer.parseInt(id));
			message.setSendId(sendId);
			message.setStatus("未读");
			message.setTheme(theme);
			message.setTime(new Date());
			
			suc = messageService.addMessage(message);
		}

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("suc", suc);
		sendMSG(jsonObject.toString());
	}
	
	public void getSendMessageList() throws JSONException, IOException {
		
		Integer sendId = (Integer)getSession().get("logId");
		
		List<Message> messageList = messageService.getSendMessageList(sendId);
		
		JSONObject json = new JSONObject();
		JSONArray jArray = new JSONArray();
		
		for (Message message : messageList) {
			JSONObject object = new JSONObject();
			object.put("id", message.getId());
			object.put("theme", message.getTheme());
			object.put("recName", message.getRecName());
			object.put("time", message.getTime());
			object.put("status", message.getStatus());
			
			jArray.put(object);
		}
		
		json.put("Rows", jArray);		
		sendMSG(json.toString());
	}
	
	public void getRecMessageList() throws JSONException, IOException {
		
		Integer recId = (Integer)getSession().get("logId");
		
		List<Message> messageList = messageService.getRecMessageList(recId);
		
		JSONObject json = new JSONObject();
		JSONArray jArray = new JSONArray();
		
		for (Message message : messageList) {
			JSONObject object = new JSONObject();
			object.put("id", message.getId());
			object.put("theme", message.getTheme());
			object.put("sendName", message.getSendName());
			object.put("time", message.getTime());
			object.put("status", message.getStatus());
			
			jArray.put(object);
		}
		
		json.put("Rows", jArray);		
		sendMSG(json.toString());
	}
	
	
	public void lookMessage() throws JSONException, IOException {
		Integer messageId = Integer.parseInt(request.getParameter("id")+"");
		String isRead = (String)request.getParameter("isRead");
		
		Message message = messageService.getMessage(messageId);
		
		if ("1".equals(isRead)) {
			
			message.setStatus("已读");
			messageService.updateMessage(message);
		}
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("theme", message.getTheme());
		jsonObject.put("recName", message.getRecName());
		jsonObject.put("sendName", message.getSendName());
		jsonObject.put("time", message.getTime());
		jsonObject.put("content", message.getContent());
		sendMSG(jsonObject.toString());
	}
	
	public void getMessageCount() throws JSONException, IOException {
		
		Integer recId = (Integer)getSession().get("logId");
		
		List<Message> messageList = messageService.getRecMessageList(recId);
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("num", messageList.size());
		sendMSG(jsonObject.toString());
	}
}
