package com.taskService.dbOperation;


import org.json.simple.JSONObject;

import com.taskService.model.GroupModel;
import com.taskService.model.TaskModel;

public interface DbOperationService {

	public void createTag(String name, String tagTypeUser, String email);

	public JSONObject createGroup(GroupModel groupmodel);

	public JSONObject createTask(TaskModel taskModel);

	public TaskModel fetchTask(String taskid);

	public void createTaskTag(TaskModel taskModel);

	public TaskModel updateTaskStatus(String email, String taskId, String taskStatus);

	public JSONObject closeTask(String email, String taskId, String taskStatus);

	public void createUserTaskMap(TaskModel taskModel);

	public JSONObject getNewTasks(String email, String status);
	
}
