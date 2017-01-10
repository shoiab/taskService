package com.taskService.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class GroupModel {
	
	private String groupName;
	private String GroupMailList;
	private String dateOfCreation;
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getGroupMailList() {
		return GroupMailList;
	}
	public void setGroupMailList(String groupMailList) {
		GroupMailList = groupMailList;
	}
	
	@JsonIgnore
	public String getDateOfCreation() {
		return dateOfCreation;
	}
	
	@JsonIgnore
	public void setDateOfCreation(String dateOfCreation) {
		this.dateOfCreation = dateOfCreation;
	}

}
