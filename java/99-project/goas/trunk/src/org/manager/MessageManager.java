package org.manager;

import java.util.List;

import org.model.Message;
import org.springframework.stereotype.Repository;
import org.sys.base.BaseManagerImpl;

@Repository
public class MessageManager extends BaseManagerImpl<Message, Integer> {

	public Boolean addMessage(Message message) {
		return this.save(message);
	}
	
	public Boolean updateMessage(Message message) {
		return this.saveOrUpdate(message);
	}
	
	public Boolean deleteMessage(Message message) {
		return this.delete(message);
	}
	
	public Boolean deleteMessage(Integer id) {
		return this.delete(id);
	}
	
	public Boolean deleteMessageList(List<Integer> idList) {
		Boolean suc = false;
		for (Integer id :idList) {
			suc = deleteMessage(id);
		}
		return suc;
	}
	
	public Message getMessage(Integer id) {
		return this.findById(id);
	}
	
	public List<Message> getMessageList() {
		return this.findAll();
	}
	
	public List<Message> getReceiveMessageList(Integer userId) {
		return this.find("from Message where recId = ? order by time desc", userId);
	}
	
	public List<Message> getSendMessageList(Integer userId) {
		return this.find("from Message where sendId = ? order by time desc", userId);
	}
	
}
