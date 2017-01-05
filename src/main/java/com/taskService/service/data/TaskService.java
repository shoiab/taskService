package com.taskService.service.data;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.http.HttpStatus;

import com.taskService.model.TaskModel;

public interface TaskService {

	HttpStatus createTask(TaskModel taskModel) throws SolrServerException, IOException;

	SolrDocumentList getAllTasks(String auth_key) throws SolrServerException, IOException;

	TaskModel fetchTask(String taskName);

}
