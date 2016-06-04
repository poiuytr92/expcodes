package org.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.model.Inform;
import org.model.User;
import org.service.InformService;
import org.service.UserService;
import org.sys.base.BaseAction;
import org.util.DateUnit;

@SuppressWarnings("serial")
public class InformAction extends BaseAction {

	@Resource
	private InformService informService;
	
	@Resource
	private UserService userService;
	
	public void addInform() throws JSONException, IOException {
		Map<String, Object> session = this.getSession();
		Inform inform = new Inform();
		inform.setTheme((String)request.getParameter("theme"));
		inform.setContent((String)request.getParameter("content"));
		inform.setStatus((String)request.getParameter("status"));
		inform.setUserId((Integer)session.get("logId"));
		inform.setDate(new Date());
		JSONObject jsonObject = new JSONObject();
		Boolean suc = informService.addInform(inform);
		jsonObject.put("suc", suc);
		this.sendMSG(jsonObject.toString());
		return;
	}
	
	public void updateInform() throws JSONException, IOException {
		Map<String, Object> session = this.getSession();
		Inform inform = informService.getInform(Integer.parseInt((String)request.getParameter("informId")));
		inform.setTheme((String)request.getParameter("theme"));
		inform.setContent((String)request.getParameter("content"));
		inform.setStatus((String)request.getParameter("status"));
		inform.setUserId((Integer)session.get("logId"));
		inform.setDate(new Date());
		JSONObject jsonObject = new JSONObject();
		Boolean suc = informService.updateInform(inform);
		jsonObject.put("suc", suc);
		this.sendMSG(jsonObject.toString());
		return;
	}
	
	public void deleteInformList() throws JSONException, IOException {
		List<Integer> idList = new ArrayList<Integer>();
		String idListStr = (String)request.getParameter("idListStr");
		String[] idStrs = idListStr.split(":");
		
		for (String id : idStrs) {
			idList.add(Integer.parseInt(id));
		}
		Boolean suc = informService.delInformList(idList);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("suc", suc);
		this.sendMSG(jsonObject.toString());
		return;
	}
	
	public void getInform() throws JSONException, IOException {
		Map<String, Object> session = this.getSession();
		String logUserName = (String)session.get("name");
		Integer informId = Integer.parseInt(((String)request.getParameter("informId")));
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("logUserName", logUserName);
		jsonObject.put("currentDate", DateUnit.dateToStr(new Date(), "MEDIUM"));
		
		if(informId.intValue() == 0) {
			this.sendMSG(jsonObject.toString());
			return;
		}
		Inform inform = informService.getInform(informId);
		
		jsonObject.put("informId", informId);
		jsonObject.put("status", inform.getStatus());
		jsonObject.put("theme", inform.getTheme());
		jsonObject.put("content", inform.getContent());
		jsonObject.put("date", DateUnit.dateToStr(inform.getDate(), "MEDIUM"));
		
		if(inform.getUserId() == null) {
			jsonObject.put("userId", 0);
			jsonObject.put("userName", "");
		}
		else {
			User user = userService.getUser(inform.getUserId());
			if(user == null) {
				jsonObject.put("userId", inform.getUserId());
				jsonObject.put("userName", "");
			}
			else {
				jsonObject.put("userId", inform.getUserId());
				jsonObject.put("userName", user.getName());
			}
		}
		
		this.sendMSG(jsonObject.toString());
		return;
	}
	
	public void getInformList() throws JSONException, IOException {
		JSONObject json = new JSONObject();
		JSONArray jArray = new JSONArray();
		
		List<Inform> newInformList = informService.getSortInform(-1, 'D');
		
		if (newInformList!=null && newInformList.size()>0) {
			for (Inform inform : newInformList) {
				JSONObject object = new JSONObject();
				object.put("informId", inform.getId());
				object.put("status", inform.getStatus());
				object.put("theme", inform.getTheme());
				object.put("content", inform.getContent());
				object.put("date", inform.getDate());
				
				if(inform.getUserId() == null) {
					object.put("userId", 0);
					object.put("userName", "");
				}
				else {
					User user = userService.getUser(inform.getUserId());
					if(user == null) {
						object.put("userId", inform.getUserId());
						object.put("userName", "");
					}
					else {
						object.put("userId", inform.getUserId());
						object.put("userName", user.getName());
					}
				}
				
				jArray.put(object);
			}
		}
		json.put("Rows", jArray);
		this.sendMSG(json.toString());
		return;
	}
	
	public void getNewInform() throws JSONException, IOException {
		JSONArray jArray = new JSONArray();
		List<Inform> newInformList = informService.getSortInform(5, 'D');
		
		if (newInformList!=null && newInformList.size()>0) {
			for (Inform inform : newInformList) {
				JSONObject object = new JSONObject();
				String status = inform.getStatus();
				String theme = inform.getTheme();
				String date = DateUnit.dateToStr(inform.getDate());
				String userName;
				Integer userId = inform.getUserId();
				User user = userService.getUser(userId);
				if(user == null) {
					userName = "GOAS";
				}
				else {
					userName = user.getName();
				}
				object.put("inform", "["+status+"] "+theme+"<span style=\"display: block; float: right; width: 250px; text-align: right; overflow: hidden\"> [ "+date+" "+userName+" ] 发布</span>");
				jArray.put(object);
			}
		}
		
		JSONObject json = new JSONObject();
		json.put("Rows", jArray);
		this.sendMSG(json.toString());
		return;
	}
}
