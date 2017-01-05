package com.taskService.dbOperation;


import org.json.simple.JSONObject;

import com.taskService.model.GroupModel;
import com.taskService.model.TaskModel;

public interface DbOperationService {

	public void createTag(String name, String tagTypeUser, String email);

	public JSONObject createGroup(GroupModel groupmodel);

	public JSONObject createTask(TaskModel taskModel);

	public TaskModel fetchTask(String taskName);

	public void createTaskTag(TaskModel taskModel);

}
