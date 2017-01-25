package com.taskService.service.data.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocumentList;
import org.json.simple.JSONObject;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.taskService.constants.Constants;
import com.taskService.dbOperation.DbOperationService;
import com.taskService.jobScheduler.TaskJob;
import com.taskService.model.TaskAssigneeModel;
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

	/*@Override
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
		
		try {
			Date triggerDate = simpleDateFormat.parse(taskModel.getNotificationTime());
			logger.info("triggerDate" +triggerDate);
            JobDetail job = JobBuilder.newJob(TaskJob.class).withIdentity(taskModel.getTaskId()).build();
            
            JobDataMap jobDataMap=  job.getJobDataMap();
            jobDataMap.put("objectName", taskModel);
            
            Trigger trigger = TriggerBuilder.newTrigger().startAt(triggerDate).usingJobData(jobDataMap).build();
            
            Trigger trigger = TriggerBuilder.newTrigger().withSchedule(
            		SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(30).repeatForever())
            		.build();
            SchedulerFactory schFactory = new StdSchedulerFactory();
            Scheduler scheduler = schFactory.getScheduler(); 
            scheduler.start();
            scheduler.scheduleJob(job, trigger);

        }catch (SchedulerException e) {

            e.printStackTrace();
        }

		JSONObject taskobj = dbservice.createTask(taskModel);
		if (taskobj.get("httpStatus") != HttpStatus.FOUND) {
			solrservice.createTask(taskModel);
			dataservice.createTaskTag(taskModel);
		}
		return (HttpStatus) taskobj.get("httpStatus");
	}*/
	
	@Override
	public JSONObject createNewTask(TaskModel taskModel) throws ParseException, SolrServerException, IOException {
		
		JSONObject statusobj = new JSONObject();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd kk:mm:ss");
		TaskAssigneeModel assigneemodel = new TaskAssigneeModel();
		
		if(taskModel.getDescription().isEmpty()){
			statusobj.put("status", HttpStatus.BAD_REQUEST.value());
			statusobj.put("message", "Task description cannot be empty");
			return statusobj;
		}
		
		taskModel.setTaskStatus(Constants.TASK_STATUS_OPEN);
		String currentDate = simpleDateFormat.format(new Date());
		taskModel.setTaskCreationDate(currentDate);

		UUID uuidForTask = generateuuid.generateUUID();
		final String key = String.format("task%s", uuidForTask);
		taskModel.setTaskId(key);
		
		
		assigneemodel.setAssignee(taskModel.getTaskAssigner());
		assigneemodel.setAssigneeType(Constants.TAG_TYPE_USER);
		List<TaskAssigneeModel> assigneeList = taskModel.getAssigneeList();
		assigneeList.add(assigneemodel);
		taskModel.setAssigneeList(assigneeList);
		
		if(!taskModel.getEndDate().isEmpty()){
			setTaskNotification(taskModel);
		}
		
		statusobj = dbservice.createTask(taskModel);
		if (statusobj.get("status") != HttpStatus.FOUND) {
			solrservice.createTask(taskModel);
			//dataservice.createTaskTag(taskModel);
			dataservice.createUserTaskMap(taskModel);
		}
		
		return statusobj;
	}

	private void setTaskNotification(TaskModel taskModel) throws ParseException {
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd kk:mm:ss");
		
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
		
		createJobScheduler(taskModel);
		
	}

	private void createJobScheduler(TaskModel taskModel) throws ParseException {
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd kk:mm:ss");

		try {
			Date triggerDate = simpleDateFormat.parse(taskModel.getNotificationTime());
			logger.info("triggerDate" +triggerDate);
            JobDetail job = JobBuilder.newJob(TaskJob.class).withIdentity(taskModel.getTaskId()).build();
            
            JobDataMap jobDataMap=  job.getJobDataMap();
            jobDataMap.put("objectName", taskModel);
            
            Trigger trigger = TriggerBuilder.newTrigger().startAt(triggerDate).usingJobData(jobDataMap).build();
            
            /*Trigger trigger = TriggerBuilder.newTrigger().withSchedule(
            		SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(30).repeatForever())
            		.build();*/
            SchedulerFactory schFactory = new StdSchedulerFactory();
            Scheduler scheduler = schFactory.getScheduler(); 
            scheduler.start();
            scheduler.scheduleJob(job, trigger);

        }catch (SchedulerException e) {

            e.printStackTrace();
        }
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

	@Override
	public JSONObject changeTaskStatus(String email, String taskId,
			String taskStatus) throws SolrServerException, IOException, ParseException {
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
	
	
	//Fetch tasks from solr
	@Override
	public JSONObject getTasksForStatusv2(String email, String taskStatus)
			throws SolrServerException, IOException {
		return solrservice.getTasksForStatusv2(email, taskStatus);
	}
	
	//fetch count from solr
	@Override
	public JSONObject getTasksCountv2(String email) throws SolrServerException, IOException, ParseException {
		return solrservice.getTasksCountv2(email);
	}

	
	//Fetch tasks from mongoDB
	@Override
	public JSONObject getTasksForStatusv1(String email, String status) {
		
		JSONObject taskobj = new JSONObject();
		
		switch (status) {
		case Constants.TASK_STATUS_NEW:
			taskobj = dbservice.getNewTasks(email, status);
			break;
		
		case Constants.TASK_STATUS_TODAY:
			taskobj = dbservice.getTodayTasks(email,status);
			break;
			
		case Constants.TASK_STATUS_OVERDUE:
			taskobj = dbservice.getOverdueTasks(email, status);
			break;
			
		case Constants.TASK_STATUS_CLOSED:
			taskobj = dbservice.getClosedTasks(email, status);
			break;

		default:
			break;
		}
		return taskobj;
	}

	

}
