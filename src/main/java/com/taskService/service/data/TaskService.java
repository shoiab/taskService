package com.taskService.service.data;

import java.io.IOException;
import java.text.ParseException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocumentList;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;

import com.taskService.model.TaskModel;

public interface TaskService {

	//HttpStatus createTask(TaskModel taskModel) throws SolrServerException, IOException, ParseException;

	SolrDocumentList getAllTasks(String auth_key) throws SolrServerException, IOException;

	TaskModel fetchTask(String taskid);

	/*JSONObject getCreatedTasks(String email) throws SolrServerException, IOException;

	JSONObject getCompletedCreatedTasks(String email) throws SolrServerException, IOException;*/

	/*JSONObject getPendingTasks(String email) throws SolrServerException, IOException;

	JSONObject getCompletedTasks(String email) throws SolrServerException, IOException;*/

	JSONObject changeTaskStatus(String email, String taskId, String taskStatus) throws SolrServerException, IOException, ParseException;

	JSONObject getTasksForStatusv2(String email, String taskStatus) throws SolrServerException, IOException;

	//JSONObject getAssignedTasksForStatus(String email, String taskStatus) throws SolrServerException, IOException;

	JSONObject createNewTask(TaskModel taskModel) throws ParseException, SolrServerException, IOException;

	JSONObject getTasksCountv2(String email) throws SolrServerException, IOException, ParseException;

	JSONObject getTasksForStatusv1(String email, String status);

}
