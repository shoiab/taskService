package com.taskService.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class TaskModel {

	private String taskid;
	private String taskTitle;
	private String description;
	private String taskCreationDate;
	private String dateOfStart;
	private String dateOfCompletion;
	private String statusOfCompletion;
	private String priority;
	private String taskCreator;
	private String notificationTime;
	private List<TaskRecipientModel> recipientList;

	public String getDateOfStart() {
		return dateOfStart;
	}

	public void setDateOfStart(String dateOfStart) {
		this.dateOfStart = dateOfStart;
	}

	public String getDateOfCompletion() {
		return dateOfCompletion;
	}

	public void setDateOfCompletion(String dateOfCompletion) {
		this.dateOfCompletion = dateOfCompletion;
	}

	@JsonIgnore
	public String getStatusOfCompletion() {
		return statusOfCompletion;
	}

	@JsonIgnore
	public void setStatusOfCompletion(String statusOfCompletion) {
		this.statusOfCompletion = statusOfCompletion;
	}

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

	@JsonIgnore
	public String getTaskCreator() {
		return taskCreator;
	}

	@JsonIgnore
	public void setTaskCreator(String taskCreator) {
		this.taskCreator = taskCreator;
	}

	@JsonIgnore
	public String getTaskid() {
		return taskid;
	}

	public List<TaskRecipientModel> getRecipientList() {
		return recipientList;
	}

	public void setRecipientList(List<TaskRecipientModel> recipientList) {
		this.recipientList = recipientList;
	}

	@JsonIgnore
	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	public String getTaskTitle() {
		return taskTitle;
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

}
