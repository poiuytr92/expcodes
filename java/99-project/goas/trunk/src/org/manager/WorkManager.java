package org.manager;

import java.util.List;

import org.model.Work;
import org.springframework.stereotype.Repository;
import org.sys.base.BaseManagerImpl;

@Repository
public class WorkManager extends BaseManagerImpl<Work, Integer> {

	public Boolean addWork(Work work) {
		return this.save(work);
	}
	
	public Boolean updateWork(Work work) {
		return this.saveOrUpdate(work);
	}
	
	public Boolean deleteWork(Work work) {
		return this.delete(work);
	}
	
	public Boolean deleteWork(Integer id) {
		return this.delete(id);
	}
	
	public Work getWork(Integer id) {
		return this.findById(id);
	}
	
	public List<Work> getWorkList() {
		return this.findAll();
	}
	
}
