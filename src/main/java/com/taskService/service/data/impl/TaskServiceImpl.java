package com.taskService.service.data.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.log4j.Logger;
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
public class TaskServiceImpl implements TaskService {

	public static final Logger logger = Logger.getLogger(TaskServiceImpl.class
			.getName());

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
	public HttpStatus createTask(TaskModel taskModel)
			throws SolrServerException, IOException, ParseException,
			NumberFormatException {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd kk:mm:ss");

		String currentDate = simpleDateFormat.format(new Date());

		taskModel.setTaskCreationDate(currentDate);
		if (taskModel.getNotificationTime().isEmpty()
				|| taskModel.getNotificationTime() == null) {
			long defaultNotificationDiffrence = Integer.parseInt(environment
					.getProperty("notificationTime")) * 60 * 1000;
			String stringDateOfCompleteion = taskModel.getEndDate();
			Date date = simpleDateFormat.parse(stringDateOfCompleteion);

			String defaultNotificationDate = simpleDateFormat.format(date
					.getTime() - defaultNotificationDiffrence);

			taskModel.setNotificationTime(defaultNotificationDate);
		}
		taskModel.setTaskStatus(Constants.TASK_STATUS_OPEN);

		UUID uuidForTask = generateuuid.generateUUID();
		final String key = String.format("task%s", uuidForTask);
		taskModel.setTaskId(key);

		JSONObject taskobj = dbservice.createTask(taskModel);
		if (taskobj.get("httpStatus") != HttpStatus.FOUND) {
			solrservice.createTask(taskModel);
			dataservice.createTaskTag(taskModel);
		}
		return (HttpStatus) taskobj.get("httpStatus");
	}

	@Override
	public TaskModel fetchTask(String taskid) {
		return dbservice.fetchTask(taskid);
	}

	@Override
	public SolrDocumentList getAllTasks(String auth_key)
			throws SolrServerException, IOException {
		return solrservice.getAllTasks(dataservice.getUserEmail(auth_key));
	}

	/*
	 * @Override public JSONObject getCreatedTasks(String email) throws
	 * SolrServerException, IOException { return
	 * solrservice.getCreatedTasks(email); }
	 * 
	 * @Override public JSONObject getCompletedCreatedTasks(String email) throws
	 * SolrServerException, IOException { return
	 * solrservice.getCompletedCreatedTasks(email); }
	 */

	/*
	 * @Override public JSONObject getPendingTasks(String email) throws
	 * SolrServerException, IOException { return
	 * solrservice.getPendingTasks(email); }
	 * 
	 * @Override public JSONObject getCompletedTasks(String email) throws
	 * SolrServerException, IOException{ return
	 * solrservice.getCompletedTasks(email); }
	 */

	@Override
	public JSONObject changeTaskStatus(String email, String taskId,
			String taskStatus) throws SolrServerException, IOException {
		logger.info("status :: " + taskStatus);

		JSONObject statusobj = new JSONObject();

		if (taskStatus.equals(Constants.TASK_STATUS_CLOSED)) {
			statusobj = dataservice.closeTask(email, taskId,
					taskStatus);
			
			TaskModel taskmodel = (TaskModel) statusobj.get("taskobj");

			if (taskmodel != null) {
				logger.info("status updated");
				solrservice.updateTaskStatus(email, taskId, taskStatus);
				solrservice.createTask(taskmodel);

				statusobj.put("message", "status updated successfully");
				statusobj.put("status", HttpStatus.OK.value());
			} else {
				statusobj.remove("taskobj");
				return statusobj;
			}

		} else {
			TaskModel taskmodel = dataservice.updateTaskStatus(email, taskId,
					taskStatus);

			if (taskmodel != null) {
				logger.info("status updated");
				solrservice.updateTaskStatus(email, taskId, taskStatus);
				solrservice.createTask(taskmodel);

				statusobj.put("message", "status updated successfully");
				statusobj.put("status", HttpStatus.OK.value());
			} else {
				statusobj
						.put("message", "Updation cannot be done");
				statusobj.put("status", HttpStatus.CONFLICT.value());
			}
		}
		
		return statusobj;

	}

	@Override
	public JSONObject getTasksForStatus(String email, String taskStatus)
			throws SolrServerException, IOException {
		return solrservice.getTasksForStatus(email, taskStatus);
	}

	@Override
	public JSONObject getAssignedTasksForStatus(String email, String taskStatus)
			throws SolrServerException, IOException {
		return solrservice.getAssignedTasksForStatus(email, taskStatus);
	}

}
