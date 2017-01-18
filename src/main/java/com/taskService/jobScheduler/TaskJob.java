package com.taskService.jobScheduler;

import org.json.simple.JSONObject;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.taskService.model.TaskModel;

public class TaskJob implements Job{
	
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException{
    	
    	final Logger logger = LoggerFactory.getLogger(this.getClass());
    	RestTemplate restTemplate = new RestTemplate();
    	
    	logger.info("Quartz scheduler started.....");
    	JobDataMap dataMap = context.getJobDetail().getJobDataMap();
    	JSONObject statusobj = new JSONObject();

		if (dataMap != null) {
			Gson gson = new Gson();
			String url = "http://localhost:8081/api/notifier/sendEmail";
			logger.info(dataMap.toString());
			TaskModel taskmodel = (TaskModel)dataMap.get("objectName");
			logger.info(taskmodel.toString());
			String task = gson.toJson(taskmodel);
			statusobj = restTemplate.postForObject(url, task, JSONObject.class);
			logger.info(statusobj.toString());
		}
    }
}
