package org.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.model.User;
import org.service.UserService;
import org.sys.base.BaseAction;

@SuppressWarnings("serial")
public class UserAction extends BaseAction {

private String username;
	
	private Integer id;

	private String password;
	
	private String name;
	
	private String sex;
	
	private String dept;
	
	private String role;
	
	private String title;
	
	private String idNum;
	
	private String tel;
	
	private String address;
	
	private String mail;
	
	@Resource
	private UserService userService;
	
	public void showUserList() throws Exception{
		List<User> userList = userService.getUserList();
		
		JSONObject json = new JSONObject();
		JSONArray jArray = new JSONArray();
		
		for (User user : userList) {
			JSONObject object = new JSONObject();
			object.put("id", user.getId());
			object.put("username", user.getUsername());
			object.put("name", user.getName());
			object.put("address", user.getAddress());
			object.put("dept", user.getDept());
			object.put("idNum", user.getIdNum());
			object.put("mail", user.getMail());
			object.put("role", user.getRole());
			object.put("sex", user.getSex());
			object.put("tel", user.getTel());
			object.put("title", user.getTitle());
			
			jArray.put(object);
		}
		
		json.put("Rows", jArray);		
		sendMSG(json.toString());
	}
	
	public void getUser() throws JSONException, IOException {
		User user = userService.getUser(id);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("address", user.getAddress());
		jsonObject.put("id", id);
		jsonObject.put("dept", user.getDept());
		jsonObject.put("idNum", user.getIdNum());
		jsonObject.put("mail", user.getMail());
		jsonObject.put("name", user.getName());
		jsonObject.put("role", user.getRole());
		jsonObject.put("sex", user.getSex());
		jsonObject.put("tel", user.getTel());
		jsonObject.put("title", user.getTitle());
		jsonObject.put("username", user.getUsername());
		jsonObject.put("password", user.getPassword());
		sendMSG(jsonObject.toString());
	}
	
	public void addUser() throws JSONException, IOException {
		User user = new User();
		user.setAddress(address);
		user.setDept(dept);
		user.setIdNum(idNum);
		user.setMail(mail);
		user.setName(name);
//		user.setPassword(password);
		user.setPassword("1");
		user.setRole(role);
		user.setSex(sex);
		user.setTel(tel);
		user.setTitle(title);
		user.setUsername(username);
		System.out.println(username);
		System.out.println(name);
		System.out.println(dept);
		System.out.println(role);
		Boolean suc = userService.addUser(user);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("suc", suc);
		sendMSG(jsonObject.toString());
	}
	
	public void updateUser() throws JSONException, IOException {
		User user = new User();
		user.setId(id);
		user.setAddress(address);
		user.setDept(dept);
		user.setIdNum(idNum);
		user.setMail(mail);
		user.setName(name);
		user.setPassword(password);
		user.setRole(role);
		user.setSex(sex);
		user.setTel(tel);
		user.setTitle(title);
		user.setUsername(username);
		
		Boolean suc = userService.updateUser(user);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("suc", suc);
		sendMSG(jsonObject.toString());
	}
	
	public void deleteUserList() throws JSONException, IOException {
		
		List<Integer> idList = new ArrayList<Integer>();
		String idListStr = (String)request.getParameter("idListStr");
		String[] idStrs = idListStr.split(":");
		
		for (String id : idStrs) {
			
			idList.add(Integer.parseInt(id));
		}
		Boolean suc = userService.deleteUserList(idList);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("suc", suc);
		sendMSG(jsonObject.toString());
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIdNum() {
		return idNum;
	}

	public void setIdNum(String idNum) {
		this.idNum = idNum;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}
}
