package com.taskService.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocumentList;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.taskService.model.TaskModel;
import com.taskService.service.data.DataService;
import com.taskService.service.data.TaskService;

@RestController
public class TaskController {

	@Autowired
	TaskService taskservice;

	@Autowired
	DataService dataservice;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private Environment environment;

	/*@RequestMapping(value = "/createTask", method = RequestMethod.POST)
	public @ResponseBody JSONObject createTask(
			@RequestHeader(value = "auth_key") String auth_key,
			@RequestBody TaskModel taskModel) throws NoSuchAlgorithmException,
			SolrServerException, IOException, ParseException {

		taskModel.setTaskAssigner(dataservice.getUserEmail(auth_key));
		HttpStatus status = taskservice.createTask(taskModel);

		JSONObject statusobj = new JSONObject();
		statusobj.put("status", status.value());
		if (status == HttpStatus.FOUND) {
			statusobj.put("message", "Task already exists");
		} else {
			statusobj.put("message", "Task created successfully");
		}

		return statusobj;
	}*/
	
	@RequestMapping(value = "/createNewTask", method = RequestMethod.POST)
	public @ResponseBody JSONObject createNewTask(
			@RequestHeader(value = "auth_key") String auth_key,
			@RequestBody TaskModel taskModel) throws NoSuchAlgorithmException,
			SolrServerException, IOException, ParseException {
		
		taskModel.setTaskAssigner(dataservice.getUserEmail(auth_key));	
		JSONObject statusobj = taskservice.createNewTask(taskModel);
		
		return statusobj;
	}
	
	

	/*@RequestMapping(value = "/postTask", method = RequestMethod.GET)
	public @ResponseBody JSONObject notifyTask(
			@RequestHeader(value = "auth_key") String auth_key,
			@RequestParam(value = "taskid") String taskid)
			throws URISyntaxException {

		TaskModel taskmodel = taskservice.fetchTask(taskid);
		JSONObject statusobj = new JSONObject();

		if (taskmodel != null) {
			String url = "http://localhost:8081/api/notifier/sendEmail";

			restTemplate.getMessageConverters().add(
					new MappingJackson2HttpMessageConverter());
			Gson gson = new Gson();

			String task = gson.toJson(taskmodel);
			statusobj = restTemplate.postForObject(url, task, JSONObject.class);

			return statusobj;

		}
		return statusobj;
	}*/

	@RequestMapping(value = "/getUserTasks", method = RequestMethod.POST)
	public @ResponseBody SolrDocumentList getMyTasks(
			@RequestHeader(value = "auth_key") String auth_key)
			throws NoSuchAlgorithmException, SolrServerException, IOException {
		return taskservice.getAllTasks(auth_key);
	}

	//Fetch tasks from solr
	/*@RequestMapping(value = "/getTasksForStatus/v2", method = RequestMethod.POST)
	public @ResponseBody JSONObject getTasksForStatus(
			@RequestHeader(value = "auth_key") String auth_key,
			@RequestHeader(value = "taskStatus") String taskStatus)
			throws NoSuchAlgorithmException, SolrServerException, IOException {
		String email = dataservice.getUserEmail(auth_key);

		return taskservice.getTasksForStatusv2(email, taskStatus);
	}*/
	
	@RequestMapping(value = "/changeTaskStatus", method = RequestMethod.POST)
	public @ResponseBody JSONObject changeTaskStatus(
			@RequestHeader(value = "auth_key") String auth_key,
			@RequestHeader(value = "taskId") String taskId,
			@RequestHeader(value = "taskStatus") String taskStatus)
			throws NoSuchAlgorithmException, SolrServerException, IOException, ParseException {
		String email = dataservice.getUserEmail(auth_key);

		return taskservice.changeTaskStatus(email, taskId, taskStatus);
	}
	
	
	//Fetch tasks from mongoDB
	 @RequestMapping(value = "/getTasksForStatus/v1", method = RequestMethod.GET)
		public @ResponseBody JSONObject getOverduedTasksv1(
				@RequestHeader(value = "auth_key") String auth_key,
				@RequestHeader(value = "status") String status) throws SolrServerException, IOException{
			String email = dataservice.getUserEmail(auth_key);
			return taskservice.getTasksForStatusv1(email, status);
		}
	
	 
	//Fetch task count from solr
	@RequestMapping(value = "/getTaskCount/v2", method = RequestMethod.GET)
	public @ResponseBody JSONObject getTasksCountv2(
			@RequestHeader(value = "auth_key") String auth_key) throws SolrServerException, IOException, ParseException{
		String email = dataservice.getUserEmail(auth_key);
		return taskservice.getTasksCountv2(email);
	}
	

}
