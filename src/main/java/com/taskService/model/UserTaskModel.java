package com.taskService.model;

import java.util.List;

public class UserTaskModel {

	private String empEmail;
	private List<String> taskList;
	
	public String getEmpEmail() {
		return empEmail;
	}
	public void setEmpEmail(String empEmail) {
		this.empEmail = empEmail;
	}
	public List<String> getTaskList() {
		return taskList;
	}
	public void setTaskList(List<String> taskList) {
		this.taskList = taskList;
	}

}
