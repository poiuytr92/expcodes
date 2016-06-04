package org.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.manager.InformManager;
import org.model.Inform;
import org.springframework.stereotype.Service;
import org.util.sortRule.InformSortRuleA;
import org.util.sortRule.InformSortRuleD;

@Service
public class InformService {

	@Resource
	private InformManager informManager;
	
	public Boolean addInform(Inform inform) {
		return informManager.addInform(inform);
	}
	
	public Boolean delInform(Integer id) {
		return informManager.deleteInform(id);
	}
	
	public Boolean delInformList(List<Integer> idList) {
		return informManager.deleteInformList(idList);
	}
	
	public Boolean updateInform(Inform inform) {
		return informManager.updateInform(inform);
	}
	
	//获取指定的通告
	public Inform getInform(Integer id) {
		return informManager.getInform(id);
	}
	
	//获取cnt条通告
	//sort=='a' 按通告发布时间升序排序（优先显示最旧的通告）
	//sort=='d' 按通告发布时间降序排序（优先显示最新的通告）
	public List<Inform> getSortInform(int cnt, char sort) {
		return getRangeInform(0, cnt, sort);
	}
	
	//获取最新的第start到第end条记录
	//若end==-1 ，则获取所有记录（并按sort排序）
	public List<Inform> getRangeInform(int start, int end, char sort) {
		
		boolean flag = false;
		if(end == -1) {
			flag = true;
		}
		else if(start<0 || start>end) {
			return new ArrayList<Inform>();
		}
		
		List<Inform> allInformList = getAllInform();
		if(start > allInformList.size()) {
			start = allInformList.size();
			end = allInformList.size();
		}
		if(end > allInformList.size()) {
			end = allInformList.size();
		}
		
		//对通告记录排序
		if(sort=='a' || sort=='A') {
			InformSortRuleA ascending = new InformSortRuleA();
			Collections.sort(allInformList, ascending);
		}
		else {
			InformSortRuleD descending = new InformSortRuleD();
			Collections.sort(allInformList, descending);
		}
		
		List<Inform> rangeInformList = new ArrayList<Inform>();
		if(flag == true) {
			rangeInformList.addAll(allInformList);
		}
		else {
			for(int i=start; i<end; i++) {
				rangeInformList.add(allInformList.get(i));
			}
		}
		return rangeInformList;
	}
	
	//获取所有通告
	public List<Inform> getAllInform() {
		return informManager.getInformList();
	}
}
