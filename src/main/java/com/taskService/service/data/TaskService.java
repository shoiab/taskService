package com.taskService.service.data;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocumentList;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;

import com.taskService.model.TaskModel;

public interface TaskService {

	HttpStatus createTask(TaskModel taskModel) throws SolrServerException, IOException;

	SolrDocumentList getAllTasks(String auth_key) throws SolrServerException, IOException;

	TaskModel fetchTask(String taskName);

	JSONObject getCreatedTasks(String email) throws SolrServerException, IOException;

	JSONObject getCompletedCreatedTasks(String email) throws SolrServerException, IOException;

	JSONObject getPendingTasks(String email) throws SolrServerException, IOException;

	JSONObject getCompletedTasks(String email) throws SolrServerException, IOException;

}
