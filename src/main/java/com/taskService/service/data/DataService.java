package com.taskService.service.data;

import com.taskService.model.TaskModel;


public interface DataService {
	
	public String getUserEmail(String auth_key);

	public void createTaskTag(TaskModel taskModel);

}
