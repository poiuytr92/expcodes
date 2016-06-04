package org.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.manager.ResourceManager;
import org.manager.UserManager;
import org.model.User;
import org.springframework.stereotype.Service;

@Service
public class ResourceService {

	@Resource
	private UserManager userManager;
	
	@Resource
	private ResourceManager resourceManager;
	
	public Boolean addResource(org.model.Resource resource) {
		return resourceManager.addResource(resource);
	}
	
	public Boolean updateResource(org.model.Resource resource) {
		return resourceManager.updateResource(resource);
	}
	
	public org.model.Resource getResource(Integer id) {
		return resourceManager.getResource(id);
	}
	
	public List<org.model.Resource> getResourceList(Integer userId) {
		List<org.model.Resource> resourceList = resourceManager.getResourceList(userId);
		
		return setUploader(resourceList);
	}
	
	public List<org.model.Resource> getResourceList() {
		List<org.model.Resource> resourceList = resourceManager.getResourceList();
		
		return setUploader(resourceList);
	}
	
	private List<org.model.Resource> setUploader(List<org.model.Resource> resourceList) {
		Map<Integer, User> userMap = userManager.getUserMap();
		
		String name = "用户已删除";
		for (org.model.Resource resource : resourceList) {
			if (userMap.containsKey(resource.getUserId()))
				name = userMap.get(resource.getUserId()).getName();
			else
				name = "用户已删除";
			resource.setUploader(name);
		}
		
		return resourceList;
	}
	
	public Boolean deleteResourceList(List<Integer> idList) {
		return resourceManager.deleteResourceList(idList);
	}

	public Boolean updateResourceList(List<Integer> idList, String sort) {
		for (Integer id : idList) {
			org.model.Resource resource = resourceManager.getResource(id);
			resource.setSort(sort);
			resourceManager.updateResource(resource);
		}
		return true;
	}
}
