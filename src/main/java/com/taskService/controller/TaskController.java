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

	@RequestMapping(value = "/createTask", method = RequestMethod.POST)
	public @ResponseBody JSONObject createTask(
			@RequestHeader(value = "auth_key") String auth_key,
			@RequestBody TaskModel taskModel) throws NoSuchAlgorithmException,
			SolrServerException, IOException, ParseException {

		/*SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
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
		taskModel.setStatusOfCompletion(Constants.TASK_STATUS_OPEN);*/
		taskModel.setTaskCreator(dataservice.getUserEmail(auth_key));
		HttpStatus status = taskservice.createTask(taskModel);

		JSONObject statusobj = new JSONObject();
		statusobj.put("status", status.value());
		if (status == HttpStatus.FOUND) {
			statusobj.put("message", "Task already exists");
		} else {
			statusobj.put("message", "Task created successfully");
		}

		return statusobj;
	}

	@RequestMapping(value = "/postTask", method = RequestMethod.GET)
	public @ResponseBody JSONObject notifyTask(
			@RequestHeader(value = "auth_key") String auth_key,
			@RequestParam(value = "taskTitle") String taskTitle)
			throws URISyntaxException {

		TaskModel taskmodel = taskservice.fetchTask(taskTitle);
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
	}

	@RequestMapping(value = "/getUserTasks", method = RequestMethod.POST)
	public @ResponseBody SolrDocumentList getMyTasks(
			@RequestHeader(value = "auth_key") String auth_key)
			throws NoSuchAlgorithmException, SolrServerException, IOException {
		return taskservice.getAllTasks(auth_key);
	}

	@RequestMapping(value = "/getOpenCreatedTasks", method = RequestMethod.POST)
	public @ResponseBody JSONObject getOpenCreatedTasks(
			@RequestHeader(value = "auth_key") String auth_key)
			throws NoSuchAlgorithmException, SolrServerException, IOException {
		String email = dataservice.getUserEmail(auth_key);

		return taskservice.getCreatedTasks(email);

	}

	@RequestMapping(value = "/getCompletedCreatedTasks", method = RequestMethod.POST)
	public @ResponseBody JSONObject completedTasks(
			@RequestHeader(value = "auth_key") String auth_key)
			throws NoSuchAlgorithmException, SolrServerException, IOException {
		String email = dataservice.getUserEmail(auth_key);

		return taskservice.getCompletedCreatedTasks(email);

	}

	@RequestMapping(value = "/getPendingTasks", method = RequestMethod.POST)
	public @ResponseBody JSONObject getPendingTasks(
			@RequestHeader(value = "auth_key") String auth_key)
			throws NoSuchAlgorithmException, SolrServerException, IOException {
		String email = dataservice.getUserEmail(auth_key);

		return taskservice.getPendingTasks(email);
	}
	
	@RequestMapping(value = "/getCompletedTasks", method = RequestMethod.POST)
	public @ResponseBody JSONObject getCompletedTasks(
			@RequestHeader(value = "auth_key") String auth_key)
			throws NoSuchAlgorithmException, SolrServerException, IOException {
		String email = dataservice.getUserEmail(auth_key);

		return taskservice.getCompletedTasks(email);
	}

}
