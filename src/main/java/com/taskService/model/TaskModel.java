package com.taskService.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class TaskModel {

	private String taskId;
	private String taskTitle;
	private String description;
	private String taskCreationDate;
	private String startDate;
	private String endDate;
	private String taskStatus;
	private String priority;
	private String taskAssigner;
	private String notificationTime;
	private List<TaskAssigneeModel> assigneeList;

	
	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}
	
	public String getNotificationTime() {
		return notificationTime;
	}
	
	public void setNotificationTime(String notificationTime) {
		this.notificationTime = notificationTime;
	}

	public String getTaskTitle() {
		return taskTitle;
	}

	@JsonIgnore
	public String getTaskId() {
		return taskId;
	}

	@JsonIgnore
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public void setTaskTitle(String taskTitle) {
		this.taskTitle = taskTitle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@JsonIgnore
	public String getTaskCreationDate() {
		return taskCreationDate;
	}

	@JsonIgnore
	public void setTaskCreationDate(String taskCreationDate) {
		this.taskCreationDate = taskCreationDate;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	@JsonIgnore
	public String getTaskStatus() {
		return taskStatus;
	}

	@JsonIgnore
	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}

	public List<TaskAssigneeModel> getAssigneeList() {
		return assigneeList;
	}

	public void setAssigneeList(List<TaskAssigneeModel> assigneeList) {
		this.assigneeList = assigneeList;
	}

	@JsonIgnore
	public String getTaskAssigner() {
		return taskAssigner;
	}

	@JsonIgnore
	public void setTaskAssigner(String taskAssigner) {
		this.taskAssigner = taskAssigner;
	}	

}
