package org.action;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;

import org.json.JSONException;
import org.json.JSONObject;
import org.model.User;
import org.service.PersonService;
import org.sys.base.BaseAction;
import org.util.NumberFormat;

@SuppressWarnings("serial")
public class PersonAction extends BaseAction {

	@Resource
	private PersonService personService;
	
	public void getLogUserInfo() throws JSONException, IOException {
		Map<String, Object> session = this.getSession();
		Integer logUserId = (Integer)session.get("logId");
		User user = personService.getUser(logUserId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("username", user.getUsername());
		jsonObject.put("password", user.getPassword());
		jsonObject.put("name", user.getName());
		jsonObject.put("idNum", user.getIdNum());
		jsonObject.put("dept", user.getDept());
		jsonObject.put("role", user.getRole());
		jsonObject.put("title", user.getTitle());
		jsonObject.put("tel", user.getTel());
		jsonObject.put("address", user.getAddress());
		jsonObject.put("mail", user.getMail());
		if("女".equals(user.getSex())) {
			jsonObject.put("sex", "F");
		}
		else {
			jsonObject.put("sex", "M");
		}
		this.sendMSG(jsonObject.toString());
		return;
	}
	
	public void checkIdNum() throws JSONException, IOException {
		int flag = 1;
		JSONObject jsonObject = new JSONObject();
		String idNum = (String)request.getParameter("idNum");
		
		if(idNum.length() == 18) {
//			String regNum = idNum.substring(0, 6);
			String yearNum = idNum.substring(6, 10);
			String monthNum = idNum.substring(10, 12);
			String dayNum = idNum.substring(12, 14);
//			String queNum = idNum.substring(14, 16);
			char sexNum = idNum.charAt(16);
			char lastNum = idNum.charAt(17);
			
			if(NumberFormat.isInteger(idNum.substring(0, 17)) == false) {
				flag = 0;
			}
			
			if(lastNum<'0' || lastNum>'9') {
				if (lastNum!='X' && lastNum!='x') {
					flag = 0;
				}
			}
			
			int year = Integer.parseInt(yearNum);
			int month = Integer.parseInt(monthNum);
			int day = Integer.parseInt(dayNum);
			if(month > 12) {
				flag = 0;
			}
			if(day > 31) {
				flag = 0;
			}
			if(month==4 || month==6 || month==9 || month==11) {
				if(day > 30) {
					flag = 0;
				}
			}
			if(month == 2) {
				if((year%4==0 && year%100!=0) || year%400==0) {
					if(day > 29) {
						flag = 0;
					}
				}
				else {
					if(day > 28) {
						flag = 0;
					}
				}
			}
			
			if(flag == 1) {
				String birthday = yearNum + "-" + monthNum + "-" + dayNum;
				jsonObject.put("birthday", birthday);
				
				int sex = sexNum - '0';
				if(sex%2 == 0) {
					jsonObject.put("sex", "F");
				}
				else {
					jsonObject.put("sex", "M");
				}
			}
		}
		else {
			flag = 0;
		}
		jsonObject.put("suc", flag);
		this.sendMSG(jsonObject.toString());
		return;
	}
	
	public void editUserInfo() throws JSONException, IOException {
		Map<String, Object> session = this.getSession();
		Integer logUserId = (Integer)session.get("logId");
		User user = personService.getUser(logUserId);
		
		user.setPassword((String)request.getParameter("password"));
		user.setName((String)request.getParameter("name"));
		user.setIdNum((String)request.getParameter("idNum"));
		user.setTitle((String)request.getParameter("title"));
		user.setTel((String)request.getParameter("tel"));
		user.setAddress((String)request.getParameter("address"));
		user.setMail((String)request.getParameter("mail"));
		
		char sexNum = user.getIdNum().charAt(16);
		int sex = sexNum - '0';
		if(sex%2 == 0) {
			user.setSex("女");
		}
		else {
			user.setSex("男");
		}
		Boolean suc = personService.updateUser(user);
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("suc", suc);
		this.sendMSG(jsonObject.toString());
		return;
	}
	

}
