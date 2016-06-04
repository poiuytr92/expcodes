package org.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.struts2.ServletActionContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.model.Resource;
import org.service.ResourceService;
import org.sys.base.BaseAction;

@SuppressWarnings("serial")
public class ResourceAction extends BaseAction {

	@javax.annotation.Resource
	private ResourceService resourceService;
	
	private File uploadify;
	
    private String uploadifyContentType;
    
    private String uploadifyFileName;
    
    private String savePath;
    
    private Integer resourceId;
    
    private InputStream inputStream;
    
    private String fileName;
    
    public String execute() throws Exception {   
    	
    	Resource resource = resourceService.getResource(resourceId);
    	
		String filePath = ServletActionContext.getServletContext().getRealPath("/")+"\\" + resource.getResUrl();
		fileName = resource.getName();
		
		fileName = new String(fileName.getBytes(), "ISO8859-1");
		
		inputStream = new FileInputStream(filePath);
       
        return "success";
    }     
    

	public void upload() throws IOException, JSONException {
        File dir=new File(getSavePath());  
	    if(!dir.exists()){  
	        dir.mkdirs();  
	    }

	    Resource resource = new Resource();
	    resource.setDate(new Date());
	    resource.setName(getUploadifyFileName());
	    //resource.setSort((String)request.getParameter("sort"));
	    resource.setSort("new");
	    resource.setUserId((Integer)getSession().get("logId"));
	    
		Boolean suc = resourceService.addResource(resource);
		String fileName = getUploadifyFileName();
		fileName = resource.getId() + fileName.substring(fileName.lastIndexOf("."));
		resource.setResUrl("\\uploads\\"+fileName);
		String url = getSavePath()+"\\" + fileName;
		resourceService.updateResource(resource);

		FileOutputStream fos=new FileOutputStream(url);  
        FileInputStream fis=new FileInputStream(getUploadify());  
        byte[] buffers=new byte[1024];  
        int len=0;  
        while((len=fis.read(buffers))!=-1){  
            fos.write(buffers,0,len);
        }
        fis.close(); //关闭输入流
        fos.close(); //关闭输出流
        
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("suc", suc);
		jsonObject.put("id", resource.getId()+"");
		sendMSG(jsonObject.toString());
    }
    
	public void showResourceList() throws Exception{
		
		List<Resource> resourceList = resourceService.getResourceList((Integer)getSession().get("logId"));
		
		JSONObject json = new JSONObject();
		JSONArray jArray = new JSONArray();
		
		for (Resource resource : resourceList) {
			JSONObject object = new JSONObject();
			object.put("id", resource.getId());
			object.put("name", resource.getName());
			object.put("sort", resource.getSort());
			object.put("uploader", resource.getUploader());
			object.put("date", resource.getDate());
			object.put("resUrl", resource.getResUrl());
			
			jArray.put(object);
		}
		
		json.put("Rows", jArray);		
		sendMSG(json.toString());
	}
	
	public void showAllResourceList() throws Exception{
		
		List<Resource> resourceList = resourceService.getResourceList();
		
		JSONObject json = new JSONObject();
		JSONArray jArray = new JSONArray();
		
		for (Resource resource : resourceList) {
			JSONObject object = new JSONObject();
			object.put("id", resource.getId());
			object.put("name", resource.getName());
			object.put("sort", resource.getSort());
			object.put("uploader", resource.getUploader());
			object.put("date", resource.getDate());
			object.put("resUrl", resource.getResUrl());
			
			jArray.put(object);
		}
		
		json.put("Rows", jArray);		
		sendMSG(json.toString());
	}
	
	
	public void deleteResourceList() throws JSONException, IOException {
		
		String path = getSavePath()+"\\";
    	
		List<Integer> idList = new ArrayList<Integer>();
		String idListStr = (String)request.getParameter("idListStr");
		String[] idStrs = idListStr.split(":");
		
		for (String str : idStrs) {
			String[] idUrl = str.split("\\*");
			if (idUrl.length == 2) {
				String[] url = idUrl[1].split("[/|\\\\]");
				File file=new File(path + url[url.length-1]);
				file.delete();
			}
			idList.add(Integer.parseInt(idUrl[0]));
		}
		Boolean suc = resourceService.deleteResourceList(idList);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("suc", suc);
		sendMSG(jsonObject.toString());
	}
	
	public void updateResourceList() throws JSONException, IOException {
		
		List<Integer> idList = new ArrayList<Integer>();
		String idListStr = (String)request.getParameter("idListStr");
		String sort = (String)request.getParameter("sort");
		String[] idStrs = idListStr.split(":");
		for (String id : idStrs) {
			idList.add(Integer.parseInt(id));
		}
		Boolean suc = resourceService.updateResourceList(idList, sort);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("suc", suc);
		sendMSG(jsonObject.toString());
	}
	
	public File getUploadify() {
	        return uploadify;
	}

    public void setUploadify(File uploadify) {
        this.uploadify = uploadify;
    }

    public String getUploadifyFileName() {
        return uploadifyFileName;
    }

    public void setUploadifyFileName(String uploadifyFileName) {
        this.uploadifyFileName = uploadifyFileName;
    }

	public String getUploadifyContentType() {
		return uploadifyContentType;
	}

	public void setUploadifyContentType(String uploadifyContentType) {
		this.uploadifyContentType = uploadifyContentType;
	}

	public String getSavePath() {
		return ServletActionContext.getServletContext().getRealPath(savePath); 
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public Integer getResourceId() {
		return resourceId;
	}

	public void setResourceId(Integer resourceId) {
		this.resourceId = resourceId;
	}
	
	 public InputStream getInputStream() {
			return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
