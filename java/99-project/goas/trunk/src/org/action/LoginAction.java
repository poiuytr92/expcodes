package org.action;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;

import org.json.JSONException;
import org.json.JSONObject;
import org.model.User;
import org.service.LoginService;
import org.sys.base.BaseAction;

@SuppressWarnings("serial")
public class LoginAction extends BaseAction {

	@Resource
	private LoginService loginService;
	
	public void in() throws JSONException, IOException {
		String username = (String)request.getParameter("username");
		String password = (String)request.getParameter("password");
		
		User user = loginService.getUser(username, password);

		JSONObject jsonObject = new JSONObject();
		if (user != null) {
			Map<String, Object> session = getSession();
			session.put("user_id", user.getId());
			session.put("name", user.getName());
			session.put("role", user.getRole());
			session.put("dept", user.getDept());
			session.put("logId", user.getId());
			jsonObject.put("suc", 1);
		} else {
			jsonObject.put("suc", 0);
		}
		sendMSG(jsonObject.toString());
	}
	
	public String out() {
		getSession().clear();
		return "out";
	}
	
	public void getLogInfo() throws JSONException, IOException {
		Map<String, Object> session = this.getSession();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("name", (String)session.get("name"));
		jsonObject.put("role", (String)session.get("role"));
		jsonObject.put("dept", (String)session.get("dept"));
		jsonObject.put("logId", (Integer)session.get("logId"));
		this.sendMSG(jsonObject.toString());
		return;
	}
}
