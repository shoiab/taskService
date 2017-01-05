package com.taskService.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class GroupModel {
	
	private String groupName;
	private String GroupMailList;
	private Date dateOfCreation;
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
	public Date getDateOfCreation() {
		return dateOfCreation;
	}
	
	@JsonIgnore
	public void setDateOfCreation(Date dateOfCreation) {
		this.dateOfCreation = dateOfCreation;
	}

}
