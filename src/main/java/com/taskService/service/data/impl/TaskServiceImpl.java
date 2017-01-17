package com.taskService.service.data.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocumentList;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.taskService.constants.Constants;
import com.taskService.dbOperation.DbOperationService;
import com.taskService.model.TaskModel;
import com.taskService.service.data.DataService;
import com.taskService.service.data.TaskService;
import com.taskService.solrService.SearchHandler;
import com.taskService.utils.UUIDGeneratorForUser;

@Service
public class TaskServiceImpl implements TaskService{
	
	@Autowired
	SearchHandler solrservice;
	
	@Autowired
	DbOperationService dbservice;
	
	@Autowired
	DataService dataservice;
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private UUIDGeneratorForUser generateuuid;

	@Override
	public HttpStatus createTask(TaskModel taskModel) throws SolrServerException, IOException, ParseException, NumberFormatException {
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd kk:mm:ss");

		String currentDate = simpleDateFormat.format(new Date());

		taskModel.setTaskCreationDate(currentDate);
		if (taskModel.getNotificationTime().isEmpty()
				|| taskModel.getNotificationTime() == null) {
			long defaultNotificationDiffrence = Integer.parseInt(environment
					.getProperty("notificationTime")) * 60 * 1000;
			String stringDateOfCompleteion = taskModel.getDateOfCompletion();
			Date date = simpleDateFormat.parse(stringDateOfCompleteion);

			String defaultNotificationDate = simpleDateFormat.format(date
					.getTime() - defaultNotificationDiffrence);

			taskModel.setNotificationTime(defaultNotificationDate);
		}
		taskModel.setStatusOfCompletion(Constants.TASK_STATUS_OPEN);
		
		UUID uuidForTask = generateuuid.generateUUID();
		final String key = String.format("task:%s", uuidForTask);
		taskModel.setTaskid(key);
		JSONObject taskobj = dbservice.createTask(taskModel);
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
