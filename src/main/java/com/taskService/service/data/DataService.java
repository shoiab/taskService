package com.taskService.service.data;

import org.json.simple.JSONObject;

import com.taskService.model.TaskModel;


public interface DataService {
	
	public String getUserEmail(String auth_key);

	public void createTaskTag(TaskModel taskModel);

	public TaskModel updateTaskStatus(String email, String taskId, String taskStatus);

	public JSONObject closeTask(String email, String taskId, String taskStatus);

	public void createUserTaskMap(TaskModel taskModel);

}
