package org.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.manager.MessageManager;
import org.manager.UserManager;
import org.model.Message;
import org.model.User;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

	@Resource
	private UserManager userManager;
	
	@Resource
	private MessageManager messageManager;
	
	public List<User> getUserList() {
		return userManager.getUserList();
	}
	
	public List<Message> getSendMessageList(Integer userId) {
		return transIdToName(messageManager.getSendMessageList(userId));
	}
	
	public List<Message> getRecMessageList(Integer userId) {
		return transIdToName(messageManager.getReceiveMessageList(userId));
	}
	
	private List<Message> transIdToName(List<Message> messageList) {
		
		Map<Integer, User> userMap = userManager.getUserMap();
		
		String name = "用户已删除";
		for (Message message : messageList) {
			if (userMap.containsKey(message.getSendId()))
				name = userMap.get(message.getSendId()).getName();
			else
				name = "用户已删除";
			message.setSendName(name);
			
			if (userMap.containsKey(message.getRecId()))
				name = userMap.get(message.getRecId()).getName();
			else
				name = "用户已删除";
			message.setRecName(name);
		}
		
		return messageList;
	}
	
	public Boolean addMessage(Message message) {
		return messageManager.addMessage(message);
	}
	
	public Boolean updateMessage(Message message) {
		return messageManager.updateMessage(message);
	}
	
	public Message getMessage(Integer id) {
		Message message = messageManager.getMessage(id);
		User sendUser = userManager.getUser(message.getSendId());
		User recUser = userManager.getUser(message.getRecId());
		message.setSendName(sendUser.getName());
		message.setRecName(recUser.getName());
		
		return message;
	}

	public Boolean deleteMessageList(List<Integer> idList) {
		return messageManager.deleteMessageList(idList);
	}
}
