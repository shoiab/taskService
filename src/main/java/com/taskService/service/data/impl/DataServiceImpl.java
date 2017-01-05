package com.taskService.service.data.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.taskService.dbOperation.DbOperationService;
import com.taskService.model.TaskModel;
import com.taskService.service.data.DataService;
import com.taskService.service.data.TagService;
import com.taskService.solr.SearchHandler;
import com.taskService.utils.Encrypt;
import com.taskService.utils.UUIDGeneratorForUser;

@Service
public class DataServiceImpl implements DataService {

	@Autowired
	public DbOperationService dbservice;

	@Autowired
	public Encrypt encryptor;
	
	@Autowired
	private UUIDGeneratorForUser generateuuid;

	@Autowired
	private RedisTemplate<String, Object> template;
	
	@Autowired
	private SearchHandler solrService;
	
	@Autowired
	private TagService tagservice;

	@Override
	public String getUserEmail(String auth_key) {
		String email = template.opsForHash().entries(auth_key).get("email").toString();
		return email;
	}

	@Override
	public void createTaskTag(TaskModel taskModel) {
		dbservice.createTaskTag(taskModel);
	}

}