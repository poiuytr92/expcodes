package org.action;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.manager.UserManager;
import org.manager.WorkManager;
import org.model.User;
import org.model.Work;
import org.sys.base.BaseAction;

public class WorkflowAction extends BaseAction {

	private static final long serialVersionUID = 3682073768897732267L;
	
	@Resource
	private WorkManager workManager;
	
	@Resource
	private UserManager userManager;
	
	public void add() throws JSONException, IOException {	
		JSONObject jsonObject = new JSONObject();
		Work x = new Work();
		try {
			x.setName( (String)request.getParameter("name") );
			x.setUserId( Integer.parseInt((String)request.getParameter("user_id")) );
			x.setSort( (String)request.getParameter("sort") );
			x.setContent( (String)request.getParameter("content") );
			x.setDate( new Date() );
			x.setStatus( (String)request.getParameter("status") );
			
			String wf = (String)request.getParameter("workflow");
			String[] wfSet = wf.split(",");
			String new_wf= "";
			for (int i = 1; i < wfSet.length-1; i++)
				new_wf += wfSet[i] + ",";
			new_wf += wfSet[ wfSet.length-1 ];
			x.setWorkflow( new_wf );
			x.setStage( wfSet[0] );
//			System.out.println(x.getName());
//			System.out.println(x.getContent());
			workManager.addWork(x);
			jsonObject.put("suc", 1);
		}catch(Exception e) {
			jsonObject.put("suc", 0);
		}
		sendMSG(jsonObject.toString());
	}
	
	public void del() throws JSONException, IOException {
		Integer jobId = Integer.parseInt((String)request.getParameter("id"));
		Work work = workManager.getWork(jobId);
		if( work.getStatus().equals("未审核") && workManager.delete(jobId) )
			sendMSG("{'suc':1}");
		else
			sendMSG("{'suc':0}");
	}
	
	public void notice() throws JSONException, IOException{
		Map<String, Object> session = this.getSession();
		String dept = (String)session.get("dept");
		String role = (String)session.get("role");
		
		if( role.equals("领导")) {
			List<Work> workList = workManager.getWorkList();
			
			JSONObject json = new JSONObject();
			JSONArray jArray = new JSONArray();
			
			for (Work work : workList) {
				if( work.getStage().equals(dept) ) {
					JSONObject object = new JSONObject();
					object.put("id", work.getId());
					object.put("user_id", work.getUserId());
					object.put("name", work.getName());
					object.put("sort", work.getSort());
					object.put("date", work.getDate());
					object.put("status", work.getStatus());
					jArray.put(object);
				}
			}
			json.put("Rows", jArray);
			sendMSG(json.toString());
		}
		else
			sendMSG("{'Rows':[]}");
	}
	
	public void audit() throws JSONException, IOException {
		Integer jobId = Integer.parseInt((String)request.getParameter("id"));
		Work work = workManager.getWork(jobId);
		
		if( ! work.getStatus().equals("通过")) {
			String[] wf = work.getWorkflow().split(",");
			String new_wf = "";
			
			for (int i = 1; i < wf.length-1; i++)
				new_wf += wf[i] + ",";
			new_wf += wf[ wf.length-1 ];
			
			if(wf[0].equals("完成"))
				work.setStatus("通过");
			else
				work.setStatus("审核中");
			work.setStage(wf[0]);
			work.setWorkflow(new_wf);
			workManager.updateWork(work);
			sendMSG("{'suc':1}");
		}
		else
			sendMSG("{'suc':0}");
	}
	
	public void ban() throws JSONException, IOException {
		Integer jobId = Integer.parseInt((String)request.getParameter("id"));
		Work work = workManager.getWork(jobId);		
		work.setStatus("驳回");
		work.setStage("完成");
		work.setWorkflow("");
		workManager.updateWork(work);
		sendMSG("{'suc':1}");
	}
	
	public void showNeedDeal() throws Exception{
		
		Map<String, Object> session = this.getSession();
		String dept = (String)session.get("dept");
		List<Work> workList = workManager.getWorkList();
		
		System.out.println(workList.size());
		System.out.println(dept);
		
		
		JSONObject json = new JSONObject();
		JSONArray jArray = new JSONArray();
		
		for (Work work : workList) {
			if( work.getStage().equals(dept) 
					&& !work.getStatus().equals("驳回") 
					&& !work.getStatus().equals("通过")) {
				JSONObject object = new JSONObject();
				object.put("id", work.getId());
				object.put("sort", work.getSort());
				object.put("name", work.getName());
				object.put("date", work.getDate());
				object.put("status", work.getStatus());
				object.put("workflow", work.getWorkflow());
				jArray.put(object);
			}
		}
		json.put("Rows", jArray);
		sendMSG(json.toString());
	}
	
	public void showOne() throws Exception{
		
		Work work = workManager.getWork(
				Integer.parseInt((String)request.getParameter("id")));
		User user = userManager.getUser(work.getUserId());
		
		JSONObject obj = new JSONObject();
		obj.put("id", work.getId());
		obj.put("name", work.getName());
		obj.put("dept", user.getDept());
		obj.put("sort", work.getSort());
		obj.put("date", work.getDate());
		obj.put("status", work.getStatus());
		obj.put("content", work.getContent());

		sendMSG(obj.toString());
	}

	public void showOnesAll() throws Exception{
		
		Map<String, Object> session = this.getSession();
		Integer logUserId = (Integer)session.get("logId");
		List<Work> workList = workManager.getWorkList();
		
		System.out.println(workList.size());
		System.out.println(logUserId);
		
		
		JSONObject json = new JSONObject();
		JSONArray jArray = new JSONArray();
		
		for (Work work : workList) {
			if( work.getUserId().equals(logUserId) ) {
				JSONObject object = new JSONObject();
				object.put("id", work.getId());
				object.put("sort", work.getSort());
				object.put("date", work.getDate());
				object.put("status", work.getStatus());
				object.put("content", work.getContent());
				jArray.put(object);
			}
		}
		json.put("Rows", jArray);
		sendMSG(json.toString());
	}
}