package com.taskService.service.data.impl;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocumentList;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.taskService.dbOperation.DbOperationService;
import com.taskService.model.TaskModel;
import com.taskService.service.data.DataService;
import com.taskService.service.data.TaskService;
import com.taskService.solrService.SearchHandler;

@Service
public class TaskServiceImpl implements TaskService{
	
	@Autowired
	SearchHandler solrservice;
	
	@Autowired
	DbOperationService dbservice;
	
	@Autowired
	DataService dataservice;

	@Override
	public HttpStatus createTask(TaskModel taskModel) throws SolrServerException, IOException {
		
		JSONObject taskobj = dbservice.createTask(taskModel);
		taskModel.setTaskid(taskobj.get("id").toString());
		if(taskobj.get("httpStatus") != HttpStatus.FOUND){
			solrservice.createTask(taskModel);
			dataservice.createTaskTag(taskModel);
		}
		return (HttpStatus) taskobj.get("httpStatus");
	}

	@Override
	public TaskModel fetchTask(String taskName) {
		return dbservice.fetchTask(taskName);
	}

	@Override
	public SolrDocumentList getAllTasks(String auth_key) throws SolrServerException, IOException {
		return solrservice.getAllTasks(dataservice.getUserEmail(auth_key));
	}

	@Override
	public JSONObject getCreatedTasks(String email) throws SolrServerException, IOException {
		return solrservice.getCreatedTasks(email);
	}

	@Override
	public JSONObject getCompletedCreatedTasks(String email) throws SolrServerException, IOException {
		return solrservice.getCompletedCreatedTasks(email);
	}

	@Override
	public JSONObject getPendingTasks(String email) throws SolrServerException,
			IOException {
		return solrservice.getPendingTasks(email);
	}

	@Override
	public JSONObject getCompletedTasks(String email) throws SolrServerException,
	IOException{
		return solrservice.getCompletedTasks(email);
	}

}
