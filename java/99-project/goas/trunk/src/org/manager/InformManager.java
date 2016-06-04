package org.manager;

import java.util.List;

import org.model.Inform;
import org.springframework.stereotype.Repository;
import org.sys.base.BaseManagerImpl;

@Repository
public class InformManager extends BaseManagerImpl<Inform, Integer> {

	public Boolean addInform(Inform inform) {
		return this.save(inform);
	}
	
	public Boolean updateInform(Inform inform) {
		return this.saveOrUpdate(inform);
	}
	
	public Boolean deleteInform(Inform inform) {
		return this.delete(inform);
	}
	
	public Boolean deleteInform(Integer id) {
		return this.delete(id);
	}
	
	public Boolean deleteInformList(List<Integer> idList) {
		for(Integer id : idList) {
			this.delete(id);
		}
		return true;
	}
	
	public Inform getInform(Integer id) {
		return this.findById(id);
	}
	
	public List<Inform> getInformList() {
		return this.findAll();
	}
	
}
