package org.manager;

import java.util.List;

import org.model.Resource;
import org.springframework.stereotype.Repository;
import org.sys.base.BaseManagerImpl;

@Repository
public class ResourceManager extends BaseManagerImpl<Resource, Integer> {

	public Boolean addResource(Resource resource) {
		return this.save(resource);
	}
	
	public Boolean updateResource(Resource resource) {
		return this.saveOrUpdate(resource);
	}
	
	public Boolean deleteResource(Resource resource) {
		return this.delete(resource);
	}
	
	public Boolean deleteResource(Integer id) {
		return this.delete(id);
	}
	
	public Boolean deleteResourceList(List<Integer> idList) {
		for (Integer id : idList) {
			deleteResource(id);
		}
		return true;
	}
	
	public Resource getResource(Integer id) {
		return this.findById(id);
	}
	
	public List<Resource> getResourceList() {
		return this.find("from Resource order by date desc");
	}
	
	public List<Resource> getResourceList(Integer userId) {
		return this.find("from Resource where userId = ? order by date desc", userId);
	}
	
}
